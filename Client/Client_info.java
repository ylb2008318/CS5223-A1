import java.io.Serializable;

public class Client_info implements Serializable {
    public int playerID;
    public int size;
  
    public void setPlayerID(int ID) {
	playerID=ID;
    }

    public void setSize(int size) {
	this.size=size;
    }

     public void display(Map_obj[][] game_map) {
        int i,j;
		for (i=0;i<size;i++){
			for(j=0;j<size;j++){
				if (game_map[i][j] instanceof Game_player) System.out.print("P  ");
				else if (game_map[i][j] instanceof Treasure) System.out.print("T  ");
				else System.out.print("O  ");
			}
			System.out.print("\n");
		}
    }
}