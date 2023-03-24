package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private static HistoryManager historyManager;
    private static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("Add and Get test for HistoryManager")
    void addAndReturnHistoryListTest() {
        Task task = new Task("Task1", "Descr1", TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,10,0), 30);
        inMemoryTaskManager.createNewTask(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void removeDuplicatedTasksFromHistoryListTest() {
        Task task = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,10,0), 30);
        inMemoryTaskManager.createNewTask(task);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "повторная задача найдена.");
    }

    @Test
    void removeTaskFromHistoryListByIdTest() {
        Task task = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,10,0), 30);
        inMemoryTaskManager.createNewTask(task);
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        boolean isTaskRemoved = history.contains(task);
        assertFalse(isTaskRemoved, "Задача не удалена.");
    }

    @Test
    void removeTaskFromPositionFromHistoryListTest() {
        historyManager.getHistory().clear();
        Task task = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,8,5,10,0), 60);
        Task task2 = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,9,6,12,15), 30);
        Task task3 = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,10,7,15,30), 600);
        Task task4 = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,11,8,22,25), 60);
        Task task5 = new Task("Task1", "Descr1",TaskStatus.NEW,
                LocalDateTime.of(2022,12,10,9,0), 15);
        inMemoryTaskManager.createNewTask(task);
        inMemoryTaskManager.createNewTask(task2);
        inMemoryTaskManager.createNewTask(task3);
        inMemoryTaskManager.createNewTask(task4);
        inMemoryTaskManager.createNewTask(task5);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.remove(task.getId());
        Task newFirst = historyManager.getHistory().get(0);
        assertEquals(task2, newFirst, "Неверное смещение при удалении первой задачи.");
        historyManager.remove(task5.getId());
        int lastIndex = historyManager.getHistory().size() - 1;
        Task newLast = historyManager.getHistory().get(lastIndex);
        assertEquals(task4, newLast, "Неверно определена последняя задача.");
        int index = historyManager.getHistory().indexOf(task3);
        Task preTask = historyManager.getHistory().get(index - 1);
        Task postTask = historyManager.getHistory().get(index + 1);
        historyManager.remove(task3.getId());
        assertEquals(preTask, task2, "Предшествующая задача восстановлена неверно.");
        assertEquals(postTask, task4, "Следующая задача восстановлена неверно.");
    }

    @Test
    void returnNullWhenHistoryListIsEmptyTest() {
        int historyListSize = historyManager.getHistory().size();
        assertEquals(0, historyListSize, "История задач не пустая.");
    }
}