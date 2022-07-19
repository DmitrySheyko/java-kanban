package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
 * В предыдущем спринте я все тесты размемтил в классах TaskTest, EpicTest, SubTaskTest.
 * Тогда так казалось логичнее. Сейчас перенес тесты в этот класс, а там закоментировал.
 */

class TasksManagerTest {
    static TasksManager tasksManager = Managers.getDefaultManager();
    static TasksManager fileBackedTasksManager = Managers.getDefaultBackedManager();
    static DateTimeFormatter dateTimeFormatter;

    @BeforeAll
    static void beforeAll() {
        tasksManager = Managers.getDefaultManager();
        fileBackedTasksManager = Managers.getDefaultBackedManager();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    @Test
    void shouldCreateNewTaskInFileBackedTasksManager() {
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1", "testTask1 description"));
        int taskId = testTask.getId();
        Task savedTask = fileBackedTasksManager.getTaskById(taskId);
        assertNotNull(testTask, "Task не создан.");
        assertNotNull(savedTask, "Task на возвращается по Id.");
        assertEquals(testTask, savedTask, "Созданный Task и возвращенный по Id не совпадают.");
    }

    @Test
    void shouldUpdateTaskInFileBackedTasksManager() {
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        testTask.setName("newName");
        testTask.setDescription("newDescription");
        testTask.setStatus(Status.IN_PROGRESS);
        testTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testTask.setDuration(90);
        Task updatedTestTask = fileBackedTasksManager.updateTaskByNewTask(testTask);
        assertEquals(testTask, updatedTestTask, "Task не обновился.");
        assertEquals(updatedTestTask.getName(), "newName", "Имя Task не обновилось.");
        assertEquals(updatedTestTask.getDescription(), "newDescription", "Описание Task не обновилось.");
        assertEquals(updatedTestTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала Task не обновилось.");
        assertEquals(updatedTestTask.getDuration(), 90, "Длительность Task не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedTestTask.getStatus(), "Статус Task не обновился.");

        testTask.setStatus(null);
        assertEquals(Status.IN_PROGRESS, testTask.getStatus(), "Статус Task некоректен.");

        testTask = new Task(Integer.MAX_VALUE, "testTask1", "testTask1 description");
        updatedTestTask = fileBackedTasksManager.updateTaskByNewTask(testTask);
        assertNull(updatedTestTask, "Проведено обновление Task с аргументом null.");
    }

    @Test
    void shouldGetListOfTasksInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Map<Integer, Task> testList = fileBackedTasksManager.getListOfTasks();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Task testTask1 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        Task testTask2 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask2",
                "testTask2 description"));
        Task testTask3 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask3",
                "testTask3 description"));
        assertEquals(3, testList.size(), "Ошибка при формировании taskList");
        assertEquals(testTask1, fileBackedTasksManager.getListOfTasks().get(testTask1.getId()));
        assertEquals(testTask2, fileBackedTasksManager.getListOfTasks().get(testTask2.getId()));
        assertEquals(testTask3, fileBackedTasksManager.getListOfTasks().get(testTask3.getId()));
    }

    @Test
    void shouldDeleteTaskByIdInFileBackedTasksManager() {
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        int testTaskId = testTask.getId();
        fileBackedTasksManager.deleteTaskById(testTaskId);
        assertNull(fileBackedTasksManager.getTaskById(testTaskId), "Task не удалился.");

        assertNull(fileBackedTasksManager.deleteTaskById(-1), "Выполнена попытка удления несуществующего Task");
    }

    @Test
    void shouldCreateNewTaskInTasksManager() {
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        int taskId = testTask.getId();
        Task savedTask = tasksManager.getTaskById(taskId);
        assertNotNull(testTask, "Task не создан.");
        assertNotNull(savedTask, "Task на возвращается по Id.");
        assertEquals(testTask, savedTask, "Созданный Task и возвращенный по Id не совпадают.");
    }

    @Test
    void shouldUpdateTaskInTasksManager() {
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        testTask.setName("newName");
        testTask.setDescription("newDescription");
        testTask.setStatus(Status.IN_PROGRESS);
        testTask.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testTask.setDuration(90);
        Task updatedTestTask = tasksManager.updateTaskByNewTask(testTask);
        assertEquals(testTask, updatedTestTask, "Task не обновился.");
        assertEquals(updatedTestTask.getName(), "newName", "Имя Task не обновилось.");
        assertEquals(updatedTestTask.getDescription(), "newDescription", "Описание Task не обновилось.");
        assertEquals(updatedTestTask.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала Task не обновилось.");
        assertEquals(updatedTestTask.getDuration(), 90, "Длительность Task не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedTestTask.getStatus(), "Статус Task не обновился.");

        testTask.setStatus(null);
        assertEquals(Status.IN_PROGRESS, testTask.getStatus(), "Статус Task некоректен.");

        testTask = new Task(Integer.MAX_VALUE, "testTask1", "testTask1 description");
        updatedTestTask = tasksManager.updateTaskByNewTask(testTask);
        assertNull(updatedTestTask, "Проведено обновление Task с аргументом null.");
    }

    @Test
    void shouldGetListOfTasksInTasksManager() {
        tasksManager.deleteAllTasks();
        Map<Integer, Task> testList = tasksManager.getListOfTasks();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Task testTask1 = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        Task testTask2 = tasksManager.creationOfTask(new Task(10, "testTask2",
                "testTask2 description"));
        Task testTask3 = tasksManager.creationOfTask(new Task(10, "testTask3",
                "testTask3 description"));
        assertEquals(3, testList.size(), "Ошибка при формировании taskList");
        assertEquals(testTask1, tasksManager.getListOfTasks().get(testTask1.getId()));
        assertEquals(testTask2, tasksManager.getListOfTasks().get(testTask2.getId()));
        assertEquals(testTask3, tasksManager.getListOfTasks().get(testTask3.getId()));
    }

    @Test
    void shouldDeleteTaskByIdInTasksManager() {
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        int testTaskId = testTask.getId();
        tasksManager.deleteTaskById(testTaskId);
        assertNull(tasksManager.getTaskById(testTaskId), "Task не удалился.");

        assertNull(tasksManager.deleteTaskById(-1),
                "Выполнена попытка удления несуществующего Task");
    }

    @Test
    void shouldCreateEpicWithoutSubtasksInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        assertEquals(Status.NEW, testEpic1.getStatus(), "Неверный статус Epic");

        Epic testEpic2 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", null));
        assertNull(testEpic2, "Создан Epic, с subTaskList = null");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusNEWInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1Description", testEpic1.getId()));
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2Description", testEpic1.getId()));
        assertEquals(Status.NEW, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.NEW, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.NEW, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusDONEInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1Description", testEpic1.getId()));
        testSubTask1.setStatus(Status.DONE);
        fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask1);
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2Description", testEpic1.getId()));
        testSubTask2.setStatus(Status.DONE);
        fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.DONE, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusesNEWandDONEInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1Description", testEpic1.getId()));
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2Description", testEpic1.getId()));
        testSubTask2.setStatus(Status.DONE);
        fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.NEW, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusIN_PROGRESSInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1Description", testEpic1.getId()));
        testSubTask1.setStatus(Status.IN_PROGRESS);
        fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask1);
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2Description", testEpic1.getId()));
        testSubTask2.setStatus(Status.IN_PROGRESS);
        fileBackedTasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.IN_PROGRESS, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldGetListOfEpicsInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Map<Integer, Epic> testList = fileBackedTasksManager.getListOfEpics();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic1",
                "testEpic1 description", new ArrayList<>()));
        Epic testEpic2 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic2",
                "testEpic2 description", new ArrayList<>()));
        Epic testEpic3 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic3",
                "testEpic3 description", new ArrayList<>()));
        assertEquals(3, testList.size(), "Ошибка при формировании SubtaskList");
        assertEquals(testEpic1, fileBackedTasksManager.getListOfEpics().get(testEpic1.getId()),
                "Ошибка при формировании Epics");
        assertEquals(testEpic2, fileBackedTasksManager.getListOfEpics().get(testEpic2.getId()),
                "Ошибка при формировании Epics");
        assertEquals(testEpic3, fileBackedTasksManager.getListOfEpics().get(testEpic3.getId()),
                "Ошибка при формировании Epics");
    }

    @Test
    void shouldDeleteEpicByIdInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1Description", testEpic1.getId()));
        int idOfTestEpic1 = testEpic1.getId();
        int idOftestSubTask1 = testSubTask1.getEpicId();
        fileBackedTasksManager.deleteEpicById(idOfTestEpic1);
        assertNull(fileBackedTasksManager.getEpicById(idOfTestEpic1), "Epic не удален");
        assertNull(fileBackedTasksManager.getSubTaskById(idOftestSubTask1), "SubTask  не удален");

        assertNull(fileBackedTasksManager.deleteEpicById(-1), "Выполнена попытка удления несуществующего Epic");
    }

    @Test
    void shouldGetListOfSubTasksOfEpicInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        assertNull(fileBackedTasksManager.getListOfSubTasksOfEpic(-1),
                "При вызове ListOfSubTasks несуществующего Epic не возвращается  Null");
        SubTask testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        List<Integer> listOfSubTask = fileBackedTasksManager.getListOfSubTasksOfEpic(testEpic1.getId());
        assertEquals(2, listOfSubTask.size());
        assertEquals(testSubTask1.getId(), listOfSubTask.get(0), "Получен неверный Id для SubTask " +
                testSubTask1.getId());
        assertEquals(testSubTask2.getId(), listOfSubTask.get(1), "Получен неверный Id для SubTask " +
                testSubTask1.getId());
    }

    @Test
    void shouldUpdateEpicInFileBackedTasksManager() {
        Epic testEpic = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        testEpic.setName("newName");
        testEpic.setDescription("newDescription");
        testEpic.setStatus(Status.IN_PROGRESS);
        testEpic.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testEpic.setDuration(90);
        Epic updatedTestEpic = fileBackedTasksManager.updateEpicByNewEpic(testEpic);
        assertEquals(testEpic, updatedTestEpic, "Epic не обновился.");
        assertEquals(updatedTestEpic.getName(), "newName", "Имя Epic не обновилось.");
        assertEquals(updatedTestEpic.getDescription(), "newDescription", "Описание SEpic не обновилось.");
        assertEquals(updatedTestEpic.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала Epic не обновилось.");
        assertEquals(updatedTestEpic.getDuration(), 90, "Длительность Epic не обновилось.");
        assertEquals(Status.NEW, updatedTestEpic.getStatus(), "Статус Epic не обновился.");

        testEpic.setStatus(null);
        assertEquals(Status.NEW, testEpic.getStatus(), "Статус Epic некоректен.");

        Epic testEpic2 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        testEpic2.setId(Integer.MAX_VALUE);
        updatedTestEpic = fileBackedTasksManager.updateEpicByNewEpic(testEpic2);
        assertNull(updatedTestEpic, "Прошло обновление Epic не входящего в epicList");
    }

    @Test
    void shouldCreateEpicWithoutSubtasksInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        assertEquals(Status.NEW, testEpic1.getStatus(), "Неверный статус Epic");

        Epic testEpic2 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", null));
        assertNull(testEpic2, "Создан Epic, с subTaskList = null");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusNEWInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        assertEquals(Status.NEW, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.NEW, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.NEW, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusDONEInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask1.setStatus(Status.DONE);
        tasksManager.updateSubTaskByNewSubTask(testSubTask1);
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        testSubTask2.setStatus(Status.DONE);
        tasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.DONE, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusesNEWandDONEInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        testSubTask2.setStatus(Status.DONE);
        tasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.NEW, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.DONE, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldCreateEpicWithTwoSubtasksWIthStatusIN_PROGRESSInTaskManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask1.setStatus(Status.IN_PROGRESS);
        tasksManager.updateSubTaskByNewSubTask(testSubTask1);
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        testSubTask2.setStatus(Status.IN_PROGRESS);
        tasksManager.updateSubTaskByNewSubTask(testSubTask2);
        assertEquals(Status.IN_PROGRESS, testSubTask1.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testSubTask2.getStatus(), "Неверный статус subTask1");
        assertEquals(Status.IN_PROGRESS, testEpic1.getStatus(), "Неверный статус Epic");
    }

    @Test
    void shouldDeleteEpicByIdInTasksManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1Description", new ArrayList<>()));
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        int idOfTestEpic1 = testEpic1.getId();
        int idOftestSubTask1 = testSubTask1.getEpicId();
        tasksManager.deleteEpicById(idOfTestEpic1);
        assertNull(tasksManager.getEpicById(idOfTestEpic1), "Epic не удален");
        assertNull(tasksManager.getSubTaskById(idOftestSubTask1), "SubTask входяший в Epic не удален");

        assertNull(tasksManager.deleteEpicById(-1), "Выполнена попытка удления несуществующего Epic");
    }

    @Test
    void shouldGetListOfSubTasksOfEpicInTasksManager() {
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        assertNull(tasksManager.getListOfSubTasksOfEpic(-1),
                "При вызове ListOfSubTasks несуществующего Epic не возвращается  Null");
        SubTask testSubTask1 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        SubTask testSubTask2 = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        List<Integer> listOfSubTask = tasksManager.getListOfSubTasksOfEpic(testEpic1.getId());
        assertEquals(2, tasksManager.getListOfSubTasksOfEpic(testEpic1.getId()).size());
        assertEquals(testSubTask1.getId(), listOfSubTask.get(0), "Получен неверный Id для SubTask " +
                testSubTask1.getId());
        assertEquals(testSubTask2.getId(), listOfSubTask.get(1), "Получен неверный Id для SubTask " +
                testSubTask1.getId());
    }

    @Test
    void shouldUpdateEpicInTasksManager() {
        Epic testEpic = tasksManager.creationOfEpic(new Epic(10, "testEpic", "testEpic1 description",
                new ArrayList<>()));
        testEpic.setName("newName");
        testEpic.setDescription("newDescription");
        testEpic.setStatus(Status.IN_PROGRESS);
        testEpic.setStartTime(LocalDateTime.parse("20.08.2023 11:32", dateTimeFormatter));
        testEpic.setDuration(90);
        Epic updatedTestEpic = tasksManager.updateEpicByNewEpic(testEpic);
        assertEquals(testEpic, updatedTestEpic, "Epic не обновился.");
        assertEquals(updatedTestEpic.getName(), "newName", "Имя Epic не обновилось.");
        assertEquals(updatedTestEpic.getDescription(), "newDescription", "Описание SEpic не обновилось.");
        assertEquals(updatedTestEpic.getStartTime().format(dateTimeFormatter), "20.08.2023 11:32",
                "Время начала Epic не обновилось.");
        assertEquals(updatedTestEpic.getDuration(), 90, "Длительность Epic не обновилось.");
        assertEquals(Status.NEW, updatedTestEpic.getStatus(), "Статус Epic не обновился.");

        Epic testEpic2 = tasksManager.creationOfEpic(new Epic(10, "testEpic", "testEpic1Description",
                new ArrayList<>()));
        testEpic2.setId(Integer.MAX_VALUE);
        updatedTestEpic = tasksManager.updateEpicByNewEpic(testEpic2);
        assertNull(updatedTestEpic, "Прошло обновление Epic не входящего в epicList");
    }

    @Test
    void shouldGetListOfEpicsInTasksManager() {
        tasksManager.deleteAllTasks();
        Map<Integer, Epic> testList = tasksManager.getListOfEpics();
        assertEquals(0, testList.size(), " Ошибка при формировании taskList");

        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic1",
                "testEpic1 description", new ArrayList<>()));
        Epic testEpic2 = tasksManager.creationOfEpic(new Epic(10, "testEpic2",
                "testEpic2 description", new ArrayList<>()));
        Epic testEpic3 = tasksManager.creationOfEpic(new Epic(10, "testEpic3",
                "testEpic3 description", new ArrayList<>()));
        assertEquals(3, testList.size(), "Ошибка при формировании SubtaskList");
        assertEquals(testEpic1, tasksManager.getListOfEpics().get(testEpic1.getId()),
                "Ошибка при формировании Epics");
        assertEquals(testEpic2, tasksManager.getListOfEpics().get(testEpic2.getId()),
                "Ошибка при формировании Epics");
        assertEquals(testEpic3, tasksManager.getListOfEpics().get(testEpic3.getId()),
                "Ошибка при формировании Epics");
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
    void shouldSetDateTimeAndDurationInFileBackedTasksManager() {
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

    @Test
    void shouldGetListOfAllTasksInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        assertEquals(3, fileBackedTasksManager.getListOfAllTasks().size());
    }

    @Test
    void shouldGetPrioritizedTasksInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        Task testTask2 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask2",
                "testTask2 description"));
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask, "12.12.2025 19:02");
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testTask2, "12.12.2025 16:02");
        fileBackedTasksManager.setTaskAndSubTaskStartDateTime(testSubTask, "12.12.2025 14:02");
        List<Task> resultListOfTime = fileBackedTasksManager.getPrioritizedTasks();
        assertEquals(testEpic1, resultListOfTime.get(0));
        assertEquals(testSubTask, resultListOfTime.get(1));
        assertEquals(testTask2, resultListOfTime.get(2));
        assertEquals(testTask, resultListOfTime.get(3));
    }

    @Test
    void shouldDeleteAllTasksInFileBackedTasksManager() {
        Epic testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        fileBackedTasksManager.deleteAllTasks();
        assertEquals(0, fileBackedTasksManager.getListOfAllTasks().size());
    }

    @Test
    void shouldGetHistoryInFileBackedTasksManager() {
        fileBackedTasksManager.deleteAllTasks();
        Epic testEpic = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic.getId()));
        Task testTask = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        fileBackedTasksManager.getEpicById(testEpic.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask.getId());
        fileBackedTasksManager.getTaskById(testTask.getId());
        List<Task> history = new ArrayList<>(fileBackedTasksManager.getHistory());
        StringBuilder expectedHistory = new StringBuilder();
        StringBuilder resultHistory = new StringBuilder();
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        expectedHistory.append(testEpic.getId()).append(",").
                append(testSubTask.getId()).append(",").
                append(testTask.getId()).append(",");
        assertEquals(expectedHistory.toString(), resultHistory.toString(),
                "Неверно отображается история вызовов Task");
        fileBackedTasksManager.getTaskById(testTask.getId());
        fileBackedTasksManager.getTaskById(testTask.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask.getId());
        fileBackedTasksManager.getEpicById(testEpic.getId());
        fileBackedTasksManager.getEpicById(testEpic.getId());
        history = new ArrayList<>(fileBackedTasksManager.getHistory());
        resultHistory.delete(0, resultHistory.length());
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        expectedHistory.delete(0, expectedHistory.length()).
                append(testTask.getId()).append(",").
                append(testSubTask.getId()).append(",").
                append(testEpic.getId()).append(",");
        assertEquals(expectedHistory.toString(), resultHistory.toString(),
                "Неверно отображается история вызовов Task");
    }

    @Test
    void shouldGetListOfAllTasksInTasksManager() {
        tasksManager.deleteAllTasks();
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        assertEquals(3, tasksManager.getListOfAllTasks().size());
    }

    @Test
    void shouldDeleteAllTasksInTasksManager() {
        tasksManager.deleteAllTasks();
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        tasksManager.deleteAllTasks();
        assertEquals(0, tasksManager.getListOfAllTasks().size());
    }

    @Test
    void shouldGetHistoryInTasksManager() {
        tasksManager.deleteAllTasks();
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        tasksManager.getEpicById(1);
        tasksManager.getSubTaskById(2);
        tasksManager.getTaskById(3);
        List<Task> history = new ArrayList<>(tasksManager.getHistory());
        StringBuilder resultHistory = new StringBuilder();
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        assertEquals("1,2,3,", resultHistory.toString(), "Неверно отображается история вызовов Task");
        tasksManager.getTaskById(3);
        tasksManager.getTaskById(3);
        tasksManager.getSubTaskById(2);
        tasksManager.getSubTaskById(2);
        tasksManager.getEpicById(1);
        tasksManager.getEpicById(1);
        history = new ArrayList<>(tasksManager.getHistory());
        resultHistory.delete(0, resultHistory.length());
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        assertEquals("3,2,1,", resultHistory.toString(), "Неверно отображается история вызовов Task");
    }

    @Test
    void shouldGetPrioritizedTasksInTasksManager() {
        tasksManager.deleteAllTasks();
        Task testTask = tasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        Task testTask2 = tasksManager.creationOfTask(new Task(10, "testTask2",
                "testTask2 description"));
        Epic testEpic1 = tasksManager.creationOfEpic(new Epic(10, "testEpic",
                "testEpic1 description", new ArrayList<>()));
        SubTask testSubTask = tasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        tasksManager.setTaskAndSubTaskStartDateTime(testTask, "12.12.2025 19:02");
        tasksManager.setTaskAndSubTaskStartDateTime(testTask2, "12.12.2025 16:02");
        tasksManager.setTaskAndSubTaskStartDateTime(testSubTask, "12.12.2025 14:02");
        List<Task> resultListOfTime = tasksManager.getPrioritizedTasks();
        assertEquals(testEpic1, resultListOfTime.get(0));
        assertEquals(testSubTask, resultListOfTime.get(1));
        assertEquals(testTask2, resultListOfTime.get(2));
        assertEquals(testTask, resultListOfTime.get(3));
    }
}