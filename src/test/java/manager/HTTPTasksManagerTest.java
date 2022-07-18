package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.KVServer;
import gsonAdapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTasksManagerTest {
    static DateTimeFormatter dateTimeFormatter;
    static Gson gson;
    static KVServer kvServer;
    static HttpTaskServer httpTaskServer;
    static HttpClient httpClient;

    @BeforeAll
    static void beforeAll() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        gson = new GsonBuilder().serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer("http://localhost:8078", "save1");
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void shouldCreateNewTask() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStartTime(LocalDateTime.parse("25.07.2022 20:40", dateTimeFormatter));
        testTask1.setDuration(60);
        testTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Task createdTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, createdTask, "Ошибка при создании Task через HTTPTasksManager");
    }

    @Test
    void shouldCreateNewEpicAndSubTask() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");

        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body2).build();
        HttpResponse<String> httpResponse1 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        SubTask createdSubTask = gson.fromJson(httpResponse1.body(), SubTask.class);
        assertEquals(testSubTask1, createdSubTask, "Ошибка при создании SubTask через HTTPTasksManager");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List <String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List <Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(1, taskList.size(), "Ошибка при создании Task через HTTPTasksManager");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        jsonList = gson.fromJson(httpResponse.body(), List.class);
        taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(0, taskList.size(), "Ошибка при удалении Task через HTTPTasksManager");
    }

    @Test
    void shouldDeleteEpicAndSubTaskById() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List <String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List <Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(2, taskList.size(), "Ошибка при создании Epic через HTTPTasksManager");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?2"))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        jsonList = gson.fromJson(httpResponse.body(), List.class);
        taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(1, taskList.size(), "Ошибка при удалении SubTask через HTTPTasksManager");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?1"))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        jsonList = gson.fromJson(httpResponse.body(), List.class);
        taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(0, taskList.size(), "Ошибка при удалении Epic через HTTPTasksManager");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List <String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List <Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(3, taskList.size(), "Ошибка при создании задач через HTTPTasksManager");

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        jsonList = gson.fromJson(httpResponse.body(), List.class);
        taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(0, taskList.size(), "Ошибка при создании задач через HTTPTasksManager");
    }

    @Test
    void shouldUpdateTaskById() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Task createdTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, createdTask, "Ошибка при создании Task через HTTPTasksManager");

        testTask1.setStartTime(LocalDateTime.parse("25.07.2022 20:40", dateTimeFormatter));
        testTask1.setDuration(60);
        testTask1.setStatus(Status.DONE);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Task updatedTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, updatedTask, "Ошибка при обновлении Task через HTTPTasksManager");
    }

    @Test
    void shouldUpdateEpicById() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");

        testEpic1.setDescription("Измененное описание");
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?1"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?1"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic updatedEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, updatedEpic, "Ошибка при обновлении Epic через HTTPTasksManager");
    }

    @Test
    void shouldUpdateSubTaskById() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");

        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body2).build();
        HttpResponse<String> httpResponse1 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        SubTask createdSubTask = gson.fromJson(httpResponse1.body(), SubTask.class);
        assertEquals(testSubTask1, createdSubTask, "Ошибка при создании SubTask через HTTPTasksManager");

        testSubTask1.setStartTime(LocalDateTime.parse("25.07.2022 15:00", dateTimeFormatter));
        testSubTask1.setDuration(60);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?2"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?2"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask updatedSubTask = gson.fromJson(httpResponse.body(), SubTask.class);
        assertEquals(testSubTask1, updatedSubTask, "Ошибка при обновлении SubTask через HTTPTasksManager");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?2"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?3"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<String> gsonHistoryList = gson.fromJson(httpResponse.body(), List.class);
        List<Integer> historyList = gsonHistoryList.stream().
                map(string -> gson.fromJson(string, Task.class)).
                map(Task::getId).
                collect(Collectors.toList());
        List<Integer> expected = List.of(2, 1, 3);
        assertEquals(expected, historyList, "Ошибка в получении истории вызовов");
    }

    @Test
    void shouldGetAllTasksAfterStartNewSessionFromKVServer() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        testEpic1.setSubTaskIdList(List.of(3));
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        httpTaskServer.stop();
        HttpTaskServer newHttpTaskServer = new HttpTaskServer("http://localhost:8078", "save1");
        newHttpTaskServer.start();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromSave = gson.fromJson(httpResponse.body(), Task.class);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?2"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromSave = gson.fromJson(httpResponse.body(), Epic.class);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?3"))
                .GET()
                .build();
        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromSave = gson.fromJson(httpResponse.body(), SubTask.class);

        assertEquals(testTask1, taskFromSave, "Ошибка при получении сохраненого Task");
        assertEquals(testEpic1, epicFromSave, "Ошибка при получении сохраненого Epic");
        assertEquals(testSubTask1, subTaskFromSave, "Ошибка при получении сохраненого SubTask");
    }
}