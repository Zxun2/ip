package duke.entities;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duke.enums.TaskType;
import duke.exceptions.DukeException;
import duke.utils.CustomValidator;

/**
 * Represents a Task Object.
 */
public abstract class Task {
    /** Task validation **/
    public static final Pattern EVENT =
            Pattern.compile("^(event) (?<description>.+) /from (?<from>.+) /to (?<to>.+)$",
                    Pattern.CASE_INSENSITIVE);
    public static final Pattern DEADLINE =
            Pattern.compile("^(deadline) (?<description>.+) /by (?<by>.+)$",
                    Pattern.CASE_INSENSITIVE);
    public static final Pattern TODO = Pattern.compile("^(todo) (?<description>.+)$", Pattern.CASE_INSENSITIVE);

    /** Date validation **/
    private static final String INVALID_DATE_MESSAGE = "Date is not valid! Please try again.";
    private static final Pattern FORMAT_DATE =
            Pattern.compile("^(?<year>\\d{4})-(?<month>0[0-9]|1[0-2])-(?<day>0[0-9]|1[0-9]|2[0-9]|3[0-1])$");

    /** Task attributes **/
    protected TaskType taskType;
    protected String description;
    protected boolean isDone;

    /**
     * Instantiates a Task Object that can be placed into the TaskList.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Converts an existing task to a SerializableTask that can marshal and unmarshal.
     *
     * @return SerializableTask
     */
    public abstract SerializableTask serialize();
    public abstract boolean isActiveOn(LocalDate date);

    /**
     * Filters a task based on the matched string.
     *
     * @param string The string to match.
     * @return a boolean value indicating if a match is found.
     */
    public boolean matchString(String string) {
        Matcher pattern = Pattern.compile(".*" + string + ".*$", Pattern.CASE_INSENSITIVE).matcher(description);
        return pattern.matches();
    }

    /**
     * Returns the status of a task.
     *
     * @return A string indicating the status of a task.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    private static boolean validateDate(String date) {
        return CustomValidator.validate(date, (String val) -> FORMAT_DATE.matcher(val).matches());
    }

    private static Task createTask(TaskType type, String description, Matcher matcher) throws DukeException {
        switch (type) {
        case TODO: return new Todo(description);
        case EVENT:
            String from = matcher.group("from");
            String to = matcher.group("to");
            if (!validateDate(from) || !validateDate(to)) {
                throw type.getErr();
            }
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            if (!fromDate.isBefore(toDate) && fromDate.isAfter(LocalDate.now())) {
                throw new DukeException(INVALID_DATE_MESSAGE);
            }
            return new Event(description, fromDate, toDate);
        case DEADLINE:
            String by = matcher.group("by");
            if (!validateDate(by)) {
                throw type.getErr();
            }
            LocalDate byDate = LocalDate.parse(by);
            if (!byDate.isAfter(LocalDate.now())) {
                throw new DukeException(INVALID_DATE_MESSAGE);
            }
            return new Deadline(description, byDate);
        default: throw type.getErr();
        }
    }

    /**
     * Processes the given task by creating and storing into the TaskList.
     *
     * @param matcher A regex matcher that matches the type of command.
     * @param type Specifies the task type.
     * @throws DukeException An exception to be thrown if an invalid task is to be created.
     */
    public static String processTask(Matcher matcher, TaskType type, TaskList store) throws DukeException {
        if (!matcher.find()) {
            throw type.getErr();
        }
        String description = matcher.group("description");
        Task task = createTask(type, description, matcher);
        return store.addTask(task);
    }

    /**
     * Marks the given task as complete.
     */
    public String markTask() {
        isDone = true;
        return "Nice! I've marked this task as done:\n [X] " + description;
    }

    /**
     * Marks the given task as incomplete.
     */
    public String unmarkTask() {
        isDone = false;
        return "OK, I've marked this task as not done yet:\n [ ] " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
