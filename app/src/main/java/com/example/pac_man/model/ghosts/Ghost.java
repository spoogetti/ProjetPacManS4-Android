package com.example.pac_man.model.ghosts;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Ghost implements Serializable  {
    private int posX;
    private int posY;
    private Direction direction;
    private Direction nextMove;
    private Grid grid;
    private int lastEatenValue = 6; // 6 pour une pastille, on suppose qu'au départ le fantôme est sur un pastille


    Ghost(int posX, int posY, Direction direction, Direction nextMove, Grid grid) {
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
        this.nextMove = nextMove;
        this.grid = grid;
    }

    public abstract Grid getNextGrid(Grid grid);

    void move() {
        switch (this.getDirection()) {
            case UP:
                this.setPosY(this.getPosY()-1);
                break;
            case DOWN:
                this.setPosY(this.getPosY()+1);
                break;
            case LEFT:
                this.setPosX(this.getPosX()-1);
                break;
            case RIGHT:
                this.setPosX(this.getPosX()+1);
                break;
        }
    }

    Boolean isDirectionAvailable(Direction direction, int posX, int posY, Grid grid) {
        boolean directionAvailable = false;
        switch (direction) {
            case UP:
                directionAvailable = noWallAtPos(grid, posX, posY - 1);
                break;
            case DOWN:
                directionAvailable = noWallAtPos(grid, posX, posY + 1);
                break;
            case LEFT:
                directionAvailable = noWallAtPos(grid, posX - 1, posY);
                break;
            case RIGHT:
                directionAvailable = noWallAtPos(grid, posX + 1, posY);
                break;
        }
        return directionAvailable;
    }

    private boolean noWallAtPos(Grid grid, int x, int y) {
        return grid.getGrid().get(y).get(x) != 5;
    }

    void updateGrid(Grid grid, Direction direction, int posX, int posY, int entityValue) {
        switch (direction) {
            case UP:
                updateGridValue(grid, posX, posY, entityValue, posY - 1, posX);
                break;
            case DOWN:
                updateGridValue(grid, posX, posY, entityValue, posY + 1, posX);
                break;
            case LEFT:
                updateGridValue(grid, posX, posY, entityValue, posY, posX - 1);
                break;
            case RIGHT:
                updateGridValue(grid, posX, posY, entityValue, posY, posX + 1);
                break;
        }
    }

    private void updateGridValue(Grid grid, int posX, int posY, int entityValue, int posY2, int posX2) {
        // remet à la même valeur la position où l'entité était avant
        grid.getGrid().get(posY)
                .set(posX, lastEatenValue);

        lastEatenValue =  grid.getGrid().get(posY2).get(posX2);
        if(lastEatenValue != 6 && lastEatenValue != 7) {
            lastEatenValue = 7; // si un fantome mange un autre fantome ou pacman,
            // il laisse une case vide sur son chemin // TODO trouver une alternative
        }

        // met l'entité sur sa nouvelle position
        grid.getGrid().get(posY2)
                .set(posX2, entityValue);
    }

    void updateGhostObjectInGrid() {
        List<Ghost> ghosts = grid.getGhosts();
        List<Ghost> ghostsUpdated = new ArrayList<>();

        for (Ghost ghost : ghosts) {
            if (this.equals(ghost))
                ghost = this;
            ghostsUpdated.add(ghost);
        }

        grid.setGhosts(ghostsUpdated);
    }

    /**
     * GETTER & SETTER
     */

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getNextMove() {
        return nextMove;
    }

    public void setNextMove(Direction nextMove) {
        this.nextMove = nextMove;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public int getLastEatenValue() {
        return lastEatenValue;
    }

    public void setLastEatenValue(int lastEatenValue) {
        this.lastEatenValue = lastEatenValue;
    }
}
