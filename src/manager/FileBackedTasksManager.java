package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = Managers
                .loadFromFile(new File("java-kanban/Save/Save.csv"));

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

    public void checkSaveFile() {
        File directory = new File(saveFile.getParent());
        try {
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new ManagerSaveException("Ошибка при создании директории для сохранения");
                }
            }
            if (!saveFile.exists()) {
                try {
                    if (!saveFile.createNewFile()) {
                        throw new ManagerSaveException("Ошибка при создании файла для сохранения");
                    }
                } catch (IOException e) {
                    throw new ManagerSaveException("Ошибка записи файла сохранения");
                }
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveToFile() {
        checkSaveFile();
        String head = "id,type,name,status,description,epic,subtasks\n";
        StringBuilder tasksInString = new StringBuilder();
        for (Task task : getListOfAllTasks().values()) {
            tasksInString.append(tasksToString(task));
        }
        try {
            try (FileWriter fileWriter = new FileWriter(saveFile)) {
                fileWriter.write(head + tasksInString + historyToString());
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи файла сохранения");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public void readSaveFromFile() {
        checkSaveFile();
        StringBuilder resultOfReading = new StringBuilder();
        try {
            try (FileReader fileReader = new FileReader(saveFile)) {

                while (fileReader.ready()) {
                    resultOfReading.append((char) fileReader.read());
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка чтения файла сохранения");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        String value = resultOfReading.toString();
        if (!value.isBlank()) {
            String[] lines = value.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    tasksFromString(lines[i]);
                } else {
                    historyFromString(lines[i + 1]);
                }
            }
            saveToFile();
        }
    }

    public String tasksToString(Task task) {
        StringBuilder stringOfTask = new StringBuilder();
        String classOfTask = task.getClass().getSimpleName();
        switch (classOfTask) {
            case "Task" -> stringOfTask.append(task.getId()).append(',')
                    .append("Task").append(',')
                    .append(task.getName()).append(',')
                    .append(task.getStatus().toString()).append(',')
                    .append(task.getDescription()).append(',')
                    .append("-").append(',')
                    .append("-,\n");
            case "Epic" -> {
                Epic epic = (Epic) task;
                StringBuilder subTasks = new StringBuilder(epic.getSubTaskIdList().toString()
                        .replace(',', '/'));
                subTasks.deleteCharAt(0).deleteCharAt(subTasks.length() - 1);
                stringOfTask.append(epic.getId()).append(',')
                        .append("Epic").append(',')
                        .append(epic.getName()).append(',')
                        .append(epic.getStatus().toString()).append(',')
                        .append(epic.getDescription()).append(',')
                        .append("-").append(",")
                        .append(subTasks).append(",\n");
            }
            case "SubTask" -> {
                SubTask subTask = (SubTask) task;
                stringOfTask.append(subTask.getId()).append(',')
                        .append("SubTask").append(',')
                        .append(subTask.getName()).append(',')
                        .append(subTask.getStatus().toString()).append(',')
                        .append(subTask.getDescription()).append(',')
                        .append(subTask.getEpicId()).append(',')
                        .append("-,\n");
            }
        }
        return stringOfTask.toString();
    }

    public void tasksFromString(String stringOfTask) {
        String[] words = stringOfTask.split(",");
        String classOfTask = words[1];
        switch (classOfTask) {
            case "Task" -> creationOfTask(new Task(Integer.parseInt(words[0]), words[2], words[4]))
                    .setStatus(readStatus(words[3]));
            case "Epic" ->
                    creationOfEpic(new Epic(Integer.parseInt(words[0]), words[2], words[4], new ArrayList<Integer>()));
            case "SubTask" -> creationOfSubTask(new SubTask(Integer.parseInt(words[0]), words[2], words[4],
                    Integer.parseInt(words[5]))).setStatus(readStatus(words[3]));
        }
    }

    public Status readStatus(String status) {
        if (status.equals("NEW")) {
            return Status.NEW;
        } else if (status.equals("IN_PROGRESS")) {
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

    @Override
    public Task creationOfTask(Task task) {
        uniqueTaskId++;
        Task newTask = new Task(uniqueTaskId, task.getName(), task.getDescription());
        newTask.setStatus(Status.NEW);
        taskList.put(uniqueTaskId, newTask);
        saveToFile();
        return newTask;
    }

    @Override
    public Epic creationOfEpic(Epic epic) {
        if (epic.getSubTaskIdList() != null) {
            uniqueTaskId++;
            Epic newEpic = new Epic(uniqueTaskId, epic.getName(), epic.getDescription(), epic.getSubTaskIdList());
            newEpic.setStatus(Status.NEW);
            epicList.put(uniqueTaskId, newEpic);
            saveToFile();
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
                    subTask.getEpicId());
            newSubTask.setStatus(Status.NEW);
            subTaskList.put(uniqueTaskId, newSubTask);
            Epic epicForUpdate = epicList.get(subTask.getEpicId());
            epicForUpdate.getSubTaskIdList().add(uniqueTaskId);
            saveToFile();
            return newSubTask;
        } else {
            return null;
        }
    }

    @Override
    public Map<Integer, Task> deleteAllTasks() {
        epicList.clear();
        taskList.clear();
        subTaskList.clear();
        saveToFile();
        return getListOfAllTasks();
    }

    @Override
    public Map<Integer, Task> deleteTasks() {
        taskList.clear();
        saveToFile();
        return taskList;
    }

    @Override
    public Map<Integer, Epic> deleteEpics() {
        subTaskList.clear();
        epicList.clear();
        saveToFile();
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
                saveToFile();
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
            saveToFile();
            return taskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epicList.get(id) != null) {
            historyManager.add(epicList.get(id));
            saveToFile();
            return epicList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (subTaskList.get(id) != null) {
            historyManager.add(subTaskList.get(id));
            saveToFile();
            return subTaskList.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Task updateTaskByNewTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            Task replacedTask = taskList.replace(task.getId(), task);
            saveToFile();
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
                saveToFile();
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
            saveToFile();
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
            saveToFile();
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
            saveToFile();
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
            saveToFile();
            return deletedSubTask;
        } else {
            return null;
        }
    }
}
