package com.example.pac_man.model;

import com.example.pac_man.model.ghosts.Ghost;

import java.io.Serializable;
import java.util.List;

public class PacManAction implements Serializable {
    private static final int VALUE = 1;

    private Grid grid;
    private PacMan pacMan;
    private List<Ghost> ghosts;

    public PacManAction(Grid grid, PacMan pacMan, List<Ghost> ghosts) {
        this.grid = grid;
        this.pacMan = pacMan;
        this.ghosts = ghosts;
    }

    public void refreshPacman(PacMan pacMan) {
        this.pacMan = pacMan;
    }

    public Grid getNextGrid(Grid grid) {
        this.setGrid(grid);
        movePacMan(this.getPacMan());
        this.getGrid().setPacMan(this.getPacMan());
        return this.getGrid();
    }

    private void movePacMan(PacMan pacMan) {
        // Si on a donné une nouvelle direction à pacman et quelle est disponible au prochain tour de boucle, elle devient sa nouvelle direction
        if(pacMan.getNextMove() != null && !pacMan.getDirection().equals(pacMan.getNextMove())) {
            PacMan pacImage = pacMan.clone();
            pacImage.setDirection(pacImage.getNextMove());
            Boolean nextDirectionAvailable = isDirectionAvailable(pacImage.getDirection(), pacImage.getPosX(), pacImage.getPosY(), this.grid);
            if(nextDirectionAvailable) {
                pacMan.setDirection(pacMan.getNextMove());
                pacMan.setNextMove(null);
            }
            // TODO gérer le cas quand pacman atteint la taille max de la grille sans qu'il y ai de mur pour le stopper
        }

        Boolean directionAvailable = isDirectionAvailable(this.pacMan.getDirection(), this.pacMan.getPosX(), this.pacMan.getPosY(), this.grid);
        if(directionAvailable) {
            updateGrid(this.grid, pacMan.getDirection(), pacMan.getPosX(), pacMan.getPosY());
            updatePacMan(pacMan);
        }
    }

    private void updatePacMan(PacMan pacMan) {
        switch (pacMan.getDirection()) {
            case UP:
                pacMan.setPosY(pacMan.getPosY()-1);
                break;
            case DOWN:
                pacMan.setPosY(pacMan.getPosY()+1);
                break;
            case LEFT:
                pacMan.setPosX(pacMan.getPosX()-1);
                break;
            case RIGHT:
                pacMan.setPosX(pacMan.getPosX()+1);
                break;
        }
    }

    private void updateGrid(Grid grid, Direction direction, int posX, int posY) {
        switch (direction) {
            case UP:
                if(grid.getGrid().get(posY - 1).get(posX) == 6) {
                    pacMan.setScore(pacMan.getScore() + 1);
                }

                // met à vide la position où l'entité était avant
                grid.getGrid().get(posY)
                        .set(posX, 7);

                // met l'entité sur sa nouvelle position
                grid.getGrid().get(posY - 1)
                        .set(posX, PacManAction.VALUE);
                break;
            case DOWN:
                if(grid.getGrid().get(posY + 1).get(posX) == 6) {
                    pacMan.setScore(pacMan.getScore() + 1);
                }

                grid.getGrid().get(posY)
                        .set(posX, 7);

                grid.getGrid().get(posY + 1)
                        .set(posX, PacManAction.VALUE);
                break;
            case LEFT:
                if(grid.getGrid().get(posY).get(posX - 1) == 6) {
                    pacMan.setScore(pacMan.getScore() + 1);
                }

                grid.getGrid().get(posY)
                        .set(posX, 7);

                grid.getGrid().get(posY)
                        .set(posX - 1, PacManAction.VALUE);
                break;
            case RIGHT:
                if(grid.getGrid().get(posY).get(posX + 1) == 6) {
                    pacMan.setScore(pacMan.getScore() + 1);
                }

                grid.getGrid().get(posY)
                        .set(posX, 7);

                grid.getGrid().get(posY)
                        .set(posX + 1, PacManAction.VALUE);
                break;
        }
    }

    private Boolean isDirectionAvailable(Direction direction, int posX, int posY, Grid grid) {
        boolean directionAvailable = false;
        switch (direction) {
            case UP:
                directionAvailable = checkIfWallAtPos(grid, posX, posY - 1);
                break;
            case DOWN:
                directionAvailable = checkIfWallAtPos(grid, posX, posY + 1);
                break;
            case LEFT:
                directionAvailable = checkIfWallAtPos(grid, posX - 1, posY);
                break;
            case RIGHT:
                directionAvailable = checkIfWallAtPos(grid, posX + 1, posY);
                break;
        }
        return directionAvailable;
    }

    private boolean checkIfWallAtPos(Grid grid, int x, int y) {
        return grid.getGrid().get(y).get(x) != 5;
    }

    /**
     * GETTER & SETTER
     */

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
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
}
