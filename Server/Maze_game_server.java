
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
        
        boolean exit = false;
        Scanner sc = new Scanner(System.in);
        while (!exit) {
            System.out.println();
            System.out.println();
            System.out.println("Select an option:");
            System.out.println("1: Unbind");
            System.out.println("2: End Game");
            
            int x = 0;
            System.out.println();
            x = sc.nextInt();
            switch (x) {
                case 1:
                    try {
                        registry.unbind("game_control");
                    } catch (Exception ex) {
                        System.err.println("Server exception: " + ex.toString());
                    }
                    exit = true;
                    break;
                case 2:
                    server_obj.endGame();
                    break;
                default:
                    System.out.println("wrong value, try again.");
                    break;
            }
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

        if (args.length == 3) {
            server = new Maze_game_server();
            server.start_server(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        } else {
            server = new Maze_game_server();
            server.start_server(10, 10);
        }

    }
}
