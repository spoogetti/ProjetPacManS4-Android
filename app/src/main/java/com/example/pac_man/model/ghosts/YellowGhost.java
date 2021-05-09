package com.example.pac_man.model.ghosts;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;

public class YellowGhost extends Ghost {
    private static final int VALUE = 2;

    public YellowGhost(int posX, int posY, Direction direction, Direction nextMove, Grid grid) {
        super(posX, posY, direction, nextMove, grid);
    }

    @Override
    public Grid getNextGrid(Grid grid) {
        // on a la grille et sa position courante et on veux un nouveau des deux
        this.setGrid(grid);
        // nouvelle direction
        this.setDirection(this.goForward(this.getGrid()));
        // nouvelle grille mise à jour
        this.updateGrid(this.getGrid(), this.getDirection(), this.getPosX(), this.getPosY(), this.VALUE);
        // nouvelle position (on le fait après la mise à jour de la grille
        // parce qu'on a besoin de la position de départ du fantôme
        this.move();
        return this.getGrid();
    }

    // YELLOW GHOST AI

    // le fantôme va toujours tout droit, lorsqu'il rencontre un mur il va à droite,
    // si il ne peut pas, il va a gauche

    private Direction goForward(Grid grid) {
        if(isDirectionAvailable(this.getDirection(), this.getPosX(), this.getPosY(), grid)) {
            return this.getDirection();
        } else if(isDirectionAvailable( goRight(this.getDirection()), this.getPosX(), this.getPosY(), grid)) {
            return goRight(this.getDirection());
        } else if(isDirectionAvailable( goLeft(this.getDirection()), this.getPosX(), this.getPosY(), grid)) {
            return goLeft(this.getDirection());
        } else {
            return reverseDirection(this.getDirection());
        }
    }

    private Direction goRight(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.RIGHT;
            case DOWN:
                return Direction.LEFT;
            case RIGHT:
                return Direction.DOWN;
            case LEFT:
                return Direction.UP;
            default:
                return null;
        }
    }

    private Direction goLeft(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.LEFT;
            case DOWN:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.UP;
            case LEFT:
                return Direction.DOWN;
            default:
                return null;
        }
    }

    private Direction reverseDirection(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                return null;
        }
    }
}
