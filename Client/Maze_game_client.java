/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import maze_game_server.Server_interface;

/**
 *
 * @author ghome
 */
public class Maze_game_client {
    private String serverName;
    private int serverPort;
    private String registryURL;
    private Server_interface server_stub;
    private Registry registry;
    private int playerID;
    private Client_interface client_interface;
    
    public Maze_game_client() {
        registryURL = "rmi://"+this.serverName+":"+Integer.toString(this.serverPort)+"/game_control";
        playerID = -1;
    }

    public String display() {
        return "";
    }
    
    public int connect_server(){
	try {
	    registry = LocateRegistry.getRegistry();
	    server_stub = (Server_interface) registry.lookup(registryURL);
	    client_interface = new Client_impl();
            playerID = server_stub.connectServer(client_interface);
	    System.out.println("OK");
	} catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	} 
        return playerID;
    }
    
    public boolean disconnect_server(){
        boolean result = false;
        try {
            result = server_stub.disconnectServer(playerID);
        } catch (RemoteException ex) {
            Logger.getLogger(Maze_game_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
