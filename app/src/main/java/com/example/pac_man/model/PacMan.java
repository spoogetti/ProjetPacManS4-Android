package com.example.pac_man.model;

import java.io.Serializable;

public class PacMan implements Serializable {
    int posX;
    int posY;
    Direction direction;
    Direction nextMove;
    int score = 0;

    public PacMan(int posX, int posY, Direction direction, Direction nextMove) {
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
        this.nextMove = nextMove;
    }

    public PacMan clone() {
        return new PacMan(this.posX, this.posY, this.direction, this.nextMove);
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
