package client;

import client.game.GameSimulationClient;
import client.shapes.ShapeClient;
import client.task.TaskManagerDemoClient;
import client.vector.VectorCounterClient;
import java.util.Locale;

public final class MainClient {
    private MainClient() {
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        String demo = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "all";

        switch (demo) {
            case "shapes":
                ShapeClient.runDemo(host);
                break;
            case "game":
                GameSimulationClient.runDemo(host);
                break;
            case "vectors":
                VectorCounterClient.runDemo(host);
                break;
            case "tasks":
                TaskManagerDemoClient.runDemo(host);
                break;
            case "all":
                runAll(host);
                break;
            default:
                System.out.println("Usage: java -cp target/classes client.MainClient [host] [all|shapes|game|vectors|tasks]");
        }
    }

    private static void runAll(String host) throws Exception {
        System.out.println("=== Shapes ===");
        ShapeClient.runDemo(host);
        System.out.println();

        System.out.println("=== Vectors & Counters ===");
        VectorCounterClient.runDemo(host);
        System.out.println();

        System.out.println("=== Tasks ===");
        TaskManagerDemoClient.runDemo(host);
        System.out.println();

        System.out.println("=== Game Simulation ===");
        GameSimulationClient.runDemo(host);
    }
}
