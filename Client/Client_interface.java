import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ghome
 */
public interface Client_interface extends Remote {
    public boolean notify_info() throws RemoteException;
}
