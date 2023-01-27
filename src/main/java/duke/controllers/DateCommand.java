package duke.controllers;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import duke.entities.TaskList;
import duke.enums.CommandType;
import duke.exceptions.DukeException;
import duke.utils.CustomValidator;

/**
 * Represents the DateCommand.
 * The date command can be used to determine active tasks on a certain date.
 */
public class DateCommand extends Command {
    private static final Pattern VALID_DATE =
            Pattern.compile("(?<year>\\d{4})-(?<month>0[0-9]|1[0-2])-(?<day>0[0-9]|1[0-9]|2[0-9]|3[0-1])$");
    private final String args;

    /**
     * Initializes the Date Command.
     *
     * @param args The parsed arguments.
     */
    public DateCommand(String args) {
        super(CommandType.DATE);
        this.args = args;
    }

    /**
     * {@inheritDoc}
     * This method parses the command to verify and filter tasks with the date specified.
     */
    @Override
    public String execute(Supplier<? extends TaskList> taskList) throws DukeException {
        TaskList store = taskList.get();
        boolean valid = CustomValidator.validate(args.strip(), input -> VALID_DATE.matcher(input).find());
        if (valid) {
            return store.filter(task -> task.activeOn(LocalDate.parse(args.split(" ")[1])),
                    "There are no active tasks on this date!");
        } else {
            String dateFormatError = INVALID_FORMAT_ERROR + " " + "Please follow: date [yyyy-mm-dd].";
            throw new DukeException(dateFormatError);
        }
    }
}