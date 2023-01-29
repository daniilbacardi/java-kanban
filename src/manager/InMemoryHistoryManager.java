package manager;

import tasksTypes.Task;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksViewHistory = new ArrayList<>();
    private static final int VIEW_TASKS_LIMIT = 10;

    @Override
    public void add(Task task) {
        tasksViewHistory.add(task);
        int viewCounter = tasksViewHistory.size();
        if (tasksViewHistory.size() > VIEW_TASKS_LIMIT) {
            for (int i = 0; i < viewCounter - VIEW_TASKS_LIMIT; i++) {
                tasksViewHistory.remove(i);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        if (tasksViewHistory.isEmpty()) {
            System.out.println("Вы еще не просматривали задачи");
            return null;
        } else {
            System.out.println("Последние просмотренные задачи:" + tasksViewHistory);
        }
        return tasksViewHistory;
    }
}
