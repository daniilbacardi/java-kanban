import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import manager.Manager;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("Тест: Task сущность\n");

        Task taskOne = new Task("Task 1", "Описание Task 1");
        Task taskTwo = new Task("Task 2", "Описание Task 2");

        manager.createNewTask(taskOne);
        manager.createNewTask(taskTwo);

        System.out.println("Данные по добавленной Task 1 " + manager.getTaskById(0));
        System.out.println("Данные по добавленным Task 1 и Task 2 " + manager.getAllTasks());

        Task updatedTaskOne = new Task(
                taskOne.getId(),
                "Task 1",
                "Обновленное описание Task 1",
                "IN_PROGRESS");
        Task updatedTaskTwo = new Task(
                taskTwo.getId(),
                "Task 2",
                "Обновленное описание Task 2",
                "DONE");

        manager.updateTask(updatedTaskOne);
        manager.updateTask(updatedTaskTwo);

        System.out.println("Данные по обновленным Task 1 (статус IN_PROGRESS) и Task 2 (статус DONE) "
                + manager.getAllTasks());

        manager.deleteTaskById(0);

        System.out.println("Task 1 удалена, осталась только Task 2 " + manager.getAllTasks());

        manager.deleteAllTasks();

        System.out.println("Все Task-и удалены " + manager.getAllTasks() + "\n");

        System.out.println("Тест: Epic и Subtask сущности\n");

        Epic epicOne = new Epic("Epic 1", "Описание Epic 1");
        Epic epicTwo = new Epic("Epic 2", "Описание Epic 2");

        manager.createNewEpic(epicOne);
        manager.createNewEpic(epicTwo);

        System.out.println("Данные по добавленным Epic 1 и Epic 2 " + manager.getAllEpics());
        System.out.println("В Epic 1 пока нет Subtask-ов " + manager.getEpicSubtasks(epicOne.getId()));

        Subtask subtaskOneEpicOne = new Subtask(
                "Subtask 1 Epic 1",
                "Описание Subtask 1 Epic 1",
                epicOne.getId());
        Subtask subtaskTwoEpicOne = new Subtask(
                "Subtask 2 Epic 1",
                "Описание Subtask 2 Epic 1",
                epicOne.getId());
        Subtask subtaskTreeEpicOne = new Subtask(
                "Subtask 3 Epic 1",
                "Описание Subtask 3 Epic 1",
                epicOne.getId());
        Subtask subtaskOneEpicTwo = new Subtask(
                "Subtask 1 Epic 2",
                "Описание Subtask 1 Epic 2",
                epicTwo.getId());

        manager.createNewSubtask(subtaskOneEpicOne);
        manager.createNewSubtask(subtaskTwoEpicOne);
        manager.createNewSubtask(subtaskTreeEpicOne);
        manager.createNewSubtask(subtaskOneEpicTwo);

        System.out.println("Данные по добавленным 3-м Subtask-ам в Epic 1 и 1-м Subtask-е в Epic 2 "
                + manager.getAllSubtasks());
        System.out.println("Данные по добавленной Subtask-е с id=4 в Epic 1 " + manager.getSubtaskById(4));
        System.out.println("Данные по добавленным 3-м Subtask-ам в Epic 1 "
                + manager.getEpicSubtasks(epicOne.getId()));
        System.out.println("Данные по добавленной 1-й Subtask-е в Epic 2 "
                + manager.getEpicSubtasks(epicTwo.getId()));

        Subtask updatedSubtaskOneEpicOne = new Subtask(
                subtaskOneEpicOne.getId(),
                "Обновленная Subtask 1 Epic 1",
                "Описание обновленной Subtask 1 Epic 1",
                "DONE",
                epicOne.getId());

        manager.updateSubtask(updatedSubtaskOneEpicOne);

        System.out.println("Данные по Subtask-ам Epic 1: обновлена Subtask 1 (статус - DONE)"
                + manager.getEpicSubtasks(epicOne.getId()));
        System.out.println("Данные по обновленному Epic 1 (статус изменился на IN_PROGRESS) "
                + manager.getEpicById(2));

        Subtask updatedSubtaskTreeEpicOne = new Subtask(
                subtaskTreeEpicOne.getId(),
                "Обновленная Subtask 3 Epic 1",
                "Описание обновленной Subtask 3 Epic 1",
                "DONE",
                epicOne.getId());

        manager.updateSubtask(updatedSubtaskTreeEpicOne);

        System.out.println("Данные по обновленной Subtask 3 Epic 1 (статус изменился на DONE) "
                + manager.getSubtaskById(6));
        System.out.println("Данные по Epic 1 (статус не изменился, текущий - IN_PROGRESS, "
                + "т.к. не все Subtask-и в статусах DONE) "
                + manager.getEpicById(2));

        Subtask updatedSubtaskOneEpicTwo = new Subtask(
                subtaskOneEpicTwo.getId(),
                "Обновленная Subtask 1 of Epic 2",
                "Описание обновленной Subtask 1 of Epic 2",
                "DONE",
                epicTwo.getId());

        manager.updateSubtask(updatedSubtaskOneEpicTwo);

        System.out.println("Данные по обновленному Epic 2 (статус изменился на DONE) "
                + "т.к. все Subtask-и в статусах DONE) "
                + manager.getEpicById(3));

        Subtask subtaskTwoEpicTwo = new Subtask(
                "Subtask 2 Epic 2",
                "Описание Subtask 2 Epic 2",
                epicTwo.getId());

        manager.createNewSubtask(subtaskTwoEpicTwo);

        System.out.println("Данные по обновленному Epic 2 (статус изменился на IN_PROGRESS) "
                + "т.к. добавилась Subtask-а в статусе NEW) "
                + manager.getEpicById(3));

        System.out.println(manager.getEpicSubtasks(epicTwo.getId()));

        manager.deleteSubtaskById(8);

        System.out.println("Subtask 1 Epic 1 (id = 8) - удалена " + manager.getSubtaskById(8));
        System.out.println("Данные по обновленному Epic 2 (статус изменился на DONE) "
                + "т.к. ранее добавленная Subtask-а в статусе NEW была удалена) "
                + manager.getEpicById(3));

        manager.deleteAllSubtasks();

        System.out.println("Все Sutask-и удалены " + manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());

        manager.deleteEpicById(2);

        System.out.println("Epic 1 (id = 2) - удален " + manager.getEpicById(2));

        manager.deleteAllEpics();

        System.out.println("Все Epic-и удалены " + manager.getAllEpics());
    }
}
