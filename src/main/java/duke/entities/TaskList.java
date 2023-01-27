package duke.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import duke.exceptions.DukeException;
import duke.storage.Storage;
import duke.views.UI;

/**
 * TaskList represents a data structure that holds Tasks.
 */
public class TaskList {
    private final List<Task> taskList = new ArrayList<>();
    private final Storage storage;

    /**
     * Initializes a TaskList with preloaded data.
     *
     * @param storage duke.storage class for writing to hard disk.
     */
    public TaskList(Storage storage) throws DukeException {
        this.storage = storage;
        try {
            Boolean success = storage.load(this);
            if (success) {
                System.out.println("Successfully loaded data.");
            } else {
                System.out.println("Data load unsuccessful. Initializing empty duke.storage.");
            }
        } catch (DukeException e) {
            System.out.println(e.getMessage() + "\n Initializing empty duke.storage.");
        }
    }

    /**
     * A generic filter function which parses through the TaskList and return tasks that match.
     *
     * @param predicate A boolean function.
     */
    public String filter(Predicate<? super Task> predicate, String emptyMsg) {
        return this.filter(predicate, emptyMsg, false);
    }

    private String filter(Predicate<? super Task> predicate, String emptyMsg, boolean index) {
        List<Task> filteredList = taskList.stream().filter(predicate).collect(Collectors.toList());
        if (filteredList.size() == 0) {
            return emptyMsg;
        }
        ListIterator<Task> it = filteredList.listIterator();
        StringBuilder sb = new StringBuilder("These are the tasks you asked for.");
        while (it.hasNext()) {
            if (index) {
                sb.append(UI.indentMessage(String.valueOf(it.nextIndex() + 1))).append(". ").append(it.next());
            } else {
                sb.append(UI.indentMessage(String.valueOf(it.next())));
            }
        }
        return sb.toString();
    }

    /**
     * Adds a given task into the TaskList.
     *
     * @param task The task to be added.
     * @param print Boolean value indicating if console messages should be printed.
     */
    public String addTask(Task task, boolean print) {
        taskList.add(task);
        if (print) {
            return "Successfully added task to memory.";
        }
        return UI.newLine();
    }

    /**
     * Adds a task into the taskList.
     *
     * @param task The task to be added.
     * @throws DukeException A duke specific exception thrown due to adding error.
     */
    public String addTask(Task task) throws DukeException {
        taskList.add(task);
        storage.write(task);
        return "Got it. I've added this task:"
                + UI.indentMessage(String.valueOf(task))
                + UI.newLine() + "Now you have " + taskList.size() + " tasks in the list.";
    }

    private boolean isValidKey(Integer key) {
        return (key <= taskList.size() && key > 0);
    }

    public Task getTask(Integer key) throws DukeException {
        if (!isValidKey(key)) {
            throw new DukeException("This task don't exists! Please select one from the list.");
        }
        // accounts for 0-based indexing
        return taskList.get(key - 1);
    }

    public String getTaskandToggle(Integer key, boolean mark) throws DukeException {
        Task task = this.getTask(key);
        String msg = mark ? task.markTask() : task.unmarkTask();
        storage.writeAll(this);
        return msg;
    }

    /**
     * Deletes the given task.
     *
     * @param key The task identifier.
     * @return Status message of the executed command.
     */
    public String deleteTask(Integer key) throws DukeException {
        if (!isValidKey(key)) {
            throw new DukeException("This task don't exists! Please select one from the list.");
        }
        Task task = taskList.get(key - 1);
        taskList.remove(key - 1);
        storage.writeAll(this);

        return "Noted. I've removed the task:"
                + UI.indentMessage(String.valueOf(task))
                + UI.newLine() + "Now you have " + taskList.size() + " tasks in the list.";
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * Lists the tasks in the TaskList.
     */
    public String listTasks(Predicate<? super Task> predicate, boolean index) {
        return this.filter(predicate, "There are no outstanding tasks!", index);
    }
}