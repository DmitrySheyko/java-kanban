/*
 * Version 6.0
 *
 * Author: Sheyko Dmitry
 */

import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
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

        // Создаю Epic1 с двумя подзадачами
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
        SubTask subTask2OfEpic2 = new SubTask(10, "Варка в кастрюле"
                , "Сварить овощи и мясо", idOfCreatedEpic1);
        System.out.println("Создана подзадача: \n" + taskManager.creationOfSubTask(subTask2OfEpic2) + "\n");

        // Создаю Epic2 с одной подзадачей
        ArrayList<Integer> subTasksListIdOfEpic2 = new ArrayList<>();
        Epic epic2 = new Epic(10, "Ремонт велосипеда", "Отремонтировать велосипед"
                , subTasksListIdOfEpic2);
        int idOfCreatedEpic2 = taskManager.creationOfEpic(epic2).getId();
        System.out.println("Создан Epic: \n" + taskManager.getListOfEpics().get(idOfCreatedEpic2) + "\n");

        //  - подзадача 1 для Epic2
        SubTask subTask1OfEpic2 = new SubTask(10, "Починка колеса", "Заклеить пробитую шину"
                , idOfCreatedEpic2);
        System.out.println("Создана подзадача: \n" + taskManager.creationOfSubTask(subTask1OfEpic2) + "\n");

        // Печатаю список всех задач
        printAllTasks(taskManager);

        // Получаю задачу по ID
        System.out.println("Получаю задачу по Id 1: \n" + taskManager.getTaskById(1));
        System.out.println("Получаю задачу по Id 2: \n" + taskManager.getTaskById(2));
        System.out.println("Получаю задачу по Id 3: \n" + taskManager.getEpicById(3));
        System.out.println("Получаю задачу по Id 4: \n" + taskManager.getSubTaskById(4));
        System.out.println("Получаю задачу по Id 5: \n" + taskManager.getSubTaskById(5));
        System.out.println("Получаю задачу по Id 6: \n" + taskManager.getEpicById(6));
        System.out.println("Получаю задачу по Id 7: \n" + taskManager.getSubTaskById(7));
        System.out.println("Получаю задачу по Id 1: \n" + taskManager.getTaskById(1));
        System.out.println("Получаю задачу по Id 2: \n" + taskManager.getTaskById(2));
        System.out.println("Получаю задачу по Id 3: \n" + taskManager.getEpicById(3));
        System.out.println("Получаю задачу по Id 4: \n" + taskManager.getSubTaskById(4));
        System.out.println("Получаю задачу по Id 5: \n" + taskManager.getSubTaskById(5));
        System.out.println("Получаю задачу по Id 6: \n" + taskManager.getEpicById(6));
        System.out.println("Получаю задачу по Id 7: \n" + taskManager.getSubTaskById(7) + "\n");

        // Обновляю задачу id 1 обновленной задачей
        Task task3 = new Task(1, "Покупка носков (обновлено)", "Купить черные носки (обновлено)");
        task3.setStatus(Status.DONE);
        taskManager.updateTaskByNewTask(task3);

        // Обновляю подзадачу id 5 Эпика 1 обновленной подзадачей
        SubTask subTask3OfEpic1 = new SubTask(5, "Варка в кастрюле(обновлено)"
                , "Сварить овощи и мясо (обновлено)", 3);
        subTask3OfEpic1.setStatus(Status.DONE);
        taskManager.updateSubTaskByNewSubTask(subTask3OfEpic1);

        // Печатаю список всех задач для проверки внесенных изменений
        printAllTasks(taskManager);

        // Удаляю одну задачу id 2
        taskManager.deleteTaskById(2);

        //Удаляю один эпик id 6
        taskManager.deleteEpicById(6);

        // Печатаю список всех задач для проверки удаления
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
