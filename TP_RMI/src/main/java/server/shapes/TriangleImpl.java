package server.shapes;

import java.rmi.RemoteException;

public class TriangleImpl extends AbstractShape {
    private final double a;
    private final double b;
    private final double c;

    public TriangleImpl(double a, double b, double c) throws RemoteException {
        requirePositive(a, "side a");
        requirePositive(b, "side b");
        requirePositive(c, "side c");
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new RemoteException("Triangle sides do not satisfy the triangle inequality.");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double area() {
        double s = perimeter() / 2.0;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    public double perimeter() {
        return a + b + c;
    }

    @Override
    public String describe() {
        return String.format("Triangle(a=%.2f, b=%.2f, c=%.2f)", a, b, c);
    }
}
