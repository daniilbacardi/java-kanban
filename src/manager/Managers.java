package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getNewDefault(File file) {
        return new FileBackedTasksManager(file);
    }

    public static TaskManager getDefault(File file) {
        return (TaskManager) FileBackedTasksManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
