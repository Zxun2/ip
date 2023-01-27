package duke;

import duke.controllers.Command;
import duke.entities.TaskList;
import duke.exceptions.DukeException;
import duke.parser.Parser;
import duke.storage.Storage;

/**
 * Represents the duke.Duke Chat bot.
 * Running a duke object loads data from the specified file into memory,
 * and exiting the program writes data to the hard disk.
 */
public class Duke {
    private static TaskList taskList;

    /**
     * duke.Duke Constructor for initializing the duke.Duke Object.
     *
     * @param filename location of Storage
     */
    public Duke(String filename) {
        Storage storage = new Storage(filename);
        try {
            storage.connect();
            taskList = new TaskList(storage);
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieves the command for the given command string.
     *
     * @param input The given command string
     * @return Status of the command execution
     */
    public String getResponse(String input) {
        Command cmd = Parser.parse(input);
        System.out.println(input);
        try {
            return cmd.execute(() -> taskList);
        } catch (DukeException e) {
            return e.getMessage();
        }
    }
}
