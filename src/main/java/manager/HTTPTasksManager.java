package manager;

import http.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public class HTTPTasksManager extends FileBackedTasksManager {
    private final String keyForSave;
    private KVTaskClient kvTaskClient;
    private final Gson gson;

    public HTTPTasksManager(String url, String keyForSave) {
        this.keyForSave = keyForSave;
        try {
            kvTaskClient = new KVTaskClient(url);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании KVTaskClient \n" + e.getMessage());
        }
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        loadFromSave();
    }

    @Override
    public void save() {
        String head = "id,type,name,status,description,epic,subtasks,Date&Time,Duration\n";
        StringBuilder tasksInString = new StringBuilder();
        getListOfAllTasks().values().stream().
                forEach(task -> tasksInString.append(tasksToString(task)));
        String stringForSave = head + tasksInString + historyToString();
        String json = gson.toJson(stringForSave);
        try {
            kvTaskClient.put(keyForSave, json);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при сохранении данных на сервер \n" + e.getMessage());
        }
    }

    @Override
    public void loadFromSave() {
        String json = null;
        try {
            json = kvTaskClient.load(keyForSave);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при загрузке данных с сервера \n" + e.getMessage());
        }
        String value = gson.fromJson(json, String.class);
        if (value != null && (!value.isBlank())) {
            String[] lines = value.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    tasksFromString(lines[i]);
                } else {
                    historyFromString(lines[i + 1]);
                    break;
                }
            }
        }
    }
}

