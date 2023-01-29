package manager;

import tasksTypes.Task;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksViewHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        tasksViewHistory.add(task);
        int viewCount = tasksViewHistory.size();
        if (tasksViewHistory.size() > 10) {
            for (int i = 0; i < viewCount - 10; i++) {
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
