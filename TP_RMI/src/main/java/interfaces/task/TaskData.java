package interfaces.task;

import java.io.Serializable;

public class TaskData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private int priority;

    public TaskData() {
    }

    public TaskData(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public TaskData(TaskData other) {
        this(other.title, other.description, other.priority);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "TaskData{"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + ", priority=" + priority
                + '}';
    }
}
