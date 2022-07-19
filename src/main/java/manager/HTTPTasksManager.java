package manager;

import exceptions.KVServerException;
import exceptions.KVTaskClientException;
import http.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.http.HttpResponse;

public class HTTPTasksManager extends FileBackedTasksManager {
    private final String keyForSave;
    private KVTaskClient kvTaskClient = null;
    private final Gson gson;

    public HTTPTasksManager(String url, String keyForSave) {
        this.keyForSave = keyForSave;
        try {
            kvTaskClient = new KVTaskClient(url);
        } catch (KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        loadFromSave();
    }

    @Override
    public void save() {
        try {
            StringBuilder tasksInString = new StringBuilder();
            String head = "id,type,name,status,description,epic,subtasks,Date&Time,Duration\n";
            getListOfAllTasks().values().stream().
                    forEach(task -> tasksInString.append(tasksToString(task)));
            String stringForSave = head + tasksInString + historyToString();
            String json = gson.toJson(stringForSave);
            HttpResponse<String> response = kvTaskClient.put(keyForSave, json);
            if (response == null) {
                throw new KVServerException("Code 500. Ошибка сохранения!");
            }
            int responseStatus = response.statusCode();
            switch (responseStatus) {
                case 200:
                    break;
                case 400:
                    throw new KVServerException("Code 400. Ошибка сохранения! " +
                            "Сервером получен запрос с пустым значением KeyForSave");
                case 401:
                    throw new KVServerException("Code 401. Ошибка сохранения! " +
                            "Сервером получен запрос без API_TOKEN");
                case 405:
                    throw new KVServerException("Code 405. Ошибка сохранения! " +
                            "Сервером получен неверный тип запроса. Ожидается POST.");
                case 500:
                    throw new KVServerException("Code 500. Ошибка сохранения! " +
                            "Ошибка открытия OutPutStream в ходе работы KVServer");
                default:
                    throw new KVServerException("Code 500. Ошибка сохранения!");
            }
        } catch (KVServerException | KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Поменял логику. Теперь если KVServer прислал информацию об ошибке загрузки или сохранения,
     * будет выброшено исключение KVServerException с описанием. Аналогично, при ошибках в работы KVTaskClient,
     * предусмотрен выброс исключений KVTaskClientException.
     */
    @Override
    public void loadFromSave() {
        try {
            HttpResponse<String> response = kvTaskClient.load(keyForSave);
            if (response == null) {
                throw new KVServerException("Code 500. Ошибка загрузки данных!");
            }
            int responseStatus = response.statusCode();
            switch (responseStatus) {
                case 200:
                    readKVServerResponseBody(response.body());
                    break;
                case 400:
                    throw new KVServerException("Code 400. Ошибка загрузки данных! " +
                            "Сервером получен запрос с пустым значением KeyForSave");
                case 401:
                    throw new KVServerException("Code 401. Ошибка загрузки данных! " +
                            "Сервером получен запрос без API_TOKEN");
                case 404:
                    break;
                case 405:
                    throw new KVServerException("Code 405. Ошибка загрузки данных! " +
                            "Сервером получен неверный тип запроса. Ожидается GET.");
                case 500:
                    throw new KVServerException("Code 500. Ошибка загрузки данных! " +
                            "Ошибка работы OutPutStream");
                default:
                    throw new KVServerException("Code 500. Ошибка загрузки данных!");
            }
        } catch (KVServerException | KVTaskClientException e) {
            System.out.println(e.getMessage());
        }
    }

    public void readKVServerResponseBody(String json) {
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
