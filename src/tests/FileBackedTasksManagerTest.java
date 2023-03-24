package tests;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final File file = new File("src/files", "SaveTasks.csv");
    private TaskManager fileBackedTasksManager;
    private TaskManager newFileBackedTasksManager;
    private Task task;
    private Task task2;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        fileBackedTasksManager = createTaskManager();
        task = new Task("Task1", "Descr1", TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,10,0), 30);
        task2 = new Task("Task2", "Descr2",TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022,7,10,10,0), 30);
        epic = new Epic("Epic1", "Descr1", null,0);
        subtask = new Subtask("Subtask", "Descr",
                LocalDateTime.of(2022,8,20,15,30), 120, epic.getId());
        subtask2 = new Subtask("Subtask2", "Descr2",
                LocalDateTime.of(2022,9,20,15,30), 120, epic.getId());
    }

    @Override
    public TaskManager createTaskManager() {
        return new FileBackedTasksManager();
    }

    @Test
    public void saveAndReadFromFileTest() {
        fileBackedTasksManager.deleteAllTaskTypes();
        fileBackedTasksManager.createNewTask(task);
        fileBackedTasksManager.createNewEpic(epic);
        fileBackedTasksManager.getTaskById(task.getId());
        fileBackedTasksManager.getEpicById(epic.getId());

        newFileBackedTasksManager = (TaskManager) FileBackedTasksManager.loadFromFile(file);
        List<Task> taskList = newFileBackedTasksManager.getAllTasks();
        assertEquals(1, taskList.size(), "Из файла восстановлено неверное количество задач.");
        assertEquals(task, taskList.get(0), "Задача из файла восстановлена неверно.");

        List<Epic> epicList = newFileBackedTasksManager.getAllEpics();
        Epic loadedEpic = epicList.get(0);
        assertEquals(1, epicList.size(), "Из файла восстановлено неверное количество эпиков'.");
        assertEquals(epic, loadedEpic, "Эпик из файла восстановлен неверно.");

        List<Task> historyList = newFileBackedTasksManager.getHistory();
        Task taskInHistory = historyList.get(0);
        assertEquals(2, historyList.size(), "Неверное количество задач в истории.");
        assertEquals(task, taskInHistory, "Задача в истории не совпадает.");
    }

    @Test
    public void saveAndReadFromFileWithNoTasksTest() {
        fileBackedTasksManager.deleteAllTaskTypes();
        fileBackedTasksManager.createNewTask(task);
        fileBackedTasksManager.getTaskById(task.getId());
        newFileBackedTasksManager = (TaskManager) FileBackedTasksManager.loadFromFile(file);
        List<Task> taskList = newFileBackedTasksManager.getAllTasks();
        assertEquals(1, taskList.size(), "Из файла восстановлено неверное количество задач.");
        List<Task> historyList = newFileBackedTasksManager.getHistory();
        assertEquals(1, historyList.size(), "Неверное количество задач в истории.");
    }

    @Test
    public void saveAndReadFromFileWithEmptyHistoryTest() {
        fileBackedTasksManager.deleteAllTaskTypes();
        fileBackedTasksManager.createNewTask(task);
        fileBackedTasksManager.getTaskById(task.getId());
        newFileBackedTasksManager = (TaskManager) FileBackedTasksManager.loadFromFile(file);
        List<Task> historyList = newFileBackedTasksManager.getHistory();
        assertEquals(1, historyList.size(), "Неверное количество задач в истории.");
    }

    @Test
    public void saveAndReadFromFileWithEpicNoSubtasksTest() {
        fileBackedTasksManager.deleteAllTaskTypes();
        fileBackedTasksManager.createNewEpic(epic);
        fileBackedTasksManager.getEpicById(epic.getId());
        newFileBackedTasksManager = (TaskManager) FileBackedTasksManager.loadFromFile(file);
        List<Epic> epicList = newFileBackedTasksManager.getAllEpics();
        Epic loadedEpic = epicList.get(0);
           assertEquals(1, epicList.size(), "Из файла восстановлено неверное количество эпиков'.");
        assertEquals(epic, loadedEpic, "Эпик из файла восстановлен неверно.");
    }
}