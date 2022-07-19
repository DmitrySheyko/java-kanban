//package task;
//
//import manager.Managers;
//import manager.TasksManager;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TaskTest {
//
//    static TasksManager fileBackedTasksManager;
//    static TasksManager tasksManager;
//    static DateTimeFormatter dateTimeFormatter;
//
//    @BeforeAll
//    static void beforeAll() {
//        fileBackedTasksManager = Managers.getDefaultBackedManager();
//        tasksManager = Managers.getDefaultManager();
//        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
//    }
//
//    @Test
//    void shouldCreateNewTaskInFileBackedTasksManager() {
//        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1", "testTask1 description"));
//        int taskId = testTask.getId();
//        Task savedTask = fileBackedTasksManager.getTaskById(taskId);
//        assertNotNull(testTask, "Task не создан.");
//        assertNotNull(savedTask, "Task на возвращается по Id.");
//        assertEquals(testTask, savedTask, "Созданный Task и возвращенный по Id не совпадают.");
//    }
//
//
//    @Test
//    void shouldUpdateTaskInFileBackedTasksManager() {
//        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1", "testTask1 description"));
//        testTask.setName("newName");
//        testTask.setDescription("newDescription");
//        testTask.setStatus(Status.IN_PROGRESS);
//        testTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
//        testTask.setDuration(90);
//        Task updatedTestTask = fileBackedTasksManager.updateTaskByNewTask(testTask);
//        assertEquals(testTask, updatedTestTask, "Task не обновился.");
//        assertEquals(updatedTestTask.getName(), "newName", "Имя Task не обновилось.");
//        assertEquals(updatedTestTask.getDescription(), "newDescription", "Описание Task не обновилось.");
//        assertEquals(updatedTestTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32", "Время начала Task не обновилось.");
//        assertEquals(updatedTestTask.getDuration(), 90, "Длительность Task не обновилось.");
//        assertEquals(Status.IN_PROGRESS, updatedTestTask.getStatus(), "Статус Task не обновился.");
//
//        testTask.setStatus(null);
//        assertEquals(Status.IN_PROGRESS, testTask.getStatus(), "Статус Task некоректен.");
//
//        testTask = new Task(Integer.MAX_VALUE, "testTask1", "testTask1 description");
//        updatedTestTask = fileBackedTasksManager.updateTaskByNewTask(testTask);
//        assertNull(updatedTestTask, "Проведено обновление Task с аргументом null.");
//    }
//
//    @Test
//    void shouldSetDateTimeAndDurationInFileBackedTasksManager() {
//        fileBackedTasksManager.deleteAllTasks();
//        Task testTask1 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1", "testTask1 description"));
//        Task testTask2 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask2", "testTask2 description"));
//        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask1, "20.08.2025 11:00");
//        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 11:00");
//        assertEquals(fileBackedTasksManager.getStartDateTime(testTask1), LocalDateTime.parse("20.08.2025 11:00", dateTimeFormatter));
//        assertNull(fileBackedTasksManager.getStartDateTime(testTask2), "Установлено время занятое другой Task");
//
//        fileBackedTasksManager.setTaskAndSubTaskDuration(testTask1, 120);
//        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 13:00");
//        assertNull(fileBackedTasksManager.getStartDateTime(testTask2), "Установлено время занятое другой Task");
//        assertEquals(fileBackedTasksManager.getTaskDuration(testTask1), 120, "Неверно установлена длительность Task");
//
//        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 10:00");
//        assertEquals(fileBackedTasksManager.getStartDateTime(testTask2), LocalDateTime.parse("20.08.2025 10:00", dateTimeFormatter));
//        fileBackedTasksManager.setTaskAndSubTaskDuration(testTask2, 120);
//        assertEquals(fileBackedTasksManager.getTaskDuration(testTask2), 0, "Неверно установлена длительность Task");
//        fileBackedTasksManager.setTaskAndSubTaskDuration(testTask2, 40);
//        assertEquals(fileBackedTasksManager.getTaskDuration(testTask2), 40, "Неверно установлена длительность Task");
//    }
//
//    @Test
//    void shouldGetListOfTasksInFileBackedTasksManager() {
//        fileBackedTasksManager.deleteAllTasks();
//        Map<Integer, Task> testList = fileBackedTasksManager.getListOfTasks();
//        assertEquals(0, testList.size(), " Ошибка при формировании taskList");
//
//        Task testTask1 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        Task testTask2 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask2",
//                "testTask2 description"));
//        Task testTask3 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask3",
//                "testTask3 description"));
//        assertEquals(3, testList.size(), "Ошибка при формировании taskList");
//        assertEquals(testTask1, fileBackedTasksManager.getListOfTasks().get(testTask1.getId()));
//        assertEquals(testTask2, fileBackedTasksManager.getListOfTasks().get(testTask2.getId()));
//        assertEquals(testTask3, fileBackedTasksManager.getListOfTasks().get(testTask3.getId()));
//    }
//
//    @Test
//    void shouldDeleteTaskByIdInFileBackedTasksManager() {
//        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        int testTaskId = testTask.getId();
//        fileBackedTasksManager.deleteTaskById(testTaskId);
//        assertNull(fileBackedTasksManager.getTaskById(testTaskId), "Task не удалился.");
//
//        assertNull(fileBackedTasksManager.deleteTaskById(-1), "Выполнена попытка удления несуществующего Task");
//    }
//
//    @Test
//    void shouldCreateNewTaskInTasksManager() {
//        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        int taskId = testTask.getId();
//        Task savedTask = tasksManager.getTaskById(taskId);
//        assertNotNull(testTask, "Task не создан.");
//        assertNotNull(savedTask, "Task на возвращается по Id.");
//        assertEquals(testTask, savedTask, "Созданный Task и возвращенный по Id не совпадают.");
//    }
//
//    @Test
//    void shouldUpdateTaskInTasksManager() {
//        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        testTask.setName("newName");
//        testTask.setDescription("newDescription");
//        testTask.setStatus(Status.IN_PROGRESS);
//        testTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
//        testTask.setDuration(90);
//        Task updatedTestTask = tasksManager.updateTaskByNewTask(testTask);
//        assertEquals(testTask, updatedTestTask, "Task не обновился.");
//        assertEquals(updatedTestTask.getName(), "newName", "Имя Task не обновилось.");
//        assertEquals(updatedTestTask.getDescription(), "newDescription", "Описание Task не обновилось.");
//        assertEquals(updatedTestTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
//                "Время начала Task не обновилось.");
//        assertEquals(updatedTestTask.getDuration(), 90, "Длительность Task не обновилось.");
//        assertEquals(Status.IN_PROGRESS, updatedTestTask.getStatus(), "Статус Task не обновился.");
//
//        testTask.setStatus(null);
//        assertEquals(Status.IN_PROGRESS, testTask.getStatus(), "Статус Task некоректен.");
//
//        testTask = new Task(Integer.MAX_VALUE, "testTask1", "testTask1 description");
//        updatedTestTask = tasksManager.updateTaskByNewTask(testTask);
//        assertNull(updatedTestTask, "Проведено обновление Task с аргументом null.");
//    }
//
//    @Test
//    void shouldSetDateAndTimeInTasksManager() {
//        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        tasksManager.setTaskAndSubTaskStartDateTime(testTask, "20.08.2023 11:32");
//        assertEquals(tasksManager.getStartDateTime(testTask), LocalDateTime.parse("20.08.2023 11:32",
//                dateTimeFormatter));
//    }
//
//    @Test
//    void shouldSetDateTimeAndDurationInTasksManager() {
//        tasksManager.deleteAllTasks();
//        Task testTask1 = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        Task testTask2 = tasksManager.creationOfTask(new Task(10, "testTask2",
//                "testTask2 description"));
//        tasksManager.setTaskAndSubTaskStartDateTime(testTask1, "20.08.2025 11:00");
//        tasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 11:00");
//        assertEquals(tasksManager.getStartDateTime(testTask1), LocalDateTime.parse("20.08.2025 11:00",
//                dateTimeFormatter));
//        assertNull(tasksManager.getStartDateTime(testTask2), "Установлено время занятое другой Task");
//
//        tasksManager.setTaskAndSubTaskDuration(testTask1, 120);
//        tasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 13:00");
//        assertNull(tasksManager.getStartDateTime(testTask2), "Установлено время занятое другой Task");
//        assertEquals(tasksManager.getTaskDuration(testTask1), 120,
//                "Неверно установлена длительность Task");
//
//        tasksManager.setTaskAndSubTaskStartDateTime(testTask2, "20.08.2025 10:00");
//        assertEquals(tasksManager.getStartDateTime(testTask2), LocalDateTime.parse("20.08.2025 10:00",
//                dateTimeFormatter));
//        tasksManager.setTaskAndSubTaskDuration(testTask2, 120);
//        assertEquals(tasksManager.getTaskDuration(testTask2), 0,
//                "Неверно установлена длительность Task");
//        tasksManager.setTaskAndSubTaskDuration(testTask2, 40);
//        assertEquals(tasksManager.getTaskDuration(testTask2), 40,
//                "Неверно установлена длительность Task");
//    }
//
//    @Test
//    void shouldGetListOfTasksInTasksManager() {
//        tasksManager.deleteAllTasks();
//        Map<Integer, Task> testList = tasksManager.getListOfTasks();
//        assertEquals(0, testList.size(), " Ошибка при формировании taskList");
//
//        Task testTask1 = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        Task testTask2 = tasksManager.creationOfTask(new Task(10, "testTask2",
//                "testTask2 description"));
//        Task testTask3 = tasksManager.creationOfTask(new Task(10, "testTask3",
//                "testTask3 description"));
//        assertEquals(3, testList.size(), "Ошибка при формировании taskList");
//        assertEquals(testTask1, tasksManager.getListOfTasks().get(testTask1.getId()));
//        assertEquals(testTask2, tasksManager.getListOfTasks().get(testTask2.getId()));
//        assertEquals(testTask3, tasksManager.getListOfTasks().get(testTask3.getId()));
//    }
//
//
//    @Test
//    void shouldDeleteTaskByIdInTasksManager() {
//        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
//                "testTask1 description"));
//        int testTaskId = testTask.getId();
//        tasksManager.deleteTaskById(testTaskId);
//        assertNull(tasksManager.getTaskById(testTaskId), "Task не удалился.");
//
//        assertNull(tasksManager.deleteTaskById(-1),
//                "Выполнена попытка удления несуществующего Task");
//    }
//}
