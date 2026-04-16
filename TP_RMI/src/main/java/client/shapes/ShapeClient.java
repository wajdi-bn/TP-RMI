package client.shapes;

import interfaces.RmiNames;
import interfaces.shapes.Shape;
import interfaces.shapes.ShapeFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

public final class ShapeClient {
    private ShapeClient() {
    }

    public static void runDemo(String host) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RmiNames.DEFAULT_PORT);
        ShapeFactory factory = (ShapeFactory) registry.lookup(RmiNames.SHAPE_FACTORY);

        System.out.println("Available shapes: " + Arrays.toString(factory.availableTypes()));
        List<Shape> shapes = List.of(
                factory.createShape("circle", 2.5),
                factory.createShape("rectangle", 4.0, 3.0),
                factory.createShape("triangle", 3.0, 4.0, 5.0),
                factory.createShape("pentagon", 2.0)
        );

        for (Shape shape : shapes) {
            System.out.printf(
                    "%s | area=%.4f | perimeter=%.4f%n",
                    shape.describe(),
                    shape.area(),
                    shape.perimeter()
            );
        }
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        runDemo(host);
    }
}
