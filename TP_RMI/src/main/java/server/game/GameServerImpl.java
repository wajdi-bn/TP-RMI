package server.game;

import interfaces.game.GameCallback;
import interfaces.game.GameServer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {
    private final Map<String, PlayerSession> players = new ConcurrentHashMap<>();
    private final AtomicInteger playerSequence = new AtomicInteger();

    public GameServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String joinGame(String playerName, GameCallback cb) throws RemoteException {
        if (playerName == null || playerName.isBlank()) {
            throw new RemoteException("playerName must not be blank.");
        }
        if (cb == null) {
            throw new RemoteException("callback must not be null.");
        }

        String playerId = String.format("P%03d", playerSequence.incrementAndGet());
        PlayerSession session = new PlayerSession(playerId, playerName, cb);
        players.put(playerId, session);

        notifyPlayers(playerId, false, callback -> callback.onPlayerJoined(playerName));
        cb.onGameEvent("Welcome " + playerName + " (" + playerId + ")");
        return playerId;
    }

    @Override
    public void sendAction(String playerId, String action) throws RemoteException {
        PlayerSession session = players.get(playerId);
        if (session == null) {
            throw new RemoteException("Unknown playerId: " + playerId);
        }
        String eventMessage = session.playerName + " -> " + action;
        notifyPlayers(null, true, callback -> callback.onGameEvent(eventMessage));
    }

    @Override
    public void leaveGame(String playerId) throws RemoteException {
        PlayerSession session = players.remove(playerId);
        if (session == null) {
            return;
        }
        notifyPlayers(null, true, callback -> callback.onGameEvent(session.playerName + " left the game."));
        if (players.size() == 1) {
            PlayerSession winner = players.values().iterator().next();
            notifyPlayers(null, true, callback -> callback.onGameOver(winner.playerName));
        }
    }

    @Override
    public int getPlayerCount() {
        return players.size();
    }

    private void notifyPlayers(String excludedPlayerId, boolean includeExcluded, CallbackAction action) {
        for (Map.Entry<String, PlayerSession> entry : players.entrySet()) {
            String playerId = entry.getKey();
            if (!includeExcluded && playerId.equals(excludedPlayerId)) {
                continue;
            }
            PlayerSession session = entry.getValue();
            try {
                action.invoke(session.callback);
            } catch (RemoteException e) {
                players.remove(playerId);
            }
        }
    }

    @FunctionalInterface
    private interface CallbackAction {
        void invoke(GameCallback callback) throws RemoteException;
    }

    private static final class PlayerSession {
        private final String playerId;
        private final String playerName;
        private final GameCallback callback;

        private PlayerSession(String playerId, String playerName, GameCallback callback) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.callback = callback;
        }
    }
}
