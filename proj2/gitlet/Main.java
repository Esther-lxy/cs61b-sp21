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
            throw new GitletException("Please enter a command.");
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
                if (args.length == 1) {
                    throw new GitletException("Please enter a commit message.");
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
                String MessageToFind = args[2];
                Repository.find(MessageToFind);
                break;
        }
    }
}
