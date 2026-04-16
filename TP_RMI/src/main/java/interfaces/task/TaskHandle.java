package interfaces.task;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskHandle extends Remote {
    void updateProgress(int percent) throws RemoteException;

    void complete(String result) throws RemoteException;

    String getStatus() throws RemoteException;
}
