import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

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
        registryURL = "game_control";
        System.out.println(registryURL);
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
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
    
    public boolean end_server() {
        try {
            registry.unbind(registryURL);
            return true;
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
    
    public static void main(String[] args) {
        //-h hostname hostport -p size treasurecount
        Maze_game_server server;
        if(args.length==6) {
            server = new Maze_game_server(args[1],Integer.parseInt(args[2]));
            server.start_server(Integer.parseInt(args[4]),Integer.parseInt(args[5]));
        } else {
            server = new Maze_game_server("localhost",1099);
            server.start_server(10, 10);
        }
        
    }
}
