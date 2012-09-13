import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 *
 * @author ghome
 */
public class Server_impl implements Server_interface {

    private int size;
    private int treasure_count;
    private Map<Integer,Client_interface> player_info; // List of client to notify
    private int game_stat; // 0:created,1:started,2:ended
    private Map_obj[][] game_map;
    private int max_player_ID;
    private Map<Integer,Game_player> player_list;
    private Lock joinlock = new ReentrantLock();
    private Lock connectlock = new ReentrantLock();
    private ReadWriteLock moveLock = new ReentrantReadWriteLock(false);
    private int curr_treasure;
    
    public Server_impl() throws RemoteException {
        super();
    }

	public Server_impl(int size, int treasure_count) throws RemoteException {
        this.size = size;
        this.treasure_count = treasure_count;
        player_info = new HashMap<Integer,Client_interface>();
        game_stat = 2;
        game_map = new Map_obj[this.size][this.size];
        max_player_ID = 0;
        player_list = new HashMap<Integer,Game_player>();
    }
    

    @Override
    public synchronized void connectServer(Client_interface client_obj) throws RemoteException {
	connectlock.lock();
        try {
            if(!player_info.containsValue(client_obj)) {
                player_info.put(++max_player_ID, client_obj);
                client_obj.setPlayerID(max_player_ID);
                client_obj.setSize(size);
                // set treasure
                System.out.println("A Player is connected - PlayerID : " + max_player_ID);
            } else {
                System.out.println("A Player is already connected.");
            }
        } finally {
            connectlock.unlock();
        }
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
   System.out.println("Player : " + playerID + " tried to join the game. Creat game");             
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
        boolean result = true;
        int temp_stat;
        joinlock.lock();
        try {
            temp_stat = game_stat;
        } finally {
            joinlock.unlock();
        }
        if(temp_stat!=1) {
            return false;
        } 
        moveLock.writeLock().lock();
        try {
            Game_player aPlayer = player_list.get(playerID);
            //moveLock.writeLock().lock();
            int newX = -1;
            int newY = -1;
            switch(direction){
                case 0://N
                    newX = aPlayer.getX();
                    newY = aPlayer.getY()-1;
                    break;
                case 1://S
                    newX = aPlayer.getX();
                    newY = aPlayer.getY()+1;
                    break;
                case 2://W
                    newX = aPlayer.getX()-1;
                    newY = aPlayer.getY();
                    break;
                case 3://E
                    newX = aPlayer.getX()+1;
                    newY = aPlayer.getY();
                    break;
                default:
                    break;  
            }

            if((newX < 0)||(newX>=size)||(newY < 0)||(newY>=size)) {
                // out of bound
                result = false;
            } else {
                if(game_map[newX][newY]==null){
                    // Nothing, move
                    game_map[newX][newY] = game_map[aPlayer.getX()][aPlayer.getY()];
                    game_map[aPlayer.getX()][aPlayer.getY()] = null;
                    ((Game_player)aPlayer).setXY(newY, newY);
                } else if(game_map[newX][newY] instanceof Game_player) {
                    // A player is there cant move
                    result = false;
                } else if(game_map[newX][newY] instanceof Treasure) {
                    // A treasure is there, move and take it
                    game_map[newX][newY] = game_map[aPlayer.getX()][aPlayer.getY()];
                    game_map[aPlayer.getX()][aPlayer.getY()] = null;
                    ((Game_player)aPlayer).setXY(newY, newY);
                    ((Game_player)aPlayer).addTreasure();
                    curr_treasure--;
                }
            }
        } finally {
            moveLock.writeLock().unlock();
        }
        if(result) {
            publishInfo();
        }
        if(curr_treasure==0) {
            endGame();
        }

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
    
    private void endGame() {
        joinlock.lock();
        try {
            game_stat = 2;
            initMap();
        } finally {
            joinlock.unlock();
        }
    }
    
    private void initMap() {
        // put treasures and players
        moveLock.writeLock().lock();
        try {
            curr_treasure = treasure_count;
            Random random = new Random();
            int tempX = 0;
            int tempY = 0;
            // put player
            for(int i=0;i<player_list.size();i++) {
                do {
                    tempX = random.nextInt(size);
                    tempY = random.nextInt(size);
                } while(game_map[tempX][tempY]==null);
                game_map[tempX][tempY] = player_list.get(i);
                ((Game_player)game_map[tempX][tempY]).setXY(tempX, tempY);
            }
            
            // put treasure
            for(int i=0;i<treasure_count;i++) {
                do {
                    tempX = random.nextInt(size);
                    tempY = random.nextInt(size);
                } while(game_map[tempX][tempY]==null);
                game_map[tempX][tempY] = new Treasure(tempX, tempY);
            }
System.out.println("The game is started.");
        } finally {
            moveLock.writeLock().unlock();
        }

    }
    
    private void publishInfo() throws RemoteException{
        moveLock.readLock().lock();
        try {
            connectlock.lock();
            try {
                Iterator iter = player_info.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry entry = (Entry) iter.next();
                    if (player_list.containsKey((Integer) entry.getKey())) {
                        ((Client_interface) entry.getValue()).display(game_map);
                    }
                }
            } finally {
                connectlock.unlock();
            }
        } finally {
            moveLock.readLock().unlock();
        }
    }
}
