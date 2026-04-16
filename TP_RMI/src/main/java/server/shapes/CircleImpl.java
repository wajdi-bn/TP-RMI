package server.shapes;

import java.rmi.RemoteException;

public class CircleImpl extends AbstractShape {
    private final double radius;

    public CircleImpl(double radius) throws RemoteException {
        requirePositive(radius, "radius");
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2.0 * Math.PI * radius;
    }

    @Override
    public String describe() {
        return String.format("Circle(radius=%.2f)", radius);
    }
}
