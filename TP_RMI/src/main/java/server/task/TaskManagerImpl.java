package server.task;

import interfaces.task.TaskCallback;
import interfaces.task.TaskData;
import interfaces.task.TaskHandle;
import interfaces.task.TaskManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManagerImpl extends UnicastRemoteObject implements TaskManager {
    private final Map<String, TaskHandleImpl> tasks = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<TaskCallback> subscribers = new CopyOnWriteArrayList<>();
    private final AtomicInteger taskSequence = new AtomicInteger();

    public TaskManagerImpl() throws RemoteException {
        super();
    }

    @Override
    public TaskHandle createTask(TaskData data) throws RemoteException {
        validateTaskData(data);

        String taskId = String.format("TASK-%03d", taskSequence.incrementAndGet());
        TaskData serverCopy = new TaskData(data);
        TaskHandleImpl handle = new TaskHandleImpl(taskId, serverCopy, this);
        tasks.put(taskId, handle);
        notifyTaskCreated(serverCopy);
        return handle;
    }

    @Override
    public void subscribe(TaskCallback cb) throws RemoteException {
        if (cb == null) {
            throw new RemoteException("Subscriber callback must not be null.");
        }
        subscribers.addIfAbsent(cb);
    }

    @Override
    public List<String> listPendingTasks() {
        List<String> pending = new ArrayList<>();
        for (TaskHandleImpl handle : tasks.values()) {
            if (handle.isPendingForListing()) {
                pending.add(handle.toPendingListEntry());
            }
        }
        pending.sort(String::compareTo);
        return pending;
    }

    void notifyTaskCompleted(String taskId, String result) {
        for (TaskCallback subscriber : subscribers) {
            try {
                subscriber.onTaskCompleted(taskId, result);
            } catch (RemoteException e) {
                subscribers.remove(subscriber);
            }
        }
    }

    private void notifyTaskCreated(TaskData data) {
        for (TaskCallback subscriber : subscribers) {
            try {
                subscriber.onTaskCreated(new TaskData(data));
            } catch (RemoteException e) {
                subscribers.remove(subscriber);
            }
        }
    }

    private void validateTaskData(TaskData data) throws RemoteException {
        if (data == null) {
            throw new RemoteException("TaskData must not be null.");
        }
        if (data.getTitle() == null || data.getTitle().isBlank()) {
            throw new RemoteException("Task title must not be blank.");
        }
        if (data.getPriority() < 1 || data.getPriority() > 3) {
            throw new RemoteException("Task priority must be between 1 and 3.");
        }
    }
}
