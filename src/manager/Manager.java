package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idGenerator = -1;

    private int generateNewId() {
        return ++idGenerator;
    }

    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(this.epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subTasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            subTasks.add(subtasks.get(id));
        }
        return subTasks;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createNewTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        task.setStatus("NEW");
        tasks.put(id, task);
    }

    public void createNewEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epic.setStatus("NEW");
        epics.put(id, epic);
    }

    public void createNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        int id = generateNewId();
        subtask.setId(id);
        subtask.setStatus("NEW");
        epic.addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private ArrayList<String> getEpicSubtasksStatuses(int epicId) {
        ArrayList<String> statuses = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            statuses.add(subtasks.get(id).getStatus());
        }
        return statuses;
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        ArrayList<String> statusesOfSubtasks;
        statusesOfSubtasks = getEpicSubtasksStatuses(epic.getId());
        if (statusesOfSubtasks.contains("NEW")
                && !statusesOfSubtasks.contains("IN_PROGRESS")
                && !statusesOfSubtasks.contains("DONE")) {
            epic.setStatus("NEW");
        } else if (!statusesOfSubtasks.contains("NEW")
                && !statusesOfSubtasks.contains("IN_PROGRESS")
                && statusesOfSubtasks.contains("DONE")) {
            epic.setStatus("DONE");
        } else if (statusesOfSubtasks.isEmpty()) {
            epic.setStatus("NEW");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        updateEpic(epic);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            subtasks.remove(id);
        }
        epics.remove(epicId);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            subtasks.remove(id);
            updateEpic(epic);
        } else {
            System.out.println("Subtask не найден");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpic(epic);
        }
    }
}
