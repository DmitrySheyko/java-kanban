package manager;

import java.io.File;
import java.net.URI;

public class Managers {

    public static InMemoryTasksManager getDefaultManager() {
        return new InMemoryTasksManager();
    }

    public static FileBackedTasksManager getDefaultBackedManager() {
        return new FileBackedTasksManager(new File("resources/task.csv"));
    }

    public static HTTPTasksManager getHttpTaskManager(String keyForSave) {
        return new HTTPTasksManager(keyForSave);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
