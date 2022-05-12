package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private static int uniqueTaskId = 0;
    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, SubTask> subTaskList;
    private final HashMap<Integer, Epic> epicList;
    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.subTaskList = new HashMap<>();
        this.epicList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task creationOfTask(Task task) {
        uniqueTaskId++;
        Task newTask = new Task(uniqueTaskId, task.getName(), task.getDescription());
        newTask.setStatus(Status.NEW);
        taskList.put(uniqueTaskId, newTask);
        return newTask;
    }

    @Override
    public Epic creationOfEpic(Epic epic) {
        if (epic.getSubTaskIdList() != null) {
            uniqueTaskId++;
            Epic newEpic = new Epic(uniqueTaskId, epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
            newEpic.setStatus(Status.NEW);
            epicList.put(uniqueTaskId, newEpic);
            return newEpic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask creationOfSubTask(SubTask subTask) {
        if (epicList.containsKey(subTask.getEpicId())) {
            uniqueTaskId++;
            SubTask newSubTask = new SubTask(uniqueTaskId, subTask.getName(), subTask.getDescription()
                    , subTask.getEpicId());
            newSubTask.setStatus(Status.NEW);
            subTaskList.put(uniqueTaskId, newSubTask);
            Epic epicForUpdate = epicList.get(subTask.getEpicId());
            epicForUpdate.getSubTaskIdList().add(uniqueTaskId);
            return newSubTask;
        } else {
            return null;
        }
    }

    // Данный метод не был private т.к. используется в классе Main.
    // Предлагаю не убирать его из интерфейса.
    @Override
    public HashMap<Integer, Task> getListOfAllTasks() {
        HashMap<Integer, Task> listOfAllTasks = new HashMap<>();
        listOfAllTasks.putAll(epicList);
        listOfAllTasks.putAll(taskList);
        listOfAllTasks.putAll(subTaskList);
        return listOfAllTasks;
    }

    /* Приведенные ниже методы, как я понял из ТЗ спринта 3, могут использоваться в классе Main.
     * По этой причине я изначально делал их публичными. Предлагаю не убирать его из интерфейса.
     * getListOfTasks()
     * getListOfEpics()
     * getListOfSubTasks()
     */
    @Override
    public HashMap<Integer, Task> getListOfTasks() {
        return taskList;
    }

    @Override
    public HashMap<Integer, Epic> getListOfEpics() {
        return epicList;
    }

    @Override
    public HashMap<Integer, SubTask> getListOfSubTasks() {
        return subTaskList;
    }

    @Override
    public HashMap<Integer, Task> deleteAllTasks() {
        epicList.clear();
        taskList.clear();
        subTaskList.clear();
        return getListOfAllTasks();
    }

    @Override
    public HashMap<Integer, Task> deleteTasks() {
        taskList.clear();
        return taskList;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpics() {
        subTaskList.clear();
        epicList.clear();
        return epicList;
    }

    @Override
    public HashMap<Integer, SubTask> deleteSubTasks() {
        for (SubTask subTaskForDelete : subTaskList.values()) {
            Integer idOfEpicForClearItSubTasksList = subTaskForDelete.getEpicId();
            if (epicList.containsKey(idOfEpicForClearItSubTasksList)) {
                epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().clear();
                epicList.get(idOfEpicForClearItSubTasksList)
                        .setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
            } else {
                return null;
            }
        }
        subTaskList.clear();
        return subTaskList;
    }

    @Override
    public Task getTaskById(Integer id) {
        if (taskList.get(id) != null) {
            historyManager.add(taskList.get(id));
            return taskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epicList.get(id) != null) {
            historyManager.add(epicList.get(id));
            return epicList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (subTaskList.get(id) != null) {
            historyManager.add(subTaskList.get(id));
            return subTaskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Task updateTaskByNewTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            return taskList.replace(task.getId(), task);
        } else {
            return null;
        }
    }

    @Override
    public Epic updateEpicByNewEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            if (epic.getSubTaskIdList() != null) {
                Epic replacedEpic = epicList.replace(epic.getId(), epic);
                epic.setStatus(checkEpicStatus(epic.getSubTaskIdList()));
                return replacedEpic;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public SubTask updateSubTaskByNewSubTask(SubTask subTask) {
        Epic epicForCheckStatus = epicList.get(subTask.getEpicId());
        if (subTaskList.containsKey(subTask.getId())
                && epicForCheckStatus.getSubTaskIdList().contains(subTask.getId())) {
            SubTask replacedSubTask = subTaskList.replace(subTask.getId(), subTask);
            epicForCheckStatus.setStatus(checkEpicStatus(epicForCheckStatus.getSubTaskIdList()));
            return replacedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Task deleteTaskById(Integer id) {
        if (taskList.containsKey(id)) {
            return taskList.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        if (epicList.containsKey(id)) {
            for (Integer subTaskId : epicList.get(id).getSubTaskIdList()) {
                if (subTaskList.containsKey(subTaskId) && subTaskList.get(subTaskId).getEpicId() == id) {
                    subTaskList.remove(subTaskId);
                } else {
                    return null;
                }
            }
            return epicList.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask deleteSubTaskById(Integer id) {
        if (subTaskList.containsKey(id)) {
            Integer idOfEpicForClearItSubTasksList = subTaskList.get(id).getEpicId();
            if (epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().contains(id)) {
                epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().remove(id);
                epicList.get(idOfEpicForClearItSubTasksList)
                        .setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
            } else {
                return null;
            }
            return subTaskList.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Integer> getListOfSubTasksOfEpic(Integer epicId) {
        if (epicList.containsKey(epicId)) {
            return epicList.get(epicId).getSubTaskIdList();
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Убрал метод из интерфейса и вернул модификатор private
    private Status checkEpicStatus(ArrayList<Integer> subTaskIdList) {
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;
        if (subTaskIdList.isEmpty()) {
            return Status.NEW;
        } else {
            for (Integer subTaskId : subTaskIdList) {
                if ((subTaskList.get(subTaskId)).getStatus() == Status.NEW) {
                    isNew = true;
                } else if ((subTaskList.get(subTaskId)).getStatus() == Status.IN_PROGRESS) {
                    isInProgress = true;
                } else {
                    isDone = true;
                }
            }
            if ((isNew && !isInProgress && !isDone)) {
                return Status.NEW;
            } else if (isDone && !isNew && !isInProgress) {
                return Status.DONE;
            } else {
                return Status.IN_PROGRESS;
            }
        }
    }
}