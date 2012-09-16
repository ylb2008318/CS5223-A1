
import java.rmi.*;
import java.util.Map;

/**
 *
 * @author ghome
 */
public interface Server_interface extends Remote {

    public void connectServer(Client_interface client_obj) throws RemoteException;

    public boolean disconnectServer(int playerID) throws RemoteException;

    public boolean joinGame(int playerID) throws RemoteException;

    public boolean move(int playerID, int direction) throws RemoteException;

    public boolean upToDate(Map_obj[][] game_map) throws RemoteException;

    public boolean setPlayer_info(Map<Integer, Client_interface> player_info) throws RemoteException;

    public void printStat(String traceCpoy) throws RemoteException;

    public boolean isAlive() throws RemoteException;

    public void setPrimaryServerID(int primaryServerID) throws RemoteException;

    public void setBackupServerID(int backupServerID) throws RemoteException;

    public void setIsPrimaryServer(boolean isPS) throws RemoteException;

    public void becomePS() throws RemoteException;
}
