package manager;

import exceptions.IntersectionException;
import exceptions.ManagerSaveException;
import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public static int id = -1;
    public static final HashMap<Integer, Task> tasks = new HashMap<>();
    public static final HashMap<Integer, Epic> epics = new HashMap<>();
    public static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public static int idGenerator = -1;
    public static final HistoryManager historyManager = Managers.getDefaultHistory();
    public static Set<Task> prioritizedTasks = new TreeSet<>();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        addNewPrioritizedTask(task);
        task.setStatus(TaskStatus.NEW);
        tasks.put(id, task);
   }

    @Override
    public void createNewEpic(Epic epic) {
        int id = generateNewId();
        epic.setId(id);
        epic.setStatus(TaskStatus.NEW);
        epics.put(id, epic);
        calcEpicStartAndFinish(epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        int id = generateNewId();
        subtask.setId(id);
        addNewPrioritizedTask(subtask);
        subtask.setStatus(TaskStatus.NEW);
        epic.addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
        calcEpicStartAndFinish(epic);
    }

    @Override
    public void updateTask(Task task) {
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
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
        addNewPrioritizedTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        updateEpic(epic);
        calcEpicStartAndFinish(epic);
    }

    private void calcEpicStart(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epic);
        if (epicSubtasks.size() > 0) {
            LocalDateTime startTime = null;
            for (Subtask subtask : epicSubtasks) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                }else if (subtask.getStartTime().isBefore(startTime)){
                    startTime = subtask.getStartTime();
                }
            }
            epic.setStartTime(startTime);
        }
    }

    private void calcEpicDuration(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epic);
        if (epicSubtasks.size() > 0) {
            int duration = 0;
            for (Subtask subtask : epicSubtasks) {
                duration += subtask.getDuration();
            }
            epic.setDuration(duration);
        }
    }

    private void calcEpicFinish(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epic);
        if (epicSubtasks.size() > 0) {
            LocalDateTime endTime = null;
            for (Subtask subtask : epicSubtasks) {
                if (endTime == null) {
                    endTime = subtask.getEndTime();
                } else if (subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
            epic.setEndTime(endTime);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
          return new ArrayList<>(prioritizedTasks);
    }

    public void validateTask(Task newTask) {
        for (Task task : prioritizedTasks) {
            if ((newTask.getStartTime().isBefore(task.getStartTime()) &&
                    newTask.getEndTime().isAfter(task.getStartTime())
                    || (newTask.getStartTime().isAfter(task.getStartTime()) &&
                    newTask.getEndTime().isBefore(task.getEndTime()))
                    || (newTask.getStartTime().isBefore(task.getEndTime()) &&
                    newTask.getEndTime().isAfter(task.getEndTime())))) {
                throw new IntersectionException("Задача пересекается по времени.");
            }
        }
    }

    private ArrayList<String> getEpicSubtasksStatuses(int epicId) {
        ArrayList<String> statuses = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            statuses.add(String.valueOf(subtasks.get(id).getStatus()));
        }
        return statuses;
    }

    private void calcEpicStartAndFinish(Epic epic) {
        calcEpicStart(epic);
        calcEpicDuration(epic);
        calcEpicFinish(epic);
    }

    private void addNewPrioritizedTask(Task task) {
        if (task == null) {
            return;
        }
        validateTask(task);
        prioritizedTasks.add(task);

    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtaskList.add(subtasks.get(subtaskId));
        }
        return subtaskList;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = getTaskById(id);
        prioritizedTasks.remove(task);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (int id : epic.getSubtaskIds()) {
            prioritizedTasks.remove(getSubtaskById(id));
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        int idEpic = subtask.getEpicId();
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            prioritizedTasks.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpic(epic);
            calcEpicStartAndFinish(epics.get(idEpic));
        } else {
            System.out.println("Subtask не найден");
        }
    }

    @Override
    public void deleteAllTasks() {
        for(Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for(Integer subTaskId : subtasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        for(Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for(Integer subTaskId : subtasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            if (epic == null) {
                throw new ManagerSaveException("Список эпиков и подзадач пуст.");
            }
            List<Integer> arraylist = epic.getSubtaskIds();
            arraylist.clear();
            calcEpicStartAndFinish(epic);
        }
    }

    @Override
    public void deleteAllTaskTypes() {
        deleteAllSubtasks();
        deleteAllEpics();
        deleteAllTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    @Override
    public int getId() {
        int res = id;
        return ++res;
    }

    private int generateNewId() {
        return ++idGenerator;
    }
}