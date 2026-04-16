package client.game;

import interfaces.RmiNames;
import interfaces.game.GameServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class GameSimulationClient {
    private static final String[] PLAYERS = {"Alice", "Bob", "Charlie"};
    private static final String[] ACTIONS = {
            "moves north",
            "collects a coin",
            "casts shield",
            "opens a chest",
            "attacks a goblin"
    };

    private GameSimulationClient() {
    }

    public static void runDemo(String host) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RmiNames.DEFAULT_PORT);
        GameServer server = (GameServer) registry.lookup(RmiNames.GAME_SERVER);

        ExecutorService executor = Executors.newFixedThreadPool(PLAYERS.length);
        CountDownLatch done = new CountDownLatch(PLAYERS.length);

        for (String player : PLAYERS) {
            executor.submit(() -> simulatePlayer(server, player, done));
        }

        done.await(15, TimeUnit.SECONDS);
        executor.shutdownNow();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Connected players after simulation: " + server.getPlayerCount());
    }

    private static void simulatePlayer(GameServer server, String playerName, CountDownLatch done) {
        GameCallbackImpl callback = null;
        String playerId = null;
        try {
            callback = new GameCallbackImpl(playerName);
            playerId = server.joinGame(playerName, callback);
            pause(300, 700);

            List<String> actions = pickTwoActions();
            for (String action : actions) {
                server.sendAction(playerId, action);
                pause(250, 650);
            }

            Thread.sleep(2_000L);
            server.leaveGame(playerId);
        } catch (Exception e) {
            System.err.println("Simulation failed for " + playerName + ": " + e.getMessage());
        } finally {
            if (callback != null) {
                try {
                    UnicastRemoteObject.unexportObject(callback, true);
                } catch (Exception ignored) {
                }
            }
            done.countDown();
        }
    }

    private static List<String> pickTwoActions() {
        List<String> chosen = new ArrayList<>(2);
        while (chosen.size() < 2) {
            String candidate = ACTIONS[ThreadLocalRandom.current().nextInt(ACTIONS.length)];
            if (!chosen.contains(candidate)) {
                chosen.add(candidate);
            }
        }
        return chosen;
    }

    private static void pause(int minMillis, int maxMillis) throws InterruptedException {
        int value = ThreadLocalRandom.current().nextInt(minMillis, maxMillis + 1);
        Thread.sleep(value);
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        runDemo(host);
    }
}
