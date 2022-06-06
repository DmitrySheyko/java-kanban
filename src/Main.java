/*
 * Version 8.0
 *
 * Author: Sheyko Dmitry
 */

import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создаю задачу 1
        Task task1 = new Task(10, "Покупка носков", "Купить черные носки");
        System.out.println("Создана задача: \n" + taskManager.creationOfTask(task1) + "\n");

        // Создаю задачу 2
        Task task2 = new Task(10, "Продажа машины", "Продать машину");
        System.out.println("Создана задача: \n" + taskManager.creationOfTask(task2) + "\n");

        // Создаю Epic1 с тремя подзадачами
        ArrayList<Integer> subTasksListIdOfEpic1 = new ArrayList<>();
        Epic epic1 = new Epic(10, "Приготовление борща", "Сварить много борща"
                , subTasksListIdOfEpic1);
        int idOfCreatedEpic1 = taskManager.creationOfEpic(epic1).getId();
        System.out.println("Создан Epic: \n" + taskManager.getListOfEpics().get(idOfCreatedEpic1) + "\n");

        // - подзадача 1 для Epic1
        SubTask subTask1OfEpic1 = new SubTask(10, "Подготовка ингредиентов"
                , "Подготовить овощи и мясо", idOfCreatedEpic1);
        System.out.println("Создана подзадача: \n" + taskManager.creationOfSubTask(subTask1OfEpic1) + "\n");

        //  - подзадача 2 для Epic1
        SubTask subTask2OfEpic1 = new SubTask(10, "Варка в кастрюле"
                , "Сварить овощи и мясо", idOfCreatedEpic1);
        System.out.println("Создана подзадача: \n" + taskManager.creationOfSubTask(subTask2OfEpic1) + "\n");

        //  - подзадача 3 для Epic1
        SubTask subTask3OfEpic1 = new SubTask(10, "Добавление соли"
                , "Посолить по вкусу", idOfCreatedEpic1);
        System.out.println("Создана подзадача: \n" + taskManager.creationOfSubTask(subTask3OfEpic1) + "\n");

        // Создаю Epic2 без подзадач
        ArrayList<Integer> subTasksListIdOfEpic2 = new ArrayList<>();
        Epic epic2 = new Epic(10, "Ремонт велосипеда", "Отремонтировать велосипед"
                , subTasksListIdOfEpic2);
        int idOfCreatedEpic2 = taskManager.creationOfEpic(epic2).getId();
        System.out.println("Создан Epic: \n" + taskManager.getListOfEpics().get(idOfCreatedEpic2) + "\n");

        // Печатаю список всех задач
        printAllTasks(taskManager);

        // Обращаюсь к задачам по их ID
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        taskManager.getEpicById(7);

        // Печатаю историю вызова задач
        printHistory(taskManager);

        // Удаляю адачу ID 1
        taskManager.deleteTaskById(1);

        // Удаляю эпик id 3. Подзадачи 4, 5 и 6 удалятся автоматически
        taskManager.deleteEpicById(3);

        // Печатаю список всех задач
        printAllTasks(taskManager);

        // Печатаю историю вызова задач
        printHistory(taskManager);

    }

    public static void printAllTasks(TaskManager taskManager) {
        String[] array = taskManager.getListOfAllTasks().toString().split("},");
        StringBuilder result = new StringBuilder("Список всех задач: \n");
        for (String line : array) {
            result.append(line).append(".\n");
        }
        System.out.println(result);
    }

    public static void printHistory(TaskManager taskManager) {
        String[] array = taskManager.getHistory().toString().split("},");
        StringBuilder result = new StringBuilder("История обращения к задачам: \n");
        for (String line : array) {
            result.append(line).append(".\n");
        }
        System.out.println(result);
    }
}
