/*
 * Version 7
 * Tech requirements of sprint 7
 * Author: Sheyko Dmitry.
 */

import manager.Managers;
import manager.TasksManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TasksManager tasksManager = Managers.getDefaultBackedManager();

        // Создаю задачу 1
        Task task1 = tasksManager.creationOfTask(new Task(100, "Тестирование 1",
                "Создать тестовый Task 1"));
        tasksManager.setTaskAndSubTaskStartDateTime(task1, "25.07.2022 20:40");
        tasksManager.setTaskAndSubTaskDuration(task1, 60);

        // Создаю задачу 2
        Task task2 = tasksManager.creationOfTask(new Task(100, "Тестирование 2",
                "Создать тестовый Task 2"));
        tasksManager.setTaskAndSubTaskStartDateTime(task2, "26.07.2022 12:30");
        tasksManager.setTaskAndSubTaskDuration(task2, 60);

        // Создаю задачу 3
        Task task3 = tasksManager.creationOfTask(new Task(100, "Тестирование 3",
                "Создать тестовый Task 3"));

        // Создаю задачу 4
        Task task4 = tasksManager.creationOfTask(new Task(100, "Тестирование 4",
                "Создать тестовый Task 4"));

        // Создаю задачу 5
        Task task5 = tasksManager.creationOfTask(new Task(100, "Тестирование 5",
                "Создать тестовый Task 5"));

        // Создаю Epic1 с двумя подзадачами
        Epic epic1 = tasksManager.creationOfEpic(new Epic(100, "Тестирование 3",
                "Создать тестовый Epic 6"
                , new ArrayList<>()));
        int idOfCreatedEpic1 = epic1.getId();

        // - подзадача 1 для Epic1
        SubTask subTask1OfEpic1 = tasksManager.creationOfSubTask(new SubTask(100, "Тестирование 4"
                , "Создать тестовый SubTask 1", idOfCreatedEpic1));
        tasksManager.setTaskAndSubTaskStartDateTime(subTask1OfEpic1, "25.07.2022 15:00");
        tasksManager.setTaskAndSubTaskDuration(subTask1OfEpic1, 60);

        //  - подзадача 2 для Epic1
        SubTask subTask2OfEpic1 = tasksManager.creationOfSubTask(new SubTask(10, "Тестирование 5"
                , "Создать тестовый SubTask 2", idOfCreatedEpic1));
        tasksManager.setTaskAndSubTaskStartDateTime(subTask2OfEpic1, "25.07.2022 13:00");
        tasksManager.setTaskAndSubTaskDuration(subTask2OfEpic1, 60);

        //  - подзадача 3 для Epic1
        SubTask subTask3OfEpic1 = tasksManager.creationOfSubTask(new SubTask(10, "Тестирование 6"
                , "Создать тестовый SubTask 3", idOfCreatedEpic1));
        tasksManager.setTaskAndSubTaskStartDateTime(subTask3OfEpic1, "25.07.2022 18:00");
        tasksManager.setTaskAndSubTaskDuration(subTask3OfEpic1, 60);

        //  меняю статус всех подзадач для Epic
        subTask1OfEpic1.setStatus(Status.DONE);
        subTask2OfEpic1.setStatus(Status.DONE);
        subTask3OfEpic1.setStatus(Status.DONE);

        //  Обращаюсь к задачам по их ID для заполнения имстории просмотров
        tasksManager.getSubTaskById(8);
        tasksManager.getSubTaskById(8);
        tasksManager.getEpicById(6);
        tasksManager.getEpicById(6);
        tasksManager.getTaskById(4);
        tasksManager.getTaskById(4);
        tasksManager.getTaskById(2);
        tasksManager.getTaskById(2);

        // печатаю перечень всех задач
        printAllTasks(tasksManager);

        // Печатаю историю просмотров
        printHistory(tasksManager);

        // вызываю метод getPrioritizedTasks()
        printPrioritizedTasks(tasksManager);
    }

    public static void printAllTasks(TasksManager tasksManager) {
        String[] array = tasksManager.getListOfAllTasks().toString().split("},");
        StringBuilder result = new StringBuilder("Список всех задач: \n");
        for (String line : array) {
            result.append(line).append(".\n");
        }
        System.out.println(result);
    }

    public static void printHistory(TasksManager tasksManager) {
        String[] array = tasksManager.getHistory().toString().split("},");
        StringBuilder result = new StringBuilder("История обращения к задачам: \n");
        for (String line : array) {
            result.append(line).append(".\n");
        }
        System.out.println(result);
    }

    public static void printPrioritizedTasks(TasksManager tasksManager) {
        System.out.println("Задачи в порядки срока выполнения:");
        for (Task task : tasksManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}



