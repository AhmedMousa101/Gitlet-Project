package gitlet;

import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Represents a gitlet commit object.
 *  Commit class, is a class that create an object for each commit done by the user
 *  It keeps track of some metadata:
 *  Files tracked by this commit, parent commit, message, and Timestamp
 *
 *  @author Ahmed Mousa
 */
public class Commit implements Serializable {
    /**
     *
     * @vars:
     * String Message -> Message for the commit
     * Date Timestamp -> Holds the date in which the commit was made
     * String parent -> Store the SHA of the parent Commit
     * Map<String, String> blobs -> holds the name of all tracked files with their SHA
     * String ShaHash -> Store the hash String of this commit
     */


    /** The message of this Commit. */
    private String Message;
    /** The Timestamp for this Commit. */
    private Date TimeStamp;
    /** The parent Commit */
    private String Parent;
    /** The files this Commit tracks */
    private Map<String, String> blobs;
    /** Ths Hash String of this commit. */
    public String shaHash;

    /**
     * Constructor to set a commit with Message message
     * @param
     * message of the current commit
     * */
    public Commit(String message){
        this.Message = message;
        this.Parent = Repository.getHead();
        this.TimeStamp = new Date();
        blobs = new HashMap<>();
        this.getData(Parent);
    }

    /** A method to set commit to be the initial commit. */
    public static void setInitial(Commit commit){
        commit.TimeStamp = new Date(0);
    }

    /** A method to copy the data from my parent commit. */
    public void getData(String parent){
        if (parent == null){
            return;
        }
        Commit from = Repository.getCommit(parent);
        this.blobs.putAll(from.blobs);
    }

    /** A method to commit the changes in the index. */
    public void setUpdates(){
        setStaged();
        setRemoved();
    }

    /** A method to check if a certain file is tracked by the current head commit. */
    public boolean isTracking(String path){
        return this.blobs.containsKey(path);
    }

    /** A method to get sha of a certain file */
    public String getSha(String path){
        if (!blobs.containsKey(path)){
            return null;
        }
        return blobs.get(path);
    }

    /** ِA method to add all the staged blobs to the current commit */
    public void setStaged(){
        Map<String, String>staged = Repository.getStagedBlobs();
        for (Map.Entry<String, String> entry : staged.entrySet()){
            String key = entry.getKey(), val = entry.getValue();
            this.blobs.put(key, val);
        }
    }

    /** A commit to lose track of all the removed files */
    public void setRemoved(){
        Set<String> removed = Repository.getremovedBlobs();
        for (String s : removed){
            if (blobs.containsKey(s)){
                blobs.remove(s);
            }
        }
    }

    /** Returns the size of the blobs the commit is tracking. */
    public int size(){
        return this.blobs.size();
    }

    /** Return the SHA of the parent commit */
    public String getParent(){
        return this.Parent;
    }

    /** Return a string describing the commit
     * sha, Merge if found, Date, commit message */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("commit ").append(shaHash).append('\n');
        sb.append("Date: ").append(TimeStamp).append('\n');
        sb.append(Message).append('\n');
        return sb.toString();
    }

    /** A method to return the message of the commit */
    public String getMessage(){
        return this.Message;
    }

}
