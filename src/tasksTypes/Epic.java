package tasksTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus) {
        super(id, name, description, taskStatus);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public int getNumberOfSubtasks() {
        return subtaskIds.size();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtaskIds);
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                "Epic" + "," +
                super.getName() + "," +
                super.getStatus() + "," +
                super.getDescription();
    }
}
