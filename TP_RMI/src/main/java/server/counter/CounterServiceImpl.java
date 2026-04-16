package server.counter;

import interfaces.counter.CounterService;
import interfaces.counter.SharedCounter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CounterServiceImpl extends UnicastRemoteObject implements CounterService {
    private final Map<String, SharedCounterImpl> counters = new ConcurrentHashMap<>();

    public CounterServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public SharedCounter createCounter(String name) throws RemoteException {
        if (name == null || name.isBlank()) {
            throw new RemoteException("Counter name must not be blank.");
        }

        SharedCounterImpl existing = counters.get(name);
        if (existing != null) {
            return existing;
        }

        synchronized (counters) {
            existing = counters.get(name);
            if (existing != null) {
                return existing;
            }

            SharedCounterImpl created = new SharedCounterImpl(name);
            counters.put(name, created);
            return created;
        }
    }

    @Override
    public void atomicIncrement(SharedCounter c, int n) throws RemoteException {
        if (c == null) {
            throw new RemoteException("Counter reference must not be null.");
        }
        if (n < 0) {
            throw new RemoteException("n must be >= 0.");
        }
        for (int i = 0; i < n; i++) {
            c.increment();
        }
    }

    @Override
    public int sum(SharedCounter c1, SharedCounter c2) throws RemoteException {
        if (c1 == null || c2 == null) {
            throw new RemoteException("Both counters must be provided.");
        }
        return c1.getValue() + c2.getValue();
    }
}
