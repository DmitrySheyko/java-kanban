package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TasksManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskServer {
    static TasksManager httpTaskManager;
    public static final int PORT = 8080;
    HttpServer httpServer;
    Gson gson;

    public HttpTaskServer() throws IOException {
        httpTaskManager = Managers.getHttpTaskManager("save1");
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        httpServer.createContext("/tasks", this::tasksHandler);
        httpServer.createContext("/tasks/task", this::taskHandler);
        httpServer.createContext("/tasks/epic", this::epicHandler);
        httpServer.createContext("/tasks/subtask", this::subTaskHandler);
        httpServer.createContext("/tasks/history", this::historyHandler);
    }

    public void start() {
        System.out.println("Запущен HttpTaskServer на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    // getPrioritizedTasks()
    private void tasksHandler(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");
        String response = "";
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> {
                List<String> prioritizedListOfAllTasks = httpTaskManager.getPrioritizedTasks().stream()
                        .map(task -> gson.toJson(task))
                        .collect(Collectors.toList());
                response = gson.toJson(prioritizedListOfAllTasks);
                httpExchange.sendResponseHeaders(200, 0);
            }
            case "DELETE" -> {
                httpTaskManager.deleteAllTasks();
                response = gson.toJson("Все задачи удалены");
                httpExchange.sendResponseHeaders(200, 0);
            }
            default -> httpExchange.sendResponseHeaders(405, 0);
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Task
    private void taskHandler(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks/task запроса от клиента.");
        String response = "";
        int idFromURL = 0;
        boolean isURLHaveId = false;
        String stringIdFromURL = httpExchange.getRequestURI().getQuery();
        if (stringIdFromURL != null) {
            try {
                idFromURL = Integer.parseInt(stringIdFromURL);
                isURLHaveId = true;
            } catch (NumberFormatException e) {
                System.out.println("Неверно указан ID задачи");
                return;
            }
        }
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfTasks().containsKey(idFromURL)) {
                        Task requestedTask = httpTaskManager.getTaskById(idFromURL);
                        response = gson.toJson(requestedTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            case "POST" -> {
                String body = new String(httpExchange.getRequestBody().readAllBytes());
                JsonElement jsonElement = JsonParser.parseString(body);
                if (!jsonElement.isJsonObject()) {
                    System.out.println("запрос не соответствует ожидаемому.");
                    return;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Task taskFromRequest = gson.fromJson(jsonObject, Task.class);
                if (!isURLHaveId) {
                    Task createdTask = httpTaskManager.creationOfTask(taskFromRequest);
                    response = gson.toJson(createdTask);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    if (httpTaskManager.getListOfTasks().containsKey(idFromURL)) {
                        Task updatedTask = httpTaskManager.updateTaskByNewTask(taskFromRequest);
                        response = gson.toJson(updatedTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
            }
            case "DELETE" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfTasks().containsKey(idFromURL)) {
                        Task deletedTask = httpTaskManager.deleteTaskById(idFromURL);
                        response = gson.toJson(deletedTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            default -> httpExchange.sendResponseHeaders(405, 0);
        }
        try (
                OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Epic
    private void epicHandler(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks/epic запроса от клиента.");
        String response = "";
        int idFromURL = 0;
        boolean isURLHaveId = false;
        String stringIdFromURL = httpExchange.getRequestURI().getQuery();
        if (stringIdFromURL != null) {
            try {
                idFromURL = Integer.parseInt(stringIdFromURL);
                isURLHaveId = true;
            } catch (NumberFormatException e) {
                System.out.println("Неверно указан ID задачи");
                return;
            }
        }
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfEpics().containsKey(idFromURL)) {
                        Epic requestedEpic = httpTaskManager.getEpicById(idFromURL);
                        response = gson.toJson(requestedEpic);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            case "POST" -> {
                String body = new String(httpExchange.getRequestBody().readAllBytes());
                JsonElement jsonElement = JsonParser.parseString(body);
                if (!jsonElement.isJsonObject()) {
                    System.out.println("запрос не соответствует ожидаемому.");
                    return;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Epic epicFromRequest = gson.fromJson(jsonObject, Epic.class);
                if (!isURLHaveId) {
                    Epic createdEpic = httpTaskManager.creationOfEpic(epicFromRequest);
                    response = gson.toJson(createdEpic);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    if (httpTaskManager.getListOfEpics().containsKey(idFromURL)) {
                        Epic updatedEpic = httpTaskManager.updateEpicByNewEpic(epicFromRequest);
                        response = gson.toJson(updatedEpic);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
            }
            case "DELETE" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfEpics().containsKey(idFromURL)) {
                        Epic deletedEpic = httpTaskManager.deleteEpicById(idFromURL);
                        response = gson.toJson(deletedEpic);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            default -> httpExchange.sendResponseHeaders(405, 0);
        }
        try (
                OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // SubTask
    private void subTaskHandler(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks/subtask запроса от клиента.");
        String response = "";
        int idFromURL = 0;
        boolean isURLHaveId = false;
        String stringIdFromURL = httpExchange.getRequestURI().getQuery();
        if (stringIdFromURL != null) {
            try {
                idFromURL = Integer.parseInt(stringIdFromURL);
                isURLHaveId = true;
            } catch (NumberFormatException e) {
                System.out.println("Неверно указан ID задачи");
                return;
            }
        }
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfSubTasks().containsKey(idFromURL)) {
                        SubTask requestedSubTask = httpTaskManager.getSubTaskById(idFromURL);
                        response = gson.toJson(requestedSubTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            case "POST" -> {
                String body = new String(httpExchange.getRequestBody().readAllBytes());
                JsonElement jsonElement = JsonParser.parseString(body);
                if (!jsonElement.isJsonObject()) {
                    System.out.println("запрос не соответствует ожидаемому.");
                    return;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                SubTask subTaskFromRequest = gson.fromJson(jsonObject, SubTask.class);
                if (!isURLHaveId) {
                    SubTask createdSubTask = httpTaskManager.creationOfSubTask(subTaskFromRequest);
                    response = gson.toJson(createdSubTask);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    if (httpTaskManager.getListOfSubTasks().containsKey(idFromURL)) {
                        SubTask updatedSubTask = httpTaskManager.updateSubTaskByNewSubTask(subTaskFromRequest);
                        response = gson.toJson(updatedSubTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
            }
            case "DELETE" -> {
                if (isURLHaveId) {
                    if (httpTaskManager.getListOfSubTasks().containsKey(idFromURL)) {
                        SubTask deletedSubTask = httpTaskManager.deleteSubTaskById(idFromURL);
                        response = gson.toJson(deletedSubTask);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
            default -> httpExchange.sendResponseHeaders(405, 0);
        }
        try (
                OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // History
    private void historyHandler(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks/history запроса от клиента.");
        String response = "";
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET" -> {
                List<String> history = httpTaskManager.getHistory().stream()
                        .map(task -> gson.toJson(task))
                        .collect(Collectors.toList());
                response = gson.toJson(history);
                httpExchange.sendResponseHeaders(200, 0);
            }
            default -> httpExchange.sendResponseHeaders(405, 0);
        }
        try (
                OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}


