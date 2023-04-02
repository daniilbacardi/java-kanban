package manager;

import java.io.File;
import java.net.URL;

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

    public static TaskManager getDefault(URL url) {
        return new HTTPTaskManager(url);
    }
}
