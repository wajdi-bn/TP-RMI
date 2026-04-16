package client.task;

import interfaces.RmiNames;
import interfaces.task.TaskData;
import interfaces.task.TaskHandle;
import interfaces.task.TaskManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public final class TaskManagerDemoClient {
    private TaskManagerDemoClient() {
    }

    public static void runDemo(String host) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RmiNames.DEFAULT_PORT);
        TaskManager manager = (TaskManager) registry.lookup(RmiNames.TASK_MANAGER);

        TaskCallbackImpl callback = new TaskCallbackImpl("observer-1");
        try {
            manager.subscribe(callback);

            TaskData report = new TaskData("Write report", "Prepare the distributed systems summary", 3);
            TaskHandle reportHandle = manager.createTask(report);
            report.setTitle("Mutated only on the client");

            TaskData slides = new TaskData("Prepare slides", "Build the presentation deck", 2);
            TaskHandle slidesHandle = manager.createTask(slides);

            printPending("Pending right after creation", manager.listPendingTasks());

            reportHandle.updateProgress(40);
            slidesHandle.updateProgress(65);
            System.out.println(reportHandle.getStatus());
            System.out.println(slidesHandle.getStatus());

            reportHandle.complete("Report shared with the team");
            slidesHandle.updateProgress(90);
            slidesHandle.complete("Slides delivered");

            printPending("Pending after completion", manager.listPendingTasks());
        } finally {
            try {
                UnicastRemoteObject.unexportObject(callback, true);
            } catch (Exception ignored) {
            }
        }
    }

    private static void printPending(String label, List<String> pending) {
        System.out.println(label + ":");
        if (pending.isEmpty()) {
            System.out.println("  <none>");
            return;
        }
        for (String entry : pending) {
            System.out.println("  " + entry);
        }
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        runDemo(host);
    }
}
