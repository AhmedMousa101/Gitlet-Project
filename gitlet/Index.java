package gitlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.File;
import java.util.Set;

/**
 * Index class to keep to be the staging area for files
 * ready to be commited;
 * @author Ahemd Mousa
 * */
public class Index implements Serializable{
    /**
     * @vars
     * public static MAP<String, String> staged -> to keep track of staged files
     * public static Map<String, String> removed -> to keep track of removed files
     */

    /** Keep track of staged files */
    public Map<String, String> staged;

    /** Keep track of removed files */
    public Set<String> removed;

    public Index(){
        this.staged = new HashMap<>();
        this.removed = new HashSet<>();
    }

    /** A method to add a file to the index */
    public void add(File file){
        String sha = Repository.getSha(file);
        String path = Repository.getPath(file);
        addFile(path, sha);
        if (removeStaged(path)){
            removed.remove(path);
        }
        FileCheck(file, sha);
    }

    /** A method to remove a file from the index. */
    public void remove(File file){
        String path = Repository.getPath(file);
        boolean needed = false;
        if (Repository.isTracked(file)){
            needed = true;
            removed.add(path);
            if (file.exists()){
                file.delete();
            }
        }
        if (addStaged(path)){
            needed = true;
            staged.remove(path);
        }
        if (!needed){
            System.out.println("No reason to remove the file.");
            System.exit(1);
        }

    }

    /** A method to add a file to the index. */
    public void addFile(String FilePath, String FileSha){
        staged.put(FilePath, FileSha);
    }

    /** A method to check if a sha is found exactly in the head commit. */
    private void FileCheck(File file, String IndexSha){
        if (Repository.FileHeadSha(file, IndexSha)){
            this.staged.remove(Repository.getPath(file));
        }
    }

    /** Clear the index after commiting */
    public void clear(){
        staged.clear();
        removed.clear();
    }

    /** Override to_String method */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("=== Staged Files ===\n");
        for (String s : staged.keySet()){
            sb.append(s + '\n');
        }
        sb.append("\n");
        sb.append("=== Removed Files ===\n");
        for (String s : removed){
            sb.append(s + '\n');
        }
        return sb.toString();
    }

    /** A method to save all of the staged files to be commited
     * Called in case of a new commit
     */
    public void saveBlobs(){
        for (Map.Entry<String, String> entry : staged.entrySet()){
            Repository.saveBlob(entry.getKey(), entry.getValue());
        }
    }

    /** A method to check if a file is staged for addition or not. */
    public boolean addStaged(String path){
        return staged.containsKey(path);
    }

    /** A method to check if a file is staged for addition of not. */
    public boolean removeStaged(String path){
        return removed.contains(path);
    }
}
