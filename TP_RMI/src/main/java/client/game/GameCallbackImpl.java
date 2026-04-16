package client.game;

import interfaces.game.GameCallback;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GameCallbackImpl extends UnicastRemoteObject implements GameCallback {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String playerName;

    public GameCallbackImpl(String playerName) throws RemoteException {
        super();
        this.playerName = playerName;
    }

    @Override
    public void onPlayerJoined(String joinedPlayerName) {
        log("player joined: " + joinedPlayerName);
    }

    @Override
    public void onGameEvent(String eventMessage) {
        log(eventMessage);
    }

    @Override
    public void onGameOver(String winner) {
        log("game over, last active player: " + winner);
    }

    private void log(String message) {
        System.out.printf("[%s] %s NOTIF: %s%n", LocalTime.now().format(TIME_FORMAT), playerName, message);
    }
}
