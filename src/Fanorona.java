import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import processing.core.PApplet;
import processing.core.PFont;

public class Fanorona extends PApplet {
	private enum Screen {MAIN, DIFFICULITY, GAME_TYPE, GAME, GAME_OVER};
	private PlayingField p;
	private MenuInterface mainMenu;
	private MenuInterface difficultSelection;
	private MenuInterface gameSpeed;
	private ConfermMenuInterface gameOver;
	private Screen currScreen;
	
	public void settings() {
		size(800,600);
	}
	
	public void setup() {
		fill(255);
		makeMainMenu();
		makeDifficulityMenu();
		surface.setResizable(true);
		currScreen = Screen.MAIN;
		PFont font = createFont("cour.ttf", 12);
		textFont(font);
	}
	
	private void makeDifficulityMenu() {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Can I Play Daddy?", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				//startGame("super_easy.txt");
				gameSpeed = createTypeSelection("super_easy.txt");
				currScreen = Screen.GAME_TYPE;
				return null;
			}

		});

		menu.put("Easy", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				//startGame("easy.txt");
				gameSpeed = createTypeSelection("easy.txt");
				currScreen = Screen.GAME_TYPE;
				return null;
			}

		});

		menu.put("Normal", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				//startGame("normal.txt");
				gameSpeed = createTypeSelection("normal.txt");
				currScreen = Screen.GAME_TYPE;
				return null;
			}

		});

		menu.put("Back", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});

		difficultSelection = new MenuInterface(this, menu);
		difficultSelection.setButtonHeightScale(0.45f);
		difficultSelection.setYPaddingScale(1.7f);
	}
	
	private MenuInterface createTypeSelection(String board) {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Normal", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, false);
				return null;
			}

		});

		menu.put("Blitz Mode", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, true);
				return null;
			}

		});


		menu.put("Back", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});
		
		return new MenuInterface(this, menu);

	}
	
	private void startGame(String board, boolean blitz) {
		currScreen = Screen.GAME;
		if (!blitz) {
			p = new PlayingField(this, board);
		} else {
			int[] whiteTimes = {3, 0, 0};
			int[] blackTimes = {3, 0, 0};
			p = new PlayingField(this, board, whiteTimes, blackTimes);
		}
	}

	private void makeMainMenu() {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Start", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				currScreen = Screen.DIFFICULITY;
				return null;
			}
		});
		menu.put("Quit", new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});
		mainMenu = new MenuInterface(this, menu);
	}
	
	public void draw() {
		background(255,159,2);
		switch (currScreen) {
		case MAIN:
			mainMenu.draw();
			break;
		case DIFFICULITY:
			difficultSelection.draw();
			break;
		case GAME_TYPE:
			gameSpeed.draw();
			break;
		case GAME:
			p.draw();
			break;
		case GAME_OVER:
			gameOver.draw();
			break;

		}
	}
	
	public void mousePressed() {
		if (currScreen == Screen.GAME) {
			p.mousePressed(mouseX, mouseY);
		}
	}
	
	public void keyPressed() {
		if (key == 'b') {
			back();
		} if (key == 's') {
			saveFrame("frame.png");
		}
	}
	
	private void back() {
		switch (currScreen) {
		case DIFFICULITY:
			currScreen = Screen.MAIN;
			break;
		case GAME:
			currScreen = Screen.DIFFICULITY;
			break;
		case GAME_TYPE:
			currScreen = Screen.DIFFICULITY;
			break;
		case GAME_OVER:
			currScreen = Screen.DIFFICULITY;
			break;
		case MAIN:
			exit();
			break;
		}
	}
	
	public void showGameOver(String message, float rotation, String board, boolean blitz) {
		LinkedHashMap<String, Callable<Object>> menu = new LinkedHashMap<String, Callable<Object>>();
		menu.put("Rematch!", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				startGame(board, blitz);
				return null;
			}
		});
		menu.put("Main Menu", new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				back();
				return null;
			}
		});
		gameOver = new ConfermMenuInterface(this, menu, message, rotation);
		currScreen = Screen.GAME_OVER;
	}

	public static void main(String[] args) {
		PApplet.main("Fanorona");
	}

}
