package com.example.pac_man.model.ghosts;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlueGhost extends Ghost{
    private static final int VALUE = 4;

    public BlueGhost(int posX, int posY, Direction direction, Direction nextMove, Grid grid) {
        super(posX, posY, direction, nextMove, grid);
    }

    @Override
    public Grid getNextGrid(Grid grid) {
        // on a la grille et sa position courante et on veux un nouveau des deux
        this.setGrid(grid);
        // nouvelle direction
        this.setRandomDirection();
        // nouvelle grille mise à jour
        this.updateGrid(this.getGrid(), this.getDirection(), this.getPosX(), this.getPosY(), BlueGhost.VALUE);
        // nouvelle position (on le fait après la mise à jour de la grille
        // parce qu'on a besoin de la position de départ du fantôme
        this.move();
        return this.getGrid();
    }

    // BLUE GHOST AI

    private void setRandomDirection() {
        Boolean validDirection = false;
        Boolean ghostDeadLocked = checkDeadEnd();
        if(!ghostDeadLocked)
            do {
                final int randDirection = new Random().nextInt(4);
                switch (randDirection) {
                    case 0 : // UP
                        validDirection = changeDirection(Direction.UP);
                        break;
                    case 1 : // DOWN
                        validDirection = changeDirection(Direction.DOWN);
                        break;
                    case 2 : // LEFT
                        validDirection = changeDirection(Direction.LEFT);
                        break;
                    case 3 : // RIGHT
                        validDirection = changeDirection(Direction.RIGHT);
                        break;
                    default:
                        break;
                }
            } while (!validDirection);
        else
            this.setDirection(reverseDirection(this.getDirection()));
    }

    private Boolean checkDeadEnd() {
        boolean deadEnd = true;
        List<Direction> otherDirections = getOtherDirections(reverseDirection(this.getDirection()));
        for (Direction otherDirection : otherDirections) {
            if(isDirectionAvailable(otherDirection, this.getPosX(), this.getPosY(), this.getGrid()))
                deadEnd = false;
        }
        return deadEnd;
    }

    private List<Direction> getOtherDirections(Direction direction) {
        List<Direction> otherDirections = new ArrayList<>();
        if(direction != Direction.UP)
            otherDirections.add(Direction.UP);
        if(direction != Direction.RIGHT)
            otherDirections.add(Direction.RIGHT);
        if(direction != Direction.DOWN)
            otherDirections.add(Direction.DOWN);
        if(direction != Direction.LEFT)
            otherDirections.add(Direction.LEFT);
        return otherDirections;
    }

    private Boolean changeDirection(Direction direction) {
        Boolean validDirection = false;
        if (isDirectionAvailable(direction, this.getPosX(), this.getPosY(), this.getGrid()) && dontBackTrack(direction)) {
            validDirection = true;
            this.setDirection(direction);
        }

        return validDirection;
    }

    private boolean dontBackTrack(Direction direction) {
        return direction != reverseDirection(this.getDirection());
    }

    private Direction reverseDirection(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case RIGHT:
                return Direction.LEFT;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            default:
                break;
        }
        return null;
    }
}
