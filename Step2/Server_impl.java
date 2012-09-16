
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ghome
 */
public class Server_impl implements Server_interface {

    private int size;
    private int treasure_count;
    private Map<Integer, Client_interface> player_info; // List of client to notify
    private int game_stat; // 0:created,1:started,2:ended
    private Map_obj[][] game_map;
    private int max_player_ID;
    private Map<Integer, Game_player> player_list;
    private Lock joinlock = new ReentrantLock();
    private Lock connectlock = new ReentrantLock();
    private ReadWriteLock moveLock = new ReentrantReadWriteLock(false);
    private int curr_treasure;
    private Server_interface backupServer; 
    private int primaryServerID, backupServerID;
    private boolean isPrimaryServer;

    public Server_impl() throws RemoteException {
        super();
    }

    public Server_impl(int size, int treasure_count) throws RemoteException {
        this.size = size;
        this.treasure_count = treasure_count;
        player_info = new HashMap<Integer, Client_interface>();
        game_stat = 2;
        game_map = new Map_obj[this.size][this.size];
        max_player_ID = 0;
        player_list = new HashMap<Integer, Game_player>();
    }

    @Override
    public void connectServer(Client_interface client_obj) throws RemoteException {
        connectlock.lock();
        try {
            if (!player_info.containsValue(client_obj)) {
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
        boolean endGame = false;
        connectlock.lock();
        try {
            if (player_info.containsKey(playerID)) {
                player_info.remove(playerID);
                joinlock.lock();
                try {
                    moveLock.writeLock().lock();
                    try {
                        player_list.remove(playerID);
                        if (player_list.isEmpty() && (game_stat==1 || game_stat==0)) {
                            endGame = true;
                        }
                    } finally {
                        moveLock.writeLock().unlock();
                    }
                } finally {
                    joinlock.unlock();
                }
                System.out.println("A Player is disconnected - PlayerID : " + playerID);
                if(endGame) {
                    endGame();
                }
                
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
            if (game_stat == 0) {
                if (player_list.size() < 9) {
                    Iterator iter = player_list.entrySet().iterator();
                    while (iter.hasNext()) {
                        Entry entry = (Entry) iter.next();
                        if ((Integer) entry.getKey() == playerID) {
                            result = false;
                            System.out.println("Player : " + playerID + " tried to join the game. But he is already in.");
                            break;
                        }
                    }
                    if (result) {
                        player_list.put(playerID, new Game_player(playerID));
                        System.out.println("Player : " + playerID + " joined the game.");
                    }
                } else {
                    System.out.println("Player : " + playerID + " tried to join the game. But no place is reserved for him.");
                    result = false;
                }
            } else if (game_stat == 1) {
                System.out.println("Player : " + playerID + " tried to join the game. But game is already started.");
                result = false;
            } else if (game_stat == 2) {
                System.out.println("Player : " + playerID + " tried to join the game. Creat game");
                creatGame();
                player_list.put(playerID, new Game_player(playerID));
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
    public synchronized boolean move(int playerID, int direction) throws RemoteException {
        boolean moved = true;
        String outputS = null;
        int temp_stat;
        joinlock.lock();
        try {
            temp_stat = game_stat;
        } finally {
            joinlock.unlock();
        }
        if (temp_stat != 1) {
            outputS = "Player : " + playerID + " trying to move. But the game is not started";
            return false;
        }
        moveLock.writeLock().lock();
        try {
            Game_player aPlayer = null;
            Iterator iter = player_list.entrySet().iterator();
            while (iter.hasNext()) {
                Entry entry = (Entry) iter.next();
                if ((Integer) entry.getKey() == playerID) {
                    aPlayer = (Game_player)entry.getValue();
                    break;
                }
            }
            if(aPlayer == null) {
                moved = false;
            }
            //System.out.println("Player : " + playerID + " at (" + aPlayer.getX() + "," + aPlayer.getY() + ")");
            int newX = -1;
            int newY = -1;
            switch (direction) {
                case 1://N
                    newX = aPlayer.getX() - 1;
                    newY = aPlayer.getY();
                    //System.out.print("North\r\n");
                    break;
                case 2://S
                    newX = aPlayer.getX() + 1;
                    newY = aPlayer.getY();
                    //System.out.print("South\r\n");
                    break;
                case 3://W
                    newX = aPlayer.getX();
                    newY = aPlayer.getY() - 1;
                    //System.out.print("West\r\n");
                    break;
                case 4://E
                    newX = aPlayer.getX();
                    newY = aPlayer.getY() + 1;
                    //System.out.print("Est\r\n");
                    break;
                default:
                    break;
            }

            if ((newX < 0) || (newX >= size) || (newY < 0) || (newY >= size)) {
                // out of bound
                outputS = "Player : " + playerID + " (" + newX + "," + newY + ") move out of bound ";
                moved = false;
            } else {
                if (game_map[newX][newY] == null) {
                    // Nothing, move
                    game_map[newX][newY] = game_map[aPlayer.getX()][aPlayer.getY()];
                    game_map[aPlayer.getX()][aPlayer.getY()] = null;
                    ((Game_player) aPlayer).setXY(newX, newY);
                    outputS = "Player : " + playerID + " moved to (" + newX + "," + newY + ").";
                } else if (game_map[newX][newY] instanceof Game_player) {
                    // A player is there cant move
                    outputS = "Player : " + playerID + " cannot move to (" + newX + "," + newY + ") another player :" + ((Game_player)game_map[newX][newY]).getPlayerID() + " is there";
                    moved = false;
                } else if (game_map[newX][newY] instanceof Treasure) {
                    // A treasure is there, move and take it
                    game_map[newX][newY] = game_map[aPlayer.getX()][aPlayer.getY()];
                    game_map[aPlayer.getX()][aPlayer.getY()] = null;
                    ((Game_player) aPlayer).setXY(newX, newY);
                    ((Game_player) aPlayer).addTreasure();
                    curr_treasure--;
                    outputS = "Player : " + playerID + " moved to (" + newX + "," + newY + ") and get a treasure.";
                }
            }
        } finally {
            moveLock.writeLock().unlock();
        }
        backupServer.printStat(outputS);
        if (moved) {
            publishInfo();
        }
        if (curr_treasure == 0) {
            outputS = "No Treasure anymore, the game will ended";
            backupServer.printStat(outputS);
            endGame();
        }
        
        return moved;
    }

    private void creatGame() {
        player_list.clear();
        game_stat = 0;
        //start 20s count
        new Timer(true).schedule(new TimerTask() {

            int time_remain = 5;

            @Override
            public void run() {
                System.out.println(time_remain + "s to game start.");
                if (time_remain-- == 0) {
                    joinlock.lock();
                    try {
                        if(game_stat==0) {
                            game_stat = 1;
                            initMap();
                            try {
                                publishInfo();
                            } catch (Exception e) {
                                System.err.println("Server exception: " + e.toString());
                            }
                        }
                    } finally {
                        joinlock.unlock();
                    }
                    this.cancel();
                }
            }
        }, 1000, 1000);
    }

    public void endGame() {
        joinlock.lock();
        try {
            game_stat = 2;
            System.out.println("Game Ended.");
        } finally {
            joinlock.unlock();
        }
    }

    private void initMap() {
        // put treasures and players
        moveLock.writeLock().lock();
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    game_map[i][j] = null;
                }
            }
            
            curr_treasure = treasure_count;
            Random random = new Random();
            int tempX = 0;
            int tempY = 0;
            
            // put player
            Iterator iter = player_list.entrySet().iterator();
            while (iter.hasNext()) {
                Entry entry = (Entry) iter.next();
                do {
                    tempX = random.nextInt(size);
                    tempY = random.nextInt(size);
                } while (game_map[tempX][tempY] != null);
                game_map[tempX][tempY] = (Map_obj) entry.getValue();
                ((Game_player) game_map[tempX][tempY]).setXY(tempX, tempY);
            }

            // put treasure
            for (int i = 0; i < treasure_count; i++) {
                do {
                    tempX = random.nextInt(size);
                    tempY = random.nextInt(size);
                } while (game_map[tempX][tempY] != null);
                game_map[tempX][tempY] = new Treasure(tempX, tempY);
            }
            
            creatBackupServer();
            
            System.out.println("The game is started.");
        } finally {
            moveLock.writeLock().unlock();
        }
    }

    private void creatBackupServer() {
        Iterator iter = player_info.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            if (player_list.containsKey((Integer) entry.getKey()) && (Integer)entry.getKey() != this.primaryServerID) {
                try {
                    backupServerID = (Integer)entry.getKey();
                    backupServer = ((Client_interface) entry.getValue()).createServer(this.size,this.treasure_count);
                    backupServer.setPlayer_info(player_info);
                    backupServer.upToDate(game_map);
                    System.out.println("Backup Server is created");
                } catch (RemoteException ex) {
                    Logger.getLogger(Server_impl.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        
        // up to date backupServer in clients
        Iterator iter2 = player_info.entrySet().iterator();
        while (iter2.hasNext()) {
            Entry entry2 = (Entry) iter2.next();
            if (player_list.containsKey((Integer) entry2.getKey())) {
                try {
                    ((Client_interface) entry2.getValue()).setBackupServer(backupServer);
                } catch (RemoteException ex) {
                    Logger.getLogger(Server_impl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void publishInfo() throws RemoteException {
        moveLock.readLock().lock();
        try {
            connectlock.lock();
            try {
                // up to date backup server
                try{
                    backupServer.upToDate(game_map);
                } catch (ConnectException ex){
                    // remove bs and reset a bs                            
                }
                
                // up to date clients
                Iterator iter = player_info.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry entry = (Entry) iter.next();
                    if (player_list.containsKey((Integer) entry.getKey())) {
                        try{
                            ((Client_interface) entry.getValue()).display(game_map);
                        } catch (ConnectException ex){
                            // remove client verify if it is a bs                            
                        }
                    }
                }
            } finally {
                connectlock.unlock();
            }
        } finally {
            moveLock.readLock().unlock();
        }
    }

    @Override
    public boolean upToDate(Map_obj[][] game_map) throws RemoteException {
        moveLock.readLock().lock();
        try {
            this.game_map = game_map;
        } finally {
            moveLock.readLock().unlock();
        }
        return true;
    }

    @Override
    public boolean setPlayer_info(Map<Integer, Client_interface> player_info) throws RemoteException {
        connectlock.lock();
        try {
            this.player_info = player_info;
        } finally {
            connectlock.unlock();
        }
        return true;
    }

    @Override
    public void printStat(String traceCpoy) throws RemoteException {
        System.out.println(traceCpoy);
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    @Override
    public void setPrimaryServerID(int primaryServerID) throws RemoteException {
        this.primaryServerID = primaryServerID;
    }

    @Override
    public synchronized void becomePS() {
        if(!isPrimaryServer){
            // become Primary Server
            // create player list
            int i, j;
            Map<Integer,Integer> player_treasure = new HashMap<Integer,Integer>();
            for (i = 0; i < size; i++) {
                for (j = 0; j < size; j++) {
                    if (game_map[i][j] instanceof Game_player) {
                        player_list.put(((Game_player)game_map[i][j]).getPlayerID(), (Game_player)game_map[i][j]);
                    }
                }
            }
            
            // remove inexiste client
            player_list.remove(primaryServerID);
            player_info.remove(primaryServerID);
            
            primaryServerID = backupServerID;
            // up to date primaryserver in clients
            Iterator iter2 = player_info.entrySet().iterator();
            while (iter2.hasNext()) {
                Entry entry2 = (Entry) iter2.next();
                if (player_list.containsKey((Integer) entry2.getKey())) {
                    try {
                        ((Client_interface) entry2.getValue()).setPrimaryServer((Server_interface) UnicastRemoteObject.exportObject(this, 0));
                    } catch (RemoteException ex) {
                        Logger.getLogger(Server_impl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            creatBackupServer();
        }   
    }
}
