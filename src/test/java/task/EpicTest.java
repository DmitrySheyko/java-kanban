package task;

import manager.Managers;
import manager.TasksManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
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
}
