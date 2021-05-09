package com.example.pac_man;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;
import com.example.pac_man.model.ghosts.BlueGhost;
import com.example.pac_man.model.ghosts.RedGhost;
import com.example.pac_man.model.ghosts.YellowGhost;

class CustomAdapter extends BaseAdapter {
    private int sizeX;
    private int sizeY;
    private Grid grid;
    private LayoutInflater thisInflater;

    private Integer[] pacManImages = {
            R.raw.pacman_u,
            R.raw.pacman_r,
            R.raw.pacman_d,
            R.raw.pacman_l
    };

    private Integer[] terrainImages = {
            R.raw.wall,
            R.raw.pacgum,
            R.raw.empty
    };

    private Integer[] blueGhostImages = {
            R.raw.blue_u,
            R.raw.blue_r,
            R.raw.blue_d,
            R.raw.blue_l
    };

    private Integer[] yellowGhostImages = {
            R.raw.yellow_u,
            R.raw.yellow_r,
            R.raw.yellow_d,
            R.raw.yellow_l
    };

    private Integer[] redGhostImages = {
            R.raw.red_u,
            R.raw.red_r,
            R.raw.red_d,
            R.raw.red_l
    };


    CustomAdapter(Context context, Grid grid) {
        this.thisInflater = LayoutInflater.from(context);
        this.sizeX = grid.getSizeX();
        this.sizeY = grid.getSizeY();
        this.grid = grid;
    }

    @Override
    public int getCount() {
        return sizeY*sizeX;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null ) {
            int gridValueAtPos = getGridValueAtPos(position);

            convertView = thisInflater.inflate(R.layout.grid_item, null);
            ImageView tileImage = convertView.findViewById(R.id.tile);

            tileImage.setImageResource(getImageFromGridValue(gridValueAtPos));
        }
        return convertView;
    }

    private int getImageFromGridValue(int gridValueAtPos) {
        switch (gridValueAtPos) {
            case 1:
                return pacManImages[getImageIndexFromDirection(grid.getPacMan().getDirection())];
            case 2:
                // yellow
                return yellowGhostImages[getImageIndexFromDirection(grid.getGhost(YellowGhost.class).getDirection())];
            case 3:
                // red
                return redGhostImages[getImageIndexFromDirection(grid.getGhost(RedGhost.class).getDirection())];
            case 4:
                // blue
                return blueGhostImages[getImageIndexFromDirection(grid.getGhost(BlueGhost.class).getDirection())];
            case 5:
                // wall
                return terrainImages[0];
            case 6:
                // pastille
                return terrainImages[1];
            case 7:
                // case vide
                return terrainImages[2];
            default :
                break;
        }

        return 0;
    }

    private int getImageIndexFromDirection(Direction direction) {
        switch (direction) {
            case UP:
                return 0;
            case RIGHT:
                return 1;
            case DOWN:
                return 2;
            case LEFT:
                return 3;
            default:
                return 0;
        }
    }

    private int getGridValueAtPos(int position) {
        // Passer de la position en 1D à une position en 2D
        int row = position/sizeX;
        int col = position%sizeX;
        return this.grid.getGrid().get(row).get(col);
    }
}
