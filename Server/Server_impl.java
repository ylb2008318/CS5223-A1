import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author ghome
 */
public class Server_impl implements Server_interface {

    private int size;
    private int treasure_count;
    private Map<Integer,Client_info> player_info; // List of client to notify
    private int game_stat; // 0:created,1:started,2:ended
    private Map_obj[][] game_map;
    private int max_player_ID;
    private Map<Integer,Game_player> player_list;
    private Lock joinlock = new ReentrantLock();
    private Lock connectlock = new ReentrantLock();
    
    public Server_impl() throws RemoteException {
        super();
    }

	public Server_impl(int size, int treasure_count) throws RemoteException {
        this.size = size;
        this.treasure_count = treasure_count;
        player_info = new HashMap<Integer,Client_info>();
        game_stat = 2;
        game_map = new Map_obj[this.size][this.size];
        max_player_ID = 0;
        player_list = new HashMap<Integer,Game_player>();
    }
    

    @Override
    public void connectServer(Client_info client_obj) throws RemoteException {
	connectlock.lock();
        try {
            player_info.put(++max_player_ID, client_obj);
            System.out.println("A Player is connected - PlayerID : " + max_player_ID);
        } finally {
            connectlock.unlock();
        }
        client_obj.setPlayerID(max_player_ID);
    }
 

    @Override
    public boolean disconnectServer(int playerID) throws RemoteException {
        boolean result = true;
        connectlock.lock();
        try {
            if (player_info.containsKey(playerID)) {
                player_info.remove(playerID);
                System.out.println("A Player is disconnected - PlayerID : " + max_player_ID);
            } else {
                result = false;
            }
        } finally {
            connectlock.unlock();
        }
        return result;
    }

    @Override
    public boolean joinGame(int playerID) throws RemoteException {      
        boolean result = true;
        joinlock.lock();
        try {
            if (game_stat==0) {
                if (player_list.size() < 9) {
                    for (int i=0; i < player_list.size(); i++) {
                        if (player_list.get(i).getPlayerID() == playerID) {
                            result = false;
                            System.out.println("Player : " + playerID + " tried to join the game. But he is already in.");
                            break;
                        }
                    }
                    if (result) {
                        player_list.put(playerID,new Game_player(playerID));
                        System.out.println("Player : " + playerID + " joined the game.");
                    }
                } else {
                    System.out.println("Player : " + playerID + " tried to join the game. But no place is reserved for him.");
                    result = false;
                }
            } else if (game_stat==1) {
                System.out.println("Player : " + playerID + " tried to join the game. But game is already started.");
                result = false;
            } else if (game_stat==2) {
                creatGame();
                player_list.put(playerID,new Game_player(playerID));
                System.out.println("The game is created.\r\nPlayer : " + playerID + " joined the game.");
            } else {
                result = false;
            }
        } finally {
            joinlock.unlock();
        }
        return result;
    }

    @Override
    public boolean move(int playerID, int direction) throws RemoteException {
        player_list.get(playerID);
        
        return false;
    }
    
    private void creatGame() {
        player_list.clear();
        game_stat = 0;
        //start 20s count
        new Timer(true).schedule(new TimerTask() {   
            @Override
            public void run() {   
                joinlock.lock();
                try {
                    game_stat = 1;
                    initMap();
                } finally {
                    joinlock.unlock();
                }
            }   
        }, 20000);
    }
    
    private void initMap() {
        // put treasures and players
    }
}
