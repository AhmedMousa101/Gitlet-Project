package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ahmed Mousa
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
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
                    System.out.println("Invalid number of arguments");
                    System.exit(1);
                }
                Repository.add(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Invalid number of arguments");
                    System.exit(1);
                }
                Repository.status();
                break;
            case "commit":
                if (args.length > 2) {
                    System.out.println("Invalid number of arguments");
                    System.exit(1);
                }
                else if (args.length == 1){
                    System.out.println("Please enter a commit message.");
                    System.exit(1);
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                if (args.length > 2){
                    System.out.println("Invalid number of arguments");
                    System.exit(1);
                }
                else if (args.length == 1){
                    System.out.println("Please enter a file to be removed.");
                    System.exit(1);
                }
                Repository.remove(args[1]);
        }
    }
}
