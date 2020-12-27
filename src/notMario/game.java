package notMario;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.applet.*;
import java.awt.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

@SuppressWarnings("serial") 
public class game extends JPanel implements Runnable, KeyListener{
	//Audio setup
	Clip bgSong;
	AudioClip deathSound, stompSound, coinSound, jumpSound, bumpSound, winSound;
	boolean musicChanged = true;
	
	//Graphics setup
	Graphics offScreenBuffer;
	Image marioLeft,  runLeft1, runLeft2, jumpLeft;
	Image marioRight, runRight1, runRight2, jumpRight;
	Image deadMario;
	Image goomba1, goomba2;
	Image brick, ground, spike, question, empty, coin, flag;
	Image offScreenImage;
	
	//Buffered Images
	BufferedImage name, wasd;
	BufferedImage bg;
	BufferedImage play, menuOptions, menuExit;
	BufferedImage soundLabel, musicLabel, onLabel, offLabel;
	BufferedImage winScreen;
	
	//Background image setup
	int bgWidth;
	int bgSpeed = 1;
	int bgX = 0;
	
    //Basic setup
	int FPS = 60;
	Thread thread;
	int screenWidth = 700;
	int screenHeight = 600;
	
	//Player states
	boolean falling, jumping, running;
	boolean standing = true;
	boolean facingLeft = false;
	boolean facingRight = true;
	int leftCounter = 0;
	int rightCounter = 0;
	int deathLength = 600;
	
	//Game states
	boolean menu = true;
	boolean options = false;
	boolean game = false;
	boolean winLevel = false;
	boolean winGame = false;
	boolean level1State = false;
	boolean level2State = false;
	boolean level3State = false;
	boolean changed = false;
	boolean dead = false;
	boolean music = true;
	boolean sound = true;
	
	//Menu buttons
	Rectangle playButton;
	Rectangle optionsButton;
	Rectangle exitButton;
	
	//Options buttons
	Rectangle musicButton;
	Rectangle soundButton;
	Rectangle leaveButton;
	
	//Player setup 
	int spawnX = 250;
	int spawnY = 450;
	Rectangle rect = new Rectangle(325, 450, 40, 60);
	//Player movement
	boolean jump, left, right;
	double speed = 3.5;			
	double jumpSpeed = 15;		
	double xVel = 0;
	double yVel = 0;
	double gravity = 0.8;
	boolean airborne = true;
	
	//Map setup
	Rectangle[] wallsGround = new Rectangle [20];
	Rectangle[] wallsBrick = new Rectangle [20];
	Rectangle[] spikes = new Rectangle [20];
	Rectangle[] questions = new Rectangle [20];
	Rectangle[] emptyQuestion = new Rectangle [20];
	Rectangle[] enemySpawn = new Rectangle [20];
	Rectangle[] invBrick = new Rectangle [20];
	Rectangle[] emptyBrick = new Rectangle [20];
	Rectangle[] emptyGround = new Rectangle[20];
	Rectangle[] invSpikes = new Rectangle[20];
	Rectangle[] flags = new Rectangle[1];
	int numRows = 12;
	int numCols = 22;
	int tileWidth = 50;
	int tileHeight = 50;
	//Coin animation setup
	boolean spawn = false;
	Rectangle spawnBlock;
	int coinCounterX = 1;
	int coinCounterY = 1;
	//Death animation setup
	int diedCounter1 = 0;
	int diedCounter2 = 0;
	
	//Camera setup
	int camCounter = 0;
	int cam = 0;
	int xSubtract = 0;
	int camSpeed = 1;
	
	//Enemy setup
	int enemiesSpawning = 0;
	int enemiesSpawned = 0;
	int enemyCounter = 50;
	int goombaSpeed = 2;
	int goombaCounter = 1;
	Rectangle[] enemies = new Rectangle [enemyCounter];
	
	/* 
	Tiles:
	1 = ground
	2 = brick
	3 = spikes
	4 = question box
	5 = empty question box
	6 = enemy
	7 = invisible brick
	8 = non solid brick
	9 = non solid ground
	10 = invisible spikes
	11 = flag
	*/
	
	//Original array is used to reset the map
	int[][] map;
	int[][] level1 = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 2, 2, 2, 0, 0, 0, 0,11, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0,10,10,10, 0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0},	
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 4, 2, 2, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 7, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 6, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0,10,10,10, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0,10,10,10,10,10,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 1, 1, 1, 1, 1, 0, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 1, 1, 1, 1, 1, 0, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
		};
	
	int[][] level1Orig = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 2, 2, 2, 0, 0, 0, 0,11, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0,10,10,10, 0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0},	
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 4, 2, 2, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 7, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 6, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0,10,10,10, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0,10,10,10,10,10,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 1, 1, 1, 1, 1, 0, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 1, 1, 1, 1, 1, 0, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
		};
	
	int[][] level2 = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,11, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 4, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10,10,10, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 2, 0, 2, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 2, 0, 2,10, 2, 0, 2, 0, 0, 2, 0, 2,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 1, 1, 0, 0, 9, 9, 9, 9, 9, 9, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 1, 1, 0, 0, 9, 9, 9, 9, 9, 9, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
	};
	
	int[][] level2Orig = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,11, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 4, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10,10,10, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 2, 0, 2, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 2, 0, 2,10, 2, 0, 2, 0, 0, 2, 0, 2,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 1, 1, 0, 0, 9, 9, 9, 9, 9, 9, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 1, 1, 0, 0, 9, 9, 9, 9, 9, 9, 1, 1, 1, 1, 1, 1, 1, 9, 9, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
	};
	
	int[][] level3 = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,11, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 2,10,10, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 6, 6, 6, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 9, 1, 1, 9, 1, 1, 1, 1, 0, 1, 0, 9, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 9, 1, 1, 9, 1, 1, 1, 1, 0, 1, 0, 9, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	};
	
	int[][] level3Orig = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2,10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,11, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 2,10,10, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 6, 6, 6, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 9, 1, 1, 9, 1, 1, 1, 1, 0, 1, 0, 9, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 0, 9, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 9, 1, 1, 9, 1, 1, 1, 1, 0, 1, 0, 9, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	};

	/*
	 -1 = play
	 -2 = options
	 -3 = exit
	 */
	int[][] menuMap = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0,-1, 0, 0,-2, 0, 0,-3, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	
	/*
	  -4 = sound off/on
	  -5 = music off/on
	  -6 = leave
	 */
	int[][] optionsMap = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0,-4, 0, 0,-5, 0, 0,-6, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	
	//This method is used to load in all sounds and images
	@SuppressWarnings("deprecation")
	public game() {
		//sets up JPanel
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
		
		//Starting the thread
		thread = new Thread(this);
		thread.start();
		
		//Loads images
		MediaTracker tracker = new MediaTracker (this);
		brick = Toolkit.getDefaultToolkit().getImage("brick.png");
		tracker.addImage(brick, 0);
		ground = Toolkit.getDefaultToolkit().getImage("ground.png");
		tracker.addImage(ground, 1);
		spike = Toolkit.getDefaultToolkit().getImage("spikes.png");
		tracker.addImage(spike, 2);
		question = Toolkit.getDefaultToolkit().getImage("question.png");
		tracker.addImage(question, 3);
		coin = Toolkit.getDefaultToolkit().getImage("coin.png");
		tracker.addImage(coin, 4);
		empty = Toolkit.getDefaultToolkit().getImage("empty.png");
		tracker.addImage(empty, 5);
		marioLeft = Toolkit.getDefaultToolkit().getImage("marioLeft.png");
		tracker.addImage(marioLeft, 6);
		marioRight = Toolkit.getDefaultToolkit().getImage("marioRight.png");
		tracker.addImage(marioRight, 7);
		runLeft1 = Toolkit.getDefaultToolkit().getImage("runLeft1.png");
		tracker.addImage(runLeft1, 8);
		runLeft2 = Toolkit.getDefaultToolkit().getImage("runLeft2.png");
		tracker.addImage(runLeft2, 9);
		runRight1 = Toolkit.getDefaultToolkit().getImage("runRight1.png");
		tracker.addImage(runRight1, 10);
		runRight2 = Toolkit.getDefaultToolkit().getImage("runRight2.png");
		tracker.addImage(runRight2, 11);
		jumpLeft = Toolkit.getDefaultToolkit().getImage("jumpLeft.png");
		tracker.addImage(jumpLeft, 12);
		jumpRight = Toolkit.getDefaultToolkit().getImage("jumpRight.png");
		tracker.addImage(jumpRight, 13);
		deadMario = Toolkit.getDefaultToolkit().getImage("death.png");
		tracker.addImage(deadMario, 14);
		goomba1 = Toolkit.getDefaultToolkit().getImage("goomba1.png");
		tracker.addImage(goomba1, 15);
		goomba2 = Toolkit.getDefaultToolkit().getImage("goomba2.png");
		tracker.addImage(goomba2, 16);
		flag = Toolkit.getDefaultToolkit().getImage("flag.png");
		
		try
		{
		    tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		
		//Loads background music
		if(music == true) {
		try {
			File musicPath = new File("backGroundSong.wav");
			
			if(musicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
				bgSong = AudioSystem.getClip();
				bgSong.open(audioInput);
				bgSong.loop(bgSong.LOOP_CONTINUOUSLY);
			}
			else {
			}
		}
		catch (Exception ex)
		{
			
		}
		}
		
		//Loads sound effects
		if(sound == true) {
		deathSound = Applet.newAudioClip (getCompleteURL ("deathSound.wav"));
		coinSound = Applet.newAudioClip(getCompleteURL("coin.wav"));
		stompSound = Applet.newAudioClip(getCompleteURL("stomp.wav"));
		jumpSound = Applet.newAudioClip(getCompleteURL("jump.wav"));
		bumpSound = Applet.newAudioClip(getCompleteURL("bump.wav"));
		winSound = Applet.newAudioClip(getCompleteURL("winSound.wav"));
		}
		
		try {
			bg = ImageIO.read(new File("background.png"));
			name = ImageIO.read(new File("logo.png"));
			wasd = ImageIO.read(new File("wasd.png"));
			play = ImageIO.read(new File("play.png"));
			menuOptions = ImageIO.read(new File("options.png"));
			menuExit = ImageIO.read(new File("exit.png"));
			soundLabel = ImageIO.read(new File("sound.png"));
			musicLabel = ImageIO.read(new File("music.png"));
			onLabel = ImageIO.read(new File("on.png"));
			offLabel = ImageIO.read(new File("off.png"));
			winScreen = ImageIO.read(new File("winScreen.png"));
			bgWidth = bg.getWidth();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//This method consists of the main game loop
	@Override
	public void run() {
		while(true) {
			//Main game loop
			this.repaint();
			//Game stops when won
			if(!winGame)
				update();
			else
				break;
			
			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//This method changes the map according to the level and calls other methods to be run repeatedly
	public void update() {
		//Setups the map and its variables according the game's state
		if(menu == true && changed == false) {
			map = menuMap;
			numRows = 12;
			numCols = 14;
			changed = true;
		}
		else if(options == true && changed == false) {
			map = optionsMap;
			numRows = 12;
			numCols = 14;
			changed = true;
		}
		else if (game == true && level1State == true && changed == false) {
			map = level1;
			numRows = 12;
			numCols = 83;
			changed = true;
		}
		else if (game == true && level2State == true && changed == false) {
			map = level2;
			numRows = 12;
			numCols = 78;
			changed = true;
		}
		else if (game == true && level3State == true && changed == false) {
			map = level3;
			numRows = 12;
			numCols = 78;
			bgSpeed = 2;
			goombaSpeed = 3;
			changed = true;
		}
		
		//Creates rectangle arrays for each type of tile and generates the enemies
		if (game == true) {
			findWalls();
			enemyMovement();
		}
		else 
			findWallsMenu();
		
		//Camcounter affects when the map loads in the next column of the level
		camCounter++;
		//These counters affect what player image is shown to create running animations
		leftCounter++;
		rightCounter++;
		
		//Allow for movement
		move();
		keepInBound();
		
		//Resets the map once the death animation is finished
		if(diedCounter2 > 1200) {
			deadCheck();
			game = true;
			if(music == true)
				bgSong.loop(bgSong.LOOP_CONTINUOUSLY);
		}
		
		//Turns the music off or on
		if(music == false && musicChanged == false) {
			bgSong.stop();
			musicChanged = true;
		}
		else if(music == true && musicChanged == false) {
			bgSong.loop(bgSong.LOOP_CONTINUOUSLY);
			musicChanged = true;
		}
		
		//This section iterates through each tile array and checks for collision for both the player and enemies
		applyCheckCollision(wallsGround, false, false, 0, false);
		if(game == true) {
			applyCheckCollision(wallsBrick, false, false, 0, false);
			applyCheckCollision(questions, false, true, 0, false);
			applyCheckCollision(emptyQuestion, false, false, 0, false);
			applyCheckCollision(emptyBrick, false, false, 8, true);
			applyCheckCollision(emptyGround, false, false, 9, true);
			for(int i = 0; i < invBrick.length ; i++)
				checkCollision(rect, invBrick[i], false, false, 7);
			for(int i = 0; i < spikes.length ; i++)
				checkCollision(rect, spikes[i], true, false, 0);
			for(int i = 0; i < invSpikes.length; i++)
				checkCollision(rect, invSpikes[i], true, false, 10);
			for(int i = 0 ; i < enemies.length ; i++) {
				if(enemies[i] == null)
					continue;
			checkCollision(rect, enemies[i], true, false, 6);
			}
			if (flags[0] != null)
					checkCollision(rect, flags[0], false, false, 11);
		}
		else if (menu == true) {
			checkCollision(rect, playButton, false, false, -1);
			checkCollision(rect, optionsButton, false, false, -2);
			checkCollision(rect, exitButton, false, false, -3);
		}
		else if(options == true) {
			checkCollision(rect, musicButton, false, false, -4);
			checkCollision(rect, soundButton, false, false, -5);
			checkCollision(rect, leaveButton, false, false, -6);
		}
		
		//Scrolls the background image continuously 
		if(!dead) {
		if (game == true) {
		bgX -= bgSpeed;
		if(bgX < - bgWidth)
			bgX = 0;
		else if(bgX > bgWidth)
			bgX = 0;
			}
		}
		
		//Checks if the player has won a level or the game
		winCheck();
	}
	
	//Images are drawn offscreen first, then drawn in the game to avoid flickering
	public void paintComponent(Graphics g) {
		if (offScreenBuffer == null)
		{
		    offScreenImage = createImage (this.getWidth (), this.getHeight ());
		    offScreenBuffer = offScreenImage.getGraphics ();
		}
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());
		
		super.paintComponent(g);
		//Background
		offScreenBuffer.drawImage(bg, bgX, 0, null);
		offScreenBuffer.drawImage(bg, bgX + bgWidth, 0, null);
		offScreenBuffer.drawImage(bg, bgX - bgWidth, 0, null);
		g.drawImage(offScreenImage, 0, 0, this);
		
		Graphics2D g2 = (Graphics2D) g;
		//Draws the enemies with alternating pictures to create a walking animation
		if(goombaCounter % 50 <= 25)
			drawArray(enemies, goomba1, tileWidth, tileHeight);
		else if(goombaCounter % 50 > 25)
			drawArray(enemies, goomba2, tileWidth, tileHeight);
		goombaCounter++;
		
		//Draws the coin going up if a question box is hit
		spawnCoin(coinCounterX, coinCounterY);
		if (spawn && coinCounterX == 1)
			if(sound == true)
				coinSound.play();
		if (coinCounterX <= 50 && spawn) {
			coinCounterX += 4;
			coinCounterY++;
		}
		else if (spawn) {
			coinCounterX = 1;
			coinCounterY = 1;
			spawn = false;
		}
		
		//Draws each tile
		drawArray(wallsGround, ground, tileWidth, tileHeight);
		drawArray(wallsBrick, brick, tileWidth, tileHeight);
		drawArray(spikes, spike, tileWidth, 30);
		drawArray(questions, question, tileWidth, tileHeight);
		drawArray(emptyQuestion, empty, tileWidth, tileHeight);
		drawArray(invBrick, null, tileWidth, tileHeight);
		drawArray(emptyBrick, brick, tileWidth, tileHeight);
		drawArray(emptyGround, ground, tileWidth, tileHeight);
		drawArray(invSpikes, null, tileWidth, 30);
		drawArray(flags, flag, 50, 60);
		
		//Draws labels and the logo along with the buttons as question boxes
		if(menu == true) {
			draw(playButton, question, tileWidth, tileHeight);
			draw(optionsButton, question, tileWidth, tileHeight);
			draw(exitButton, question, tileWidth, tileHeight);
			offScreenBuffer.drawImage(play, 105, 270, play.getWidth(), play.getHeight(), null);
			offScreenBuffer.drawImage(menuOptions, 240, 270, menuOptions.getWidth(), menuOptions.getHeight(), null);
			offScreenBuffer.drawImage(menuExit, 405, 270, menuExit.getWidth(), menuExit.getHeight(), null);
			offScreenBuffer.drawImage(name, 150, 125, name.getWidth(), name.getHeight(), null);
			offScreenBuffer.drawImage(wasd, 520, 280, wasd.getWidth(), wasd.getHeight(), null);
		}
		else if(options == true) {
			draw(musicButton, question, tileWidth, tileHeight);
			draw(soundButton, question, tileWidth, tileHeight);
			draw(leaveButton, question, tileWidth, tileHeight);
			offScreenBuffer.drawImage(musicLabel, 95, 270, musicLabel.getWidth(), musicLabel.getHeight(), null);
			offScreenBuffer.drawImage(soundLabel, 240, 270, soundLabel.getWidth(), soundLabel.getHeight(), null);
			if(music == true)
				offScreenBuffer.drawImage(onLabel, 160, 270, onLabel.getWidth(), onLabel.getHeight(), null);
			else if(music == false)
				offScreenBuffer.drawImage(offLabel, 160, 270, offLabel.getWidth(), offLabel.getHeight(), null);
			if(sound == true)
				offScreenBuffer.drawImage(onLabel, 305, 270, onLabel.getWidth(), onLabel.getHeight(), null);
			else if(sound == false)
				offScreenBuffer.drawImage(offLabel, 305, 270, offLabel.getWidth(), offLabel.getHeight(), null);
			
			offScreenBuffer.drawImage(menuExit, 405, 270, menuExit.getWidth(), menuExit.getHeight(), null);
			offScreenBuffer.drawImage(name, 150, 125, name.getWidth(), name.getHeight(), null);
			offScreenBuffer.drawImage(wasd, 520, 280, wasd.getWidth(), wasd.getHeight(), null);
		}
		
		//Draws the player and the corresponding player image based on its state
		if (!dead) {
		if (facingLeft == true) {
			if(!left && !airborne)
				draw(rect, marioLeft, 40, 60);
			else if(airborne)
				draw(rect, jumpLeft, 40, 60);
			else if (!airborne && left && leftCounter % 10 <= 5)
				draw(rect, runLeft1, 40, 60);
			else if (!airborne && left && leftCounter % 10 > 5)
				draw(rect, runLeft2, 40, 60);
		}			
		else if(facingRight == true) {
			if(!right && !airborne)
				draw(rect, marioRight, 40, 60);
			else if(airborne)
				draw(rect, jumpRight, 40, 60);
			else if (!airborne && right && rightCounter % 10 <= 5)
				draw(rect, runRight1, 40, 60);
			else if (!airborne && right && rightCounter % 10 > 5)
				draw(rect, runRight2, 40, 60);
			}
		}
				
		//Draws the death animation
		if (rect.x != spawnX){
			deathAnimate(diedCounter1, diedCounter2);
			if (diedCounter1 <= 75 && dead) {
				if (diedCounter1 == 0) {
					if(music == true)
						bgSong.stop();
					if(sound == true)
						deathSound.play();
			}
			diedCounter1 += 5;
		}
		else if (diedCounter1 > 75 && diedCounter2 <= 1200 && dead)
			diedCounter2 += 10;
		else if (diedCounter2 > 1200) {
			diedCounter1 = 0;
			diedCounter2 = 0;
		}
		}
		
		//Draws the winning screen
		if(winGame == true) {
			offScreenBuffer.drawImage(winScreen, 0, 0, screenWidth, screenHeight, null);
			if(music == true)
				bgSong.stop();
			if(sound == true)
				winSound.play();
		}
		
		g2.drawImage(offScreenImage, 0, 0, this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
			left = true;
			right = false;
		}else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
			right = true;
			left = false;
		}else if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
			jump = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
			left = false;
		}else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
			right = false;
		}else if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
			jump = false;
		}
	}

	//This method allows for player movement and also controls the movement of the camera
	void move() {
		//Player movement
		if(!dead) {
		if(left) {
			xVel = -speed;
			facingLeft = true;
			facingRight = false;
		}
		else if(right) {
			xVel = speed;
			facingRight = true;
			facingLeft = false;
		}
		else {
			xVel = 0;
			leftCounter = 0;
			rightCounter = 0;
		}
		if(airborne) {
			yVel -= gravity;
		}
		else {
			if(jump) {
				if(sound == true)
					jumpSound.play();
				airborne = true;
				yVel = jumpSpeed;
			}
		}
		if(yVel < -50)
			airborne = false;
		rect.x += xVel;
		rect.y -= yVel;
		
		//Camera movement
		if (game == true) {
		if(camCounter % camSpeed == 0) {
			if(level3State == true)
				xSubtract += 2;
			else
				xSubtract++;
		}
		if(xSubtract == 50) {
			xSubtract = 0;
			if (cam <= numCols - 16)
				cam++;
		}
		if (!(cam <= numCols - 16)) {
			bgSpeed = 0;	
			xSubtract = 0;
		}
		else
			if(level3State == true)
				rect.x -= 2;
			else
				rect.x--;
		}
		}
	}
	
	//Kills the player if they go out of bounds to the left of the screen or below it
	void keepInBound() {
		if(rect.x < 0) {
			if (game == true)
				dead = true;
			else
				rect.x = 0;
		}
		if(rect.x > screenWidth - rect.width) {
			rect.x = screenWidth - rect.width;
		}
		if(rect.y > screenHeight - rect.height) {
			rect.y = screenHeight - rect.height;
			airborne = false;
			yVel = 0;
			if(game == true)
				dead = true;
		}
	}
	
	//Checks if the player or enemy collides with a tile and changes it x and y values accordingly
	//Arguments kill and item declare whether the wall kills the player or spawns a coin
	//Argument num allows specific actions to be assigned to certain tiles
	void checkCollision(Rectangle object, Rectangle wall, boolean kill, boolean Coin, int num) {
		//Setups variables used
		double left1 = object.getX();
		double right1 = object.getX() + object.getWidth();
		double top1 = object.getY();
		double bottom1 = object.getY() + object.getHeight();
		double left2 = wall.getX();
		double right2 = wall.getX() + wall.getWidth();
		double top2 = wall.getY();
		double bottom2 = wall.getY() + wall.getHeight();
		boolean collideLeft = right1 > left2 &&  left1 < left2 && 
				   right1 - left2 < bottom1 - top2 &&  right1 - left2 < bottom2 - top1;
		boolean collideRight = left1 < right2 && right1 > right2 && 
        		right2 - left1 < bottom1 - top2 && right2 - left1 < bottom2 - top1;
		boolean collideTop = bottom1 > top2 && top1 < top2;
		boolean collideBottom = top1 < bottom2 && bottom1 > bottom2;	
		int col = (int) Math.round(wall.getX() / tileWidth);
		int row = (int) Math.round(wall.getY() / tileHeight);
		
		if(object.intersects(wall) && kill == false && num != 11) {
			//Collision from left side of the wall
			if(collideLeft) {
				if (top1 < top2) {
					if(Math.abs(wall.y - object.height - object.y) < 17) {
						object.y = wall.y - object.height;
					}
					else if (object == rect)
						object.x = wall.x - object.width;	
				}
				else 
					object.x = wall.x - object.width;
	        }
			//Collision from right side of the wall
	        else if(collideRight) {
		       	if (top1 < top2) {
	        		if(Math.abs(wall.y - object.height - object.y) < 17) {
	        			object.y = wall.y - object.height;
	        		}
	        		else if(object == rect)
	        			object.x = wall.x + wall.width;
		       	}
		       	else
	        		object.x = wall.x + wall.width;
	        }
			//Collision from top side of the wall
	        else if(collideTop) {
	        	object.y = wall.y - object.height;
	        	if (object == rect) {
	        		airborne = false;
	        		yVel = -13.2;
	        	}
	        }
			//Collision from bottom side of the wall
	        else if(collideBottom)
	        {
	       	object.y = wall.y + wall.height;
	       	yVel = 0;
	       	//Changes question box to an empty box
        	if(Coin == true) {
        		spawn = true;
        		spawnBlock = wall;
        		if(map[row][col + cam] != 4)
        			col++;
        		map[row][col + cam] = 5;
	     	}
        	//Allows the buttons to do stuff
	       	if(num == -1) {
	      		menu = false;
        		changed = false;
        		game = true;
        		level1State = true;
        	}
	       	else if(num == -2) {
	       		menu = false;
	       		changed = false;
	       		options = true;
	       	}
        	else if(num == -3) {
        		System.exit(0);
	        }
	        else if(num == -4) {
				if(music == true) {
					music = false;
					musicChanged = false;
				}
				else if(music == false) {
					music = true;
					musicChanged = false;
				}
			}
	        else if(num == -5) {
	        	if(sound == true)
	        		sound = false;
	        	else if(sound == false)
	        		sound = true;
	        }
	        else if(num == -6) {
	        	options = false;
	        	menu = true;
	        	changed = false;
	        	}
	        }
			//Reveals hidden bricks once hit
	        if(num == 7) {
				if(sound == true)
					bumpSound.play();
				if(map[row][col + cam] != 7)
        			col++;
        		map[row][col + cam] = 2;
			}
		}
		
		//Handles walls or objects that kill the player (Enemies and spikes)
		if (object.intersects(wall) && kill == true) {
			//Handles collision with enemies
			//Allows the player to kill them by jumping directly on top of them
			if(num == 6 && (collideTop || (collideLeft && top1 < top2) || (collideRight && top1 < top2)) && 
					!collideLeft && !collideRight && !collideBottom) {
				if(sound == true)
					stompSound.play();
				wall.x = -100;
				object.y = wall.y - object.height;
				yVel = jumpSpeed / 1.5;
			}
			//Reveals hidden spikes when killed
			else if(num == 10) {
				if(map[row][col + cam] != 10)
        			col++;
				map[row][col + cam] = 3;
				dead = true;
			}
			else
				dead = true;
		}
		//Handles collision with a flag
		else if(object.intersects(wall) && num == 11) {
			winLevel = true;
			}
	}
	
	//Iterates through each tile array and checks for collision of each one by calling the method above
	void applyCheckCollision(Rectangle[] array, boolean kill, boolean Coin, int num, boolean invisible) {
		for(int i = 0; i < array.length ; i++) {
			for(int n = 0 ; n < enemies.length ; n++) {
				if(enemies[n] == null)
					continue;
				checkCollision(enemies[n], array[i], false, false, 0);
			}
			if(!invisible)
				checkCollision(rect, array[i], kill, Coin, num);
		}
	}
	
	//Creates the rectangles for each button in the menu or options screen
	void findWallsMenu() {
		wallsGround = new Rectangle[28];
		int index1 = 0;
		for(int row = 0 ; row < numRows ; row++) {
			for(int col = 0; col < numCols ; col++) {
				int x = col * tileWidth;
				int y = row * tileHeight;
				if(map[row][col] == 1) {
					wallsGround[index1] = new Rectangle(x, y, tileWidth, tileHeight);
					index1++;
				}
				if(menu == true) {
					if(map[row][col] == -1)
						playButton = new Rectangle(x, y, tileWidth, tileHeight);
					else if(map[row][col] == -2)
						optionsButton = new Rectangle(x, y, tileWidth, tileHeight);
					else if(map[row][col] == -3)
						exitButton = new Rectangle(x, y, tileWidth, tileHeight);
				}
				else if(options == true) {
					if(map[row][col] == -4)
						musicButton = new Rectangle(x, y, tileWidth, tileHeight);
					else if(map[row][col] == -5)
						soundButton = new Rectangle(x, y, tileWidth, tileHeight);
					else if(map[row][col] == -6)
						leaveButton = new Rectangle(x, y, tileWidth, tileHeight);
				}
			}
		}
	}
	
	//Checks sixteen columns based on the camera
	//First it counts how big each tile array should be to avoid errors
	//Secondly it adds rectangles for each tile to allow for collision and to be drawn
	void findWalls() {
		//Setup counters for each tile
		int BrickCounter = 0;
		int groundCounter = 0;
		int spikesCounter = 0;
		int questionsCounter = 0;
		int emptyQuestionCounter = 0;
		int enemyCounter = 0;
		int invBrickCounter = 0;
		int emptyBrickCounter = 0;
		int emptyGroundCounter = 0;
		int invSpikesCounter = 0;
		//Goes through 16 columns of the level map
		if (cam <= numCols - 16) {
		for(int row = 0; row < numRows; row++) {
			for(int col = cam; col < (cam + 16); col++) {
				if (col >= numCols)
					break;
				if (map[row][col] == 1)
					groundCounter++;	
				else if (map[row][col] == 2)
					BrickCounter++;
				else if (map[row][col] == 3)
					spikesCounter++;
				else if (map[row][col] == 4)
					questionsCounter++;
				else if (map[row][col] == 5)
					emptyQuestionCounter++;
				else if (map[row][col] == 6)
					enemyCounter++;
				else if (map[row][col] == 7)
					invBrickCounter++;
				else if (map[row][col] == 8)
					emptyBrickCounter++;
				else if (map[row][col] == 9)
					emptyGroundCounter++;
				else if (map[row][col] == 10)
					invSpikesCounter++;
			}
		}
		}
		//Once the camera hits the end, only the last 16 columns are checked
		else {
			for(int row = 0; row < numRows; row++) {
				for(int col = numCols - 16; col < numCols; col++) {
					if (col >= numCols)
						break;
					if (map[row][col] == 1)
						groundCounter++;	
					else if (map[row][col] == 2)
						BrickCounter++;
					else if (map[row][col] == 3)
						spikesCounter++;
					else if (map[row][col] == 4)
						questionsCounter++;
					else if (map[row][col] == 5)
						emptyQuestionCounter++;
					else if (map[row][col] == 6)
						enemyCounter++;
					else if (map[row][col] == 7)
						invBrickCounter++;
					else if (map[row][col] == 8)
						emptyBrickCounter++;
					else if (map[row][col] == 9)
						emptyGroundCounter++;
					else if (map[row][col] == 10)
						invSpikesCounter++;
				}
			}
		}
		
		//Sets the size of each rectangle array
		wallsGround = new Rectangle[groundCounter];
		wallsBrick = new Rectangle[BrickCounter];
		spikes = new Rectangle[spikesCounter];
		questions = new Rectangle[questionsCounter];
		emptyQuestion = new Rectangle[emptyQuestionCounter];
		enemySpawn = new Rectangle[enemyCounter];
		invBrick = new Rectangle[invBrickCounter];
		emptyBrick = new Rectangle [emptyBrickCounter];
		emptyGround = new Rectangle[emptyGroundCounter];
		invSpikes = new Rectangle[invSpikesCounter];
		flags = new Rectangle[1];
		
		//Setsup index variables which correspond with each tile array
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		int i5 = 0;
		int i6 = 0;
		int i7 = 0;
		int i8 = 0;
		int i9 = 0;
		int i10 = 0;
		//Defines each element of the tile arrays
		if (cam <= numCols - 16)
			storeWalls(cam, cam + 16, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
		else 
			storeWalls(numCols - 16, numCols, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
	}
	
	//Iterates through the level map and adds rectangles to each tile array
	//Is called above to avoid two large for loops
	void storeWalls(int start, int end, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
		for(int row = 0 ; row < numRows ; row++) {
			for(int col = start ; col < end ; col++) {
				int x = col * tileWidth - xSubtract - cam * 50;
				int y = row * tileHeight;
				if (map[row][col] == 1) {
					wallsGround[i1] = new Rectangle(x, y, tileWidth, tileHeight);
					i1++;
				}
				else if (map[row][col] == 2) {
					wallsBrick[i2] = new Rectangle(x, y, tileWidth, tileHeight);
					i2++;
					
				}
				else if (map[row][col] == 3) {
					y += 20;
					spikes[i3] = new Rectangle(x, y, tileWidth, 30);
					i3++;
				}
				else if (map[row][col] == 4) {
					questions[i4] = new Rectangle(x, y, tileWidth, tileHeight);
					i4++;
				}
				else if (map[row][col] == 5) {
					emptyQuestion[i5] = new Rectangle(x, y, tileWidth, tileHeight);
					i5++;
				}
				else if (map[row][col] == 6) {
					enemySpawn[i6] = new Rectangle(x, y, tileWidth, tileHeight);
					map[row][col] = 0;
					enemiesSpawned++;
					i6++;
				}
				else if (map[row][col] == 7) {
					invBrick[i7] = new Rectangle(x, y, tileWidth, tileHeight);
					i7++;
				}
				else if (map[row][col] == 8) {
					emptyBrick[i8] = new Rectangle(x, y, tileWidth, tileHeight);
					i8++;
				}
				else if (map[row][col] == 9) {
					emptyGround[i9] = new Rectangle(x, y, tileWidth, tileHeight);
					i9++;
				}
				else if (map[row][col] == 10) {
					y += 20;
					invSpikes[i10] = new Rectangle(x, y, tileWidth, tileHeight);
					i10++;
				}
				else if(map[row][col] == 11) {
					y -= 10;
					flags[0] = new Rectangle(x, y, tileWidth, tileHeight);
				}
			}
		}
	}
	
	//Iterates through a rectangle array and draws each tile with a given image, width, and height
	void drawArray(Rectangle[] array, Image image, int width, int height) {
		for (int i = 0 ; i < array.length ; i++) {
			if(array[i] == null)
				continue;
			try
			{
			offScreenBuffer.drawImage (image, (int) Math.round(array[i].getX()), (int) Math.round(array[i].getY()), width, height, this);
			}
			catch (NullPointerException e)
			{
			}
		}
	}
	
	//Draws a single rectangle with a given image, width, and height
	void draw(Rectangle rect, Image image, int width, int height) {
		try
		{
		offScreenBuffer.drawImage (image, (int) Math.round(rect.getX()), (int) Math.round(rect.getY()), width, height, this);
		}
		catch (NullPointerException e)
		{
		}
	}
	
	//If the player is dead, variables are reset (camera, x and y values, velocities)
	void deadCheck() {
		if(dead == true) {
			rect.x = spawnX;
			rect.y = spawnY;
			xVel = 0;
			yVel = 0;
			cam = 0;
			bgX = 0;
			bgSpeed = 1;
			game = false;
			//Resets the map
			if(level1State == true) {
				resetMap(level1, level1Orig);
				map = level1;
			}
			else if(level2State == true) {
				resetMap(level2, level2Orig);
				map = level2;
			}
			else if(level3State == true) {
				resetMap(level3, level3Orig);
				map = level3;
			}
			dead = false;
			enemiesSpawning = 0;
			enemiesSpawned = 0;
			enemies = new Rectangle[enemyCounter];
		}
	}
	
	//Copies the values of the elements in the second array to the first
	//This is used above to reset the actual values of the map instead of directing its location towards the original array
	void resetMap(int[][] array1, int[][] array2) {
		for(int row = 0 ; row < numRows ; row++) {
			for(int col = 0 ; col < numCols ; col++) {
				array1[row][col] = array2[row][col];
			}
		}
	}
	
	//If the player has won a level, it resets the player and camera and changes the map to the next level
	//If the player has won the game, the main game loop is broken and a win screen is displayed
	void winCheck() {
		if(winLevel == true) {
			rect.x = spawnX;
			rect.y = spawnY;
			xVel = 0;
			yVel = 0;
			cam = 0;
			bgX = 0;
			bgSpeed = 1; 
			enemiesSpawning = 0;
			enemiesSpawned = 0;
			enemies = new Rectangle[enemyCounter];
			if(level1State == true) {
				level2State = true;
				level1State = false;
			}
			else if(level2State == true) {
				level3State = true;
				level2State = false;
			}
			else if(level3State == true) {
				winGame = true;
				level3State = false;
				game = false;
			}
			changed = false;
			winLevel = false;
		}
	}
	
	//Adjusts the y coordinates of the coin to give off an animation of rising up from the question box
	void spawnCoin(int yCounter, int xCounter) {
		if(spawn == true) {
			Rectangle rect = spawnBlock;
			int x;
			if(game && cam <= numCols - 16)
				x = (int) Math.round(rect.getX()) + 5 - xCounter;
			else
				x = (int) Math.round(rect.getX()) + 5;
			int y = (int) Math.round(rect.getY()) - yCounter;
			offScreenBuffer.drawImage(coin, x, y, 38, 40, this);
			}
	}
	
	//Adjusts the y coordinates of a picture of mario to give off an animation
	void deathAnimate(int counter1, int counter2) {
		if(dead == true && game == true) {
			int x = 0;
			int y = 0;
			if (counter1 < 75) {
				x = (int) Math.round(rect.getX());
				y = (int) Math.round(rect.getY()) - counter1;
			}
			else {
				x = (int) Math.round(rect.getX());
				y = (int) Math.round(rect.getY()) - counter1 + counter2;
			}
			offScreenBuffer.drawImage(deadMario, x, y, 40, 60, this);
		}
	}
	
	//Adjusts the x coordinate of each enemy spawned to allow for movement
	//The enemies constantly move forwards
	void enemyMovement() {
		Rectangle[] temp = enemySpawn;
		if(temp.length > 0) {
			int index = 0;
			for(int i = enemiesSpawning ; i <= enemiesSpawned - 1 ; i++) {
				enemies[i] = temp[index];
				index++;
			}
			enemiesSpawning = enemiesSpawned;
		}
		
		for(int i = 0 ; i < enemies.length ; i++) {
			if(enemies[i] != null) {
				if(enemies[i].getX() + 75 < 0)
					enemies[i] = null;
			}
			if(enemies[i] == null)
				continue;
			int x = 0;
			if(!dead) {
				x = (int) Math.round(enemies[i].getX()) - goombaSpeed;
				int y = (int) Math.round(enemies[i].getY() + 16.2);
				enemies[i] = new Rectangle(x, y, tileWidth, tileHeight);
			}
		}
	}
	
	//Is used to get the location of the sound files
	public URL getCompleteURL (String fileName)
	{
		try
		{
			return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println (e.getMessage ());
		}
		return null;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame ("Game");
		game myPanel = new game ();
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}
}