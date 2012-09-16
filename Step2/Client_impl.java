
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Client_impl implements Client_interface {
    private Server_interface primaryServer;
    private Server_interface backupServer;
    public int playerID;
    public int size;

    public Client_impl() throws RemoteException {
        playerID = 0;
    }

    @Override
    public void setPlayerID(int ID) {
        playerID = ID;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void display(Map_obj[][] game_map) {
        int i, j;
        Map<Integer,Integer> player_treasure = new HashMap<Integer,Integer>();
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (game_map[i][j] instanceof Game_player) {
                    player_treasure.put(((Game_player)game_map[i][j]).getPlayerID(), ((Game_player)game_map[i][j]).getTreasure_got());
                    System.out.print("P" + ((Game_player)game_map[i][j]).getPlayerID() + " ");
                } else if (game_map[i][j] instanceof Treasure) {
                    System.out.print("T  ");
                } else {
                    System.out.print("O  ");
                }
            }
            System.out.print("\n");
        }
        Iterator iter = player_treasure.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            System.out.println("Player " + entry.getKey() + " get " + entry.getValue() + " tresure(s).");
        }
    }

    @Override
    public Server_interface CreateServer(int size, int treasure_count) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet.");
        Server_impl server_obj = new Server_impl(size, treasure_count);
        Server_interface server_stub = (Server_interface) UnicastRemoteObject.exportObject(server_obj, 0);
        return server_stub;
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    @Override
    public void setPrimaryServer(Server_interface ps) throws RemoteException {
        //exclusion?
        primaryServer = ps;
    }

    @Override
    public void setBackupServer(Server_interface bs) throws RemoteException {
        //exclusion?
        backupServer = bs;
    }

    public Server_interface getBackupServer() {
        return backupServer;
    }

    public Server_interface getPrimaryServer() {
        return primaryServer;
    }
}