package tasksTypes;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private int duration;
    private LocalDateTime startTime;


    public Task(String name, String description, LocalDateTime startTime, int duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }

    //protected DateTimeFormatter formatter() {
    //    return DateTimeFormatter.ofPattern("dd.MM.yy HH.mm");
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public String toString() {
        return id + "," +
                "Task" + "," +
                name + "," +
                status + "," +
                description + "," +
                startTime + "," +
                duration + "," +
                getEndTime();
    }

    @Override
    public int compareTo(Task o) {
        if (this.startTime.isBefore(o.getStartTime())) {
            return -1;
        } else if (this.startTime.equals(o.getStartTime())) {
            return 0;
        } else {
            return 1;
        }
    }
}
