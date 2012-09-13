public class Game_player extends Map_obj {

    public Game_player(int x, int y) {
        super(x, y);
    }
    
    private int treasure_got;
    
    private int playerID;

    public Game_player(int playerID) {
        this.playerID = playerID;
        treasure_got = 0;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getTreasure_got() {
        return treasure_got;
    }
    
    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }
    
    public void addTreasure(){
        this.treasure_got++;
    }
}