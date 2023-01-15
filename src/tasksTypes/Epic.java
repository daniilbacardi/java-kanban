package tasksTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> SUBTASK_IDS = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, String status) {
        super(id, name, description, status);
    }

    public void addSubtaskId(int id) {
        SUBTASK_IDS.add(id);
    }

    public List<Integer> getSUBTASK_IDS() {
        return SUBTASK_IDS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(SUBTASK_IDS, epic.SUBTASK_IDS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SUBTASK_IDS);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
