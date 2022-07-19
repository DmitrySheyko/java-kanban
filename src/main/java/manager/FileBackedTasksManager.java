package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {
    private File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
        loadFromSave();
    }

    public FileBackedTasksManager() {
    }

    public void checkSaveFile() {
        File directory = new File(saveFile.getParent());
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new ManagerSaveException("Ошибка при создании директории для сохранения");
            }
        }
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при создании файла сохранения " + e.getMessage());
            }
        }
    }

    public void save() {
        checkSaveFile();
        String head = "id,type,name,status,description,epic,subtasks,Date&Time,Duration\n";
        StringBuilder tasksInString = new StringBuilder();
        for (Task task : getListOfAllTasks().values()) {
            tasksInString.append(tasksToString(task));
        }
        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            fileWriter.write(head + tasksInString + historyToString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла сохранения");
        }
    }

    public void loadFromSave() {
        checkSaveFile();
        StringBuilder resultOfReading = new StringBuilder();
        try (FileReader fileReader = new FileReader(saveFile)) {
            while (fileReader.ready()) {
                resultOfReading.append((char) fileReader.read());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла сохранения");
        }
        String value = resultOfReading.toString();
        if (!value.isBlank()) {
            String[] lines = value.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    tasksFromString(lines[i]);
                } else {
                    historyFromString(lines[i + 1]);
                    break;
                }
            }
        }
    }

    public String tasksToString(Task task) {
        StringBuilder stringOfTask = new StringBuilder();
        switch (task.getTypeTask()) {
            case TASK -> stringOfTask.append(task.getId()).append(',')
                    .append(TypeTask.TASK).append(',')
                    .append(task.getName()).append(',')
                    .append(task.getStatus().toString()).append(',')
                    .append(task.getDescription()).append(',')
                    .append("-").append(',').append("-").append(',')
                    .append(task.getStartTime() != null ? task.getStartTime().format(dateTimeFormatter) : "")
                    .append(',').append(task.getDuration()).append("\n");
            case EPIC -> {
                Epic epic = (Epic) task;
                StringBuilder subTasks =
                        new StringBuilder(epic.getSubTaskIdList().toString().replace(',', '/'));
                subTasks.deleteCharAt(0).deleteCharAt(subTasks.length() - 1);
                stringOfTask.append(epic.getId()).append(',')
                        .append(TypeTask.EPIC).append(',')
                        .append(epic.getName()).append(',')
                        .append(epic.getStatus().toString()).append(',')
                        .append(epic.getDescription()).append(',')
                        .append("-").append(",").append(subTasks).append(",")
                        .append(epic.getStartTime() != null ? epic.getStartTime().format(dateTimeFormatter) : "")
                        .append(',').append(epic.getDuration()).append("\n");
            }
            case SUBTASK -> {
                SubTask subTask = (SubTask) task;
                stringOfTask.append(subTask.getId()).append(',')
                        .append(TypeTask.SUBTASK).append(',')
                        .append(subTask.getName()).append(',')
                        .append(subTask.getStatus().toString()).append(',')
                        .append(subTask.getDescription()).append(',')
                        .append(subTask.getEpicId()).append(',').append("-").append(",")
                        .append(subTask.getStartTime() != null ? subTask.getStartTime().format(dateTimeFormatter) : "")
                        .append(',').append(subTask.getDuration()).append("\n");
            }
        }
        return stringOfTask.toString();
    }

    public void tasksFromString(String stringOfTask) {
        String[] words = stringOfTask.split(",");
        TypeTask typeTasks = TypeTask.valueOf(words[1]);
        switch (typeTasks) {
            case TASK -> {
                Task task = creationOfTaskFromSaveFile(new Task(Integer.parseInt(words[0]), words[2], words[4]),
                        readStatus(words[3]));
                if (task != null) {
                    if (!words[7].equals("")) {
                        setTaskAndSubTaskStartDateTime(task, words[7]);
                    }
                    task.setDuration(Integer.parseInt(words[8]));
                }
            }
            case EPIC -> {
                Epic epic = creationOfEpicFromSaveFile(new Epic(Integer.parseInt(words[0]), words[2], words[4],
                        new ArrayList<>()));
                if (epic != null) {
                    if (!words[7].equals("")) {
                        epic.setStartTime(LocalDateTime.parse(words[7], dateTimeFormatter));
                    }
                    epic.setDuration(Integer.parseInt(words[8]));
                }
            }
            case SUBTASK -> {
                SubTask subTask = creationOfSubTaskFromSaveFile(new SubTask(Integer.parseInt(words[0]), words[2],
                        words[4], Integer.parseInt(words[5])), readStatus(words[3]));
                if (subTask != null) {
                    if (!words[7].equals("")) {
                        setTaskAndSubTaskStartDateTime(subTask, words[7]);
                    }
                    subTask.setDuration(Integer.parseInt(words[8]));
                    updateSubTaskByNewSubTask(subTask);
                }
            }
        }
    }

    public Status readStatus(String status) {
        if (status.equals(Status.NEW.toString())) {
            return Status.NEW;
        } else if (status.equals(Status.IN_PROGRESS.toString())) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }
    }

    public String historyToString() {
        StringBuilder historyOfViews = new StringBuilder("\n");
        for (Task task : getHistory()) {
            historyOfViews.append(task.getId()).append(',');
        }
        historyOfViews.deleteCharAt(historyOfViews.length() - 1);
        return historyOfViews.toString();
    }

    public void historyFromString(String stringOfHistory) {
        String[] words = stringOfHistory.split(",");
        for (String word : words) {
            if (getListOfAllTasks().get(Integer.parseInt(word)) != null) {
                historyManager.add(getListOfAllTasks().get(Integer.parseInt(word)));
            }
        }
    }

    public Task creationOfTaskFromSaveFile(Task task, Status taskStatus) {
        Task loadedTask = new Task(task.getId(), task.getName(), task.getDescription());
        loadedTask.setStatus(taskStatus);
        taskList.put(loadedTask.getId(), loadedTask);
        save();
        return loadedTask;
    }

    public Epic creationOfEpicFromSaveFile(Epic epic) {
        Epic loadedEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
        loadedEpic.setStatus(checkEpicStatus(loadedEpic.getSubTaskIdList()));
        epicList.put(loadedEpic.getId(), loadedEpic);
        save();
        return loadedEpic;
    }

    public SubTask creationOfSubTaskFromSaveFile(SubTask subTask, Status subTaskStatus) {
        if (epicList.containsKey(subTask.getEpicId())) {
            SubTask loadedSubTask = new SubTask(subTask.getId(), subTask.getName(), subTask.getDescription(),
                    subTask.getEpicId());
            loadedSubTask.setStatus(subTaskStatus);
            subTaskList.put(loadedSubTask.getId(), loadedSubTask);
            Epic epicForUpdate = epicList.get(subTask.getEpicId());
            epicForUpdate.getSubTaskIdList().add(loadedSubTask.getId());
            updateEpicByNewEpic(epicForUpdate);
            save();
            return loadedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Task creationOfTask(Task task) {
        Task newTask = new Task(getUniqueTaskId(), task.getName(), task.getDescription());
        newTask.setStatus(Status.NEW);
        if (task.getStartTime() != null) {
            setTaskAndSubTaskStartDateTime(newTask, task.getStartTime().format(dateTimeFormatter));
            setTaskAndSubTaskDuration(newTask, task.getDuration());
        }
        taskList.put(newTask.getId(), newTask);
        save();
        return newTask;
    }

    @Override
    public Epic creationOfEpic(Epic epic) {
        if (epic.getSubTaskIdList() != null) {
            Epic newEpic = new Epic(getUniqueTaskId(), epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
            newEpic.setStatus(Status.NEW);
            epicList.put(newEpic.getId(), newEpic);
            save();
            return newEpic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask creationOfSubTask(SubTask subTask) {
        if (epicList.containsKey(subTask.getEpicId())) {
            SubTask newSubTask = new SubTask(getUniqueTaskId(), subTask.getName(), subTask.getDescription(),
                    subTask.getEpicId());
            newSubTask.setStatus(Status.NEW);
            if (subTask.getStartTime() != null) {
                setTaskAndSubTaskStartDateTime(newSubTask, subTask.getStartTime().format(dateTimeFormatter));
                setTaskAndSubTaskDuration(newSubTask, subTask.getDuration());
            }
            subTaskList.put(newSubTask.getId(), newSubTask);
            Epic epicForUpdate = epicList.get(subTask.getEpicId());
            epicForUpdate.getSubTaskIdList().add(newSubTask.getId());
            updateEpicByNewEpic(epicForUpdate);
            save();
            return newSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Map<Integer, Task> deleteAllTasks() {
        for (Integer id : getListOfAllTasks().keySet()) {
            historyManager.remove(id);
        }
        epicList.clear();
        taskList.clear();
        subTaskList.clear();
        uniqueTaskId = 1;
        save();
        return getListOfAllTasks();
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
            Epic replacedEpic = epicList.replace(epic.getId(), epic);
            epic.setStatus(checkEpicStatus(epic.getSubTaskIdList()));
            save();
            return replacedEpic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask updateSubTaskByNewSubTask(SubTask subTask) {
        if (subTaskList.containsKey(subTask.getId()) && epicList.containsKey(subTask.getEpicId())) {
            Epic epicForCheckStatus = epicList.get(subTask.getEpicId());
            if (epicForCheckStatus.getSubTaskIdList().contains(subTask.getId())) {
                SubTask replacedSubTask = subTaskList.replace(subTask.getId(), subTask);
                epicForCheckStatus.setStatus(checkEpicStatus(epicForCheckStatus.getSubTaskIdList()));
                setEpicStartDateTime(epicForCheckStatus.getId());
                setEpicDuration(epicForCheckStatus.getId());
                save();
                return replacedSubTask;
            } else {
                return null;
            }
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
                epicList.get(idOfEpicForClearItSubTasksList).
                        setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
                setEpicStartDateTime(idOfEpicForClearItSubTasksList);
                setEpicDuration(idOfEpicForClearItSubTasksList);
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
}
