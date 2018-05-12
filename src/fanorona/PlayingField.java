package fanorona;
import java.text.ParseException;
import java.util.ArrayList;

import ai.PlayingFieldTree;
import bases.FieldBase;
import fanorona.Parser.GridPair;
import processing.core.PApplet;
import processing.core.PVector;

public class PlayingField extends FieldBase<Piece, PlayingField>{
	protected Fanorona parrent;
	
	protected int lastWidth;
	protected int lastHeight;
	protected float size;
	
	protected Timer whiteTimer;
	protected Timer blackTimer;
	protected float timerHeight;
	protected float timerHeightPercent;
	
	protected boolean blitz;
	
	public PlayingField(Fanorona parrent, String board) {
		super(parrent, board);
		this.parrent = parrent;
		
		pieceGrid = makePieceGrid();
		populatePieceGrid();
		lastWidth = 0;
		lastHeight = 0;
		size = 0;
		timerHeightPercent = 0.05f;
		timerHeight = parrent.height * timerHeightPercent;
		whiteTimer = null;
		blackTimer = null;
		blitz = false;
		
		
	}
	
	public PlayingField(Fanorona parrent, String board, int[] whiteTime, int[] blackTime) {
		this(parrent, board);
		whiteTimer = new Timer(whiteTime[0], whiteTime[1], whiteTime[2]);
		blackTimer = new Timer(blackTime[0], blackTime[1], blackTime[2]);
		timerHeightPercent = 0.1f;
		timerHeight = parrent.height * timerHeightPercent;
		blitz = true;
	}
	
//	protected PlayingField(PlayingField field) {
//		directionsGrid = field.directionsGrid;
//		pieceGrid = field.pieceGrid;
//		lastWidth = field.lastWidth;
//		lastHeight = field.lastHeight;
//		size = field.size;
//		selected = field.selected;
//		currentPlayer = field.currentPlayer;
//		moved = field.moved;
//		walkedAlong = field.walkedAlong;
//		timerHeightPercent = field.timerHeightPercent;
//		timerHeight = field.timerHeight;
//		whiteTimer = field.whiteTimer;
//		blackTimer = field.blackTimer;
//		blitz = field.blitz;
//		actualPieceGrid =  new Piece[directionsGrid.length][directionsGrid[0].length];
//		for (int i = 0; i < field.actualPieceGrid.length; i++) {
//			for (int j = 0; j < field.actualPieceGrid[i].length; j++) {
//				actualPieceGrid[i][j] = field.actualPieceGrid[i][j].clone();
//			}
//		}
//	}
//	
	@Override
	public int isVictory() {
		if (whiteTimer != null && blackTimer != null) {
			if (whiteTimer.isDone()) {
				return Piece.getColor('B', parrent);
			} else if (blackTimer.isDone()) {
				return Piece.getColor('W', parrent);
			}
		}
		return super.isVictory();
	}
	
	public void draw() {
		PVector start = getStartPos();
		
		parrent.fill(255, 255, 255, 100);
		parrent.rect(0, 0, parrent.width, timerHeight);
		parrent.rect(0, parrent.height - timerHeight, parrent.width, timerHeight);
		if (parrent.width != lastWidth || parrent.height != lastHeight || moved) {
			lastWidth = parrent.width;
			lastHeight = parrent.height;
			size = getSize();
			moved = false;
			timerHeight = parrent.height * timerHeightPercent;
			for (int row = 0; row < pieceGrid.length; row++) {
				for (int col = 0; col < pieceGrid[0].length; col++) {
					Piece current = pieceGrid[row][col];
					current.setDisplayPos(PVector.add(start, new PVector(col * size, row * size)));
					current.setRadius((size/2) * 2/3);
					actualPieceGrid[current.getPos()[0]][current.getPos()[1]].setDisplayPos(current.getDisplayPos());
				}
			}
		}
		
		for (int row = 0; row < directionsGrid.length; row++) {
			for (int col = 0; col < directionsGrid[0].length; col++) {
				if (directionsGrid[row][col] != MoveDirection.CONNECTION) {
					drawLine(directionsGrid[row][col], createPos(row, col));
				}
			}
		}
		for (int row = 0; row < pieceGrid.length; row++) {
			for (int col = 0; col < pieceGrid[0].length; col++) {
				Piece current = pieceGrid[row][col];
				current.draw();
			}
		}
		
		setPossibleSelect();
		int victory = isVictory();
		if (victory != 0) {
			String winner = (victory == Piece.getColor('W', parrent)) ? "White" : "Black";
			switch (winner) {
			case "White":
				parrent.showGameOver("Sorry you lost but\nyou can challange your\noppnent to a rematch", PApplet.PI, boardName, blitz);
				break;
			case "Black":
				parrent.showGameOver("Sorry you lost but\nyou can challange your\noppnent to a rematch", 0, boardName, blitz);
			}
		}
		if (blackTimer != null && whiteTimer != null) {
			blackTimer.updateTime();
			whiteTimer.updateTime();
			drawTimers();
		}
	}
	
	private void drawTimers() {
		parrent.textSize(12);
		float textHeight = parrent.textAscent() + parrent.textDescent();
		float percentOfHeight = textHeight/(timerHeight * 0.9f);
		parrent.textSize(12 * 1/percentOfHeight);
		float x = parrent.width * 0.05f;
		float y = (timerHeight - (parrent.textAscent() + parrent.textDescent()))/2 + parrent.textAscent();
		parrent.pushMatrix();
		parrent.text(whiteTimer.getTimeString(), x, y);
		parrent.translate(parrent.width, parrent.height);
		parrent.rotate(PApplet.PI);
		parrent.text(blackTimer.getTimeString(), x, y);
		parrent.popMatrix();
	}
	
	public void aiMove(int color) {
		if (color == currentPlayer) {
			PlayingFieldTree.makeBestMove(this, parrent, 5);
		}
	}
	
	public void mousePressed(int mouseX, int mouseY) {
		Piece found = null;
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				if (p.isClicked(mouseX, mouseY) && (p.getColor() == currentPlayer || (selected != null && !p.isActive()))) {
					found = p;
				} else if (p.isClicked(mouseX, mouseY) && mustConferm) {
					conferm(p, parrent);
//					toConfermTo.conferm(p);
//					mustConferm = toConfermTo.requiresConfermation();
//					if (!mustConferm) {
//						toConfermTo = null;
//					} if (!selected.canCapture()) {
//						nextTurn(parrent);
//					}
				}
			}
		}
		if (selected != null && found != null && selected.getColor() == currentPlayer) {
			makeMove(found, parrent);
		}
		if (lastMoved == null) {
			selected = found;
			if (found != null && found.getColor() == Piece.getColor('W', parrent) && whiteTimer != null && blackTimer != null &&
					!whiteTimer.started && !blackTimer.started) {
				whiteTimer.start();
			}
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != found) {
						p.setSelected(false);
					}
				}
			}
		} else {
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != selected) {
						p.setSelected(false);
					}
				}
			}
		}
	}
	
	public void setPossibleSelect() {
		if (lastMoved == null) {
			ArrayList<Piece> all = new ArrayList<Piece>();
			ArrayList<Piece> capture = new ArrayList<Piece>();
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p.getColor() == currentPlayer && p.isActive()) {
						if (p.canCapture()) {
							capture.add(p);
						} else if (p.canMove()) {
							all.add(p);
						}
					}
					p.setCanSelect(false);
				}
			}
			if (capture.size() > 0) {
				for (Piece p : capture) {
					p.setCanSelect(true);
				}
			} else {
				for (Piece p: all) {
					p.setCanSelect(true);
				}
			}
		} else {
			for (Piece[] row : pieceGrid) {
				for (Piece p : row) {
					if (p != lastMoved) {
						p.setCanSelect(false);
					}
				}
			}
		}
	}
	
	
	protected void nextTrurn() {
		super.nextTurn(parrent);
		
		if (whiteTimer != null && blackTimer != null) {
			if (whiteTimer.started) {
				whiteTimer.stop();
				blackTimer.start();
			} else {
				blackTimer.stop();
				whiteTimer.start();
			}
		}
	}
	
	private void drawLine(MoveDirection direction, int[] pos) {
		int[] xyDelta = direction.getDelta();
		parrent.strokeWeight(PApplet.dist(0, 0, parrent.width, parrent.height)/300);
		parrent.stroke(containsPos(pos) ? parrent.color(255, 0, 0) : 100);
		Piece from = actualPieceGrid[pos[0] - xyDelta[0]][pos[1] - xyDelta[1]];
		Piece to   = actualPieceGrid[pos[0] + xyDelta[0]][pos[1] + xyDelta[1]];
		parrent.line(from.getDisplayPos().x, from.getDisplayPos().y, to.getDisplayPos().x, to.getDisplayPos().y);
	}
	
	private float getSize() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = (parrent.height - timerHeight * 2)/ ((float) numHeight);
		return (rectWidth < rectHeight) ? rectWidth  : rectHeight;
	}
	
	private PVector getStartPos() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = (parrent.height - timerHeight * 2) / ((float) numHeight);
		if (rectWidth < rectHeight) {
			return new PVector(rectWidth/2, (parrent.height - pieceGrid.length * rectWidth) / 2 + rectWidth/2);
		} else {
			return new PVector((parrent.width - pieceGrid[0].length * rectHeight)/2 + rectHeight/2, rectHeight/2 + timerHeight); 
		}
	}

	@Override
	protected Piece[][] makePieceGrid() {
		Piece[][] grid = null;
		try {
			GridPair pair = Parser.parsePieces(System.getProperty("user.dir") + "\\data\\" + boardName);
			char[][] chars = pair.chars;
			int[][][] poses = pair.poses;
			
			grid = new Piece[chars.length][chars[0].length];
			for (int row = 0; row < chars.length; row++) {
				for (int col = 0; col < chars[0].length; col++) {
					char c = chars[row][col];
					int color = Piece.getColor(c, parrent);
					grid[row][col] = new Piece(parrent, color, poses[row][col], this);
					if (c == ' ') {
						grid[row][col].setActive(false);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return grid;
	}

	@Override
	protected Piece[][] makeEmptyActualGrid() {
		return new Piece[directionsGrid.length][directionsGrid[0].length];
	}
	
	private class Timer {
		private int minutesRemaining;
		private int secondsRemaining;
		private int millisecondsRemaining;
		private boolean done;
		private boolean started;
		private int lastTime;
		
		public Timer(int minutes, int seconds, int milli) {
			minutesRemaining = minutes;
			secondsRemaining = seconds;
			millisecondsRemaining = milli;
			done = false;
			started = false;
		}
		
		public void updateTime() {
			if (!done && started) {
				int millis = parrent.millis() - lastTime;
				lastTime = parrent.millis();
				secondsRemaining -= millis/1000;
				millisecondsRemaining -= millis%1000;
				if (millisecondsRemaining < 0) {
					millisecondsRemaining += 1000;
					secondsRemaining -= 1;
				}
				if (secondsRemaining < 0) {
					secondsRemaining += 60;
					minutesRemaining -= 1;
				}
				if (minutesRemaining < 0) {
					done = true;
					minutesRemaining = 0;
					secondsRemaining = 0;
					millisecondsRemaining = 0;
				}
			}
		}
		
		public boolean isDone() {
			return done;
		}
		
		public String getTimeString() {
			return String.format("%02d:%02d.%03d", minutesRemaining, secondsRemaining, millisecondsRemaining);
		}
		
		public void start() {
			started = true;
			lastTime = parrent.millis();
		}
		
		public void stop() {
			started = false;
		}
		
		
	}
}
