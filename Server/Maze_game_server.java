import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author ghome
 */
public class Maze_game_server {
    private Server_impl server_obj;
    private Server_interface server_stub;
    Registry registry;
    

    public boolean start_server(int size, int treasure_count) {
	
	try {
	    server_obj = new Server_impl(size, treasure_count);
	    server_stub = (Server_interface) UnicastRemoteObject.exportObject(server_obj, 0);
	    registry = LocateRegistry.getRegistry();           
	    registry.bind("game_control", server_stub);
	    System.out.println("Server ready");
            return true;
	} catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
    
    public boolean end_server() {
        try {
            registry.unbind("game_control");
            return true;
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
	}
        return false;
    }
    
    public static void main(String[] args) {
        //-h hostname hostport -p size treasurecount
        Maze_game_server server;

        if(args.length==3) {
            server = new Maze_game_server();
            server.start_server(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        } else {
            server = new Maze_game_server();
		  server.start_server(10,10);
        }
        
    }
}
