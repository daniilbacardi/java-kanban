package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int epicId);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Task> getHistory();

    void remove(int id);

    int getId();
}