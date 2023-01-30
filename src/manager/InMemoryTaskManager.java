package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idGenerator = -1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subTasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            subTasks.add(subtasks.get(id));
        }
        return subTasks;
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void createNewTask(Task task) {
        int id = generateNewId();
        task.setId(id);
        task.setStatus(TaskStatus.NEW);
        tasks.put(id, task);
    }

    @Override
    public void createNewEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epic.setStatus(TaskStatus.NEW);
        epics.put(id, epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        int id = generateNewId();
        subtask.setId(id);
        subtask.setStatus(TaskStatus.NEW);
        epic.addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private ArrayList<String> getEpicSubtasksStatuses(int epicId) {
        ArrayList<String> statuses = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            statuses.add(String.valueOf(subtasks.get(id).getStatus()));
        }
        return statuses;
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        ArrayList<String> statusesOfSubtasks;
        statusesOfSubtasks = getEpicSubtasksStatuses(epic.getId());
        if (statusesOfSubtasks.contains("NEW")
                && !statusesOfSubtasks.contains("IN_PROGRESS")
                && !statusesOfSubtasks.contains("DONE")) {
            epic.setStatus(TaskStatus.NEW);
        } else if (!statusesOfSubtasks.contains("NEW")
                && !statusesOfSubtasks.contains("IN_PROGRESS")
                && statusesOfSubtasks.contains("DONE")) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusesOfSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        updateEpic(epic);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            subtasks.remove(id);
        }
        epics.remove(epicId);
    }

    @Override
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

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpic(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateNewId() {
        return ++idGenerator;
    }
}