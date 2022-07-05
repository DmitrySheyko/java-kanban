package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksManagerTest {
    static TasksManager tasksManager = Managers.getDefaultManager();
    static TasksManager fileBackedTasksManager = Managers.getDefaultBackedManager();

    @BeforeAll
    static void beforeAll() {
        tasksManager = Managers.getDefaultManager();
        fileBackedTasksManager = Managers.getDefaultBackedManager();
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