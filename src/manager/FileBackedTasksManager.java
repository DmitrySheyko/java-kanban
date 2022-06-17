package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = Managers.loadFromFile(new File("Save.csv"));

        // Создаю задачу 1
        Task task1 = new Task(100, "Тестирование 1", "Создать тестовый Task 1");
        fileBackedTasksManager.creationOfTask(task1);

        // Создаю задачу 2
        Task task2 = new Task(100, "Тестирование 2", "Создать тестовый Task 2");
        fileBackedTasksManager.creationOfTask(task2);

        // Создаю Epic1 с двумя подзадачами
        ArrayList<Integer> subTasksListIdOfEpic1 = new ArrayList<>();
        Epic epic1 = new Epic(100, "Тестирование 3", "Создать тестовый Epic 1"
                , subTasksListIdOfEpic1);
        int idOfCreatedEpic1 = fileBackedTasksManager.creationOfEpic(epic1).getId();

        // - подзадача 1 для Epic1
        SubTask subTask1OfEpic1 = new SubTask(100, "Тестирование 4"
                , "Создать тестовый SubTask 1", idOfCreatedEpic1);
        fileBackedTasksManager.creationOfSubTask(subTask1OfEpic1);

        //  - подзадача 2 для Epic1
        SubTask subTask2OfEpic1 = new SubTask(10, "Тестирование 5"
                , "Создать тестовый SubTask 2", idOfCreatedEpic1);
        fileBackedTasksManager.creationOfSubTask(subTask2OfEpic1);

        // Обращаюсь к задачам по их ID для заполнения имстории просмотров
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getEpicById(3);
        fileBackedTasksManager.getEpicById(3);
        fileBackedTasksManager.getSubTaskById(5);
        fileBackedTasksManager.getSubTaskById(4);
    }

    void save() {
        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            fileWriter.write(tasksToString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String tasksToString() {
        String head = "id,type,name,status,description,epic,subtasks\n";
        StringBuilder tasksTable = new StringBuilder();
        StringBuilder historyOfViews = new StringBuilder("\n");
        for (Task task : getHistory()) {
            historyOfViews.append(task.getId()).append(',');
        }
        historyOfViews.deleteCharAt(historyOfViews.length() - 1);
        for (Task task : getListOfAllTasks().values()) {
            if ((task.getClass().getSimpleName()).equals("Epic")) {
                Epic epic = (Epic) task;
                StringBuilder subTasks = new StringBuilder(epic.getSubTaskIdList().toString().replace(',', '/'));
                subTasks.deleteCharAt(0).deleteCharAt(subTasks.length() - 1);
                tasksTable.append(epic.getId()).append(',')
                        .append("Epic").append(',')
                        .append(epic.getName()).append(',')
                        .append(epic.getStatus().toString()).append(',')
                        .append(epic.getDescription()).append(',')
                        .append("-").append(",")
                        .append(subTasks).append(",\n");
                continue;
            } else if ((task.getClass().getSimpleName()).equals("SubTask")) {
                SubTask subTask = (SubTask) task;
                tasksTable.append(subTask.getId()).append(',')
                        .append("SubTask").append(',')
                        .append(subTask.getName()).append(',')
                        .append(subTask.getStatus().toString()).append(',')
                        .append(subTask.getDescription()).append(',')
                        .append(subTask.getEpicId()).append(',')
                        .append("-,\n");
                continue;
            } else if ((task.getClass().getSimpleName()).equals("Task")) {
                tasksTable.append(task.getId()).append(',')
                        .append("Task").append(',')
                        .append(task.getName()).append(',')
                        .append(task.getStatus().toString()).append(',')
                        .append(task.getDescription()).append(',')
                        .append("-").append(',')
                        .append("-,\n");
                continue;
            }
        }
        return head + tasksTable + historyOfViews;
    }

    void fromString() {
        StringBuilder resultOfReading = new StringBuilder();
        try (FileReader fileReader = new FileReader(saveFile)) {
            while (fileReader.ready()) {
                resultOfReading.append((char) fileReader.read());
            }
        } catch (IOException e) {
            System.out.println("Создать свое исключение");
        }
        String value = resultOfReading.toString();
        if (!value.isBlank()) {
            String[] lines = value.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    String[] words = lines[i].split(",");
                    if (words[1].equals("Epic")) {
                        creationOfEpic(new Epic(Integer.parseInt(words[0]), words[2], words[4], new ArrayList<Integer>()));
                    } else if (words[1].equals("SubTask")) {
                        creationOfSubTask(new SubTask(Integer.parseInt(words[0]), words[2], words[4],
                                Integer.parseInt(words[5]))).setStatus(readStatus(words[3]));
                    } else {
                        creationOfTask(new Task(Integer.parseInt(words[0]), words[2], words[4]))
                                .setStatus(readStatus(words[3]));

                    }
                } else {
                    String[] words = lines[i + 1].split(",");
                    for (int j = 0; j < words.length; j++) {
                        if (getListOfAllTasks().get(Integer.parseInt(words[j])) != null) {
                            historyManager.add(getListOfAllTasks().get(Integer.parseInt(words[j])));
                        }
                    }
                    save();
                    return;
                }
            }
        }
    }

    Status readStatus(String status) {
        if (status.equals("NEW")) {
            return Status.NEW;
        } else if (status.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }
    }

//    static String toString(HistoryManager manager){
//
//    }

    //    static List<Integer> fromString(String value){
//
//    }
    @Override
    public Task creationOfTask(Task task) {
        uniqueTaskId++;
        Task newTask = new Task(uniqueTaskId, task.getName(), task.getDescription());
        newTask.setStatus(Status.NEW);
        taskList.put(uniqueTaskId, newTask);
        save();
        return newTask;
    }

    @Override
    public Epic creationOfEpic(Epic epic) {
        if (epic.getSubTaskIdList() != null) {
            uniqueTaskId++;
            Epic newEpic = new Epic(uniqueTaskId, epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
            newEpic.setStatus(Status.NEW);
            epicList.put(uniqueTaskId, newEpic);
            save();
            return newEpic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask creationOfSubTask(SubTask subTask) {
        if (epicList.containsKey(subTask.getEpicId())) {
            uniqueTaskId++;
            SubTask newSubTask = new SubTask(uniqueTaskId, subTask.getName(), subTask.getDescription(),
                    subTask.getEpicId()); // перенес запятую
            newSubTask.setStatus(Status.NEW);
            subTaskList.put(uniqueTaskId, newSubTask);
            Epic epicForUpdate = epicList.get(subTask.getEpicId());
            epicForUpdate.getSubTaskIdList().add(uniqueTaskId);
            save();
            return newSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Map<Integer, Task> getListOfAllTasks() {
        Map<Integer, Task> listOfAllTasks = new HashMap<>();
        listOfAllTasks.putAll(epicList);
        listOfAllTasks.putAll(taskList);
        listOfAllTasks.putAll(subTaskList);
        return listOfAllTasks;
    }

    @Override
    public Map<Integer, Task> getListOfTasks() {
        return taskList;
    }

    @Override
    public Map<Integer, Epic> getListOfEpics() {
        return epicList;
    }

    @Override
    public Map<Integer, SubTask> getListOfSubTasks() {
        return subTaskList;
    }

    @Override
    public Map<Integer, Task> deleteAllTasks() {
        epicList.clear();
        taskList.clear();
        subTaskList.clear();
        save();
        return getListOfAllTasks();
    }

    @Override
    public Map<Integer, Task> deleteTasks() {
        taskList.clear();
        save();
        return taskList;
    }

    @Override
    public Map<Integer, Epic> deleteEpics() {
        subTaskList.clear();
        epicList.clear();
        save();
        return epicList;
    }

    @Override
    public Map<Integer, SubTask> deleteSubTasks() {
        for (SubTask subTaskForDelete : subTaskList.values()) {
            Integer idOfEpicForClearItSubTasksList = subTaskForDelete.getEpicId();
            if (epicList.containsKey(idOfEpicForClearItSubTasksList)) {
                epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().clear();
                epicList.get(idOfEpicForClearItSubTasksList)
                        .setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
                save();
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
            save();
            return taskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epicList.get(id) != null) {
            historyManager.add(epicList.get(id));
            save();
            return epicList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (subTaskList.get(id) != null) {
            historyManager.add(subTaskList.get(id));
            save();
            return subTaskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Task updateTaskByNewTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            Task replacedTask = taskList.replace(task.getId(), task);
            save();
            return replacedTask;
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
                save();
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
            save();
            return replacedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Task deleteTaskById(Integer id) {
        if (taskList.containsKey(id)) {
            historyManager.remove(id);
            Task deletedTask = taskList.remove(id);
            save();
            return deletedTask;
        } else {
            return null;
        }
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        if (epicList.containsKey(id)) {
            for (Integer subTaskId : epicList.get(id).getSubTaskIdList()) {
                if (subTaskList.containsKey(subTaskId) && subTaskList.get(subTaskId).getEpicId() == id) {
                    historyManager.remove(subTaskId);
                    subTaskList.remove(subTaskId);
                } else {
                    return null;
                }
            }
            historyManager.remove(id);
            Epic deletedEpic = epicList.remove(id);
            save();
            return deletedEpic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask deleteSubTaskById(Integer id) {
        if (subTaskList.containsKey(id)) {
            Integer idOfEpicForClearItSubTasksList = subTaskList.get(id).getEpicId();
            if (epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().contains(id)) {
                historyManager.remove(id);
                epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().remove(id);
                epicList.get(idOfEpicForClearItSubTasksList)
                        .setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
            } else {
                return null;
            }
            SubTask deletedSubTask = subTaskList.remove(id);
            save();
            return deletedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getListOfSubTasksOfEpic(Integer epicId) {
        if (epicList.containsKey(epicId)) {
            return epicList.get(epicId).getSubTaskIdList();
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
