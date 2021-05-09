package com.example.pac_man.model.ghosts;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;
import com.example.pac_man.model.aStarObjects.Node;
import com.example.pac_man.model.aStarObjects.Path;
import com.example.pac_man.model.aStarObjects.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.example.pac_man.model.Direction.*;

public class RedGhost extends Ghost {
    private static final int VALUE = 3;
    private int pacPosX = 0;
    private int pacPosY = 0;

    public RedGhost(int posX, int posY, Direction direction, Direction nextMove, Grid grid) {
        super(posX, posY, direction, nextMove, grid);
    }

    @Override
    public Grid getNextGrid(Grid grid) {
        // on a la grille et sa position courante et on veux un nouveau des deux
        this.setGrid(grid);
        // nouvelle direction
        this.findPacman(this.getGrid());
        this.setDirection(this.bestDirToChasePacMan(this.pacPosX, this.pacPosY));
        // nouvelle grille mise à jour
        this.updateGrid(this.getGrid(), this.getDirection(), this.getPosX(), this.getPosY(), RedGhost.VALUE);
        // nouvelle position (on le fait après la mise à jour de la grille
        // parce qu'on a besoin de la position de départ du fantôme
        this.move();
        return this.getGrid();
    }



    // RED GHOST AI

    // find pacMan in grid
    private void findPacman(Grid grid) {
        int i = 0;
        int j;

        boolean pacManFound = false;

        // recherche du pacman dans la grille
        while(i < grid.getSizeY() && !pacManFound) {
            j = 0;
            while(j < grid.getSizeX() && !pacManFound) {
                if(grid.getGrid().get(i).get(j) == 1) {
                    this.pacPosY = i;
                    this.pacPosX = j;
                    pacManFound = true;
                }
                j++;
            }
            i++;
        }
    }

    /** underestimate : distance of partial path travelled + straight line distance from last node in path to goal (heuristic using manhattan distance ?)
      * Branch and bound with dynamic programming -> A*
      * create a list P (1*)
      * add the start Node S to P giving it one element  (2*)
      * until first path of P ends with G, or P is empty (3*)
      *      extract the first path from P               (4*)
      *      extends first path one step to all neighbors creating X new paths (5*)
      *      reject all paths with loops                 (6*)
      *          for all paths that end at the same node. keep only the shortest one (dynamic programming) (7*)
      *      add each remaining new paths to P           (8*)
      *      sort all paths by total underestimate, shortest first (9*)
      * found -> success, else -> failure.
      */
    private Path shortestPath() {
        List<Path> pathsList = new ArrayList<>(); // (1*)

        Position startingNodePosition = new Position(this.getPosX(), this.getPosY());
        Node start = new Node(startingNodePosition);
        Path startingPath = new Path(new LinkedList<Node>());
        startingPath.getPath().add(start);
        pathsList.add(startingPath); // (2*)

        Position endingNodePosition = new Position(this.pacPosX, this.pacPosY);
        Node end = new Node(endingNodePosition);

        Deque currentPath = pathsList.get(0).getPath();
        List<Path> newFoundPaths;
        Path optimalPath = null;

        boolean endReached = endReached(currentPath, end);
        while(!endReached && !pathsList.isEmpty()) {            // (3*)
            currentPath = pathsList.remove(0).getPath();  // (4*)
            newFoundPaths = getNewPaths(currentPath);           // (5*)
            filterLoops(pathsList, newFoundPaths);              // (6*)
            pathsList.addAll(newFoundPaths);                    // (8*)
            setUnderestimates(pathsList, endingNodePosition);
            Collections.sort(pathsList);                        // (9*)

            endReached = endReached(currentPath, end);
            if(endReached)
                optimalPath = new Path(currentPath);
        }

        if(endReached && optimalPath != null)
            return optimalPath;
        return null;
    }

    private Boolean endReached(Deque<Node> currentPath, Node end) {
        return isSameNode(currentPath.getLast().getPos(), end.getPos());
    }

    private void setUnderestimates(List<Path> pathsList, Position target) {
        int pathSize;
        int pathEndPosX;
        int pathEndPosY;

        int manhattanDist;
        for (Path path: pathsList) {
            pathSize = path.getPath().size();
            pathEndPosX = path.getPath().getLast().getPos().getX();
            pathEndPosY = path.getPath().getLast().getPos().getY();
            // d(A,B)=|X_{B}-X_{A}|+|Y_{B}-Y_{A}|
            manhattanDist = Math.abs(target.getX() - pathEndPosX) + Math.abs(target.getY() - pathEndPosY);
            path.setUnderestimate(pathSize + manhattanDist);
        }
    }

    private void filterLoops(List<Path> pathsList, List<Path> newFoundPaths) {
        boolean isLoop;
        boolean isLast;

        List<Path> newFoundPathsToDelete = new ArrayList<>();
        List<Path> pathsToDeleteFromMainPathsList = new ArrayList<>();

        Position oldPathLastNode;
        Position newPathLastNode;

        for (Path newPath: newFoundPaths) { // pour tous les nouveaux chemins
            for(Path oldPath: pathsList) {  // on itère sur tous les anciens chemins
                // si un ancien chemin contient la dernière node d'un des nouveaux chemins
                isLoop = nodeInPath(oldPath.getPath(), newPath.getPath().getLast());
                // si un chemin existant contient la dernière node d'un des nouveaux chemins et que cette node s'avère être la dernière pour les deux chemins

                oldPathLastNode = oldPath.getPath().getLast().getPos();
                newPathLastNode = newPath.getPath().getLast().getPos();
                isLast = isSameNode(oldPathLastNode, newPathLastNode);
                if(isLoop && !isLast) { // si le chemin est une boucle mais que la boucle n'arrive pas sur la fin d'un autre chemin
                    pathsToDeleteFromMainPathsList.add(oldPath);
                }
                else if(isLast) { // dans le cas où les deux chemins ont la même fin -> on compare la taille
                    if(newPath.getPath().size() > oldPath.getPath().size()) { // (7*)
                        newFoundPathsToDelete.add(newPath);
                    } else if (newPath.getPath().size() < oldPath.getPath().size()) {
                        pathsToDeleteFromMainPathsList.add(oldPath);
                    } else {
                        newFoundPathsToDelete.add(newPath);
                    }
                }
            }
        }

        pathsList.removeAll(pathsToDeleteFromMainPathsList);
        newFoundPaths.removeAll(newFoundPathsToDelete);

        pathsToDeleteFromMainPathsList = new ArrayList<>();
        newFoundPathsToDelete = new ArrayList<>();

        Position firstNodePos;
        Position lastNodePos;

        // suppression des boucles pour les nouveaux chemins
        // si une des nodes du chemin est la même que la dernière
        for (Path path: newFoundPaths) {
            // si le chemin boucle sur lui-même
            lastNodePos = path.getPath().peekLast().getPos();

            Iterator<Node> pathIterator = path.getPath().iterator();

            while (pathIterator.hasNext()) {
                firstNodePos = pathIterator.next().getPos();
                if(pathIterator.hasNext() && isSameNode(firstNodePos, lastNodePos)) {
                    newFoundPathsToDelete.add(path);
                }
            }
        }

        pathsList.removeAll(pathsToDeleteFromMainPathsList);
        newFoundPaths.removeAll(newFoundPathsToDelete);
    }

    private boolean isSameNode(Position firstNodePos, Position lastNodePos) {
        return firstNodePos.getX() == lastNodePos.getX() && firstNodePos.getY() == lastNodePos.getY();
    }

    private boolean nodeInPath(Deque<Node> path, Node last) {
        boolean nodeFound = false;
        for (Node node: path) {
            if(isSameNode(node.getPos(), last.getPos())) {
                nodeFound = true;
                break;
            }
        }
        return nodeFound;
    }

    private List<Path> getNewPaths(Deque path) {
        List<Path> newFoundPaths = new ArrayList<>();
        Node lastNode = (Node) path.peekLast();
        Position lastNodePos = lastNode.getPos();

        Direction dirToIgnore = dontBackTrack(path);

        // UP
        Path newPath = addNewPathIfAvailable(path, lastNodePos, UP);
        if(newPath != null && dirToIgnore != UP)
            newFoundPaths.add(newPath);
        // RIGHT
        newPath = addNewPathIfAvailable(path, lastNodePos, RIGHT);
        if(newPath != null && dirToIgnore != RIGHT)
            newFoundPaths.add(newPath);
        // DOWN
        newPath = addNewPathIfAvailable(path, lastNodePos, DOWN);
        if(newPath != null && dirToIgnore != DOWN)
            newFoundPaths.add(newPath);
        // LEFT
        newPath = addNewPathIfAvailable(path, lastNodePos, LEFT);
        if(newPath != null && dirToIgnore != LEFT)
            newFoundPaths.add(newPath);

        return newFoundPaths;
    }

    private Direction dontBackTrack(Deque path) {
        Iterator<Node> pathIterator = path.descendingIterator();
        if(path.size() > 1) {
            Node lastItem = pathIterator.next();
            Node secondLastItem = pathIterator.next();
            int difX = lastItem.getPos().getX() - secondLastItem.getPos().getX();
            int difY = lastItem.getPos().getY() - secondLastItem.getPos().getY();

            if (difX == -1)
                return RIGHT;
            else if (difX == 1)
                return LEFT;
            else if (difY == -1)
                return DOWN;
            else if (difY == 1)
                return UP;
        }
        return null;
    }

    private Path addNewPathIfAvailable(Deque path, Position lastNodePos, final Direction direction) {
        Position newPosition = lastNodePos;
        Node newNode;
        Path newPath = null;
        if (isDirectionAvailable(direction, lastNodePos.getX(), lastNodePos.getY(), this.getGrid())) {
            switch (direction) {
                case UP:
                    newPosition = new Position(lastNodePos.getX(), lastNodePos.getY() - 1);
                    break;
                case RIGHT:
                    newPosition = new Position(lastNodePos.getX() + 1, lastNodePos.getY());
                    break;
                case DOWN:
                    newPosition = new Position(lastNodePos.getX(), lastNodePos.getY() + 1);
                    break;
                case LEFT:
                    newPosition = new Position(lastNodePos.getX() - 1, lastNodePos.getY());
                    break;
                default:
                    break;
            }
            newNode = new Node(newPosition);
            newPath = new Path();
            newPath.getPath().addAll(path);
            newPath.getPath().add(newNode);
        }
        return newPath;
    }

    private Direction bestDirToChasePacMan(int pacPosX, int pacPosY) {
        Path bestPath = shortestPath();

        if(bestPath == null)
            // if he is above -> go up...
            return getNaiveDir(pacPosX, pacPosY);
        return getSmartDir(bestPath);
    }

    private Direction getSmartDir(Path bestPath) {
        Iterator<Node> pathIterator = bestPath.getPath().iterator();
        Position startPos = pathIterator.next().getPos();
        Position nextPos = pathIterator.next().getPos();

        int difX = nextPos.getX() - startPos.getX();
        int difY = nextPos.getY() - startPos.getY();

        if (difX == -1)
            return LEFT;
        else if (difX == 1)
            return RIGHT;
        else if (difY == -1)
            return UP;
        else if (difY == 1)
            return DOWN;
        return null;
    }

    private Direction getNaiveDir(int pacPosX, int pacPosY) {
        List<Direction> possibleDirections = new ArrayList<>();
        // si pac man est au dessus
        if(pacPosY > this.getPosY()) {
            if(isDirectionAvailable(DOWN, this.getPosX(), this.getPosY(), this.getGrid())) {
                possibleDirections.add(DOWN);
            }
        } else {
            if(isDirectionAvailable(UP, this.getPosX(), this.getPosY(), this.getGrid())) {
                possibleDirections.add(UP);
            }
        }

        // si pac man est à droite
        if(pacPosX > this.getPosX()) {
            if(isDirectionAvailable(RIGHT, this.getPosX(), this.getPosY(), this.getGrid())) {
                possibleDirections.add(RIGHT);
            }
        } else  {
            if(isDirectionAvailable(LEFT, this.getPosX(), this.getPosY(), this.getGrid())) {
                possibleDirections.add(LEFT);
            }
        }

        // find one of the correct moves
        if(possibleDirections.isEmpty())
            possibleDirections.add(reverseDirection(this.getDirection()));

        if(possibleDirections.size() == 1) {
            return possibleDirections.get(0);
        }

        int chooseAtRandom = new Random().nextInt(possibleDirections.size());

        return possibleDirections.get(chooseAtRandom);
    }

    private Direction reverseDirection(Direction direction) {
        switch (direction) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return null;
        }
    }

}