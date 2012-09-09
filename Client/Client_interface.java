import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ghome
 */
public interface Client_interface extends Remote {
    public void display(Map_obj[][] game_ma) throws RemoteException;
	
    public void setPlayerID(int ID) throws RemoteException;
	
    public void setSize(int size) throws RemoteException;
}
