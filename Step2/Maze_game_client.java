
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
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

    private Client_interface client_stub;
    private Client_impl client_obj;

    public void connect_server() {
        try {
            if (client_obj.playerID == 0) {
                Registry registry = LocateRegistry.getRegistry();
                client_stub.setPrimaryServer((Server_interface) registry.lookup("game_control"));
                client_obj.getPrimaryServer().connectServer(client_stub);
                System.out.println("Connected to server.");
            } else {
                System.out.println("Already connected.");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    public boolean disconnect_server() {
        boolean result = false;
        try {
            if (client_obj != null && client_obj.playerID != 0) {
                result = client_obj.getPrimaryServer().disconnectServer(client_obj.playerID);
                client_obj.setPlayerID(0);
                System.out.println("Disconnected from server");
            } else {
                System.out.println("You are not connected.");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Maze_game_client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Maze_game_client() {
        try {
            client_obj = new Client_impl();
            client_stub = (Client_interface) UnicastRemoteObject.exportObject(client_obj, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(Maze_game_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void join_game() {
        boolean result = false;
        try {
            System.out.println("Try to join game...");
            result = client_obj.getPrimaryServer().joinGame(client_obj.playerID);

            if (result) {
                System.out.println("Connected to the game...");
            } else {
                System.out.println("Connection failed...");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    public void move() {
        boolean result = false;
        int x;
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("choose your direction:");
            x = sc.nextInt();
            while (x != 1 && x != 2 && x != 3 && x != 4) {
                System.out.println("wrong value, try again.");
                x = sc.nextInt();
            }
            result = client_obj.getPrimaryServer().move(client_obj.playerID, x);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    public static void main(String[] args) throws NotBoundException {
        Maze_game_client client = new Maze_game_client();
        if (args.length > 0) {
            if (args[0].equals("-S")) {
                try {
                    Server_interface server_stub = client.client_obj.CreateServer(10,10);
                    Registry registry = LocateRegistry.getRegistry();
                    try {
                        registry.unbind("game_control");
                    } catch (NotBoundException ex){
                        
                    } finally {
                        registry.bind("game_control", server_stub);
                    }
                    System.out.println("Server ready");
                    client.connect_server();
                } catch (AlreadyBoundException ex) {
                    System.err.println("Server exception: " + ex.toString());
                } catch (AccessException ex) {
                    System.err.println("Server exception: " + ex.toString());
                } catch (RemoteException ex) {
                    System.err.println("Server exception: " + ex.toString());
                }
            }
        }

        System.out.println("Player Interface:");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("Select an option:");
            System.out.println("1: Connect to the server");
            System.out.println("2: Join game");
            System.out.println("3: Move");
            System.out.println("4: Disconnect to the server");

            int x = -0;
            x = sc.nextInt();
            while (x != 1 && x != 2 && x != 3 && x != 4) {
                System.out.println("wrong value, try again.");
                x = sc.nextInt();
            }

            System.out.println();
            System.out.println();
            switch (x) {
                case 1:
                    client.connect_server();
                    break;
                case 2:
                    client.join_game();
                    break;
                case 3:
                    client.move();
                    break;
                case 4:
                    if (client.disconnect_server()) {
                        System.out.println("Disconnected...");
                    } else {
                        System.out.println("The disconnection has failed");
                    }
                    break;
                default:
            }
        }

    }
}