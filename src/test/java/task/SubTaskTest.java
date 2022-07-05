package task;

import manager.Managers;
import manager.TasksManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static TasksManager fileBackedTasksManager;
    static TasksManager tasksManager;
    static DateTimeFormatter dateTimeFormatter;

    @BeforeAll
    static void beforeAll() {
        fileBackedTasksManager = Managers.getDefaultBackedManager();
        tasksManager = Managers.getDefaultManager();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    @Test
    void shouldCreateNewSubTaskInFileBackedTasksManager() {
        Epic testEpic = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic.getId()));
        int testSubTaskId = testSubTask.getId();
        SubTask savedTestSubTask = fileBackedTasksManager.getSubTaskById(testSubTaskId);
        assertNotNull(testSubTask, "SubTask не создан.");
        assertNotNull(savedTestSubTask, "SubTask на возвращается по Id.");
        assertEquals(testSubTask, savedTestSubTask, "Созданный SubTask и возвращенный по Id не совпадают.");

        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", Integer.MAX_VALUE));
        assertNull(testSubTask2, "Создан SubTask для несуществующего Epic");
    }

    @Test
    void shouldUpdateSubTaskInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask.setName("newName");
        testSubTask.setDescription("newDescription");
        testSubTask.setStatus(Status.IN_PROGRESS);
        testSubTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testSubTask.setDuration(90);
        SubTask updatedTestSubTask = fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertEquals(testSubTask, updatedTestSubTask, "SubTask не обновился.");
        assertEquals(updatedTestSubTask.getName(), "newName", "Имя SubTask не обновилось.");
        assertEquals(updatedTestSubTask.getDescription(), "newDescription",
                "Описание SubTask не обновилось.");
        assertEquals(updatedTestSubTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала SubTask не обновилось.");
        assertEquals(updatedTestSubTask.getDuration(), 90, "Длительность SubTask не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedTestSubTask.getStatus(), "Статус SubTask не обновился.");

        testSubTask.setStatus(null);
        assertEquals(Status.IN_PROGRESS, testSubTask.getStatus(), "Статус SubTask некоректен.");

        testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask.setEpicId(Integer.MAX_VALUE);
        updatedTestSubTask = fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertNull(updatedTestSubTask, "Прошло обновление SubTask на SubTask не хранящийся в listOfSubTask.");

        testSubTask = new SubTask(Integer.MAX_VALUE, "testSubTask1", "testSubTask1 description",
                testEpic1.getId());
        updatedTestSubTask = fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertNull(updatedTestSubTask, "Проведено обновление SubTask на SubTask не хранящийся в subTaskList.");
    }

    @Test
    void shouldSetDateTimeAndDurationInFileBackedTasksManager(){
        fileBackedTasksManager.deleteAllTasks();
        Task testTask1 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask1, "20.08.2024 11:00");
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 11:00");
        assertEquals(fileBackedTasksManager.getStartDateTime(testTask1), LocalDateTime.parse("20.08.2024 11:00",
                dateTimeFormatter));
        assertNull(fileBackedTasksManager.getStartDateTime(testSubTask1), "Пересесчение времени с другой Task");

        fileBackedTasksManager.setTaskAndSubTaskDuration(testTask1, 120);
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 13:00");
        assertNull(fileBackedTasksManager.getStartDateTime(testSubTask1), "Пересесчение времени с другой Task");
        assertEquals(120, fileBackedTasksManager.getTaskDuration(testTask1),
                "Неверно установлена длительность Task");

        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 10:00");
        assertEquals(LocalDateTime.parse("20.08.2024 10:00", dateTimeFormatter),
                fileBackedTasksManager.getStartDateTime(testSubTask1));
        fileBackedTasksManager.setTaskAndSubTaskDuration(testSubTask1, 120);
        assertEquals(0, fileBackedTasksManager.getTaskDuration(testSubTask1),
                "Неверно установлена длительность SubTask");
        fileBackedTasksManager.setTaskAndSubTaskDuration(testSubTask1, 30);
        assertEquals(30, fileBackedTasksManager.getTaskDuration(testSubTask1),
                "Неверно установлена длительность SubTask");
        assertEquals(LocalDateTime.parse("20.08.2024 10:00", dateTimeFormatter),
                fileBackedTasksManager.getStartDateTime(testEpic1), "Неверно установлена длительность Epic");
        assertEquals(0, fileBackedTasksManager.getTaskDuration(testEpic1),
                "Неверно установлена длительность Epic");

        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testSubTask2, "20.08.2024 09:00");
        fileBackedTasksManager.setTaskAndSubTaskDuration(testSubTask2, 10);
        assertEquals(90, fileBackedTasksManager.getTaskDuration(testEpic1),
                "Неверно установлена длительность Epic");
    }

    @Test
    void shouldGetListOfSubTasksInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Map<Integer, SubTask> testList = fileBackedTasksManager.getListOfSubTasks();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        SubTask testSubTask3 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask3",
                "testSubTask3 description", testEpic1.getId()));
        assertEquals(3, testList.size(), "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask1, fileBackedTasksManager.getListOfSubTasks().get(testSubTask1.getId()),
                "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask2, fileBackedTasksManager.getListOfSubTasks().get(testSubTask2.getId()),
                "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask3, fileBackedTasksManager.getListOfSubTasks().get(testSubTask3.getId()),
                "Ошибка при формировании SubtaskList");
    }

    @Test
    void shouldDeleteSubTaskByIdInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        int testSubTaskId = testSubTask.getId();
        fileBackedTasksManager.deleteSubTaskById(testSubTaskId);
        assertNull(fileBackedTasksManager.getTaskById(testSubTaskId), "SubTask не удалился.");

        assertNull(fileBackedTasksManager.deleteSubTaskById(-1), "Попытка удления несуществующего SubTask");
    }

    @Test
    void shouldCreateNewSubTaskInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        int testSubTaskId = testSubTask.getId();
        SubTask savedTestSubTask = tasksManager.getSubTaskById(testSubTaskId);
        assertNotNull(testSubTask, "SubTask не создан.");
        assertNotNull(savedTestSubTask, "SubTask на возвращается по Id.");
        assertEquals(testSubTask, savedTestSubTask, "Созданный SubTask и возвращенный по Id не совпадают.");

        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", Integer.MAX_VALUE));
        assertNull(testSubTask2, "Создан SubTask для несуществующего Epic");
    }

    @Test
    void shouldUpdateSubTaskInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask.setName("newName");
        testSubTask.setDescription("newDescription");
        testSubTask.setStatus(Status.IN_PROGRESS);
        testSubTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testSubTask.setDuration(90);
        SubTask updatedTestSubTask = tasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertEquals(testSubTask, updatedTestSubTask, "SubTask не обновился.");
        assertEquals(updatedTestSubTask.getName(), "newName", "Имя SubTask не обновилось.");
        assertEquals(updatedTestSubTask.getDescription(), "newDescription", "Описание не обновилось.");
        assertEquals(updatedTestSubTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала SubTask не обновилось.");
        assertEquals(updatedTestSubTask.getDuration(), 90, "Длительность SubTask не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedTestSubTask.getStatus(), "Статус SubTask не обновился.");

        testSubTask.setStatus(null);
        assertEquals(Status.IN_PROGRESS, testSubTask.getStatus(), "Статус SubTask некоректен.");

        testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask.setEpicId(Integer.MAX_VALUE);
        updatedTestSubTask = tasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertNull(updatedTestSubTask, "Проведено обновление по SubTask не хранящийся в listOfSubTask.");

        testSubTask = new SubTask(Integer.MAX_VALUE, "testSubTask1", "testSubTask1 description",
                testEpic1.getId());
        updatedTestSubTask = tasksManager.updateSubTaskByNewSubTask(testSubTask);
        assertNull(updatedTestSubTask, "Проведено обновление SubTask на SubTask не хранящийся в subTaskList.");
    }

    @Test
    void shouldSetDateTimeAndDurationInTasksManager() {
        tasksManager.deleteAllTasks();
        Task testTask1 = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        tasksManager.setTaskAndSubTaskStartDateTime(testTask1, "20.08.2024 11:00");
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        tasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 11:00");
        assertEquals(tasksManager.getStartDateTime(testTask1), LocalDateTime.parse("20.08.2024 11:00",
                dateTimeFormatter));
        assertNull(tasksManager.getStartDateTime(testSubTask1), "Установлено время занятое другой Task");

        tasksManager.setTaskAndSubTaskDuration(testTask1, 120);
        tasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 13:00");
        assertNull(tasksManager.getStartDateTime(testSubTask1), "Установлено время занятое другой Task");
        assertEquals(120, tasksManager.getTaskDuration(testTask1), "Неверная  длительность Task");

        tasksManager.setTaskAndSubTaskStartDateTime(testSubTask1, "20.08.2024 10:00");
        assertEquals(LocalDateTime.parse("20.08.2024 10:00", dateTimeFormatter),
                tasksManager.getStartDateTime(testSubTask1));
        tasksManager.setTaskAndSubTaskDuration(testSubTask1, 120);
        assertEquals(0, tasksManager.getTaskDuration(testSubTask1), "Неверная  длительность Task");
        tasksManager.setTaskAndSubTaskDuration(testSubTask1, 30);
        assertEquals(30, tasksManager.getTaskDuration(testSubTask1), "Неверная  длительность Task");
        assertEquals(LocalDateTime.parse("20.08.2024 10:00", dateTimeFormatter),
                tasksManager.getStartDateTime(testEpic1), "Неверно установлена длительность Epic");
        assertEquals(0, tasksManager.getTaskDuration(testEpic1), "еверная  длительность Epic");

        tasksManager.setTaskAndSubTaskStartDateTime(testSubTask2, "20.08.2024 09:00");
        tasksManager.setTaskAndSubTaskDuration(testSubTask2, 10);
        assertEquals(90, tasksManager.getTaskDuration(testEpic1), "еверная  длительность Epic");
    }

    @Test
    void shouldGetListOfSubTasksInTasksManager() {
        tasksManager.deleteAllTasks();
        Map<Integer, SubTask> testList = tasksManager.getListOfSubTasks();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        SubTask testSubTask3 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask3",
                "testSubTask3 description", testEpic1.getId()));
        assertEquals(3, testList.size(), "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask1, tasksManager.getListOfSubTasks().get(testSubTask1.getId()),
                "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask2, tasksManager.getListOfSubTasks().get(testSubTask2.getId()),
                "Ошибка при формировании SubtaskList");
        assertEquals(testSubTask3, tasksManager.getListOfSubTasks().get(testSubTask3.getId()),
                "Ошибка при формировании SubtaskList");
    }

    @Test
    void shouldDeleteSubTaskByIdInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        int testSubTaskId = testSubTask.getId();
        tasksManager.deleteSubTaskById(testSubTaskId);
        assertNull(tasksManager.getTaskById(testSubTaskId), "SubTask не удалился.");

        assertNull(tasksManager.deleteSubTaskById(-1), "Выполнена попытка удления несуществующего SubTask");
    }
}