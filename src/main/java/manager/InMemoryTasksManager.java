package manager;

import exceptions.ManagerDateTimeException;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTasksManager implements TasksManager {
    protected int uniqueTaskId;
    protected final Map<Integer, Task> taskList;
    protected final Map<Integer, SubTask> subTaskList;
    protected final Map<Integer, Epic> epicList;
    protected final HistoryManager historyManager;
    protected final DateTimeFormatter dateTimeFormatter;

    public InMemoryTasksManager() {
        this.uniqueTaskId = 1;
        this.taskList = new HashMap<>();
        this.subTaskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    @Override
    public int getUniqueTaskId() {
        while (getListOfAllTasks().containsKey(uniqueTaskId)) {
            uniqueTaskId++;
        }
        return uniqueTaskId;
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
        return newTask;
    }

    @Override
    public Epic creationOfEpic(Epic epic) {
        if (epic.getSubTaskIdList() != null) {
            Epic newEpic = new Epic(getUniqueTaskId(), epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
            newEpic.setStatus(Status.NEW);
            epicList.put(newEpic.getId(), newEpic);
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
            return newSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Map<Integer, Task> getListOfAllTasks() {
        SortedMap<Integer, Task> listOfAllTasks = new TreeMap<>();
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
        for (Integer id : getListOfAllTasks().keySet()) {
            historyManager.remove(id);
        }
        epicList.clear();
        taskList.clear();
        subTaskList.clear();
        uniqueTaskId = 1;
        return getListOfAllTasks();
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
            epic.setStatus(checkEpicStatus(epic.getSubTaskIdList()));
            return epicList.replace(epic.getId(), epic);
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
                    historyManager.remove(subTaskId);
                    subTaskList.remove(subTaskId);
                } else {
                    return null;
                }
            }
            historyManager.remove(id);
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
                historyManager.remove(id);
                epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList().remove(id);
                epicList.get(idOfEpicForClearItSubTasksList)
                        .setStatus(checkEpicStatus(epicList.get(idOfEpicForClearItSubTasksList).getSubTaskIdList()));
                setEpicStartDateTime(idOfEpicForClearItSubTasksList);
                setEpicDuration(idOfEpicForClearItSubTasksList);
            } else {
                return null;
            }
            return subTaskList.remove(id);
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

    protected Status checkEpicStatus(List<Integer> subTaskIdList) {
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

    @Override
    public void setTaskAndSubTaskStartDateTime(Task task, String startDateTime) {
        if (task != null & startDateTime != null) {
            LocalDateTime dateTimeFromSting = LocalDateTime.parse(startDateTime, dateTimeFormatter);
            if (!dateTimeFromSting.isAfter(LocalDateTime.now())) {
                try {
                    throw new ManagerDateTimeException("Вы указали прошедшее время");
                } catch (ManagerDateTimeException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (checkIsStartTimeFree(dateTimeFromSting)) {
                switch (task.getTypeTask()) {
                    case TASK -> {
                        task.setStartTime(dateTimeFromSting);
                        updateTaskByNewTask(task);
                    }
                    case SUBTASK -> {
                        SubTask subTask = (SubTask) task;
                        subTask.setStartTime(dateTimeFromSting);
                        updateSubTaskByNewSubTask(subTask);
                    }
                    default -> {
                        try {
                            throw new ManagerDateTimeException("Время для задач типа Epic определяется временем " +
                                    "их подзадач");
                        } catch (ManagerDateTimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setTaskAndSubTaskDuration(Task task, int durationInMinutes) {
        if (task != null) {
            if (durationInMinutes < 1) {
                try {
                    throw new ManagerDateTimeException("Длительность задачи должна бать больше 0 минут");
                } catch (ManagerDateTimeException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (getStartDateTime(task) == null) {
                try {
                    throw new ManagerDateTimeException("Сперва установите время начала задачи");
                } catch (ManagerDateTimeException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (checkIsDurationFree(task.getStartTime(), durationInMinutes)) {
                switch (task.getTypeTask()) {
                    case TASK -> {
                        task.setDuration(durationInMinutes);
                        updateTaskByNewTask(task);
                    }
                    case SUBTASK -> {
                        SubTask subTask = (SubTask) task;
                        subTask.setDuration(durationInMinutes);
                        updateSubTaskByNewSubTask(subTask);
                    }
                    default -> {
                        try {
                            throw new ManagerDateTimeException("Время для задач типа Epic определяется временем " +
                                    "их подзадач");
                        } catch (ManagerDateTimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setEpicStartDateTime(int epicId) {
        if (epicList.containsKey(epicId)) {
            Epic epicForUpdate = epicList.get(epicId);
            if (epicForUpdate.getSubTaskIdList().size() == 1) {
                LocalDateTime dateTime = subTaskList.get(epicForUpdate.getSubTaskIdList().get(0)).getStartTime();
                epicForUpdate.setStartTime(dateTime);
            } else if (epicForUpdate.getSubTaskIdList().size() > 1) {
                Optional<LocalDateTime> optionalEarliestTime = epicForUpdate.getSubTaskIdList().stream()
                        .filter(id -> subTaskList.get(id).getStartTime() != null)
                        .map(id -> subTaskList.get(id).getStartTime())
                        .reduce((startTime1, startTime2) -> startTime1.isBefore(startTime2) ? startTime1 : startTime2);
                if (optionalEarliestTime.isPresent()) {
                    epicForUpdate.setStartTime(optionalEarliestTime.get());
                } else {
                    epicForUpdate.setStartTime(null);
                }
                updateEpicByNewEpic(epicForUpdate);
            }
        }
    }

    @Override
    public void setEpicDuration(int epicId) {
        int duration = 0;
        if (epicList.containsKey(epicId)) {
            Epic epicForUpdate = epicList.get(epicId);
            List<Integer> epicSubTaskList = epicForUpdate.getSubTaskIdList();
            if (epicSubTaskList.size() == 0) {
                epicForUpdate.setDuration(duration);
            } else if (epicSubTaskList.size() == 1) {
                duration = subTaskList.get(epicSubTaskList.get(0)).getDuration();
                epicForUpdate.setDuration(duration);
            } else {
                if (epicSubTaskList.stream().anyMatch(id -> (subTaskList.get(id).getStartTime() == null) ||
                        (subTaskList.get(id).getDuration() == 0))) {
                    epicForUpdate.setDuration(duration);
                } else {
                    SubTask lastSubTask = subTaskList.get(epicSubTaskList.get(0));
                    for (int i = 1; i < epicSubTaskList.size(); i++) {
                        if (subTaskList.get(epicSubTaskList.get(i)).getStartTime().
                                isAfter(lastSubTask.getStartTime())) {
                            lastSubTask = subTaskList.get(epicSubTaskList.get(i));
                        }
                    }
                    LocalDateTime endDateTimeOfLastSubTask = lastSubTask.getStartTime().
                            plus(Duration.ofMinutes(lastSubTask.getDuration()));
                    Duration epicDuration = Duration.between(epicForUpdate.getStartTime(), endDateTimeOfLastSubTask);
                    epicForUpdate.setDuration((int) epicDuration.toMinutes());
                    updateEpicByNewEpic(epicForUpdate);
                }
            }
        }
    }

    @Override
    public LocalDateTime getStartDateTime(Task task) {
        return task.getStartTime();
    }

    @Override
    public int getTaskDuration(Task task) {
        return task.getDuration();
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> tasksWithoutStartDateTime = new ArrayList<>();
        List<Task> prioritizedListOfAllTasks = getListOfAllTasks().values().stream().peek((Task task) -> {
            if (task.getStartTime() == null) {
                tasksWithoutStartDateTime.add(task);
            }
        }).filter(task -> task.getStartTime() != null).sorted((Task task1, Task task2) -> {
            if (task1.getStartTime().isEqual(task2.getStartTime())) {
                return 0;
            } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            } else {
                return 1;
            }
        }).collect(Collectors.toList());
        prioritizedListOfAllTasks.addAll(tasksWithoutStartDateTime);
        return prioritizedListOfAllTasks;
    }

    public boolean checkIsStartTimeFree(LocalDateTime startTime) {
        boolean isStartTimeFree = true;
        if (getListOfAllTasks().size() == 1) {
            return isStartTimeFree;
        } else {
            for (Task task : getListOfAllTasks().values()) {
                if ((!task.getTypeTask().equals(TypeTask.EPIC)) & getStartDateTime(task) != null) {
                    if (getTaskDuration(task) == 0) {
                        if (startTime.isEqual(getStartDateTime(task))) {
                            isStartTimeFree = false;
                            try {
                                throw new ManagerDateTimeException("Время начала задачи пересекается с ранее " +
                                        "запланированной задачей " + task.getId());
                            } catch (ManagerDateTimeException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } else {
                        if ((startTime.isEqual(getStartDateTime(task)) || (startTime.isAfter(getStartDateTime(task))) &
                                ((startTime.isEqual(getStartDateTime(task).
                                        plus(Duration.ofMinutes(task.getDuration())))) ||
                                        startTime.isBefore(getStartDateTime(task).
                                                plus(Duration.ofMinutes(task.getDuration())))))) {
                            isStartTimeFree = false;
                            try {
                                throw new ManagerDateTimeException("Время начала задачи пересекается с ранее " +
                                        "запланированной задачей " + task.getId());
                            } catch (ManagerDateTimeException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            }
            return isStartTimeFree;
        }
    }

    public boolean checkIsDurationFree(LocalDateTime startTime, int duration) {
        boolean isDurationFree = true;
        if (getListOfAllTasks().size() == 1) {
            return isDurationFree;
        } else {
            for (Task task : getListOfAllTasks().values()) {
                if ((!task.getTypeTask().equals(TypeTask.EPIC)) & getStartDateTime(task) != null &
                        getTaskDuration(task) != 0) {
                    if (startTime.isBefore(task.getStartTime().plus(Duration.ofMinutes(task.getDuration()))) &
                            startTime.plus(Duration.ofMinutes(duration)).isAfter(task.getStartTime())) {
                        isDurationFree = false;
                        try {
                            throw new ManagerDateTimeException("Время выполнения задачи пересекается с ранее " +
                                    "запланированной задачей " + task.getId());
                        } catch (ManagerDateTimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
            return isDurationFree;
        }
    }
}
