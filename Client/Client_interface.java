/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ghome
 */
public interface Client_interface extends Remote {
    public Void display(Map_obj[][] game_ma) throws RemoteException;
	
    public Void setPlayerID(int ID) throws RemoteException;
	
    public Void setSize(int size) throws RemoteException;
}
