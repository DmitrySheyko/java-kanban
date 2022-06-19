package manager;

import java.io.File;

public class Managers {

//    public static TaskManager getDefault() {
//        return new InMemoryTaskManager();
//    }

    public static FileBackedTasksManager getDefault() {
        return new FileBackedTasksManager(new File("resources/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
