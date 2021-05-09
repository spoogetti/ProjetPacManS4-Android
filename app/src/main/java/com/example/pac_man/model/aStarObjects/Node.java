package com.example.pac_man.model.aStarObjects;

public class Node {
    private Position pos;

    public Node(Position pos) {
        this.pos = pos;
    }

    /**
     * GETTER & SETTER
     */

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
