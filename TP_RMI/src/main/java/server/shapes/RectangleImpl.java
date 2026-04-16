package server.shapes;

import java.rmi.RemoteException;

public class RectangleImpl extends AbstractShape {
    private final double width;
    private final double height;

    public RectangleImpl(double width, double height) throws RemoteException {
        requirePositive(width, "width");
        requirePositive(height, "height");
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2.0 * (width + height);
    }

    @Override
    public String describe() {
        return String.format("Rectangle(width=%.2f, height=%.2f)", width, height);
    }
}
