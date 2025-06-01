package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(".gitlet", "commits");
    public static final File BLOBS_DIR = join(".gitlet", "blob");
    public static final File STAGING_DIR = join(".gitlet", "staging");

    private String message;
    private long CommitTime;
    private transient Commit parent1;
    private String parent1SHA;
    private transient Commit parent2;
    private String parent2SHA;
    private TreeMap<String, String> containedblobs;
    private boolean commited = false;

    /* TODO: fill in the rest of this class. */
    public Commit(String m, long stamp) {
        message = m;
        CommitTime = stamp;
    }

    public Commit(String m, long stamp, String p1sha1) {
        message = m;
        CommitTime = stamp;
        parent1SHA = p1sha1;
        File CC = join(COMMITS_DIR, p1sha1);
        Commit ParentCommit = readObject(CC, Commit.class);

        for (Map.Entry<String, String> entry : ParentCommit.containedblobs.entrySet()) {
            this.containedblobs.put(entry.getKey(), entry.getValue());
        }
    }

    public Commit(String m, long stamp, String p1sha1, String p2sha1) {
        this(m, stamp, p1sha1);
        parent2SHA = p2sha1;
    }

    public String getBlobSha1(String filename) {
        return containedblobs.get(filename);
    }

    /* add fileneme-sha1 reference to containedblobs Map */
    public void addBlobs(String filename, String fileSha1) {
        containedblobs.put(filename, fileSha1); //TreeMap.put will rewrite the old value if key already exists
    }

    public void deleteBlobs(String filename) {
        containedblobs.remove(filename);
    }

    public boolean BlobsContained(String filename) {
        return containedblobs.containsKey(filename);
    }

    public TreeMap<String, String> Blobs() {
        return new TreeMap<String, String>(containedblobs);
    }

    /* Get sha1 of commit and save the object as file under .gitlet/commits */
    public String SaveCommit() {
        byte[] serilizedObject = Utils.serialize(this);
        String sha1 = Utils.sha1(serilizedObject);
        File CommitF = join(COMMITS_DIR, sha1);
        Utils.writeContents(CommitF, serilizedObject);
        return sha1;
    }

    public void Printlog(String mysha1) {
        printme(mysha1);
        if (parent1SHA != null) {
            File p1 = join(COMMITS_DIR, parent1SHA);
            Commit parent = Utils.readObject(p1, Commit.class);
            parent.Printlog(parent1SHA);
        }
    }

    public void printme(String mysha1) {
        System.out.println("===");
        System.out.println("commit " + mysha1);
        if (parent1SHA != null && parent2SHA != null) {
            System.out.println("Merge: " + parent1SHA.substring(0,7) + " " + parent2SHA.substring(0, 7));
        }
        Date date = new Date(CommitTime);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getDefault());
        String formatted = sdf.format(date);
        System.out.println("Date: " + formatted);
        System.out.println(message);
        System.out.println();
    }

    public String getMessage() {
        return message;
    }

    public List<String> parents() {
        List<String> p = new ArrayList<>();
        if (parent1SHA != null) {
            p.add(parent1SHA);
        }
        if (parent2SHA != null) {
            p.add(parent2SHA);
        }
        return p;
    }
}
