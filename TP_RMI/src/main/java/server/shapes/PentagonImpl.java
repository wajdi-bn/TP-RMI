package server.shapes;

import java.rmi.RemoteException;

public class PentagonImpl extends AbstractShape {
    private final double side;

    public PentagonImpl(double side) throws RemoteException {
        requirePositive(side, "side");
        this.side = side;
    }

    @Override
    public double area() {
        return 0.25 * Math.sqrt(5.0 * (5.0 + 2.0 * Math.sqrt(5.0))) * side * side;
    }

    @Override
    public double perimeter() {
        return 5.0 * side;
    }

    @Override
    public String describe() {
        return String.format("Pentagon(side=%.2f)", side);
    }
}
