package com.example.pac_man;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.io.InputStream;

/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * Romain Roubaix <romain.roubaix@etu.u-bordeaux.fr> wrote this file.
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it,
 * you can buy me a beer in return.   Poul-Henning Kamp
 * ----------------------------------------------------------------------------
 */

/**
 * Première étape: La grille de jeu
 * La grille de jeu est stockée dans un fichier texte (à ajouter en ressource dans un répertoire raw).
 * 6,19
 * 5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5
 * 5,6,6,6,6,6,6,6,6,6,6,4,6,6,6,6,6,6,5
 * 5,6,5,5,5,5,6,5,5,6,5,5,5,5,6,6,5,6,5
 * 5,2,6,5,5,6,6,5,5,6,5,5,5,5,6,5,5,6,5
 * 5,1,6,6,6,6,6,6,6,6,6,6,6,6,6,6,3,6,5
 * 5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5
 *
 * On utilisera un Gridview(chaque case contenant un Imageview) ,
 * un Arraylist pour contenir les images à afficher et un adapter pour faire le lien entre les deux.
 * La première étape est donc d’ouvrir le fichier texte, et de créer la zone de jeu

 * Seconde étape : mouvement du pac-man
 * Créer 4 boutons et gérer le déplacement du pac-man.
 * Rien de bien difficile, pour le moment on se contentera de «manger» les petites pastilles
 * lorsqu’on passe dessus avec le pac-man et d’éviter qu’il traverse les murs.
 * Il y a une subtilité car le pac-man doit suivre le mouvement même si on appuie trop tôt sur le bouton
 * ( le virage n’est pas forcement immédiat ☺)
 * Ajout d'une classe grille qui prend la représentation de la grille, une tableau de tableau d'entiers.

 * Troisième étape : les fantômes
 * Au départ, tous les fantômes auront le même mouvement aléatoire.
 * Chaque fantôme doit avoir son propre thread.
 * On utilisera pour cela les classes Handler et runnable.
 * Modifiez maintenant l’IA des fantômes pour donner à chacun une particularité.
 * En particulier, j’aimerais un fantôme qui pourchasse le pac-man sans relâche. ☺ Le fantôme rouge

 * Quatrième étape : gérer les interactions entre les fantômes et le pacman La partie peut maintenant être perdue ou gagnée.
 * Attention ici, il y a une subtilité lorsque deux fantômes se superposent

 * Cinquième étape
 * Gérez le score, (pacMan : score)
 * la vitesse croissante des fantômes, (ghostAcc)
 * le chargement du niveau,
 * les meilleurs scores… (sharedPreferences)
 * Enfin, tout pour que votre application soit jouable…

 * Sixième étape
 * Pensez aux détails et au fun  Orientation du téléphone, langue, menu, musique…
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ImageView gameLogoView = findViewById(R.id.gameLogo);
        InputStream is = this.getResources().openRawResource(R.raw.pacman);

        Bitmap logoBitmap = BitmapFactory.decodeStream(is);
        gameLogoView.setImageBitmap(logoBitmap);

        mp=MediaPlayer.create(getApplicationContext(),R.raw.lava_city);
        mp.setLooping(true);
        mp.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_play:
                Intent levelSelection = new Intent(this, LevelSelectionActivity.class);
                startActivity(levelSelection);
                mp.stop();
                break;
            case R.id.b_highscores:
                Intent highscoresIntent = new Intent(this, HighScoreActivity.class);
                startActivity(highscoresIntent);
                mp.stop();
                break;
            default:
                break;
        }
    }
}

