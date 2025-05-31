package gitlet;

import java.io.File;
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
    private static TreeMap<String, String> branches;
    private static String CBsha1;
    private static String CBname;
    private static List<String> removal = new ArrayList<>();

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blob");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");

    /* TODO: fill in the rest of this class. */
    // Constructor: if there is already .gitlet file in CWD, abort
    // Otherwise: create .gitlet file, set default branch, create initial commit and save it
    public static void SetupRepo() {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        STAGING_DIR.mkdir();

        Date initialTime = new Date(0);
        long timestamp = initialTime.getTime();
        Commit initial = new Commit("initial commit", timestamp);
        String sha1 = initial.SaveCommit();
        String defaultBranch = "master";
        branches.put(defaultBranch, sha1);
        CBsha1 = sha1;
        CBname = defaultBranch;
    }

    public static void Staging(String filename) {
        File f = join(CWD, filename);
        if (!f.exists()) {
            throw new GitletException("File does not exist.");
        }
        String sha1 = sha1Offile(f);
        byte[] content = Utils.readContents(f);

        /* If the current working version of the file is identical to the version in the current commit,
        do not stage it to be added, and remove it from the staging area if it is already there
        (as can happen when a file is changed, added, and then changed back to it’s original version).
         */
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
        if (removal.contains(filename)) {
            removal.remove(filename);
        }
    }

    public static void MakeCommit(String message) {
        List<String> stagedFiles = Utils.plainFilenamesIn(STAGING_DIR);
        if (stagedFiles.size() == 0) {
            throw new GitletException("No changes added to the commit.");
        }
        Date initialTime = new Date();
        long timestamp = initialTime.getTime();
        Commit NewCommit = new Commit(message, timestamp, CBsha1);

        // Process blobs in staging area
        for (String f: stagedFiles) {
            File curr = join(STAGING_DIR, f);
            byte[] content = Utils.readContents(curr);
            String fsha1 = sha1Offile(curr);


            // Add filename-blobsha1 pair into Commit.containedblobs
            NewCommit.addBlobs(f, fsha1);

            // Save blobs in staging area into .gitlet/blobs
            File newblob = join(BLOBS_DIR, fsha1);
            if (!newblob.exists()) {
                Utils.writeContents(newblob, content);
            }

            // Clear staging area
            Utils.restrictedDelete(curr);
        }

        // Delete the files staged for removal from commit contained blobs
        for (String f : removal) {
            NewCommit.deleteBlobs(f);
        }
        // Clear removal list
        removal.clear();

        // move the head pointer to new commit
        CBsha1 = NewCommit.SaveCommit();
    }

    public static void remove(String filename) {
        File stagedfile = join(STAGING_DIR, filename);
        File CWDfile = join(CWD, filename);
        Commit CurrentCommit = getCommit(CBsha1);
        if (stagedfile.exists()) {
            Utils.restrictedDelete(stagedfile);
        } else if (CurrentCommit.BlobsContained(filename)) { //log N, how to achieve constant time
            removal.add(filename);
            CWDfile.delete();
        } else {
            throw new GitletException("No reason to remove the file.");
        }
    }

    public static void log() {
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
            throw new GitletException("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
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
        String sha1inblob = findsha1(CBsha1,filename);
        recover(sha1inblob, filename);
    }

    public static void checkout(String commitid, String filename) {
        List<String> commits = Utils.plainFilenamesIn(COMMITS_DIR);
        String RealCommitID = "NoSuchCommit";
        for (String c : commits) {
            if (commitid.equals(c) || commitid.equals(c.substring(0, 6))) {
                RealCommitID = c;
            }
        }
        if (RealCommitID == "NoSuchCommit") {
            throw new GitletException("No commit with that id exists.");
        }
        String sha1inblob = findsha1(RealCommitID,filename);
        recover(sha1inblob, filename);
    }

    public static void checkoutBranch(String branch) {
        if (branch.equals(CBname)) {
            throw new GitletException("No need to checkout the current branch.");
        }
        if (!branches.containsKey(branch)) {
            throw new GitletException("No such branch exists.");
        }

        String commitid = branches.get(branch);


    }

    public static String findsha1(String commitid, String filename) {
        Commit c = getCommit(commitid);
        String sha1 = c.getBlobSha1(filename);
        if (sha1 == null) {
            throw new GitletException("File does not exist in that commit.");
        } else {
            return sha1;
        }
    }

    public static void recover(String blobsha1, String filename) {
        File cwdf = join(CWD, filename);
        File tocheck = join(BLOBS_DIR, blobsha1);
        byte[] content = Utils.readContents(tocheck);
        Utils.writeContents(cwdf, content);
    }

    public static boolean sha1Equals(File file1, File file2) {
        byte[] C1 = Utils.readContents(file1);
        byte[] C2 = Utils.readContents(file2);
        String sha11 = Utils.sha1(C1);
        String sha12 = Utils.sha1(C2);
        return sha11.equals(sha12);
    }

    public static String sha1Offile(File file) {
        byte[] content = Utils.readContents(file);
        String sha1 = Utils.sha1(content);
        return sha1;
    }

    public static Commit getCommit(String sha1) {
        File CCFile = join(COMMITS_DIR, sha1);
        Commit CurrentCommit = readObject(CCFile, Commit.class);
        return CurrentCommit;
    }

    public static List<String> getUntrackedFiles() {
        List<String> untracked = new ArrayList<>();
        Commit CurrentCommit = getCommit(CBsha1);
        TreeMap<String, String> CommitTracked = CurrentCommit.Blobs();
        List<String> staged = Utils.plainFilenamesIn(STAGING_DIR);
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String f : cwdFiles) {
            if ((!staged.contains(f) && !CommitTracked.containsKey(f)) || removal.contains(f)) {
                untracked.addLast(f);
            }
        }
        return untracked;
    }
}
