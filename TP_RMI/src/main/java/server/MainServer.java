package server;

import interfaces.RmiNames;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import server.counter.CounterServiceImpl;
import server.game.GameServerImpl;
import server.shapes.ShapeFactoryImpl;
import server.task.TaskManagerImpl;
import server.vector.VectorServiceImpl;

public final class MainServer {
    private MainServer() {
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : RmiNames.DEFAULT_PORT;
        Registry registry = ensureRegistry(port);

        registry.rebind(RmiNames.SHAPE_FACTORY, new ShapeFactoryImpl());
        registry.rebind(RmiNames.GAME_SERVER, new GameServerImpl());
        registry.rebind(RmiNames.VECTOR_SERVICE, new VectorServiceImpl());
        registry.rebind(RmiNames.COUNTER_SERVICE, new CounterServiceImpl());
        registry.rebind(RmiNames.TASK_MANAGER, new TaskManagerImpl());

        System.out.println("RMI registry ready on port " + port);
        System.out.println("Bound services: "
                + String.join(", ",
                RmiNames.SHAPE_FACTORY,
                RmiNames.GAME_SERVER,
                RmiNames.VECTOR_SERVICE,
                RmiNames.COUNTER_SERVICE,
                RmiNames.TASK_MANAGER));

        synchronized (MainServer.class) {
            MainServer.class.wait();
        }
    }

    private static Registry ensureRegistry(int port) throws Exception {
        try {
            return LocateRegistry.createRegistry(port);
        } catch (ExportException e) {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list();
            return registry;
        }
    }
}
