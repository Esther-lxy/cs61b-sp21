package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.SetupRepo();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                String filename = args[1];
                Repository.Staging(filename);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                String message = args[1];
                Repository.MakeCommit(message);
                break;
            case "rm":
                String filename2 = args[1];
                Repository.remove(filename2);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                String MessageToFind = args[1];
                Repository.find(MessageToFind);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkout(args[2]);
                } else if (args.length == 4) {
                    Repository.checkout(args[1], args[3]);
                }
                break;
            case "branch":
                Repository.CreateBranch(args[1]);
                break;
            case "rm-branch":
                Repository.RemoveBranch(args[1]);
                break;
            case "reset":
                Repository.Reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
        }
    }
}
