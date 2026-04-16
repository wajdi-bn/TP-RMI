package client.task;

import interfaces.task.TaskCallback;
import interfaces.task.TaskData;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskCallbackImpl extends UnicastRemoteObject implements TaskCallback {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String clientName;

    public TaskCallbackImpl(String clientName) throws RemoteException {
        super();
        this.clientName = clientName;
    }

    @Override
    public void onTaskCreated(TaskData data) {
        log("task created -> " + data);
    }

    @Override
    public void onTaskCompleted(String taskId, String res) {
        log("task completed -> " + taskId + " result=" + res);
    }

    private void log(String message) {
        System.out.printf("[%s] %s TASK: %s%n", LocalTime.now().format(TIME_FORMAT), clientName, message);
    }
}
