package manager;

import java.io.File;

public class Managers {
    /*public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }*/

    public static TaskManager getDefault() {
        return new FileBackedTasksManager("src/files/SaveTasks.csv");
    }

    public static TaskManager getDefault(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
