/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_server;

import maze_game_server.ui.Server_UI;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author ghome
 */
public class Maze_game_server {
    private Server_impl server_obj;
    private Server_interface server_stub;
    private Registry registry;
    private int portNum;
    private String registryURL;
    private String ipAddress;
    
    public Maze_game_server() {
    }

    public Maze_game_server(int portNum, String ipAddress) {
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        registryURL = "rmi://"+this.ipAddress+":"+Integer.toString(this.portNum)+"/game_control";
    }

    public boolean start_server() {
	try {
	    server_stub = new Server_impl();
	    //server_stub = (Server_interface) UnicastRemoteObject.exportObject(server_obj, 0);
	    registry = LocateRegistry.getRegistry();
            
	    registry.bind(registryURL, server_stub);

	    System.out.println("Server ready");
            return true;
	} catch (Exception e) {
            e.printStackTrace();
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
}
