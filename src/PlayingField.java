import java.text.ParseException;

import processing.core.PApplet;
import processing.core.PVector;

public class PlayingField {
	private PApplet parrent;
	private MoveDirection[][] directionsGrid;
	private Piece[][] pieceGrid;
	private Piece[][] actualPieceGrid;
	
	private int lastWidth;
	private int lastHeight;
	private float size;
	
	public PlayingField(PApplet parrent) {
		this.parrent = parrent;
		try {

			directionsGrid = Parser.parseDirection(System.getProperty("user.dir") + "\\data\\board.txt");
			pieceGrid = Parser.parsePieces(System.getProperty("user.dir") + "\\data\\board.txt", parrent);
			actualPieceGrid = new Piece[directionsGrid.length][directionsGrid[0].length];
			populatePieceGrid();
			lastWidth = 0;
			lastHeight = 0;
			size = 0;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private void populatePieceGrid() {
		int actualRow = -1;
		int actualCol = 0;
		for (int row = 0; row < directionsGrid.length; row++) {
			boolean entireDirection = true;
			actualCol = 0;
			for (int col = 0; col < directionsGrid[0].length; col++) {
				if (directionsGrid[row][col] == MoveDirection.CONNECTION) {
					if (entireDirection) {
						actualRow++;
						entireDirection = false;
					}
					int[] pos = {actualRow, actualCol};
					actualPieceGrid[row][col] = pieceGrid[actualRow][actualCol].clone();
					actualPieceGrid[row][col].setPos(pos);
					
					actualCol++;
				}
			}
		}
	}
	
	public static int[] createPos(int a, int b) {
		int[] r = {a, b};
		return r;
	}
	
	public MoveDirection[][] getDirections() {
		return directionsGrid;
	}
	
	public Piece[][] getActualPieceGrid() {
		return actualPieceGrid;
	}
	
	public void draw() {
		PVector start = getStartPos();
		boolean updateValues = false;
		if (parrent.width != lastWidth || parrent.height != lastHeight) {
			updateValues = true;
			lastWidth = parrent.width;
			lastHeight = parrent.height;
			size = getSize();
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
				if (updateValues) {
					current.setDisplayPos(PVector.add(start, new PVector(col * size, row * size)));
					current.setRadius((size/2) * 2/3);
					actualPieceGrid[current.getPos()[0]][current.getPos()[1]].setDisplayPos(current.getDisplayPos());
				}
				current.draw();
			}
		}
	}
	
	public void mousePressed(int mouseX, int mouseY) {
		Piece found = null;
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				if (p.isClicked(mouseX, mouseY)) {
					//something
					found = p;
				}
			}
		}
		for (Piece[] row : pieceGrid) {
			for (Piece p : row) {
				if (p != found) {
					p.setSelected(false);
				}
			}
		}
	}
	
	private void drawLine(MoveDirection direction, int[] pos) {
		int[] xyDelta = direction.getDelta();
		parrent.strokeWeight(3);
		parrent.stroke(100);
		Piece from = actualPieceGrid[pos[0] - xyDelta[1]][pos[1] - xyDelta[0]];
		Piece to   = actualPieceGrid[pos[0] + xyDelta[1]][pos[1] + xyDelta[0]];
		parrent.line(from.getDisplayPos().x, from.getDisplayPos().y, to.getDisplayPos().x, to.getDisplayPos().y);
	}
	
	private float getSize() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		return (rectWidth < rectHeight) ? rectWidth  : rectHeight;
	}
	
	private PVector getStartPos() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		if (rectWidth < rectHeight) {
			return new PVector(rectWidth/2, (parrent.height - pieceGrid.length * rectWidth) / 2 + rectWidth/2);
		} else {
			return new PVector((parrent.width - pieceGrid[0].length * rectHeight)/2 + rectHeight/2, rectHeight/2); 
		}
	}
}
