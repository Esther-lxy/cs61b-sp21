package gitlet;

import java.io.File;
import java.util.Date;
import java.util.TreeMap;

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
    TreeMap<String, Commit> branches;
    String CB;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    // Constructor: if there is already .gitlet file in CWD, abort
    // Otherwise: create .gitlet file, set default branch, create initial commit
    public Repository() {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        File blobs = join(".gitlet", "blob");
        blobs.mkdir();
        File commits = join(".gitlet", "commits");
        commits.mkdir();
        File staging = join(".gitlet", "staging");

        Date initialTime = new Date(0);
        long timestamp = initialTime.getTime();
        Commit initial = new Commit("initial commit", timestamp);
        String defaultBranch = "master";
        branches.put(defaultBranch, initial);
        CB = defaultBranch;
    }
}
