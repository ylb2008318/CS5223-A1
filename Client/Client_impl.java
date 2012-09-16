
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Client_impl implements Client_interface {

    private int playerID;
    private int size;
    private boolean connected;
    private boolean inGame;

    public Client_impl() throws RemoteException {
        playerID = 0;
    }

    public void setPlayerID(int ID) {
        playerID = ID;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setConnected(boolean bool) {
        this.connected = bool;
    }

    public void setinGame(boolean bool) {
        this.inGame = bool;
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
}