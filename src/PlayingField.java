import java.text.ParseException;

import processing.core.PApplet;
import processing.core.PVector;

public class PlayingField {
	private PApplet parrent;
	private MoveDirection[][] directionsGrid;
	private Piece[][] pieceGrid;
	
	public PlayingField(PApplet parrent) {
		this.parrent = parrent;
		try {
			directionsGrid = Parser.parseDirection("C:\\Users\\Alexander\\Documents\\Javaprogram\\Fanorona\\src\\board.txt");
			pieceGrid = Parser.parsePieces("C:\\Users\\Alexander\\Documents\\Javaprogram\\Fanorona\\src\\board.txt", parrent);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private int[] createPos(int a, int b) {
		int[] r = {a, b};
		return r;
	}
	
	public void draw() {
		float size = getSize();
		PVector start = getStartPos();
		int actualRow = 0;
		int actualCol = 0;
		System.out.println(size + " " + start.x + ", " + start.y);
		System.out.println(parrent.color(255, 255, 255));
		for (int row = 0; row < pieceGrid.length; row++) {
			if (pieceGrid[row][0] != null){
				for (int col = 0; col < pieceGrid[0].length; col++) {
					if (pieceGrid[row][col] != null) {
						pieceGrid[row][col].draw(size/2, PVector.add(start, new PVector(actualCol * size - size/2, actualRow * size + size/2)));
						actualCol++;
					}
				}
				actualRow++;
				actualCol = 0;
			}
		}
	}
	
	private float getSize() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		return (rectWidth < rectHeight) ? rectWidth : rectHeight;
	}
	
	private PVector getStartPos() {
		int numHeight = pieceGrid.length;
		int numWidth = pieceGrid[0].length;
		float rectWidth = parrent.width / ((float) numWidth);
		float rectHeight = parrent.height / ((float) numHeight);
		if (rectWidth < rectHeight) {
			return new PVector(0, (parrent.height - pieceGrid.length * rectWidth) / 2);
		} else {
			return new PVector((parrent.width - pieceGrid[0].length * rectHeight)/2, 0); 
		}
	}
}
