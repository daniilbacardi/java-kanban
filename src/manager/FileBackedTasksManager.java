package manager;

import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    final private String historyFile;
    final String path = "src/files/";

    public FileBackedTasksManager(String file) {
        historyFile = file;
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }
    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }
    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }

    public void save() throws ManagerSaveException {
        try {
            Files.createDirectory(Paths.get(path));
        } catch (Exception exception) {
            exception.getStackTrace();
        }
        try (Writer fileWriter = new FileWriter(historyFile); BufferedWriter bw = new BufferedWriter(fileWriter)) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task: tasks.values()) {
                bw.write(task.toString() + "\n");
            }
            for (Epic task: epics.values()) {
                bw.write(task.toString() + "\n");
            }
            for (Subtask task: subtasks.values()) {
                bw.write(task.toString() + "\n");
            }
            bw.newLine();
            bw.write(historyToString(historyManager));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            exception.getStackTrace();
            throw new ManagerSaveException("Произошла ошибка при записи данных в файл.");
        }
    }

    private static Task fromString(String value) {
        String[] entryArray = value.split(",");
        Task task;
        switch (entryArray[1]) {
            case "Task":
                task = new Task(Integer.parseInt(entryArray[0]), entryArray[2], entryArray[4],
                        TaskStatus.valueOf(entryArray[3]));
                break;
            case "Subtask":
                task = new Subtask(Integer.parseInt(entryArray[0]), entryArray[2], entryArray[4],
                        TaskStatus.valueOf(entryArray[3]), Integer.parseInt(entryArray[5]));
                break;
            case "Epic":
                task = new Epic(Integer.parseInt(entryArray[0]), entryArray[2], entryArray[4],
                        TaskStatus.valueOf(entryArray[3]));
                break;
            default:
                task = null;
        }
        return task;
    }

    static List<Integer> historyFromString(String value) {
        List <Integer> historyList = new ArrayList<>();
        String[] taskIds = value.split(",");
        for (String taskId: taskIds) {
            historyList.add(Integer.parseInt(taskId));
        }
        return historyList;
    }

    public String toString(Task task) {
        return task.toString();
    }

    public static String historyToString(HistoryManager historyManager){
        List<Task> tasksHistory = historyManager.getHistory();
        String res = "";
        if (!tasksHistory.isEmpty()) {
            res = String.valueOf(tasksHistory.get(tasksHistory.size() - 1).getId());
            for (int i = tasksHistory.size() - 2; i > -1; i--) {
                res = tasksHistory.get(i).getId() + "," + res;
            }
        }
        return res;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getAbsolutePath());
        try(Reader reader = new FileReader(file.getAbsolutePath())) {
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            while (br.ready()) {
                builder.append(br.readLine());
                builder.append("\n");
            }
            String fileString = builder.toString();
            String[] blocks = fileString.split("\n\n");

            String[] tasksStrings = blocks[0].split("\n");
            List <Integer> historyIds = historyFromString(blocks[1].trim());
            int maxId = 0;
            for (int i = 1; i < tasksStrings.length; i++) {
                String type = tasksStrings[i].split(",")[1];
                switch (type) {
                    case "Task":
                        Task task = fromString(tasksStrings[i]);
                        fileBackedTasksManager.createNewTask(task);
                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                        break;
                    case "Subtask":
                        Subtask subtask = (Subtask) fromString(tasksStrings[i]);
                        fileBackedTasksManager.createNewSubtask(subtask);
                        if (subtask.getId() > maxId) {
                            maxId = subtask.getId();
                        }
                        break;
                    case "Epic":
                        Epic epic = (Epic) fromString(tasksStrings[i]);
                        fileBackedTasksManager.createNewEpic(epic);
                        if (epic.getId() > maxId) {
                            maxId = epic.getId();
                        }
                        break;
                    default:
                        System.out.println("Произошла ошибка при чтении файла.");
                }
            }

            fileBackedTasksManager.setId(maxId);
            for (int i = historyIds.size() - 1; i > -1; i--) {
                if (fileBackedTasksManager.tasks.containsKey(historyIds.get(i))) {
                    fileBackedTasksManager.getTaskById(historyIds.get(i));
                } else if (fileBackedTasksManager.subtasks.containsKey(historyIds.get(i))) {
                    fileBackedTasksManager.getSubtaskById(historyIds.get(i));
                } else if (fileBackedTasksManager.epics.containsKey(historyIds.get(i))) {
                    fileBackedTasksManager.getEpicById(historyIds.get(i));
                }
            }
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        } catch (IOException exception) {
            return fileBackedTasksManager;
        }
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        TaskManager newManager = Managers.getDefault(new File("src/files", "SaveTasks.csv"));

        Task taskOne = new Task("Task 1", "Описание Task 1");
        Task taskTwo = new Task("Task 2", "Описание Task 2");
        Task taskThree = new Task("Task 3", "Описание Task 3");

        manager.createNewTask(taskOne);
        manager.createNewTask(taskTwo);
        manager.createNewTask(taskThree);

        Epic epicOne = new Epic("Epic 1", "Описание Epic 1");
        Epic epicTwo = new Epic("Epic 2", "Описание Epic 2");
        Epic epicThree = new Epic("Epic 3", "Описание Epic 3");

        manager.createNewEpic(epicOne);
        manager.createNewEpic(epicTwo);
        manager.createNewEpic(epicThree);

        Subtask subtaskOneEpicOne = new Subtask(
                "Subtask 1 Epic 1",
                "Описание Subtask 1 Epic 1",
                epicOne.getId());
        Subtask subtaskTwoEpicOne = new Subtask(
                "Subtask 2 Epic 1",
                "Описание Subtask 2 Epic 1",
                epicOne.getId());
        Subtask subtaskTreeEpicOne = new Subtask(
                "Subtask 3 Epic 1",
                "Описание Subtask 3 Epic 1",
                epicOne.getId());
        Subtask subtaskOneEpicTwo = new Subtask(
                "Subtask 1 Epic 2",
                "Описание Subtask 1 Epic 2",
                epicTwo.getId());
        Subtask subtaskTwoEpicTwo = new Subtask(
                "Subtask 2 Epic 2",
                "Описание Subtask 2 Epic 2",
                epicTwo.getId());

        manager.createNewSubtask(subtaskOneEpicOne);
        manager.createNewSubtask(subtaskTwoEpicOne);
        manager.createNewSubtask(subtaskTreeEpicOne);
        manager.createNewSubtask(subtaskOneEpicTwo);
        manager.createNewSubtask(subtaskTwoEpicTwo);

        manager.getTaskById(0);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getSubtaskById(9);
        manager.getTaskById(0);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getEpicById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getSubtaskById(8);
        manager.getSubtaskById(9);
        manager.getSubtaskById(10);

        printHistory(manager.getHistory());

        printHistory(newManager.getHistory());
    }

    public static void printHistory(List<Task> history){
        System.out.print("History: \n");
        for (Task task : history) {
            System.out.print(task + "  " + "\n");
        }
        System.out.println();
    }
}
