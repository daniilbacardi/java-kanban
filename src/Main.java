import manager.Managers;
import manager.TaskManager;
import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task taskOne = new Task(
                "Task 1",
                "Описание Task 1",
                LocalDateTime.of(2022,12,10,12,0),
                30);
        Task taskTwo = new Task(
                "Task 2",
                "Описание Task 2",
                LocalDateTime.of(2022,12,10,13,0),
                30);
        Task taskThree = new Task(
                "Task 3",
                "Описание Task 3",
                LocalDateTime.of(2022,12,10,14,0),
                45);

        manager.createNewTask(taskOne);
        manager.createNewTask(taskTwo);
        manager.createNewTask(taskThree);

         Epic epicOne = new Epic(
                 "Epic 1",
                 "Описание Epic 1",
                 null,
                 0);
        Epic epicTwo = new Epic(
                "Epic 2",
                "Описание Epic 2",
                null,
                0);
        Epic epicThree = new Epic(
                "Epic 3",
                "Описание Epic 3",
                null,
                0);

        manager.createNewEpic(epicOne);
        manager.createNewEpic(epicTwo);
        manager.createNewEpic(epicThree);

        Subtask subtaskOneEpicOne = new Subtask(
                "Subtask 1 Epic 1",
                "Описание Subtask 1 Epic 1",
                LocalDateTime.of(2022,12,10,12,0),
                60,
                epicOne.getId());
        Subtask subtaskTwoEpicOne = new Subtask(
                "Subtask 2 Epic 1",
                "Описание Subtask 2 Epic 1",
                LocalDateTime.of(2022,12,10,14,0),
                60,
                epicOne.getId());
        Subtask subtaskTreeEpicOne = new Subtask(
                "Subtask 3 Epic 1",
                "Описание Subtask 3 Epic 1",
                LocalDateTime.of(2022,12,10,15,0),
                60,
                epicOne.getId());
        Subtask subtaskOneEpicTwo = new Subtask(
                "Subtask 1 Epic 2",
                "Описание Subtask 1 Epic 2",
                LocalDateTime.of(2022,12,10,12,0),
                60,
                epicTwo.getId());
        Subtask subtaskTwoEpicTwo = new Subtask(
                "Subtask 2 Epic 2",
                "Описание Subtask 2 Epic 2",
                LocalDateTime.of(2022,12,10,14,0),
                60,
                epicTwo.getId());

        manager.createNewSubtask(subtaskOneEpicOne);
        manager.createNewSubtask(subtaskTwoEpicOne);
        manager.createNewSubtask(subtaskTreeEpicOne);
        manager.createNewSubtask(subtaskOneEpicTwo);
        manager.createNewSubtask(subtaskTwoEpicTwo);

        manager.getTaskById(0);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getSubtaskById(9);
        manager.getTaskById(0);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getEpicById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getSubtaskById(8);
        manager.getSubtaskById(9);
        manager.getSubtaskById(10);

        System.out.println("\nУникальные просмотры: " + manager.getHistory());

        manager.deleteTaskById(0);

        System.out.println("\nУникальные просмотры: (удалили Task 1 с id=0) " + manager.getHistory());

        manager.deleteSubtaskById(8);

        System.out.println("\nУникальные просмотры: (удалили SubTask 3 из Epic 1 с id=8) "
                + manager.getHistory());

        manager.deleteEpicById(4);

        System.out.println("\nУникальные просмотры: (удалили Epic 2 с id=4 и SubTask-и с id=9, id=10) "
                + manager.getHistory());
    }
}