package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.DodgeNSA;
import com.mygdx.game.sprites.Ladder;
import com.mygdx.game.sprites.Obstacle;
import com.mygdx.game.sprites.Person;
import com.mygdx.game.sprites.iPhone;

import org.w3c.dom.css.Rect;

import java.util.LinkedList;

/**
 * Created by owner on 27/02/2016.
 */
public class PlayState extends State {
    private Person person;
    private Ladder LadderLeft;
    private Ladder LadderRight;
    private Obstacle obstacle1, obstacle2, obstacle3, obstacle4;
    private iPhone iphone1, iphone2, iphone3, iphone4;
    private LinkedList<Obstacle> obstacles;
    private LinkedList<iPhone> iphones;
    private Rectangle resetPosition;
    private Texture bg;

    private int score;
    private String savediPhones;
    BitmapFont bf;

    private int highScore;
    private String highScoreString;
    private Sound upSFX;
    private Sound fbi;

    public PlayState(GameStateManager gsm, int highScore) {
        super(gsm);
        person = new Person(0, 0);
        resetPosition = new Rectangle(250,400,34,24); //CHANGE IF OBSTACLE IMAGE DIMENSION CHANGES
        obstacles = new LinkedList<Obstacle>();
        LadderLeft = new Ladder(0,0);
        LadderRight = new Ladder(380,0);
        obstacle1 = new Obstacle();
        obstacle2 = new Obstacle();
        obstacle3 = new Obstacle();
        obstacle4 = new Obstacle();
        obstacles.add(obstacle1);
        obstacles.add(obstacle2);

        iphones = new LinkedList<iPhone>();
        iphone1 = new iPhone();
        iphone2 = new iPhone();
        iphone3 = new iPhone();
        iphone4 = new iPhone();
        iphones.add(iphone1);
        iphones.add(iphone2);
       // iphones.add(iphone3);
        //iphones.add(iphone4);


//        cam.setToOrtho(false, DodgeNSA.WIDTH / 2, DodgeNSA.HEIGHT / 2);
        bg = new Texture("bg.jpg");
        score = 0;
        savediPhones= "Score: 0";
        bf = new BitmapFont();
        bf.getData().setScale(2, 2);

        this.highScore = highScore;
        highScoreString = "High Score: " + highScore;
        upSFX = Gdx.audio.newSound(Gdx.files.internal("up.wav"));
        fbi = Gdx.audio.newSound(Gdx.files.internal("police.wav"));
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched())
            person.jump();
    }

    @Override
    public void update(float dt) {
        handleInput();
        person.update(dt);

        for (Obstacle obstacle : obstacles) {
            obstacle.update(dt);
            if (obstacle.getPosition().y < 0){
                //obstacles.remove(obstacle); (this generates ConcurrentModificationException)
                obstacle.resetObject();
            }
            if (obstacle.collides(person.getBounds())) {
                fbi.play();
                if (score > highScore){
                    highScore = score;
                    highScoreString = "High Score: "+highScore;
                }
                gsm.set(new GameOverState(gsm, score, highScore));
            }
            obstacle.update(dt);
        }

        for (iPhone iphone : iphones) {
            iphone.update(dt);

            if (iphone.getPosition().y < 0){
                //obstacles.remove(obstacle); (this generates ConcurrentModificationException)
                iphone.resetObject();
            }

            if (iphone.collides(person.getBounds())) {
                iphone.noNSA();
                upSFX.play();
                if (!iphone.getCollided()) {
                    score += 1;
                    savediPhones = "Score: " + score;
                    iphone.setCollided(true);
                }
            }
            iphone.update(dt);
        }



    }

    @Override
    public void render(SpriteBatch sb) {
//        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth) / 2, 0);
        bf.setColor(255, 255, 255, 255);

        sb.draw(LadderLeft.getLadder(), LadderLeft.getPosition().x, LadderLeft.getPosition().y);
        sb.draw(LadderRight.getLadder(), LadderRight.getPosition().x, LadderRight.getPosition().y);

        bf.draw(sb, savediPhones, DodgeNSA.WIDTH / 3, DodgeNSA.HEIGHT / 3 + 20);
        bf.draw(sb, highScoreString, DodgeNSA.WIDTH / 3, DodgeNSA.HEIGHT / 4 + 50);
        sb.draw(person.getPerson(), person.getPosition().x, person.getPosition().y);

        //draw all the obstacles
        for (Obstacle obstacle:obstacles) {
            sb.draw(obstacle.getObstacle(), obstacle.getPosition().x, obstacle.getPosition().y);
        }

        //draw all the iphones
        for (iPhone iphone:iphones) {
            sb.draw(iphone.getiPhone(), iphone.getPosition().x, iphone.getPosition().y);
        }

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        person.dispose();
        LadderLeft.dispose();
        LadderRight.dispose();
        for (iPhone iphone: iphones) {
            iphone.dispose();
        }
        for (Obstacle obstacle: obstacles) {
            obstacle.dispose();
        }
    }
}
