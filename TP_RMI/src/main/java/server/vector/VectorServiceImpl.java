package server.vector;

import interfaces.vector.Vector2D;
import interfaces.vector.VectorService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class VectorServiceImpl extends UnicastRemoteObject implements VectorService {
    public VectorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double magnitude(Vector2D v) throws RemoteException {
        Vector2D validated = requireVector(v, "v");
        System.out.println("VectorService.magnitude received copy: " + validated);
        return Math.hypot(validated.x, validated.y);
    }

    @Override
    public Vector2D normalize(Vector2D v) throws RemoteException {
        Vector2D validated = requireVector(v, "v");
        double magnitude = Math.hypot(validated.x, validated.y);
        if (magnitude == 0.0) {
            return new Vector2D(0.0, 0.0);
        }
        return new Vector2D(validated.x / magnitude, validated.y / magnitude);
    }

    @Override
    public Vector2D add(Vector2D a, Vector2D b) throws RemoteException {
        Vector2D left = requireVector(a, "a");
        Vector2D right = requireVector(b, "b");
        return new Vector2D(left.x + right.x, left.y + right.y);
    }

    private Vector2D requireVector(Vector2D vector, String name) throws RemoteException {
        if (vector == null) {
            throw new RemoteException(name + " must not be null.");
        }
        return vector;
    }
}
