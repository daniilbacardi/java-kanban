package tests;

import manager.HttpTaskServer;
import manager.KVServer;
import manager.HTTPTaskManager;
import org.junit.jupiter.api.Test;
import tasksTypes.Epic;
import tasksTypes.TaskStatus;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest {

    @Test
    void loadFromServer() throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
        HTTPTaskManager manager = new HTTPTaskManager(new URL("http://localhost:8078"));
        Task task = new Task("Name1", "descr1", TaskStatus.NEW,
                LocalDateTime.of(2022,10,10,10,10,10),10);
        manager.createNewTask(task);
        Epic epic = new Epic("EpicName", "EpicDescr", null, 0);
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("SubName", "SubDescr",
                LocalDateTime.of(2022,11,11,11,10), 30, epic.getId());
        manager.createNewSubtask(subtask);
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        HTTPTaskManager newManager = HTTPTaskManager.loadFromServer(new URL("http://localhost:8078"));
        assertEquals(2, newManager.getHistory().size(), "История восстановлена неверно.");

        newManager.createNewTask(new Task("name2", "des2", TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,10,10,10),10));

        assertEquals(3, newManager.getAllTasks().size(), "Восстановлено неверное количество задач.");
        assertEquals(manager.getTaskById(task.getId()), newManager.getTaskById(task.getId()),
                "Задачи не совпадают.");

        assertEquals(2, newManager.getAllSubtasks().size(),
                "Восстановлено неверное количество подзадач.");
        assertEquals(manager.getSubtaskById(subtask.getId()), newManager.getSubtaskById(subtask.getId()),
                "Подзадачи не совпадают.");

        assertEquals(2, newManager.getAllEpics().size(), "Восстановлено неверное количество эпиков.");
        assertEquals(manager.getEpicById(epic.getId()), newManager.getEpicById(epic.getId()),
                "Эпики не совпадают.");
    }
}
