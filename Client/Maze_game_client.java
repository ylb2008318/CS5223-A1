import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 *
 * @author ghome
 */
public class Maze_game_client implements Client_interface {
    private String serverName;
    private int serverPort;
    private String registryURL;
    private Server_interface server_stub;
    private Registry registry;
    private int playerID;
    private int size;
    private Client_interface client_interface;
    
    public Maze_game_client(String name, int port) {
	   serverName = name;
	   serverPort = port;
        registryURL = "rmi://"+this.serverName+":"+Integer.toString(this.serverPort)+"/game_control";
        playerID = -1;
    }

    public void display(Map_obj[][] game_map) {
        int i,j;
		for (i=0;i<size;i++){
			for(j=0;j<size;j++){
				if (game_map[i][j] instanceof Game_player) System.out.print("P  ");
				else if (game_map[i][j] instanceof Treasure) System.out.print("T  ");
				else System.out.print("O  ");
			}
			System.out.print("\n");
		}
    }
    
    public void setPlayerID(int ID) {
	playerID=ID;
    }

    public void setSize(int size) {
	this.size=size;
    }

    public void connect_server(){
	try {
	    registry = LocateRegistry.getRegistry();
	    server_stub = (Server_interface) registry.lookup(registryURL);
         playerID = server_stub.connectServer(this);
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
	Maze_game_client client = new Maze_game_client("Serveur1",1024);
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
		while (x!=1 || x!=2 || x!=3 || x!=4){
			System.out.println("wrong value, try again.");
			x = sc.nextInt();
		}
	
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