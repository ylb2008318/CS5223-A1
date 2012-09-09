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
public class Maze_game_client implement Client_interface {
    private String serverName;
    private int serverPort;
    private String registryURL;
    private Server_interface server_stub;
    private Registry registry;
    private int playerID;
    private int size;
    private Client_interface client_interface;
    
    public Maze_game_client() {
        registryURL = "rmi://"+this.serverName+":"+Integer.toString(this.serverPort)+"/game_control";
        playerID = -1;
    }

    public Void display(Map_obj[][] game_map) {
        int i,j;
		for (i=0;i<size;i++){
			for(j=0;j<size;j++){
				if (instanceof(game_map[i][j])==Game_player) System.out.print("P  ");
				else if (instanceof(game_map[i][j])==Treasure) System.out.print("T  ");
				else System.out.print("O  ");
			}
			System.out.print("\n");
		}
    }
    
    public Void setPlayerID(int ID) {
	playerID=ID;
    }

    public Void setSize(int size) {
	this.size=size;
    }

    public void connect_server(){
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




   public static void main (String[] args){
	System.out.println("Player Interface:");
	while(1) {
		System.out.println("Select an option:");
		System.out.println("1: Connect to the server");
		System.out.println("2: Join game");
		System.out.println("3: Move");
		System.out.println("4: Disconnect to the server");

	Scanner sc = new Scanner(System.in);
	int x=-0;
	x = sc.nextInt();
	while (x!=1 || x!=2 || x!=3 || x!=4){
		System.out.println("wrong value, try again.");
		x = sc.nextInt();
	}
   
	switch (x)
	{
		case 1: connect_server(); break;
		case 2: break;
		case 3: break;
		case 4: if (disconnect_server()) System.out.println("Disconnected...");
			   else System.out.println("The disconnection has failed");
			   break;
		default: 
	}

}
