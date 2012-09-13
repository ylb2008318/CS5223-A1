import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 *
 * @author ghome
 */
public class Maze_game_client {
    private Server_interface server_stub;
    private Client_interface client_stub;
    private Client_impl client_obj;
    
    
    public void connect_server(){
	try {
	    Registry registry = LocateRegistry.getRegistry();
	    server_stub = (Server_interface) registry.lookup("game_control");
	    client_obj = new Client_impl();
	    client_stub = (Client_interface) UnicastRemoteObject.exportObject(client_obj, 0);	    
	    server_stub.connectServer(client_stub);
         System.out.println("Connected to server.");

	} catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	} 
    }
    

    public boolean disconnect_server(){
        boolean result = false;
        try {
            result = server_stub.disconnectServer(client_obj.playerID);
            System.out.println("Disconnected from server");
        } catch (RemoteException ex) {
            Logger.getLogger(Maze_game_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }


   public static void main (String[] args){
	Maze_game_client client = new Maze_game_client();
	System.out.println("Player Interface:");
	while(true) {
		System.out.println("Select an option:");
		System.out.println("1: Connect to the server");
		System.out.println("2: Join game");
		System.out.println("3: Move");
		System.out.println("4: Disconnect to the server");

		Scanner sc = new Scanner(System.in);
		int x=-0;
		x = sc.nextInt();
		while (x!=1 && x!=2 && x!=3 && x!=4){
			System.out.println("wrong value, try again.");
			x = sc.nextInt();
		}
	
		System.out.println();
		System.out.println();
		switch (x)
		{
			case 1: client.connect_server(); break;
			case 2: break;
			case 3: break;
			case 4: if (client.disconnect_server()) System.out.println("Disconnected...");
				else System.out.println("The disconnection has failed");
				break;
			default: 
		}
	}

   }
}