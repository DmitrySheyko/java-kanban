/*
 * Version 9.1
 * Tech requirements of sprint 6
 * Author: Sheyko Dmitry.
 */

import manager.FileBackedTasksManager;
import manager.Managers;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getDefault();

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
}


//
//    public static void printAllTasks(TaskManager taskManager) {
//        String[] array = taskManager.getListOfAllTasks().toString().split("},");
//        StringBuilder result = new StringBuilder("Список всех задач: \n");
//        for (String line : array) {
//            result.append(line).append(".\n");
//        }
//        System.out.println(result);
//    }
//
//    public static void printHistory(TaskManager taskManager) {
//        String[] array = taskManager.getHistory().toString().split("},");
//        StringBuilder result = new StringBuilder("История обращения к задачам: \n");
//        for (String line : array) {
//            result.append(line).append(".\n");
//        }
//        System.out.println(result);
//    }
//}
