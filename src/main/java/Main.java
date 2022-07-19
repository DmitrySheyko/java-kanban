/*
 * Version 8.2
 * Tech requirements of sprint 8
 * Author: Sheyko Dmitry.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.HttpTaskServer;
import http.KVServer;
import gsonAdapters.LocalDateTimeAdapter;
import task.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final static Gson GSON = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws IOException, InterruptedException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        new KVServer().start();
        HttpTaskServer httpTaskServer1 = new HttpTaskServer("http://localhost:8078", "save1");
        httpTaskServer1.start();
        HttpClient client = HttpClient.newHttpClient();

        // Создаю задачу 1 через HttpTaskServer
        Task task1 = new Task(100, "Тест 1", "Создать Task 1");
        task1.setStartTime(LocalDateTime.parse("25.07.2022 20:40", dateTimeFormatter));
        task1.setDuration(60);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(GSON.toJson(task1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Создаю задачу 2 через HttpTaskServer
        Task task2 = new Task(100, "Тест 2", "Создать Task 2");
        task2.setStartTime(LocalDateTime.parse("26.07.2022 12:30", dateTimeFormatter));
        task2.setDuration(60);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(GSON.toJson(task2));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body2).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Создаю задачу 3 через HttpTaskServer
        HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers
                .ofString(GSON.toJson(new Task(100, "Тест 3", "Создать Task 3")));
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body3).build();
        client.send(request3, HttpResponse.BodyHandlers.ofString());

        // Создаю задачу 4 через HttpTaskServer
        HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers
                .ofString(GSON.toJson(new Task(100, "Тест 4", "Создать Task 4")));
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body4).build();
        client.send(request4, HttpResponse.BodyHandlers.ofString());

        // Создаю задачу 5 через HttpTaskServer
        HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers
                .ofString(GSON.toJson(new Task(100, "Тест 5", "Создать Task 5")));
        HttpRequest request5 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body5).build();
        client.send(request5, HttpResponse.BodyHandlers.ofString());

        // Создаю Epic1 с тремя подзадачами через HttpTaskServer
        HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers
                .ofString(GSON.toJson(new Epic(100, "Тест 6", "Создать Epic 6", new ArrayList<>())));
        HttpRequest request6 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body6).build();
        client.send(request6, HttpResponse.BodyHandlers.ofString());

        // Создаю подзадачу 1 для Epic1 через HttpTaskServer
        SubTask subTask1 = new SubTask(100, "Тест 7", "Создать SubTask 7", 6);
        subTask1.setStartTime(LocalDateTime.parse("25.07.2022 15:00", dateTimeFormatter));
        subTask1.setDuration(60);
        HttpRequest.BodyPublisher body7 = HttpRequest.BodyPublishers.ofString(GSON.toJson(subTask1));
        HttpRequest request7 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body7).build();
        HttpResponse<String> httpResponse = client.send(request7, HttpResponse.BodyHandlers.ofString());
        subTask1 = GSON.fromJson(httpResponse.body(), SubTask.class);

        // Создаю подзадачу 2 для Epic1 через HttpTaskServer
        SubTask subTask2 = new SubTask(100, "Тест 8", "Создать SubTask 8", 6);
        subTask2.setStartTime(LocalDateTime.parse("25.07.2022 13:00", dateTimeFormatter));
        subTask2.setDuration(60);
        HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(GSON.toJson(subTask2));
        HttpRequest request8 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body8).build();
        httpResponse = client.send(request8, HttpResponse.BodyHandlers.ofString());
        subTask2 = GSON.fromJson(httpResponse.body(), SubTask.class);

        // Создаю подзадачу 3 для Epic1 через HttpTaskServer
        SubTask subTask3 = new SubTask(100, "Тест 9", "Создать SubTask 9", 6);
        subTask3.setStartTime(LocalDateTime.parse("25.07.2022 18:00", dateTimeFormatter));
        subTask3.setDuration(60);
        HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(GSON.toJson(subTask3));
        HttpRequest request9 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body9).build();
        client.send(request9, HttpResponse.BodyHandlers.ofString());

        //  меняю статус подзадачи 1 для Epic1
        subTask1.setStatus(Status.IN_PROGRESS);
        HttpRequest.BodyPublisher body10 = HttpRequest.BodyPublishers.ofString(GSON.toJson(subTask1));
        HttpRequest request10 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?7"))
                .POST(body10).build();
        client.send(request10, HttpResponse.BodyHandlers.ofString());

        //  меняю статус подзадачи 2 для Epic1
        subTask2.setStatus(Status.DONE);
        HttpRequest.BodyPublisher body11 = HttpRequest.BodyPublishers.ofString(GSON.toJson(subTask2));
        HttpRequest request11 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?8"))
                .POST(body11).build();
        client.send(request11, HttpResponse.BodyHandlers.ofString());

        //  Обращаюсь к задачам 8, 6, 4 по их ID, через сервер, для заполнения имстории просмотров
        HttpRequest request12 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?8"))
                .GET()
                .build();
        httpResponse = client.send(request12, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), SubTask.class));

        HttpRequest request13 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?8"))
                .GET()
                .build();
        httpResponse = client.send(request13, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), SubTask.class));

        HttpRequest request14 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?6"))
                .GET()
                .build();
        httpResponse = client.send(request14, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), Epic.class));

        HttpRequest request15 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic?6"))
                .GET()
                .build();
        httpResponse = client.send(request15, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), Epic.class));

        HttpRequest request16 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?4"))
                .GET()
                .build();
        httpResponse = client.send(request16, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), Task.class));

        HttpRequest request17 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?4"))
                .GET()
                .build();
        httpResponse = client.send(request17, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат вызова по ID: " + GSON.fromJson(httpResponse.body(), Task.class));

        // вызываю метод getPrioritizedTasks()
        HttpRequest request18 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = client.send(request18, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nЗадачи в порядке срока выполнения:");
        printJsonList(httpResponse);

        // вызываю историю обращения к задачам
        HttpRequest request19 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .GET()
                .build();
        httpResponse = client.send(request19, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nИстория обращения к задачам:");
        printJsonList(httpResponse);


        // Выключаю httpTaskServer1 и запускаю httpTaskServer2
        httpTaskServer1.stop();
        HttpTaskServer httpTaskServer2 = new HttpTaskServer("http://localhost:8078", "save1");
        httpTaskServer2.start();
        HttpClient client2 = HttpClient.newHttpClient();

        // вызываю метод getPrioritizedTasks()
        HttpRequest request20 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        httpResponse = client2.send(request20, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nЗадачи в порядке срока выполнения: (из сохранения)");
        printJsonList(httpResponse);

        // вызываю историю обращения к задачам
        HttpRequest request21 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .GET()
                .build();
        httpResponse = client2.send(request21, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nИстория обращения к задачам: (из сохранения)");
        printJsonList(httpResponse);
    }

    public static void printJsonList(HttpResponse<String> httpResponse) {
        List<String> prioritizedList = GSON.fromJson(httpResponse.body(), List.class);
        prioritizedList.stream().map(JsonParser::parseString)
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> {
                    switch (TypeTask.valueOf(GSON.fromJson(jsonObject.get("typeTask"), String.class))) {
                        case EPIC -> {
                            return GSON.fromJson(jsonObject, Epic.class);
                        }
                        case SUBTASK -> {
                            return GSON.fromJson(jsonObject, SubTask.class);
                        }
                        default -> {
                            return GSON.fromJson(jsonObject, Task.class);
                        }
                    }
                }).forEach(System.out::println);
    }
}















//       TasksManager httpTaskManager = Managers.getHttpTaskManager("save1");
//
//        // Создаю задачу 1
//        Task task1 = httpTaskManager.creationOfTask(new Task(100, "Тестирование 1",
//                "Создать тестовый Task 1"));
//        httpTaskManager.setTaskAndSubTaskStartDateTime(task1, "25.07.2022 20:40");
//        httpTaskManager.setTaskAndSubTaskDuration(task1, 60);
//
//        // Создаю задачу 2
//        Task task2 = httpTaskManager.creationOfTask(new Task(100, "Тестирование 2",
//                "Создать тестовый Task 2"));
//        httpTaskManager.setTaskAndSubTaskStartDateTime(task2, "26.07.2022 12:30");
//        httpTaskManager.setTaskAndSubTaskDuration(task2, 60);
//
//        // Создаю задачу 3
//        Task task3 = httpTaskManager.creationOfTask(new Task(100, "Тестирование 3",
//                "Создать тестовый Task 3"));
//
//        // Создаю задачу 4
//        Task task4 = httpTaskManager.creationOfTask(new Task(100, "Тестирование 4",
//                "Создать тестовый Task 4"));
//
//        // Создаю задачу 5
//        Task task5 = httpTaskManager.creationOfTask(new Task(100, "Тестирование 5",
//                "Создать тестовый Task 5"));
//
//        // Создаю Epic1 с двумя подзадачами
//        Epic epic1 = httpTaskManager.creationOfEpic(new Epic(100, "Тестирование 3",
//                "Создать тестовый Epic 6"
//                , new ArrayList<>()));
//        int idOfCreatedEpic1 = epic1.getId();

// - подзадача 1 для Epic1
//        SubTask subTask1OfEpic1 = httpTaskManager.creationOfSubTask(new SubTask(100, "Тестирование 4"
//                , "Создать тестовый SubTask 1", idOfCreatedEpic1));
//        httpTaskManager.setTaskAndSubTaskStartDateTime(subTask1OfEpic1, "25.07.2022 15:00");
//        httpTaskManager.setTaskAndSubTaskDuration(subTask1OfEpic1, 60);

//  - подзадача 2 для Epic1
//        SubTask subTask2OfEpic1 = httpTaskManager.creationOfSubTask(new SubTask(10, "Тестирование 5"
//                , "Создать тестовый SubTask 2", idOfCreatedEpic1));
//        httpTaskManager.setTaskAndSubTaskStartDateTime(subTask2OfEpic1, "25.07.2022 13:00");
//        httpTaskManager.setTaskAndSubTaskDuration(subTask2OfEpic1, 60);

//  - подзадача 3 для Epic1
//        SubTask subTask3OfEpic1 = httpTaskManager.creationOfSubTask(new SubTask(10, "Тестирование 6"
//                , "Создать тестовый SubTask 3", idOfCreatedEpic1));
//        httpTaskManager.setTaskAndSubTaskStartDateTime(subTask3OfEpic1, "25.07.2022 18:00");
//        httpTaskManager.setTaskAndSubTaskDuration(subTask3OfEpic1, 60);
//
//        //  меняю статус всех подзадач для Epic
//        subTask1OfEpic1.setStatus(Status.DONE);
//        subTask2OfEpic1.setStatus(Status.DONE);
//        subTask3OfEpic1.setStatus(Status.DONE);
//
//        //  Обращаюсь к задачам по их ID для заполнения имстории просмотров
//        httpTaskManager.getSubTaskById(8);
//        httpTaskManager.getSubTaskById(8);
//        httpTaskManager.getEpicById(6);
//        httpTaskManager.getEpicById(6);
//        httpTaskManager.getTaskById(4);
//        httpTaskManager.getTaskById(4);
//        httpTaskManager.getTaskById(2);
//        httpTaskManager.getTaskById(2);
//
//        // Создаю новый TaskManager который полусит все озданные задачи с сервера.
//        TasksManager httpTaskManager2 = Managers.getHttpTaskManager("save1");
//
//        // печатаю перечень всех задач полученных с сервера
//        printAllTasks(httpTaskManager2);
//
//        // Печатаю историю просмотров полученную с сервера
//        printHistory(httpTaskManager2);
//
//        // вызываю метод getPrioritizedTasks()
//        printPrioritizedTasks(httpTaskManager2);
//    }
//
//    public static void printAllTasks(TasksManager tasksManager) {
//        String[] array = tasksManager.getListOfAllTasks().toString().split("},");
//        StringBuilder result = new StringBuilder("Список всех задач: \n");
//        for (String line : array) {
//            result.append(line).append(".\n");
//        }
//        System.out.println(result);
//    }
//
//    public static void printHistory(TasksManager tasksManager) {
//        String[] array = tasksManager.getHistory().toString().split("},");
//        StringBuilder result = new StringBuilder("История обращения к задачам: \n");
//        for (String line : array) {
//            result.append(line).append(".\n");
//        }
//        System.out.println(result);
//    }
//
//    public static void printPrioritizedTasks(TasksManager tasksManager) {
//        System.out.println("Задачи в порядке срока выполнения:");
//        for (Task task : tasksManager.getPrioritizedTasks()) {
//            System.out.println(task);
//        }
//    }
//}



