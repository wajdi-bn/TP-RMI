package server.task;

import interfaces.task.TaskData;
import interfaces.task.TaskHandle;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TaskHandleImpl extends UnicastRemoteObject implements TaskHandle {
    private final String taskId;
    private final TaskData data;
    private final TaskManagerImpl manager;

    private TaskStatus status = TaskStatus.PENDING;
    private int progress;
    private String result = "";

    public TaskHandleImpl(String taskId, TaskData data, TaskManagerImpl manager) throws RemoteException {
        super();
        this.taskId = taskId;
        this.data = data;
        this.manager = manager;
    }

    @Override
    public synchronized void updateProgress(int percent) throws RemoteException {
        if (status == TaskStatus.DONE) {
            throw new RemoteException("Task " + taskId + " is already completed.");
        }
        if (percent < 0 || percent > 100) {
            throw new RemoteException("Progress must be between 0 and 100.");
        }

        if (percent == 100) {
            progress = 100;
            status = TaskStatus.IN_PROGRESS;
            return;
        }

        progress = percent;
        status = percent == 0 ? TaskStatus.PENDING : TaskStatus.IN_PROGRESS;
    }

    @Override
    public synchronized void complete(String result) throws RemoteException {
        if (status == TaskStatus.DONE) {
            return;
        }

        this.result = result == null ? "" : result;
        this.progress = 100;
        this.status = TaskStatus.DONE;
        manager.notifyTaskCompleted(taskId, this.result);
    }

    @Override
    public synchronized String getStatus() {
        return String.format(
                "%s | %s | %s | %d%% | result=%s",
                taskId,
                data.getTitle(),
                status,
                progress,
                result.isBlank() ? "<pending>" : result
        );
    }

    synchronized boolean isPendingForListing() {
        return status != TaskStatus.DONE;
    }

    synchronized String toPendingListEntry() {
        return String.format("%s | %s | %s | %d%%", taskId, data.getTitle(), status, progress);
    }
}
