package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface TasksManager {

    int getUniqueTaskId();

    Task creationOfTask(Task task);

    Epic creationOfEpic(Epic epic);

    SubTask creationOfSubTask(SubTask subTask);

    Map<Integer, Task> getListOfAllTasks();

    Map<Integer, Task> getListOfTasks();

    Map<Integer, Epic> getListOfEpics();

    Map<Integer, SubTask> getListOfSubTasks();

    Map<Integer, Task> deleteAllTasks();

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    SubTask getSubTaskById(Integer id);

    Task updateTaskByNewTask(Task task);

    Epic updateEpicByNewEpic(Epic epic);

    SubTask updateSubTaskByNewSubTask(SubTask subTask);

    Task deleteTaskById(Integer id);

    Epic deleteEpicById(Integer id);

    SubTask deleteSubTaskById(Integer id);

    List<Integer> getListOfSubTasksOfEpic(Integer epicId);

    List<Task> getHistory();

    void setTaskAndSubTaskStartDateTime(Task task, String startDateTime);

    void setTaskAndSubTaskDuration(Task task, int durationInMinutes);

    LocalDateTime getStartDateTime(Task task);

    void setEpicDuration(int epicId);

    void setEpicStartDateTime(int epicId);

    int getTaskDuration(Task task);

    List <Task> getPrioritizedTasks();
}
