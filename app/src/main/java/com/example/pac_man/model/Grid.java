package com.example.pac_man.model;

import com.example.pac_man.model.ghosts.Ghost;
import com.example.pac_man.model.ghosts.YellowGhost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grid implements Serializable {
    private int sizeX;
    private int sizeY;

    private ArrayList<ArrayList<Integer>> grid;

    private PacMan pacMan;
    private List<Ghost> ghosts;

    public Grid(String[] grid) {
        String gridSize = grid[0];
        sizeY = Integer.parseInt(gridSize.split(",")[0]);
        sizeX = Integer.parseInt(gridSize.split(",")[1]);

        this.grid = convertToIntArrays(grid);
    }

    private ArrayList<ArrayList<Integer>> convertToIntArrays(String[] grid) {
        String gridSize = grid[0];
        sizeY = Integer.parseInt(gridSize.split(",")[0]);
        sizeX = Integer.parseInt(gridSize.split(",")[1]);

        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        for(int i=0; i<sizeY; i++) { // i c'est le numéro de ligne
            ArrayList<Integer> parsedRow = new ArrayList<>();

            for (int j = 0; j < sizeX; j++) { // j c'est le numéro de colonne
                String[] row = grid[i+1].split(",");
                String col = row[j];
                parsedRow.add(Integer.parseInt(col));
            }
            result.add(parsedRow);
        }
        return result;
    }

    /**
     * GETTER & SETTER
     */

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public List<ArrayList<Integer>> getGrid() {
        return grid;
    }

    public void setGrid(ArrayList<ArrayList<Integer>> grid) {
        this.grid = grid;
    }

    public PacMan getPacMan() {
        return pacMan;
    }

    public void setPacMan(PacMan pacMan) {
        this.pacMan = pacMan;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void setGhosts(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    public Ghost getGhost(Class<?> ghostClass) {
        List<Ghost> ghosts = this.getGhosts();
        Ghost ghostSeeked = null;
        for (Ghost ghost : ghosts) {
            if(ghost.getClass().equals(ghostClass)) {
                ghostSeeked = ghost;
            }
        }
        return ghostSeeked;
    }
}
