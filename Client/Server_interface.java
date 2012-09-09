/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import maze_game_client.Client_interface;
/**
 *
 * @author ghome
 */
public interface Server_interface extends Remote {
    public int connectServer(Client_interface client_obj) throws RemoteException;
    
    public boolean disconnectServer(int playerID) throws RemoteException;
    
    public boolean joinGame(int playerID) throws RemoteException;
    
    public boolean move(int playerID, int direction) throws RemoteException;

}
