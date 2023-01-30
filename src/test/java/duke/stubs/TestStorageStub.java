package duke.stubs;

import duke.entities.Task;
import duke.entities.TaskList;
import duke.storage.Storage;

/**
 * A stub implementation of storage.
 */
public class TestStorageStub extends Storage {

    /**
     * Initializes a Storage object with the specified filename.
     *
     * @param filename Filename to store the data.
     */
    public TestStorageStub(String filename) {
        super(filename);
    }

    /**
     * Simple implementation that does not actually create a file.
     */
    @Override
    public void connect() {}

    @Override
    public Boolean load(TaskList taskList) {
        return true;
    }

    @Override
    public void write(Task task) {}

    @Override
    public void writeAll(TaskList taskList) {}
}
