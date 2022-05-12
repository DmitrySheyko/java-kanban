package manager;

import task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int MAX_HISTORY_LENGTH = 10;
    private final ArrayList<Task> historyOfRequestsList;

    public InMemoryHistoryManager() {
        this.historyOfRequestsList = new ArrayList<>(MAX_HISTORY_LENGTH);
    }

    @Override
    public void add(Task task) {
        // добавлена проверка,  task на null
        if (task != null) {
            if (historyOfRequestsList.size() < MAX_HISTORY_LENGTH) {
                historyOfRequestsList.add(task);
            } else {
                historyOfRequestsList.remove(0);
                historyOfRequestsList.add(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyOfRequestsList;
    }

}

