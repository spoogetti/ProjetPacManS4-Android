package com.example.pac_man;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import com.example.pac_man.model.Direction;
import com.example.pac_man.model.Grid;
import com.example.pac_man.model.PacMan;
import com.example.pac_man.model.PacManAction;
import com.example.pac_man.model.ghosts.BlueGhost;
import com.example.pac_man.model.ghosts.Ghost;
import com.example.pac_man.model.ghosts.RedGhost;
import com.example.pac_man.model.ghosts.YellowGhost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private Grid grid;
    private PacMan pacMan;
    private PacManAction pacManAction;
    private List<Ghost> ghosts;
    static Boolean gameInitialized = false;
    static Boolean endScreenDisplayed = false;

    static final int START_DELAY = 1000;

    static int ghostAcc = 0;
    static final int G_RATE_OF_ACC = 1;

    static final int P_INIT_SPEED = 200;
    static final int G_INIT_SPEED = 600;
    static final int REFRESH_SPEED = 200;

    // fingerSlideEvent variables
    float x1;
    float x2;
    float y1;
    float y2;
    float dx;
    float dy;

    private int levelRessourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if(!gameInitialized) {
            try{
                levelRessourceId = getIntent().getExtras().getInt("levelRessourceId");
            } catch (NullPointerException ignored){}

            gameInitialized = true;
            endScreenDisplayed = false;
            ghostAcc = 0;

            fillViewWithInitialData();

            HandlerThread handlerThread = new HandlerThread("refreshView");
            handlerThread.start();

            launchViewRefresher();
            launchPacMan();
            launchGhosts();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("grid", this.grid);
        outState.putSerializable("pacmanAction", this.pacManAction);
        outState.putSerializable("pacman", this.pacMan);
        outState.putSerializable("ghosts", (Serializable) this.ghosts);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.grid = (Grid) savedInstanceState.getSerializable("grid");
        this.pacMan = (PacMan) savedInstanceState.getSerializable("pacman");
        this.pacManAction = (PacManAction) savedInstanceState.getSerializable("pacmanAction");
        this.ghosts = (List<Ghost>) savedInstanceState.getSerializable("ghosts");

        restoreViewRefresher();
    }

    private void restoreViewRefresher() {
        GridView gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(grid.getSizeX());

        // Get the GridView layout
        gridView = findViewById(R.id.gridView);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), grid);
        gridView.setAdapter(adapter);

        launchViewRefresher();
    }

    private void launchViewRefresher() {
        Looper looper = this.getMainLooper();
        final Handler gridViewHandler = new Handler(looper);

        final Runnable globalViewRefresher = new Runnable() {
            @Override
            public void run() {
                gridViewHandler.postDelayed(this, REFRESH_SPEED);
                synchronized (grid) {
                    runOnUiThread(updateGameView);
                }
            }
        };

        gridViewHandler.post(globalViewRefresher);
    }

    private void fillViewWithInitialData() {
        InputStream inputStream = getResources().openRawResource(levelRessourceId);
        String gridData = readGrid(inputStream);

        String[] splitData = gridData.split(";");
        String sizeY = splitData[0].split(",")[0];
        String sizeX = splitData[0].split(",")[1];

        GridView gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(Integer.parseInt(sizeX));
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(0);
        gridView.getColumnWidth();

        this.grid = new Grid(splitData);
        this.pacMan = createPacMan();
        this.ghosts = createGhosts();

        this.getGrid().setPacMan(this.getPacMan());
        this.getGrid().setGhosts(this.getGhosts());

        // Get the GridView layout
        gridView = findViewById(R.id.gridView);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), grid);
        gridView.setAdapter(adapter);
    }

    private void launchPacMan() {
        this.pacManAction = new PacManAction(this.getGrid(), this.getPacMan(), this.getGhosts());

        HandlerThread handlerThread = new HandlerThread("PacManHandlerThread");
        handlerThread.start();
        Looper looper = this.getMainLooper();
        final Handler pacManHandler = new Handler(looper);

        final Runnable gameOperations = new Runnable() {
            @Override
            public void run() {
                if(!endScreenDisplayed) {
                    pacManHandler.postDelayed(this, P_INIT_SPEED);
                    ghostAcc += G_RATE_OF_ACC;
                } else {
                    pacManHandler.removeCallbacks(this);
                }
                synchronized (grid) {
                    onResult(pacManAction.getNextGrid(grid));
                }
            }
        };

        pacManHandler.postDelayed(gameOperations, START_DELAY);
    }

    private void launchGhosts() {
        HandlerThread ghostHandlerThread;
        Looper ghostLooper;

        for(final Ghost ghost : ghosts) {
            ghostHandlerThread = new HandlerThread("GhostHandlerThread");
            ghostHandlerThread.start();
            ghostLooper = ghostHandlerThread.getLooper();
            final Handler ghostHandler = new Handler(ghostLooper);

            final Runnable ghostOperations = new Runnable() {
                @Override
                public void run() {
                    if(!endScreenDisplayed) {
                        ghostHandler.postDelayed(this, G_INIT_SPEED - ghostAcc);
                    } else {
                        ghostHandler.removeCallbacks(this);
                    }
                    synchronized (grid) {
                        onResult(ghost.getNextGrid(grid));
                    }
                }
            };
            ghostHandler.postDelayed(ghostOperations, START_DELAY);
        }
    }

    private void onResult(Grid grid) {
        this.setGrid(grid);

        if(!endScreenDisplayed) {
            if(isGameWon(this.getGrid())) {
                // lance l'intent pour afficher l'écran de victoire
                Intent intent = new Intent(this, WinLoseActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("win", true);
                extras.putInt("score", pacMan.getScore());
                extras.putInt("levelRessourceId", levelRessourceId);
                intent.putExtras(extras);
                startActivity(intent);
                gameInitialized = false;
                endScreenDisplayed = true;
            }

            if(isGameLost()) {
                // lance l'intent pour afficher l'écran de défaite
                Intent intent = new Intent(this, WinLoseActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("win", false);
                extras.putInt("score", pacMan.getScore());
                extras.putInt("levelRessourceId", levelRessourceId);
                intent.putExtras(extras);
                startActivity(intent);
                gameInitialized = false;
                endScreenDisplayed = true;
            }
        }
    }

    private boolean isGameLost() {
        boolean gameLost = false;
        // si pacMan est à la même position qu'un des fantômes
        for (Ghost ghost: ghosts) {
            if(ghost.getPosX() == pacMan.getPosX() && ghost.getPosY() == pacMan.getPosY()) {
                gameLost = true;
                break;
            }
        }

        return gameLost;
    }

    private boolean isGameWon(Grid grid) {
        boolean pastilleFound = false;

        int i = 0;
        int j;

        // recherche du pacman dans la grille
        while(i < grid.getSizeY() && !pastilleFound) {
            j = 0;
            while(j < grid.getSizeX() && !pastilleFound) {
                if(grid.getGrid().get(i).get(j) == 6) {
                    pastilleFound = true;
                }
                j++;
            }
            i++;
        }

        for (Ghost ghost: ghosts) {
            if(ghost.getLastEatenValue() == 6) {
                pastilleFound = true;
            }

        }

        return !pastilleFound;
    }

    final Runnable updateGameView = new Runnable() {
        @Override
        public void run() {
            GridView gridView = findViewById(R.id.gridView);
            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), getGrid());
            gridView.setAdapter(adapter);
            gridView.invalidateViews();
        }
    };

    private PacMan createPacMan() {
        int sizeY = grid.getSizeY();
        int sizeX = grid.getSizeX();

        int i = 0;
        int j;

        int pacPosY = 0;
        int pacPosX = 0;

        boolean pacManFound = false;

        // recherche du pacman dans la grille
        while(i < sizeY && !pacManFound) {
            j = 0;
            while(j < sizeX && !pacManFound) {
                if(grid.getGrid().get(i).get(j) == 1) {
                    pacPosY = i;
                    pacPosX = j;
                    pacManFound = true;
                }
                j++;
            }
            i++;
        }

        return new PacMan(pacPosX, pacPosY, Direction.RIGHT, Direction.RIGHT);
    }

    private List<Ghost> createGhosts() {
        List<Ghost> newGhosts = new ArrayList<>();

        int sizeY = grid.getSizeY();
        int sizeX = grid.getSizeX();

        int i = 0;
        int j;

        int posY;
        int posX;

        int gridValue;
        while(i < sizeY) {
            j = 0;
            while(j < sizeX) {
                gridValue = grid.getGrid().get(i).get(j);
                if(gridValue == 2 || gridValue == 3 || gridValue == 4) {
                    posY = i;
                    posX = j;
                    switch (gridValue) {
                        case 2 :
                            newGhosts.add(new YellowGhost(posX, posY, Direction.RIGHT, null, grid));
                            break;
                        case 3 :
                            newGhosts.add(new RedGhost(posX, posY, Direction.RIGHT, null, grid));
                            break;
                        case 4 :
                            newGhosts.add(new BlueGhost(posX, posY, Direction.RIGHT, null, grid));
                            break;
                        default:
                            break;
                    }
                }
                j++;
            }
            i++;
        }

        return newGhosts;
    }

    public String readGrid(InputStream inputStream) {
        String lineData;
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream != null) {
                while ((lineData = reader.readLine()) != null) {
                    stringBuilder.append(lineData + ";" );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_up:
                this.pacMan.setNextMove(Direction.UP);
                this.pacManAction.refreshPacman(this.pacMan);
                break;
            case R.id.b_down:
                this.pacMan.setNextMove(Direction.DOWN);
                this.pacManAction.refreshPacman(this.pacMan);
                break;
            case R.id.b_left:
                this.pacMan.setNextMove(Direction.LEFT);
                this.pacManAction.refreshPacman(this.pacMan);
                break;
            case R.id.b_right:
                this.pacMan.setNextMove(Direction.RIGHT);
                this.pacManAction.refreshPacman(this.pacMan);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case(MotionEvent.ACTION_DOWN):
                x1 = event.getX();
                y1 = event.getY();
                break;

            case(MotionEvent.ACTION_UP):
                x2 = event.getX();
                y2 = event.getY();
                dx = x2-x1;
                dy = y2-y1;

                // Use dx and dy to determine the direction of the move
                if(Math.abs(dx) > Math.abs(dy)) {
                    if(dx>0) {
                        this.pacMan.setNextMove(Direction.RIGHT);
                        this.pacManAction.refreshPacman(this.pacMan);
                    } else {
                        this.pacMan.setNextMove(Direction.LEFT);
                        this.pacManAction.refreshPacman(this.pacMan);
                    }
                } else {
                    if(dy>0) {
                        this.pacMan.setNextMove(Direction.DOWN);
                        this.pacManAction.refreshPacman(this.pacMan);
                    } else {
                        this.pacMan.setNextMove(Direction.UP);
                        this.pacManAction.refreshPacman(this.pacMan);
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
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