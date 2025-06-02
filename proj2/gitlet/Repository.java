package gitlet;

// import org.antlr.v4.runtime.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blob");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File REMOVAL = join(GITLET_DIR, "removal");
    public static final File BRANCHES = join(GITLET_DIR, "branches");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File HEAD_ID = join(GITLET_DIR, "head", "id");
    public static final File HEAD_NAME = join(GITLET_DIR, "head", "name");

    /* TODO: fill in the rest of this class. */
    // Constructor: if there is already .gitlet file in CWD, abort
    // Otherwise: create .gitlet file, set default branch, create initial commit and save it
    public static void SetupRepo() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        STAGING_DIR.mkdir();
        HEAD.mkdir();

        try {
            REMOVAL.createNewFile();
        } catch (IOException e) {
            System.out.println("Create new File failed");
        }

        try {
            BRANCHES.createNewFile();
        } catch (IOException e) {
            System.out.println("Create new File failed");
        }

        try {
            HEAD_ID.createNewFile();
        } catch (IOException e) {
            System.out.println("Create new File failed");
        }

        try {
            HEAD_NAME.createNewFile();
        } catch (IOException e) {
            System.out.println("Create new File failed");
        }

        Date initialTime = new Date(0);
        long timestamp = initialTime.getTime();
        Commit initial = new Commit("initial commit", timestamp);
        String sha1 = initial.SaveCommit();
        String defaultBranch = "master";
        TreeMap<String, String> branches = new TreeMap<>();
        branches.put(defaultBranch, sha1);
        ArrayList<String> removal = new ArrayList<>();
        Utils.writeObject(REMOVAL, removal);
        Utils.writeObject(BRANCHES, branches);
        Utils.writeObject(HEAD_ID, sha1);
        Utils.writeObject(HEAD_NAME, defaultBranch);
    }

    public static void Staging(String filename) {
        File f = join(CWD, filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String sha1 = sha1Offile(f);
        byte[] content = Utils.readContents(f);
        /* If the current working version of the file is identical to the version in the current commit,
        do not stage it to be added, and remove it from the staging area if it is already there
        (as can happen when a file is changed, added, and then changed back to it’s original version).
         */
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit CurrentCommit = getCommit(CBsha1);
        String CCVersionSha1 = CurrentCommit.getBlobSha1(filename);
        File Instage = join(STAGING_DIR, filename);
        if (sha1.equals(CCVersionSha1)) {
            if (Instage.exists()) {
                Utils.restrictedDelete(Instage);
            }
        } else {
            // writeContents will rewrite the file if it exists; create file and write content if it doesn't exist
            Utils.writeContents(Instage, content);
        }

        /*The file will no longer be staged for removal (see gitlet rm),
        if it was at the time of the command.
         */
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        if (removal.contains(filename)) {
            removal.remove(filename);
        }
        Utils.writeObject(REMOVAL, removal);
    }

    public static void MakeCommit(String message) {
        List<String> stagedFiles = Utils.plainFilenamesIn(STAGING_DIR);
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        if (stagedFiles.size() == 0 && removal.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Date initialTime = new Date();
        long timestamp = initialTime.getTime();
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit NewCommit = new Commit(message, timestamp, CBsha1);

        ProcessForCommit(NewCommit);
    }



    public static void MakeCommit(String message, String parent2) {
        List<String> stagedFiles = Utils.plainFilenamesIn(STAGING_DIR);
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        if (stagedFiles.size() == 0 && removal.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Date initialTime = new Date();
        long timestamp = initialTime.getTime();
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit NewCommit = new Commit(message, timestamp, CBsha1, parent2);

        ProcessForCommit(NewCommit);
    }


    private static void ProcessForCommit(Commit c) {
        List<String> stagedFiles = Utils.plainFilenamesIn(STAGING_DIR);
        // Process blobs in staging area
        for (String f: stagedFiles) {
            File curr = join(STAGING_DIR, f);
            byte[] content = Utils.readContents(curr);
            String fsha1 = sha1Offile(curr);

            // Add filename-blobsha1 pair into Commit.containedblobs
            c.addBlobs(f, fsha1);

            // Save blobs in staging area into .gitlet/blobs
            File newblob = join(BLOBS_DIR, fsha1);
            if (!newblob.exists()) {
                Utils.writeContents(newblob, content);
            }

            // Clear staging area
            curr.delete();
        }

        // Delete the files staged for removal from commit contained blobs
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        for (String f : removal) {
            c.deleteBlobs(f);
        }
        // Clear removal list
        removal.clear();
        Utils.writeObject(REMOVAL, removal);

        // move the head pointer to new commit
        String CBsha1 = c.SaveCommit();
        Utils.writeObject(HEAD_ID, CBsha1);
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        branches.put(CBname,CBsha1);
        Utils.writeObject(BRANCHES, branches);
    }


    public static void remove(String filename) {
        List<String> stagedfiles = Utils.plainFilenamesIn(STAGING_DIR);
        File CWDfile = join(CWD, filename);

        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit CurrentCommit = getCommit(CBsha1);
        if (stagedfiles.contains(filename)) {
            File file = join(STAGING_DIR, filename);
            file.delete();
        }
        if (CurrentCommit.BlobsContained(filename)) { //log N, how to achieve constant time
            ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
            removal.add(filename);
            Utils.writeObject(REMOVAL, removal);
            if (CWD.exists()) {
                Utils.restrictedDelete(CWDfile);
            }
        }
        if (!stagedfiles.contains(filename) && !CurrentCommit.BlobsContained(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    public static void log() {
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit CurrentCommit = getCommit(CBsha1);
        CurrentCommit.Printlog(CBsha1);
    }

    public static void global_log() {
        List<String> commits = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String commitsha1 : commits) {
            File f = join(COMMITS_DIR, commitsha1);
            Commit c = Utils.readObject(f, Commit.class);
            c.printme(commitsha1);
        }
    }

    public static void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(COMMITS_DIR);
        int number = 0;
        for (String commitsha1 : commits) {
            Commit c = getCommit(commitsha1);
            if (message.equals(c.getMessage())){
                System.out.println(commitsha1);
                number++;
            }
        }
        if (number == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        for (String b : branches.keySet()) {
            if (b.equals(CBname)) {
                System.out.println("*" + b);
            } else {
                System.out.println(b);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String> staged = Utils.plainFilenamesIn(STAGING_DIR);
        Collections.sort(staged);
        for (String f : staged) {
            System.out.println(f);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        List<String> removed = new ArrayList<>(removal);
        Collections.sort(removed);
        for (String r : removed) {
            System.out.println(r);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeSet<String> modi = new TreeSet<String>();
        for (String f : staged) {
            File stagedF = join(STAGING_DIR, f);
            File cwdF = join(CWD, f);
            if (!cwdF.exists()) {
                modi.add(f);
            } else {
                if (!sha1Equals(stagedF, cwdF)) {
                    modi.add(f);
                }
            }
        }

        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit CurrentCommit = getCommit(CBsha1);
        TreeMap<String, String> tracked = CurrentCommit.Blobs();
        for (Map.Entry<String, String> entry : tracked.entrySet()) {
            String name = entry.getKey();
            File cwdF = join(CWD, name);
            if (!cwdF.exists() && !removal.contains(name)) {
                modi.add(name);
            } else if (cwdF.exists()) {
                if (staged.contains(name) && !entry.getValue().equals(sha1Offile(cwdF))) {
                    modi.add(name);
                }
            }
        }
        for (String filename : modi) {
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        List<String> untracked = getUntrackedFiles();
        Collections.sort(untracked);
        for (String s : untracked) {
            System.out.println(s);
        }
        System.out.println();
    }

    public static void checkout(String filename){
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        String sha1inblob = FindFileinCommit(CBsha1, filename);
        if (sha1inblob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File curr = join(CWD, filename);
        if (!curr.exists() || (curr.exists() && !sha1Offile(curr).equals(sha1inblob))) {
            RecoverFile(sha1inblob, filename);
        }
    }

    public static void checkout(String commitid, String filename) {
        String RealCommitID = RealCommit(commitid);

        if (RealCommitID.equals("None")) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        String sha1inblob = FindFileinCommit(RealCommitID, filename);
        if (sha1inblob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        File curr = join(CWD, filename);
        if (!curr.exists() || (curr.exists() && !sha1Offile(curr).equals(sha1inblob))) {
            RecoverFile(sha1inblob, filename);
        }
    }

    public static void checkoutBranch(String branch) {
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        if (branch.equals(CBname)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        String futurecommitid = branches.get(branch);
        RecoverCommit(futurecommitid);
        ClearStaging();
        Utils.writeObject(HEAD_NAME, branch);
        Utils.writeObject(HEAD_ID, futurecommitid);
    }

    public static void CreateBranch(String name) {
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        if (branches.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        branches.put(name, CBsha1);
        Utils.writeObject(BRANCHES, branches);
    }

    public static void RemoveBranch(String name) {
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        if (name.equals(CBname)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else if (!branches.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        branches.remove(name);
        Utils.writeObject(BRANCHES, branches);
    }

    public static void Reset(String commitid) {
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        String RealID = RealCommit(commitid);
        if(RealID.equals("None")) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        RecoverCommit(RealID);
        branches.put(CBname, RealID);
        Utils.writeObject(BRANCHES, branches);
        Utils.writeObject(HEAD_ID, RealID);
        ClearStaging();
    }

    public static void merge(String branch) {
        String CBname = Utils.readObject(HEAD_NAME, String.class);
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        TreeMap<String, String> branches = Utils.readObject(BRANCHES, TreeMap.class);
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        if (branch.equals(CBname)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        if (!removal.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        List<String> staged = Utils.plainFilenamesIn(STAGING_DIR);
        if (!staged.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        String GivenID = branches.get(branch);
        if (GivenID == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String splitP = SplitPoint(CBsha1, GivenID);
        if (splitP.equals(GivenID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitP.equals(CBname)) {
            checkoutBranch(branch);
        }

        boolean isConflict = false;
        Commit GivenC = getCommit(GivenID);
        Commit CurrC = getCommit(CBsha1);
        Commit SPC = getCommit(splitP);

        Set<String> FilesinGivenC = GivenC.Blobs().keySet();
        Set<String> FilesinCurrC = CurrC.Blobs().keySet();
        Set<String> FilesinSplitP = SPC.Blobs().keySet();
        List<String> cwd = Utils.plainFilenamesIn(CWD);
        Set<String> Untracked = new TreeSet<>(cwd);
        Untracked.remove(FilesinCurrC);
        Set<String> InBoth = new TreeSet<>(FilesinCurrC);
        InBoth.retainAll(FilesinGivenC);
        Set<String> OnlyinGiven = new TreeSet<>(FilesinGivenC);
        OnlyinGiven.remove(FilesinCurrC);
        Set<String> OnlyinCurr = new TreeSet<>(FilesinCurrC);
        OnlyinCurr.remove(FilesinGivenC);
        for (String s : Untracked) {
            File f = join(CWD, s);
            if (FilesinSplitP.contains(s)) {
                if (!GivenC.getBlobSha1(s).equals(SPC.getBlobSha1(s))) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            } else {
                if (OnlyinGiven.contains(s) && !sha1Offile(f).equals(GivenC.getBlobSha1(s))) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }

        }

        for (String s : InBoth) {
            if (FilesinSplitP.contains(s)) {
                if (SPC.getBlobSha1(s).equals(CurrC.getBlobSha1(s)) && !SPC.getBlobSha1(s).equals(CurrC.getBlobSha1(s))){
                    checkout(GivenID, s);
                    Staging(s);
                } else if (!SPC.getBlobSha1(s).equals(CurrC.getBlobSha1(s)) &&
                            !SPC.getBlobSha1(s).equals(GivenC.getBlobSha1(s)) &&
                            !GivenC.getBlobSha1(s).equals(CurrC.getBlobSha1(s))) {
                    isConflict = true;
                    MergeConfilct(CBsha1, GivenID, s);
                    Staging(s);
                }
            } else {
                if(!GivenC.getBlobSha1(s).equals(CurrC.getBlobSha1(s))) {
                    isConflict = true;
                    MergeConfilct(CBsha1, GivenID, s);
                    Staging(s);
                }
            }
        }
        for (String s : OnlyinCurr) {
            if (FilesinSplitP.contains(s)) {
                if (SPC.getBlobSha1(s).equals(CurrC.getBlobSha1(s))) {
                    remove(s);
                } else {
                    isConflict = true;
                    MergeConfilct(CBsha1, GivenID, s);
                    Staging(s);
                }
            }
        }
        for (String s : OnlyinGiven) {
            if (FilesinSplitP.contains(s)) {
                if (!SPC.getBlobSha1(s).equals(GivenC.getBlobSha1(s))) {
                    isConflict = true;
                    MergeConfilct(CBsha1, GivenID, s);
                    Staging(s);
                }
            } else {
                checkout(GivenID, s);
                Staging(s);
            }
        }

        MakeCommit("Merged branch into " + CBname +".", GivenID);

        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static String FindFileinCommit(String commitid, String filename) {
        Commit c = getCommit(commitid);
        String sha1 = c.getBlobSha1(filename);
        return sha1;
    }

    private static void RecoverFile(String blobsha1, String filename) {
        File cwdf = join(CWD, filename);
        File tocheck = join(BLOBS_DIR, blobsha1);
        byte[] content = Utils.readContents(tocheck);
        Utils.writeContents(cwdf, content);
    }

    private static void ClearStaging() {
        List<String> staged = Utils.plainFilenamesIn(STAGING_DIR);
        for (String s : staged) {
            File f = join(STAGING_DIR, s);
            f.delete();
        }
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        removal.clear();
        Utils.writeObject(REMOVAL, removal);
    }

    private static boolean sha1Equals(File file1, File file2) {
        byte[] C1 = Utils.readContents(file1);
        byte[] C2 = Utils.readContents(file2);
        String sha11 = Utils.sha1(C1);
        String sha12 = Utils.sha1(C2);
        return sha11.equals(sha12);
    }

    private static String sha1Offile(File file) {
        byte[] content = Utils.readContents(file);
        String sha1 = Utils.sha1(content);
        return sha1;
    }

    private static Commit getCommit(String sha1) {
        File CCFile = join(COMMITS_DIR, sha1);
        Commit c = readObject(CCFile, Commit.class);
        return c;
    }

    private static List<String> getUntrackedFiles() {
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        ArrayList<String> removal = Utils.readObject(REMOVAL, ArrayList.class);
        List<String> untracked = new ArrayList<>();
        Commit CurrentCommit = getCommit(CBsha1);
        TreeMap<String, String> CommitTracked = CurrentCommit.Blobs();
        List<String> staged = Utils.plainFilenamesIn(STAGING_DIR);
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String f : cwdFiles) {
            if ((!staged.contains(f) && !CommitTracked.containsKey(f)) || removal.contains(f)) {
                untracked.add(f);
            }
        }
        return untracked;
    }

    private static String RealCommit(String commitid) {
        List<String> commits = Utils.plainFilenamesIn(COMMITS_DIR);
        String RealCommitID = "None";
        for (String c : commits) {
            if (commitid.equals(c) || commitid.equals(c.substring(0, 6))) {
                RealCommitID = c;
            }
        }
        return RealCommitID;
    }

    private static void RecoverCommit(String futurecommitid) {
        String CBsha1 = Utils.readObject(HEAD_ID, String.class);
        Commit FutureCommit = getCommit(futurecommitid);
        Commit CurrCommit = getCommit(CBsha1);
        List<String> cwd = Utils.plainFilenamesIn(CWD);
        Set<String> FilesinCWD = new HashSet<>(cwd);
        Set<String> FilesInCC = CurrCommit.Blobs().keySet();
        Set<String> FilesInFC = FutureCommit.Blobs().keySet();
        Set<String> Untracked = new HashSet<>(FilesinCWD);

        // For untracked files, if it will be deleted or rewriten by checkout, throw an error
        Untracked.removeAll(FilesInCC);
        for (String f : Untracked) {
            File FileinCWD = join(CWD, f);
            String sha1inCWD = sha1Offile(FileinCWD);
            String sha1inFutureBlob = FutureCommit.getBlobSha1(f);
            if(!FutureCommit.BlobsContained(f)) {
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
            if (!sha1inFutureBlob.equals(sha1inCWD)) {
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        // For tracked files, delete it if it doesn't exist in Future Commit; rewrite it if it has different sha1 with Future Commit
        Set<String> Tracked = new HashSet<>(FilesinCWD);
        Tracked.retainAll(FilesInCC);
        for (String f : Tracked) {
            File FileinCWD = join(CWD, f);
            String sha1inCWD = sha1Offile(FileinCWD);
            String sha1inFutureBlob = FutureCommit.getBlobSha1(f);
            if(!FutureCommit.BlobsContained(f)) {
                Utils.restrictedDelete(FileinCWD);
            } else if (!sha1inFutureBlob.equals(sha1inCWD)) {
                RecoverFile(sha1inFutureBlob, f);
            }
        }

        // for files in Future Commit but not in CWD, copy them into CWD
        Set<String> Extra = new HashSet<>(FilesInFC);
        Extra.removeAll(FilesinCWD);
        for (String f: Extra) {
            String sha1inFutureBlob = FutureCommit.getBlobSha1(f);
            RecoverFile(sha1inFutureBlob, f);
        }
    }

    private static String SplitPoint(String c1, String c2) {
        TreeSet<String> P1 = Parents(c1);
        Deque<String> queue = new LinkedList<>();
        queue.add(c2);
        while (!queue.isEmpty()) {
            String curr = queue.removeFirst();
            if (P1.contains(curr)) {
                return curr;
            }
            Commit com = getCommit(curr);
            List<String> currparents = com.parents();
            for (String p : currparents) {
                queue.addLast(p);
            }
        }
        return null;
    }

    private static TreeSet<String> Parents(String c) {
        TreeSet<String> AllParents = new TreeSet<>();
        Deque<String> queue = new LinkedList<>();
        queue.add(c);
        while (!queue.isEmpty()) {
            String curr = queue.removeFirst();
            AllParents.add(curr);
            Commit com = getCommit(curr);
            List<String> currparents = com.parents();
            for (String p : currparents) {
                queue.addLast(p);
            }
        }
        return AllParents;
    }

    private static void MergeConfilct(String CurrCID, String GivenCID, String filename) {
        Commit CurrC = getCommit(CurrCID);
        Commit GivenC = getCommit(GivenCID);
        File newfile = join(CWD, filename);
        String CurrblobID = CurrC.getBlobSha1(filename);
        String GivenblobID = GivenC.getBlobSha1(filename);
        String newCon;
        if (CurrblobID != null && GivenblobID != null) {
            File CurrFile = join(BLOBS_DIR, CurrblobID);
            File GivenFile = join(BLOBS_DIR, GivenblobID);
            String CurrCon = Utils.readContentsAsString(CurrFile);
            String GivenCon = Utils.readContentsAsString(GivenFile);
            newCon = "<<<<<<< HEAD" + "\n" + CurrCon + "=======" + "\n" + GivenCon + ">>>>>>>";
        } else if (CurrblobID != null && GivenblobID == null) {
            File CurrFile = join(BLOBS_DIR, CurrblobID);
            String CurrCon = Utils.readContentsAsString(CurrFile);
            newCon = "<<<<<<< HEAD" + "\n" + CurrCon + "=======" + "\n" + ">>>>>>>";
        } else {
            File GivenFile = join(BLOBS_DIR, GivenblobID);
            String GivenCon = Utils.readContentsAsString(GivenFile);
            newCon = "<<<<<<< HEAD" + "\n" + "=======" + "\n" + GivenCon + ">>>>>>>";
        }
        Utils.writeContents(newfile, newCon);
    }


}
