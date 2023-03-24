package tasksTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, LocalDateTime startTime, int duration) {
        super(name, description, null, 0);
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus, LocalDateTime startTime, int duration) {
        super(id, name, description, taskStatus, startTime, duration);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                "Epic" + "," +
                super.getName() + "," +
                super.getStatus() + "," +
                super.getDescription() + "," +
                super.getStartTime() + "," +
                super.getDuration() + "," +
                super.getEndTime();
    }
}
