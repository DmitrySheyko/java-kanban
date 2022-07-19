package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.KVServer;
import gsonAdapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.*;
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
        HttpResponse<String> httpResponse = createAndPostTestTask(testTask1);
        Task createdTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, createdTask, "Ошибка при создании Task через HTTPTasksManager");
    }

    @Test
    void shouldCreateNewEpicAndSubTask() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpResponse<String> httpResponse = createAndPutPostTestEpic(testEpic1);
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");
        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        httpResponse = createAndPostTestSubTask(testSubTask1);
        SubTask createdSubTask = gson.fromJson(httpResponse.body(), SubTask.class);
        assertEquals(testSubTask1, createdSubTask, "Ошибка при создании SubTask через HTTPTasksManager");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        createAndPostTestTask(testTask1);
        HttpResponse<String> httpResponse = getAllTasks();
        List<String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List<Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(1, taskList.size(), "Ошибка при создании Task через HTTPTasksManager");
        deleteTestTaskById("1");
        httpResponse = getAllTasks();
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
        createAndPutPostTestEpic(testEpic1);
        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        createAndPostTestSubTask(testSubTask1);
        HttpResponse<String> httpResponse = getAllTasks();
        List<String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List<Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(2, taskList.size(), "Ошибка при создании Epic через HTTPTasksManager");
        deleteTestSubTaskById("2");
        httpResponse = getAllTasks();
        jsonList = gson.fromJson(httpResponse.body(), List.class);
        taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(1, taskList.size(), "Ошибка при удалении SubTask через HTTPTasksManager");
        deleteTestEpicById("1");
        httpResponse = getAllTasks();
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
        createAndPostTestTask(testTask1);
        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        createAndPutPostTestEpic(testEpic1);
        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        createAndPostTestSubTask(testSubTask1);
        HttpResponse<String> httpResponse = getAllTasks();
        List<String> jsonList = gson.fromJson(httpResponse.body(), List.class);
        List<Task> taskList = jsonList.stream()
                .map(string -> gson.fromJson(string, Task.class))
                .collect(Collectors.toList());
        assertEquals(3, taskList.size(), "Ошибка при создании задач через HTTPTasksManager");
        deleteAllTasks();
        httpResponse = getAllTasks();
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
        HttpResponse<String> httpResponse = createAndPostTestTask(testTask1);
        Task createdTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, createdTask, "Ошибка при создании Task через HTTPTasksManager");

        testTask1.setStartTime(LocalDateTime.parse("25.07.2022 20:40", dateTimeFormatter));
        testTask1.setDuration(60);
        testTask1.setStatus(Status.DONE);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?1"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpResponse = getTestTaskById("1");
        Task updatedTask = gson.fromJson(httpResponse.body(), Task.class);
        assertEquals(testTask1, updatedTask, "Ошибка при обновлении Task через HTTPTasksManager");
    }

    @Test
    void shouldUpdateEpicById() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpResponse<String> httpResponse = createAndPutPostTestEpic(testEpic1);
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");

        testEpic1.setDescription("Измененное описание");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?1"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpResponse = getTestEpicById("1");
        Epic updatedEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, updatedEpic, "Ошибка при обновлении Epic через HTTPTasksManager");
    }

    @Test
    void shouldUpdateSubTaskById() throws IOException, InterruptedException {
        Epic testEpic1 = new Epic(1, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        HttpResponse<String> httpResponse = createAndPutPostTestEpic(testEpic1);
        Epic createdEpic = gson.fromJson(httpResponse.body(), Epic.class);
        assertEquals(testEpic1, createdEpic, "Ошибка при создании Epic через HTTPTasksManager");

        SubTask testSubTask1 = new SubTask(2, "Тест", "Создать SubTask", 1);
        testSubTask1.setStatus(Status.NEW);
        HttpResponse<String> httpResponse1 = createAndPostTestSubTask(testSubTask1);
        SubTask createdSubTask = gson.fromJson(httpResponse1.body(), SubTask.class);
        assertEquals(testSubTask1, createdSubTask, "Ошибка при создании SubTask через HTTPTasksManager");

        testSubTask1.setStartTime(LocalDateTime.parse("25.07.2022 15:00", dateTimeFormatter));
        testSubTask1.setDuration(60);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?2"))
                .POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpResponse = getTestSubTaskById("2");
        SubTask updatedSubTask = gson.fromJson(httpResponse.body(), SubTask.class);
        assertEquals(testSubTask1, updatedSubTask, "Ошибка при обновлении SubTask через HTTPTasksManager");
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        createAndPostTestTask(testTask1);
        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        createAndPutPostTestEpic(testEpic1);
        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        createAndPostTestSubTask(testSubTask1);
        getTestEpicById("2");
        getTestTaskById("1");
        getTestSubTaskById("3");
        HttpRequest request = HttpRequest.newBuilder()
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

    @DisplayName("Проверяет востановятся ли из KVServer данные после завершения " +
            "одной сессии работы HttpTasksManager и открытия новой")
    @Test
    void shouldGetAllTasksAfterStartNewSessionFromKVServer() throws IOException, InterruptedException {
        Task testTask1 = new Task(1, "Тест", "Создать Task");
        testTask1.setStatus(Status.NEW);
        createAndPostTestTask(testTask1);
        Epic testEpic1 = new Epic(2, "Тест", "Создать Epic", new ArrayList<>());
        testEpic1.setStatus(Status.NEW);
        testEpic1.setSubTaskIdList(List.of(3));
        createAndPutPostTestEpic(testEpic1);
        SubTask testSubTask1 = new SubTask(3, "Тест", "Создать SubTask", 2);
        testSubTask1.setStatus(Status.NEW);
        createAndPostTestSubTask(testSubTask1);
        httpTaskServer.stop();
        HttpTaskServer newHttpTaskServer = new HttpTaskServer("http://localhost:8078", "save1");
        newHttpTaskServer.start();
        HttpResponse<String> httpResponse = getTestTaskById("1");
        Task taskFromSave = gson.fromJson(httpResponse.body(), Task.class);
        httpResponse = getTestEpicById("2");
        Epic epicFromSave = gson.fromJson(httpResponse.body(), Epic.class);
        httpResponse = getTestSubTaskById("3");
        SubTask subTaskFromSave = gson.fromJson(httpResponse.body(), SubTask.class);
        assertEquals(testTask1, taskFromSave, "Ошибка при получении сохраненого Task");
        assertEquals(testEpic1, epicFromSave, "Ошибка при получении сохраненого Epic");
        assertEquals(testSubTask1, subTaskFromSave, "Ошибка при получении сохраненого SubTask");
    }

    private HttpResponse<String> createAndPostTestTask(Task testTask) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testTask));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> createAndPutPostTestEpic(Epic testEpic) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testEpic));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> createAndPostTestSubTask(SubTask testSubTask) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(testSubTask));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getTestTaskById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?" + id))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getTestEpicById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?" + id))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getTestSubTaskById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?" + id))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteTestTaskById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?" + id))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteTestEpicById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?" + id))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteTestSubTaskById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?" + id))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtas"))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}