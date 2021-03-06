
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

    public void connect_server() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            server_stub = (Server_interface) registry.lookup("game_control");
            if (client_obj == null || !client_obj.getConnected()) {
                client_obj = new Client_impl();
                client_stub = (Client_interface) UnicastRemoteObject.exportObject(client_obj, 0);
                server_stub.connectServer(client_stub);
                client_obj.setConnected(true);
                System.out.println("Connection to the server: done");
            } else {
                System.out.println("You are already connected.");
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }

    public boolean disconnect_server() {
        boolean result = false;
        try {
            if (client_obj.getConnected()) {
                result = server_stub.disconnectServer(client_obj.getPlayerID());
                client_obj.setPlayerID(0);
                client_obj.setConnected(false);	
                System.out.println("You are disconnected from the server");
            } else {
                System.out.println("You are not connected.");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Maze_game_client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public void join_game() {
        boolean result = false;
        if (client_obj == null || !client_obj.getConnected())	System.out.println("You are not connected.");
        else if (client_obj.getinGame()) System.out.println("You are already in game.");
	   else {	
			try {
				System.out.println("Try to join game...");
				result = server_stub.joinGame(client_obj.getPlayerID());
				client_obj.setinGame(result);
				if (result) {
					System.out.println("Connection to the game: done");
						
				} else {
					System.out.println("Connection failed, the game may have already started...");
				}
			} catch (Exception e) {
				System.err.println("Client exception: " + e.toString());
			}
        }
    }

    public void move() {
        boolean result = false;
        int x;
        if (client_obj == null || !client_obj.getConnected())	System.out.println("You are not connected.");
        else if (!client_obj.getinGame()) System.out.println("You are not in game.");
        else {        
        Scanner sc = new Scanner(System.in);
		try {
			System.out.println("choose your direction:");
			x = sc.nextInt();
			while (x != 1 && x != 2 && x != 3 && x != 4) {
				System.out.println("wrong value, try again.");
				x = sc.nextInt();
			}
			result = server_stub.move(client_obj.getPlayerID(), x);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
		}
       }
    }

    public static void main(String[] args) {
        Maze_game_client client = new Maze_game_client();
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