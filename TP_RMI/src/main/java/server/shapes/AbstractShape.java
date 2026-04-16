package server.shapes;

import interfaces.shapes.Shape;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

abstract class AbstractShape extends UnicastRemoteObject implements Shape {
    protected AbstractShape() throws RemoteException {
        super();
    }

    protected void requirePositive(double value, String label) throws RemoteException {
        if (value <= 0) {
            throw new RemoteException(label + " must be strictly positive.");
        }
    }
}
