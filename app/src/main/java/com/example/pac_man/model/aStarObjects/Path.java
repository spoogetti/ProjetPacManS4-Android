package com.example.pac_man.model.aStarObjects;

import java.util.Deque;
import java.util.LinkedList;

public class Path implements Comparable<Path>{

    private Deque<Node> path;

    // underestimate : distance of partial path travelled +
    // straight line distance from last node in path to goal (heuristic using manhattan distance)
    double underestimate = 0;

    public Path() {
        this.path = new LinkedList<>();
    }

    public Path(Path p) {
        this.path = p.path;
    }

    public Path(Deque<Node> path) {
        this.path = path;
    }

    /**
     * GETTER & SETTER
     */

    public Deque<Node> getPath() {
        return path;
    }

    public void setPath(Deque<Node> path) {
        this.path = path;
    }

    public double getUnderestimate() {
        return underestimate;
    }

    public void setUnderestimate(double underestimate) {
        this.underestimate = underestimate;
    }

    @Override
    public int compareTo(Path p) {
        // TODO verifier l'ordre
        if(this.getUnderestimate() > p.getUnderestimate()) return 1;
        else if(this.getUnderestimate() < p.getUnderestimate()) return -1;
        else return 0;
    }
}
