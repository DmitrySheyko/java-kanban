package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    static TasksManager fileBackedTasksManager;
    static Task testTask1;
    static Task testTask2;
    static Epic testEpic1;
    static Epic testEpic2;
    static SubTask testSubTask1;
    static SubTask testSubTask2;
    static List<Task> history;
    static StringBuilder expectedHistory;
    static StringBuilder resultHistory;

    @BeforeAll
    static void beforeAll() {
        fileBackedTasksManager = Managers.getDefaultBackedManager();
        history = new ArrayList<>();
        expectedHistory = new StringBuilder();
        resultHistory = new StringBuilder();
    }

    @BeforeEach
    void beforeEach() {
        fileBackedTasksManager.deleteAllTasks();
        testTask1 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask1",
                "testTask1 description"));
        testTask2 = fileBackedTasksManager.creationOfTask(new Task(10, "testTask2",
                "testTask2 description"));
        testEpic1 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic1",
                "testEpic1 description", new ArrayList<>()));
        testEpic2 = fileBackedTasksManager.creationOfEpic(new Epic(10, "testEpic2",
                "testEpic2 description", new ArrayList<>()));
        testSubTask1 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask1",
                "testSubTask1 description", testEpic1.getId()));
        testSubTask2 = fileBackedTasksManager.creationOfSubTask(new SubTask(10, "testSubTask2",
                "testSubTask2 description", testEpic1.getId()));
        fileBackedTasksManager.getSubTaskById(testSubTask2.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask2.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask1.getId());
        fileBackedTasksManager.getSubTaskById(testSubTask1.getId());
        fileBackedTasksManager.getEpicById(testEpic2.getId());
        fileBackedTasksManager.getEpicById(testEpic2.getId());
        fileBackedTasksManager.getEpicById(testEpic1.getId());
        fileBackedTasksManager.getEpicById(testEpic1.getId());
        fileBackedTasksManager.getTaskById(testTask2.getId());
        fileBackedTasksManager.getTaskById(testTask2.getId());
        fileBackedTasksManager.getTaskById(testTask1.getId());
        fileBackedTasksManager.getTaskById(testTask1.getId());
        history = fileBackedTasksManager.getHistory();
    }

    @Test
    void shouldAddTasksInHistoryOfViewInCorrectOrder() {
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        expectedHistory.append(testSubTask2.getId()).append(",")
                .append(testSubTask1.getId()).append(",")
                .append(testEpic2.getId()).append(",")
                .append(testEpic1.getId()).append(",")
                .append(testTask2.getId()).append(",")
                .append(testTask1.getId()).append(",");
        assertEquals(expectedHistory.toString(), resultHistory.toString(),
                "Неверно отображается история вызовов Task");
    }

    @Test
    void shouldRemoveDeletedTasksFromHistoryOfViews() {
        fileBackedTasksManager.deleteTaskById(testTask2.getId());
        fileBackedTasksManager.deleteEpicById(testEpic2.getId());
        fileBackedTasksManager.deleteSubTaskById(testSubTask2.getId());
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        expectedHistory.append(testSubTask1.getId()).append(",")
                .append(testEpic1.getId()).append(",")
                .append(testTask1.getId()).append(",");
        assertEquals(expectedHistory.toString(), resultHistory.toString(), "Ошибка в истории вызовов");
    }

    @Test
    void shouldReturnHistoryInList() {
        fileBackedTasksManager.deleteTaskById(testTask2.getId());
        fileBackedTasksManager.deleteEpicById(testEpic2.getId());
        fileBackedTasksManager.deleteSubTaskById(testSubTask2.getId());
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        expectedHistory.append(testSubTask1.getId()).append(",")
                .append(testEpic1.getId()).append(",")
                .append(testTask1.getId()).append(",");
        assertEquals(expectedHistory.toString(), resultHistory.toString(), "Неверно отображается история вызовов Task");

        fileBackedTasksManager.deleteTaskById(testTask1.getId());
        fileBackedTasksManager.deleteEpicById(testEpic1.getId());
        fileBackedTasksManager.deleteSubTaskById(testSubTask1.getId());
        for (Task task : history) {
            resultHistory.append(task.getId()).append(",");
        }
        assertEquals(expectedHistory.toString(), resultHistory.toString(), "Неверно отображается история вызовов Task");
    }
}