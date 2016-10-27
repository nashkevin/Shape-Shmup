package main.java.misc;
import java.awt.Point;

public class PlayerEvent {
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private Point click;

    public PlayerEvent(boolean up, boolean down, boolean left, boolean right, Point click) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.click = click;
    }
}


