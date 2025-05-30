package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private long CommitTime;
    private transient Commit parent1;
    private String parent1SHA;
    private transient Commit parent2;
    private String parent2SHA;
    private TreeMap<String, String> containedblobs;

    /* TODO: fill in the rest of this class. */
    public Commit(String m, long stamp) {
        message = m;
        CommitTime = stamp;
    }

    public Commit(String m, long stamp, Commit p1) {
        message = m;
        CommitTime = stamp;
        parent1 = p1;
    }
}
