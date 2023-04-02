package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.io.IOException;
import java.net.URL;

public class HTTPTaskManager extends FileBackedTasksManager implements TaskManager {
    private final URL url;
    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager(URL url){
        this.url = url;
        kvTaskClient = new KVTaskClient(this.url);
    }

    public static HTTPTaskManager loadFromServer(URL url) {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(url);
        httpTaskManager.readData();
        return httpTaskManager;
    }

    @Override
    public void save() {
        StringBuilder tasksStringBuilder = new StringBuilder();
        StringBuilder subtasksStringBuilder = new StringBuilder();
        StringBuilder epicsStringBuilder = new StringBuilder();
        StringBuilder historyStringBuilder = new StringBuilder();

        for (Task task : getAllTasks()) {
            tasksStringBuilder.append(task.toString()).append("//");
        }
        for (Subtask subtask : getAllSubtasks()) {
            subtasksStringBuilder.append(subtask.toString()).append("//");
        }
        for (Epic epic : getAllEpics()) {
            epicsStringBuilder.append(epic.toString()).append("//");
        }
        for (Task task : getHistory()) {
            historyStringBuilder.append(task.getId()).append(",");
        }
        try {
            kvTaskClient.put(String.valueOf(TaskKey.TASK_KEY), tasksStringBuilder.toString());
            kvTaskClient.put(String.valueOf(TaskKey.SUBTASK_KEY), subtasksStringBuilder.toString());
            kvTaskClient.put(String.valueOf(TaskKey.EPIC_KEY), epicsStringBuilder.toString());
            kvTaskClient.put(String.valueOf(TaskKey.HISTORY_KEY), historyStringBuilder.toString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка в методе save() " + e.getMessage());
        }
    }

    private void readData() {
        String loadedTasks = kvTaskClient.load(String.valueOf(TaskKey.TASK_KEY));
        if (loadedTasks.isEmpty()) {
            return;
        }
        String[] separatedTasks = loadedTasks.split("//");
        for (String t : separatedTasks) {
            Task task = fromString(t);
            createNewTask(task);
        }
        String loadedEpics = kvTaskClient.load(String.valueOf(TaskKey.EPIC_KEY));
        if (loadedEpics.isEmpty()) {
            return;
        }
        String[] separatedEpics = loadedEpics.split("//");
        for (String e : separatedEpics) {
            Epic epic = (Epic) fromString(e);
            createNewEpic(epic);
        }
        String loadedSubtasks = kvTaskClient.load(String.valueOf(TaskKey.SUBTASK_KEY));
        if (loadedSubtasks.isEmpty()) {
            return;
        }
        String[] separatedSubtasks = loadedSubtasks.split("//");
        for (String s : separatedSubtasks) {
            Subtask subtask = (Subtask) fromString(s);
            createNewSubtask(subtask);
        }
        String loadedHistory = kvTaskClient.load(String.valueOf(TaskKey.HISTORY_KEY));
        if (loadedHistory.isEmpty()) {
            return;
        }
        for (Integer id : historyFromString(loadedHistory)) {
            if (tasks.containsKey(id)) {
                Task task = tasks.get(id);
                historyManager.add(task);
            } if (epics.containsKey(id)) {
                Epic epic = epics.get(id);
                historyManager.add(epic);
            } if(subtasks.containsKey(id)) {
                Subtask subtask = subtasks.get(id);
                historyManager.add(subtask);
            }
        }
    }
}
