package client.vector;

import interfaces.RmiNames;
import interfaces.counter.CounterService;
import interfaces.counter.SharedCounter;
import interfaces.vector.Vector2D;
import interfaces.vector.VectorService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class VectorCounterClient {
    private VectorCounterClient() {
    }

    public static void runDemo(String host) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RmiNames.DEFAULT_PORT);
        VectorService vectorService = (VectorService) registry.lookup(RmiNames.VECTOR_SERVICE);
        CounterService counterService = (CounterService) registry.lookup(RmiNames.COUNTER_SERVICE);

        runVectorDemo(vectorService);
        runCounterDemo(counterService);
    }

    private static void runVectorDemo(VectorService vectorService) throws Exception {
        System.out.println("Vector demo (passage par copie)");
        Vector2D v = new Vector2D(3.0, 4.0);
        double magnitude = vectorService.magnitude(v);
        System.out.printf("magnitude(%s) = %.2f%n", v, magnitude);

        v.x = 99.0;
        v.y = -20.0;
        System.out.println("Client mutated its local vector after the first remote call: " + v);

        Vector2D normalized = vectorService.normalize(new Vector2D(3.0, 4.0));
        Vector2D added = vectorService.add(v, new Vector2D(1.0, 1.0));
        System.out.println("normalize((3,4)) = " + normalized);
        System.out.println(v + " + (1,1) = " + added);
    }

    private static void runCounterDemo(CounterService counterService) throws Exception {
        System.out.println("Counter demo (passage par reference)");
        SharedCounter alpha = counterService.createCounter("alpha");
        SharedCounter beta = counterService.createCounter("beta");
        alpha.reset();
        beta.reset();

        counterService.atomicIncrement(alpha, 4);
        counterService.atomicIncrement(beta, 2);
        System.out.printf("Initial sum via service = %d%n", counterService.sum(alpha, beta));

        alpha.increment();
        beta.increment();
        System.out.printf("Values after direct stub updates: alpha=%d, beta=%d, sum=%d%n",
                alpha.getValue(),
                beta.getValue(),
                counterService.sum(alpha, beta));
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        runDemo(host);
    }
}
