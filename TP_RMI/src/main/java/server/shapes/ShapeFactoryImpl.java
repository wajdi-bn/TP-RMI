package server.shapes;

import interfaces.shapes.Shape;
import interfaces.shapes.ShapeFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Locale;

public class ShapeFactoryImpl extends UnicastRemoteObject implements ShapeFactory {
    private static final String[] AVAILABLE_TYPES = {"circle", "rectangle", "triangle", "pentagon"};

    public ShapeFactoryImpl() throws RemoteException {
        super();
    }

    @Override
    public Shape createShape(String type, double... params) throws RemoteException {
        if (type == null || type.isBlank()) {
            throw new RemoteException("Shape type must be provided.");
        }

        String normalized = type.toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "circle":
                requireArity(normalized, params, 1);
                return new CircleImpl(params[0]);
            case "rectangle":
                requireArity(normalized, params, 2);
                return new RectangleImpl(params[0], params[1]);
            case "triangle":
                requireArity(normalized, params, 3);
                return new TriangleImpl(params[0], params[1], params[2]);
            case "pentagon":
                requireArity(normalized, params, 1);
                return new PentagonImpl(params[0]);
            default:
                throw new RemoteException("Unknown shape type: " + type);
        }
    }

    @Override
    public String[] availableTypes() {
        return AVAILABLE_TYPES.clone();
    }

    private void requireArity(String type, double[] params, int expected) throws RemoteException {
        if (params == null || params.length != expected) {
            throw new RemoteException(
                    String.format("Shape '%s' expects %d parameter(s), got %d.", type, expected, params == null ? 0 : params.length)
            );
        }
    }
}
