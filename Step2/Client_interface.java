
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ghome
 */
public interface Client_interface extends Remote {

    public void setPlayerID(int ID) throws RemoteException;

    public void setSize(int size) throws RemoteException;

    public void display(Map_obj[][] game_map) throws RemoteException;

    public Server_interface createServer(int size, int treasure_count) throws RemoteException;

    public boolean isAlive() throws RemoteException;

    public void setPrimaryServer(Server_interface ps) throws RemoteException;

    public void setBackupServer(Server_interface bs) throws RemoteException;

    public void setBStoPS() throws RemoteException;
}