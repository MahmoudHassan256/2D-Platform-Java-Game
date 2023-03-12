package objects;

import main.Game;

public class Spike extends GameObject {
    public Spike(int x, int y, int objectType) {
        super(x, y, objectType);
        initializeHitBox(32, 16);
        xDrawOffset = 0;
        yDrawOffset = (int) (16 * Game.SCALE);
        hitBox.y += yDrawOffset;
    }
}
