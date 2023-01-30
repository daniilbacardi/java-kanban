package manager;

import tasksTypes.Task;

import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksViewHistory = new LinkedList<>();
    private static final int VIEW_TASKS_LIMIT = 10;

    @Override
    public void add(Task task) {
        tasksViewHistory.add(task);
        if (tasksViewHistory.size() > VIEW_TASKS_LIMIT) {
            tasksViewHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasksViewHistory;
    }
}
