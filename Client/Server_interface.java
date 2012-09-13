import java.rmi.*;
/**
 *
 * @author ghome
 */
public interface Server_interface extends Remote {
    public void connectServer(Client_interface client_obj) throws RemoteException;
    
    public boolean disconnectServer(int playerID) throws RemoteException;
    
    public boolean joinGame(int playerID) throws RemoteException;
    
    public boolean move(int playerID, int direction) throws RemoteException;
}