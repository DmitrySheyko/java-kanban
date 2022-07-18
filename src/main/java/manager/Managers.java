package manager;

import java.io.File;

public class Managers {

    public static InMemoryTasksManager getDefaultManager() {
        return new InMemoryTasksManager();
    }

    public static HTTPTasksManager getDefaultManager(String url, String keyForSave) {
        return new HTTPTasksManager(url, keyForSave);
    }

    public static FileBackedTasksManager getDefaultBackedManager() {
        return new FileBackedTasksManager(new File("resources/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
