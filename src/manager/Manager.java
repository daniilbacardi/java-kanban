package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> TASKS = new HashMap<>();
    private final HashMap<Integer, Epic> EPICS = new HashMap<>();
    private final HashMap<Integer, Subtask> SUBTASKS = new HashMap<>();
    private int idGenerator = -1;

    public int generateNewId() {
        return ++idGenerator;
    }

    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(this.TASKS.values());
    }

    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(this.EPICS.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.SUBTASKS.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subTasks = new ArrayList<>();
        Epic epic = EPICS.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSUBTASK_IDS()) {
            subTasks.add(SUBTASKS.get(id));
        }
        return subTasks;
    }

    public Task getTaskById(int id) {
        return TASKS.get(id);
    }

    public Epic getEpicById(int id) {
        return EPICS.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return SUBTASKS.get(id);
    }

    public void addNewTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        task.setStatus("NEW");
        TASKS.put(id, task);
    }

    public void addNewEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epic.setStatus("NEW");
        EPICS.put(id, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        int id = generateNewId();
        subtask.setId(id);
        subtask.setStatus("NEW");
        epic.addSubtaskId(subtask.getId());
        SUBTASKS.put(subtask.getId(), subtask);
    }

    public void updateTask(Task task) {
        TASKS.put(task.getId(), task);
    }

    public ArrayList<String> getEpicSubtasksStatuses(int epicId) {
        ArrayList<String> statuses = new ArrayList<>();
        Epic epic = EPICS.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSUBTASK_IDS()) {
            statuses.add(SUBTASKS.get(id).getStatus());
        }

        return statuses;
    }

    public void updateEpic(Epic epic) {
        EPICS.put(epic.getId(), epic);
        ArrayList<String> statusesOfSub;
        statusesOfSub = getEpicSubtasksStatuses(epic.getId());
        if (epic.getSUBTASK_IDS() == null || statusesOfSub.contains("NEW")
                && !statusesOfSub.contains("IN_PROGRESS")
                && !statusesOfSub.contains("DONE")) {
            epic.setStatus("NEW");
        } else if (!statusesOfSub.contains("NEW")
                && !statusesOfSub.contains("IN_PROGRESS")
                && statusesOfSub.contains("DONE")) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }

    }
    public void updateSubtask(Subtask subtask) {
        SUBTASKS.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = EPICS.get(epicId);
        updateEpic(epic);
    }

    public void deleteTaskById(int id) {
        TASKS.remove(id);
    }

    public void deleteEpicById(int epicId) {
        Epic epic = EPICS.get(epicId);
        for (int id : epic.getSUBTASK_IDS()) {
            SUBTASKS.remove(SUBTASKS.get(id));
        }
        EPICS.remove(epicId);
    }

    public void deleteSubtaskById(int id) {
        SUBTASKS.remove(id);
    }

    public void deleteAllTasks() {
        TASKS.clear();
    }

    public void deleteAllEpics() {
        EPICS.clear();
        SUBTASKS.clear();
    }

    public void deleteAllSubtasks() {
        SUBTASKS.clear();
    }
}
