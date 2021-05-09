package com.example.pac_man.model;

public enum Direction {
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3);

    private int value;

    Direction(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
