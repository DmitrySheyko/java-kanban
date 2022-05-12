package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    Task creationOfTask(Task task);

    Epic creationOfEpic(Epic epic);

    SubTask creationOfSubTask(SubTask subTask);

    HashMap<Integer, Task> getListOfAllTasks();

    HashMap<Integer, Task> getListOfTasks();

    HashMap<Integer, Epic> getListOfEpics();

    HashMap<Integer, SubTask> getListOfSubTasks();

    HashMap<Integer, Task> deleteAllTasks();

    HashMap<Integer, Task> deleteTasks();

    HashMap<Integer, Epic> deleteEpics();

    HashMap<Integer, SubTask> deleteSubTasks();

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    SubTask getSubTaskById(Integer id);

    Task updateTaskByNewTask(Task task);

    Epic updateEpicByNewEpic(Epic epic);

    SubTask updateSubTaskByNewSubTask(SubTask subTask);

    Task deleteTaskById(Integer id);

    Epic deleteEpicById(Integer id);

    SubTask deleteSubTaskById(Integer id);

    ArrayList<Integer> getListOfSubTasksOfEpic(Integer epicId);

    ArrayList<Task> getHistory();

}
