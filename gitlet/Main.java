package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ahmed Mousa
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void getOut(){
        System.out.println("Invalid number of arguments");
        System.exit(1);
    }

    public static void NoRepo(){
        System.out.println("No gitlet repo was initialized");
        System.exit(1);
    }
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Undefined Command");
            return;
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                System.out.println(Repository.init());
                break;
            case "add":
                if (args.length != 2) {
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.add(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.status();
                break;
            case "commit":
                if (args.length > 2) {
                    getOut();
                }
                else if (args.length == 1){
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                if (args.length > 2){
                    getOut();
                }
                else if (args.length == 1){
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.remove(args[1]);
                break;
            case "log":
                if (args.length > 1){
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.log();
                break;
            case "global-log":
                if (args.length > 1){
                    getOut();
                }
                else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.global_log();
                break;
            case "find":
                if (args.length > 2){
                    getOut();
                }else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.find(args[1]);
                break;
            case "branch":
                if (args.length > 2 || args.length == 1) {
                    getOut();
                }else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.setBranch(args[1]);
                break;
            case "rm-branch":
                if (args.length > 2 || args.length == 1) {
                    getOut();
                }else if (!Repository.isRepo()){
                    NoRepo();
                }
                Repository.deleteBranch(args[1]);
                break;
            case "checkout":
                if (args.length == 3 && !args[1].equals("--")) {
                    getOut();
                }else if (args.length == 4 && !args[2].equals("--")){
                    getOut();
                } else if (!Repository.isRepo()){
                    NoRepo();
                }
                if (args.length == 4){
                    Repository.checkout(args[1], args[3]);
                }else if (args.length == 3){
                    Repository.checkout(args[2]);
                }else if (args.length == 2){
                    Repository.changeBranch(args[1]);
                }

        }
    }
}
