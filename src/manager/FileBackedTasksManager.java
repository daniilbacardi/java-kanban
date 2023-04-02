package manager;

import exceptions.ManagerSaveException;
import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file = new File("src/files", "SaveTasks.csv");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager() {
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
    public void deleteAllTaskTypes(){
        super.deleteAllTaskTypes();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task result = super.getTaskById(id);
        save();
        return result;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic result = super.getEpicById(id);
        save();
        return result;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask result = super.getSubtaskById(id);
        save();
        return result;
    }

    public void save() throws ManagerSaveException {
        try {
            Files.createFile(file.toPath());
        } catch (Exception exception) {
            exception.getStackTrace();
        }
        try (Writer fileWriter = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fileWriter)) {
            bw.write("id,type,name,status,description, start time, duration, end time, epic\n");
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
            throw new ManagerSaveException("Произошла ошибка при записи данных в файл.");
        }
    }

    static Task fromString(String value) {
        String[] entryArray = value.split(",");
        Task task;
        switch (entryArray[1]) {
            case "Task":
                id = Integer.parseInt(entryArray[0]);
                if (entryArray[5].equals("null")) {
                    task = new Task(entryArray[2], entryArray[4], TaskStatus.valueOf(entryArray[3]), null,
                            Optional.of(Integer.parseInt(entryArray[6])).orElse(0));
                } else {
                    task = new Task(entryArray[2], entryArray[4], TaskStatus.valueOf(entryArray[3]),
                            LocalDateTime.parse(entryArray[5]), Integer.parseInt(entryArray[6]));
                }
                task.setId(id);
                break;
            case "Subtask":
                id = Integer.parseInt(entryArray[0]);
                if (entryArray[5].equals("null")) {
                    task = new Subtask(entryArray[2], entryArray[4], TaskStatus.valueOf(entryArray[3]),
                            null, Optional.of(Integer.parseInt(entryArray[6])).orElse(0),
                            Integer.parseInt(entryArray[8]));
                } else {
                    task = new Subtask(entryArray[2], entryArray[4], TaskStatus.valueOf(entryArray[3]),
                            LocalDateTime.parse(entryArray[5]), Integer.parseInt(entryArray[6]),
                            Integer.parseInt(entryArray[8]));
                }
                task.setId(id);
                break;
            case "Epic":
                id = Integer.parseInt(entryArray[0]);
                if (entryArray[5].equals("null")) {
                    task = new Epic(entryArray[2], entryArray[4], null, Integer.parseInt(entryArray[6]));
                } else {
                    task = new Epic(entryArray[2], entryArray[4], LocalDateTime.parse(entryArray[5]),
                            Integer.parseInt(entryArray[6]));
                    task.setStartTime(LocalDateTime.parse(entryArray[5]));
                    task.setDuration(Integer.parseInt(entryArray[6]));
                    ((Epic) task).setEndTime(LocalDateTime.parse(entryArray[7]));
                }
                task.setId(id);
                task.setStatus(TaskStatus.valueOf(entryArray[3]));
                break;
            default:
                throw new ManagerSaveException("Произошла ошибка при записи данных в файл.");
        }
        return task;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        String[] list = value.split(",");
        for (String s : list) {
            historyList.add(Integer.parseInt(s));
        }
        return historyList;
    }

    public static String historyToString(HistoryManager historyManager){
        List<Task> tasksHistory = historyManager.getHistory();
        StringBuilder res = new StringBuilder();
        if (!tasksHistory.isEmpty()) {
            res = new StringBuilder(String.valueOf(tasksHistory.get(tasksHistory.size() - 1).getId()));
            for (int i = tasksHistory.size() - 2; i > -1; i--) {
                res.insert(0, tasksHistory.get(i).getId() + ",");
            }
        }
        return res.toString();
    }

    public static Object loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            FileBackedTasksManager fileBackedTasksManager =
                    new FileBackedTasksManager(new File(file.getPath()));
            List<String> tasksStrings = new ArrayList<>();
            while (br.ready()) {
                tasksStrings.add(br.readLine());
            }

            for (int i = 1; i < tasksStrings.size() - 2; i++) {
                String[] strings = tasksStrings.get(i).split(",");
                if (strings[1].equals("Task")) {
                    tasks.put(fromString(tasksStrings.get(i)).getId(), fromString(tasksStrings.get(i)));
                    fileBackedTasksManager.save();
                } else if (strings[1].equals("Epic")) {
                    epics.put(fromString(tasksStrings.get(i)).getId(), (Epic) fromString(tasksStrings.get(i)));
                    fileBackedTasksManager.save();
                } else {
                    subtasks.put(fromString(tasksStrings.get(i)).getId(), (Subtask) fromString(tasksStrings.get(i)));
                    fileBackedTasksManager.save();
                }
            }

            List<Integer> history = historyFromString(tasksStrings.get(tasksStrings.size() - 1));
            HashMap<Integer, Task> allTasks = new HashMap<>(tasks);
            allTasks.putAll(epics);
            allTasks.putAll(subtasks);

            history.forEach(i -> historyManager.add(allTasks.get(i)));

            return fileBackedTasksManager;
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            exception.getStackTrace();
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getNewDefault(new File("src/files", "SaveTasks.csv"));

        Task taskOne = new Task(
                "Task 1",
                "Описание Task 1",
                LocalDateTime.of(2022,12,10,10,0),
                30);
        Task taskTwo = new Task(
                "Task 2",
                "Описание Task 2",
                LocalDateTime.of(2022,12,10,11,0),
                30);
        Task taskThree = new Task(
                "Task 3",
                "Описание Task 3",
                LocalDateTime.of(2022,12,10,12,0),
                45);

        manager.createNewTask(taskOne);
        manager.createNewTask(taskTwo);
        manager.createNewTask(taskThree);

        Epic epicOne = new Epic(
                "Epic 1",
                "Описание Epic 1",
                null,
                0);
        Epic epicTwo = new Epic(
                "Epic 2",
                "Описание Epic 2",
                null,
                0);
        Epic epicThree = new Epic(
                "Epic 3",
                "Описание Epic 3",
                null,
                0);

        manager.createNewEpic(epicOne);
        manager.createNewEpic(epicTwo);
        manager.createNewEpic(epicThree);

        Subtask subtaskOneEpicOne = new Subtask(
                "Subtask 1 Epic 1",
                "Описание Subtask 1 Epic 1",
                LocalDateTime.of(2022,12,15,10,0),
                60,
                epicOne.getId());
        Subtask subtaskTwoEpicOne = new Subtask(
                "Subtask 2 Epic 1",
                "Описание Subtask 2 Epic 1",
                LocalDateTime.of(2022,12,15,12,0),
                120,
                epicOne.getId());
        Subtask subtaskTreeEpicOne = new Subtask(
                "Subtask 3 Epic 1",
                "Описание Subtask 3 Epic 1",
                LocalDateTime.of(2022,12,15,19,0),
                60,
                epicOne.getId());
        Subtask subtaskOneEpicTwo = new Subtask(
                "Subtask 1 Epic 2",
                "Описание Subtask 1 Epic 2",
                LocalDateTime.of(2022,12,18,12,0),
                300,
                epicTwo.getId());
        Subtask subtaskTwoEpicTwo = new Subtask(
                "Subtask 2 Epic 2",
                "Описание Subtask 2 Epic 2",
                LocalDateTime.of(2022,12,19,19,0),
                60,
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
        manager.getTaskById(0);
        manager.getSubtaskById(6);
        manager.getSubtaskById(8);
        manager.deleteTaskById(0);
        manager.deleteSubtaskById(10);

        printSortedTasks(manager.getPrioritizedTasks());
        printHistory(manager.getHistory());

        TaskManager newManager = Managers.getDefault(new File("src/files", "SaveTasks.csv"));
        printHistory(newManager.getHistory());
    }

    public static void printHistory(List<Task> history){
        System.out.print("История просмотров: \n");
        for (Task task : history) {
            System.out.print(task + "  " + "\n");
        }
        System.out.println();
    }

    public static void printSortedTasks(List<Task> tasksSet){
        System.out.print("Отсортированные по дате и времени таски/сабтаски: \n");
        for (Task task: tasksSet) {
            System.out.print(
                    "[ID " +
                    task.getId() +
                    ", Название " +
                    task.getName() +
                    " " + task.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yy HH.mm")) + "]\n");
        }
        System.out.println();
    }
}