package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import entities.SerializableTask;
import entities.Task;
import entities.TaskList;
import enums.TaskType;
import exceptions.DukeException;
import utils.Loader;

/**
 * Represents a Storage class which save the tasks in the hard disk automatically whenever the task list changes.
 * Load the data from the hard disk when Duke starts up.
 */
public class Storage implements Loader<TaskList> {
    private static final String GENERIC_ERROR = "An error occurred when creating the database: ";
    private final File file;

    /**
     * Initializes a Storage object with the specified filename.
     *
     * @param filename Filename to store the data.
     */
    public Storage(String filename) {
        String FILE_DIRECTORY = "data";
        this.file = new File(String.format("%s/%s", FILE_DIRECTORY, filename));
    }

    /**
     * Loads the data stored in the hard drive.
     *
     * @throws DukeException A generic application error that specifies the type of error thrown.
     */
    public void connect() throws DukeException {
        File folder = file.getParentFile();
        try {
            if (!folder.exists() && !folder.mkdirs()) {
                throw new DukeException(GENERIC_ERROR + folder.getName());
            }
            if (!file.exists() && !file.createNewFile()) {
                throw new DukeException(GENERIC_ERROR + file.getName());
            }
            System.out.println("Successfully connected to storage.");
        } catch (IOException | SecurityException err) {
            throw new DukeException(GENERIC_ERROR + err.getMessage());
        }
    }

    /**
     * Loads the data from the specified filename.
     *
     * @param taskList TaskList to add the loaded data to.
     * @return A boolean value indicating the success of the operation.
     * @throws DukeException An exception to be thrown if there are any errors that occur.
     */
    public Boolean load(TaskList taskList) throws DukeException {
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                List<String> data = Arrays.asList(reader.nextLine().split(" \\| "));
                // Expect only valid data to be saved to hard drive
                TaskType taskType = TaskType.valueOf(data.get(0).toUpperCase());
                boolean isDone = data.get(1).equals("1");
                String description = data.get(2);
                SerializableTask task;

                switch(taskType) {
                case TODO:
                case DEADLINE:
                case EVENT:
                    String aFlags = data.subList(3, data.size()).toString();
                    String flags = aFlags.substring(1, aFlags.length() - 1);
                    task = new SerializableTask(taskType, isDone, description, flags);
                    break;
                default: task = null;
                }
                if (task != null) {
                    taskList.addTask(task.unmarshal(), false);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            throw new DukeException(GENERIC_ERROR + e.getMessage());
        }
    }

    /**
     * Writes all task currently in memory to the hard disk.
     *
     * @param taskList The tasklist in memory.
     * @throws DukeException An exception to be thrown if there are any errors that occur.
     */
    public void write(TaskList taskList) throws DukeException {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (Task task : taskList.getTaskList()) {
                SerializableTask tsk = task.serialize();
                fileWriter.write( tsk.marshal() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new DukeException(GENERIC_ERROR + e.getMessage());
        }
    }
}
