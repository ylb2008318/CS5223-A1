/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import maze_game_client.Client_interface;
/**
 *
 * @author ghome
 */
public class Server_impl extends UnicastRemoteObject implements Server_interface {

    private int size;
    private int treasure_count;
    private Map<Integer,Client_interface> player_info; // List of client to notify
    private int game_stat; // 0:created,1:started,2:ended
    private Vector<Map_obj> game_map;
    
    public Server_impl() throws RemoteException {
        super();
    }

    public Server_impl(int size, int treasure_count) throws RemoteException {
        this.size = size;
        this.treasure_count = treasure_count;
        player_info = new HashMap<Integer,Client_interface>();
        game_stat = 2;
        game_map = new Vector<Map_obj>();
    }
    

    @Override
    public int connectServer(Client_interface client_obj) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean disconnectServer(int playerID) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean joinGame(int playerID) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean move(int playerID, int direction) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
