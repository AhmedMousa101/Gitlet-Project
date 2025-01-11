package gitlet;

import java.io.File;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  Repository class will work as the interface between the Main class
 *  and the objects
 *
 *  @author Ahemd Mousa
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory to save all the commits files. */
    private static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** Head File to  keep track of the head commit. */
    private static final File Head_File = new File(GITLET_DIR, "Head");
    /** index file to keep track of index object */
    private static final File Index_File = new File(GITLET_DIR, "Index");
    /** The blobs directory to keep track of all blobs. */
    private static final File Blobs_Dir = new File(GITLET_DIR, "blobs");
    /** The branches directory to keep track of branches */
    private static final File Branches_Dir = new File(GITLET_DIR, "branches");
    /** Cur Branch file to save the name of the current branch*/
    private static final File Branch_File = new File(GITLET_DIR, "curBranch");
    /** A map to keep track of the SHA of certain files
     * in order to achieve lazy loading & caching
     * */
    private static Map<String, String> sha = new HashMap<>();

    /** Head Commit */
    public static Commit HeadCommit = null;

    /** index */
    public static Index IndexObj = null;

    /** Head Sha */
    public static String HeadSha = null;

    /** Cur Branch */
    public static Branch curBranch = null;

    /** Check if there is a repo. */
    public static boolean isRepo(){
        return GITLET_DIR.exists();
    }

    /** A method to set the commits directory */
    public static void setupPersistence(){
        if (!COMMITS_DIR.exists()){
            COMMITS_DIR.mkdirs();
        }
        if (!Blobs_Dir.exists()){
            Blobs_Dir.mkdir();
        }
        if (!Branches_Dir.exists()){
            Branches_Dir.mkdir();
        }
    }


    /** A method to set the initial commit */
    public static String init(){
        if (COMMITS_DIR.exists()){
            return "You have already setup a gitlet repository";
        }
        setupPersistence();
        Commit Initital = intitalCommit();
        setIndex();
        return "Repository Initialized Successfully";
    }

    /** A function used to set a commit */
    public static void commit(String message){
        Index index = getIndex();
        checkCommit();
        index.saveBlobs();
        Commit cur = new Commit(message);
        cur.setUpdates();
        saveCommit(cur);
        index.clear();
        saveIndex(index);
        updateBranchHead();
    }

    /** A function to set only initial commits */
    public static Commit intitalCommit(){
        Commit Initial = new Commit("Initial Commit");
        Commit.setInitial(Initial);
        saveCommit(Initial);
        setBranch("Master");
        switchBranchTo("Master");
        return Initial;
    }

    /** A method to save a commit to keep persistence */
    private static void saveCommit(Commit commit){
        File temp = new File(COMMITS_DIR, "Commit");
        Utils.writeObject(temp, commit);
        String shaHash = getSha(temp);
        commit.shaHash = shaHash;
        temp.delete();
        temp = new File(COMMITS_DIR, shaHash);
        Utils.writeObject(temp, commit);
        saveHead(shaHash);
    }

    /** A method to return the Head Commit */
    public static String getHead(){
        if (HeadSha != null){
            return HeadSha;
        }
        if (!Head_File.exists()){
            return null;
        }
        return HeadSha = readContentsAsString(Head_File);
    }

    /** A method to return a certain commit object based on its SHA */
    public static Commit getCommit(String sha){
        File commit = join(COMMITS_DIR, sha);
        if (!commit.exists()){
            return null;
        }
        return Utils.readObject(commit, Commit.class);
    }

    /** A method to update Head. */
    private static void saveHead(String sha){
        Utils.writeContents(Head_File, sha);
    }

    /** A method to Initialize Index. */
    private static void setIndex(){
        Index index = new Index();
        saveIndex(index);
    }

    /** A method to save index. */
    private static void saveIndex(Index index){
        Utils.writeObject(Index_File, index);
    }

    /** A method to get Inedx. */
    private static Index getIndex(){
        if (IndexObj != null){
            return IndexObj;
        }
        return IndexObj = Utils.readObject(Index_File, Index.class);
    }

    /** A method to check if a certain file is tracked by the current head commit. */
    public static boolean isTracked(File file){
        Commit commit = getHeadCommit();
        return commit.isTracking(getPath(file));
    }

    /** A method to get the SHA of a certain file. */
    public static String getSha(File file){
        if (!file.exists()){
            return null;
        }
        String path = getPath(file);
        if (sha.containsKey(path)){
            return sha.get(path);
        }else {
            String shaHash = SHA.getSha(file);
            sha.put(path, shaHash);
            return shaHash;
        }
    }

    /** A method to get the sha of the file in the Commit. */
    public static boolean FileHeadSha(File file, String Sha){
        String path = getPath(file);
        Commit commit = getHeadCommit();
        if (!shaCheck(path)){
            return false;
        }
        return Sha.equals(commit.getSha(path));
    }

    /** A method to check if the sha is found in the commit. */
    public static boolean shaCheck(String shaHash){
        Commit commit = getHeadCommit();
        boolean flag = commit.isTracking(shaHash);
        return flag;
    }

    /** A method to get the Head Commit. */
    public static Commit getHeadCommit(){
        if (HeadCommit != null)
            return HeadCommit;
        return HeadCommit = getCommit(getHead());
    }

    /** A method to add files to the repo. */
    public static void add(String FileName){
        File file = join(CWD, FileName);
        if (!file.exists()){
            System.out.println("Such file doesn't exist");
            System.exit(1);  // Exit with error status code 1
        }
        Index index = getIndex();
        index.add(file);
        saveIndex(index);
    }

    /** A method to print the status of the repo. */
    public static void status(){
        Index index = getIndex();
        String branches = getBranches();
        System.out.println(branches);
        System.out.println(index.toString());
    }

    /** Repository to get relative file path. */
    public static String getPath(File file){
        String absolutePath = file.getAbsolutePath();
        String relativePath = Paths.get(CWD.getAbsolutePath()).relativize(Paths.get(absolutePath)).toString();
        return relativePath;
    }

    /** A method to get all the staged files. */
    public static Map<String, String> getStagedBlobs(){
        Index index = getIndex();
        return index.staged;
    }

    /** A method to get all the removed files. */
    public static Set<String> getremovedBlobs(){
        Index index = getIndex();
        return index.removed;
    }

    /** A method to save a certain blob */
    public static void saveBlob(String path, String shaHash){
        File file = join(Blobs_Dir, shaHash);
        if (file.exists()){
            return;
        }
        File getContent = join(CWD, path);
        if (!getContent.exists()){
            System.out.println("File doesn't exist");
            System.exit(1);
        }
        String content = Utils.readContentsAsString(getContent);
        Utils.writeContents(file, content);
    }

    /** A method to get blob data */
    public static String getBlobData(String Sha){
        File file = join(Blobs_Dir, Sha);
        if (!file.exists()){
            return null;
        }
        return Utils.readContentsAsString(file);
    }

    /** A method to check if there are any staged files to the commit. */
    private static void checkCommit(){
        Index index = getIndex();
        if (index.staged.isEmpty() && index.removed.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(1);
        }
    }

    /** A method to remove a file using gitlet rm */
    public static void remove(String path){
        File file = join(CWD, path);
        Index index = getIndex();
        index.remove(file);
        saveIndex(index);
    }

    /** A method to print the log starting from the current commit*/
    public static void log(){
        String CurCommitSha = getHead();
        while(CurCommitSha != null){
            Commit commit = getCommit(CurCommitSha);
            System.out.println("===");
            System.out.println(commit.toString());
            CurCommitSha = commit.getParent();
        }
    }

    /** A method to print the global log */
    public static void global_log(){
        File[] files = COMMITS_DIR.listFiles();
        if (files != null){
            for (File file : files){
                Commit commit = getCommit(file.getName());
                System.out.println("===");
                System.out.println(commit.toString());
            }
        }
    }

    /** A method to find commits with a certain message */
    public static void find(String message){
        File[] files = COMMITS_DIR.listFiles();
        boolean isTyped = false;
        if (files != null){
            for (File file : files){
                Commit commit = getCommit(file.getName());
                if (commit.getMessage().equals(message)){
                    System.out.println("===");
                    System.out.println(commit.shaHash + '\n');
                    isTyped = true;
                }
            }
        }
        if (!isTyped){
            System.out.println("Found no commit with that message.");
            System.exit(1);
        }
    }

    /** A method to get the current branch */
    public static Branch getCurBranch(){
        if (curBranch != null){
            return curBranch;
        }
        if (!Branch_File.exists()){
            return null;
        }
        return curBranch = getBranchByName(Utils.readContentsAsString(Branch_File));
    }

    /** A method to get the head of a ceratin branch */
    public static Branch getBranchByName(String name){
        File file = join(Branches_Dir, name);
        if (!file.exists()){
            return null;
        }
        return Utils.readObject(file, Branch.class);
    }

    /** A method to save a certain branch */
    public static void saveBranch(Branch branch){
        File file = join(Branches_Dir, branch.getName());
        Utils.writeObject(file, branch);
    }

    /** A method to set a branch with a certain name */
    public static void setBranch(String name){
        File file = join(Branches_Dir, name);
        if (file.exists()){
            System.out.println("Branch already exists.");
            System.exit(1);
        }
        String Sha = getHead();
        Branch branch = new Branch(name, Sha);
        Utils.writeObject(file, branch);
    }

    /** Update current branch head to the current branch*/
    public static void updateBranchHead(){
        Branch branch = getCurBranch();
        branch.setSha(getHead());
        saveBranch(branch);
    }

    /** A method to switch a branch to another one */
    public static void switchBranchTo(String name){
        File file = join(Branches_Dir, name);
        if (!file.exists()){
            System.exit(1);
        }
        Utils.writeContents(Branch_File, name);
    }

    /** A method to get all branches */
    public static String getBranches(){
        StringBuilder S = new StringBuilder();
        S.append("=== Branches ===\n");
        File[] files = Branches_Dir.listFiles();
        if (files != null){
            for (File file : files){
                String name = file.getName();
                if (name.equals(getCurBranch().getName())){
                    S.append("*");
                }
                S.append(name).append('\n');
            }
        }
        return S.toString();
    }

    /** A method to delete a certain branch */
    public static void deleteBranch(String name){
        Branch branch = getBranchByName(name);
        Branch cur = getCurBranch();
        if (cur.getName().equals(branch.getName())){
            System.out.println("Cannot remove the current branch.");
            System.exit(1);
        }
        File file = join(Branches_Dir, name);
        if (file.exists()){
            file.delete();
        }
        else {
            System.out.println("Branch doesn't exist.");
            System.exit(1);
        }
    }

    /** A method to change the current branch */
    public static void changeBranch(String name){
        return;
    }

    /** A method to get a certain file into a certain commit */
    public static void checkout(String Sha, String file_path){
        Commit commit = getCommit(Sha);
        if (commit == null){
            System.out.println("No commit with that id exists.");
            System.exit(1);
        }
        String blobSha = commit.getSha(file_path);
        if (blobSha == null){
            System.out.println("Commit doesn't exist.");
            System.exit(1);
        }
        String content = getBlobData(blobSha);
        File file = new File(file_path);
        Utils.writeContents(file, content);
    }

    /** A method to get a certain file into the Head commit */
    public static void checkout(String file_path){
        checkout(getHead(), file_path);
    }


}
