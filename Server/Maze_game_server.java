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

    public Maze_game_server(String ipAddress, int portNum) {
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        registryURL = "rmi://"+this.ipAddress+":"+Integer.toString(this.portNum)+"/game_control";
    }

    public boolean start_server(int size, int treasure_count) {
	try {
	    server_stub = new Server_impl(size, treasure_count);
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
    
    public boolean end_server() {
        try {
            registry.unbind(registryURL);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
    
    public static void main(String[] args) {
        //-h hostname hostport -p size treasurecount
        Maze_game_server server;
        if(args.length==6) {
            server = new Maze_game_server(args[0],Integer.parseInt(args[1]));
            server.start_server(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        } else {
            server = new Maze_game_server("localhost",1099);
        }
        
    }
}
