import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Client_impl implements Client_interface {

    private Server_interface primaryServer;
    private Server_interface backupServer;
    private int playerID;
    private int size;
    private boolean connected;
    private boolean inGame;

    public Client_impl() throws RemoteException {
        playerID = 0;
        connected = false;
        inGame = false;
    }

    @Override
    public void setPlayerID(int ID) {
        playerID = ID;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    public void setConnected(boolean bool) {
        this.connected = bool;
    }

    public void setinGame(boolean bool) {
        this.inGame = bool;
    }

    @Override
    public void setPrimaryServer(Server_interface ps) throws RemoteException {
        primaryServer = ps;

        if (this.primaryServer == null) {
            System.out.println("Client : The PS is NULL.");
        } else {
            System.out.println("Client : Primary Server is up to date.");
        }
    }

    @Override
    public void setBackupServer(Server_interface bs) throws RemoteException {
        backupServer = bs;

        if (this.backupServer == null) {
            System.out.println("Client : The BS is NULL.");
        } else {
            System.out.println("Client : Backup Server is up to date.");
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getSize() {
        return this.size;
    }

    public boolean getinGame() {
        return this.inGame;
    }

    public boolean getConnected() {
        return this.connected;
    }

    public Server_interface getBackupServer() {
        return backupServer;
    }

    public Server_interface getPrimaryServer() {
        return primaryServer;
    }

    @Override
    public void display(Map_obj[][] game_map) {
        int i, j;
        Map<Integer, Integer> player_treasure = new HashMap<Integer, Integer>();
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (game_map[i][j] instanceof Game_player) {
                    player_treasure.put(((Game_player) game_map[i][j]).getPlayerID(), ((Game_player) game_map[i][j]).getTreasure_got());
                    System.out.print("P" + ((Game_player) game_map[i][j]).getPlayerID() + " ");
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
            System.out.println("Player " + entry.getKey() + " got " + entry.getValue() + " treasure(s).");
        }
    }

    @Override
    public Server_interface createServer(int size, int treasure_count) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet.");
        Server_impl server_obj = new Server_impl(size, treasure_count);
        Server_interface server_stub = (Server_interface) UnicastRemoteObject.exportObject(server_obj, 0);
        System.out.println("A new server has been created." + server_stub.toString());
        return server_stub;
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    @Override
    public void setBStoPS() throws RemoteException {
        this.primaryServer = this.backupServer;
        this.backupServer = null;

        if (this.primaryServer == null) {
            System.out.println("Client : The PS is NULL.");
        } else {
            System.out.println("Client : Switch Server.");
        }
    }
}