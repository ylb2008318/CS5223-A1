/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ghome
 */
public class Map_obj implements java.io.Serializable {

    protected int x, y;

    public Map_obj() {
    }

    public Map_obj(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
