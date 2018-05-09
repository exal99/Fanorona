package bases;

import java.text.ParseException;
import java.util.ArrayList;

import fanorona.Fanorona;
import fanorona.MoveDirection;
import fanorona.Parser;
import fanorona.Piece;

public abstract class FieldBase <E extends PieceBase<T, E>, T extends FieldBase<E, T>> {
	protected MoveDirection[][] directionsGrid;
	protected E[][] pieceGrid;
	protected E[][] actualPieceGrid;
	protected E selected;
	protected E lastMoved;
	
	protected int currentPlayer;

	protected boolean moved;
	protected boolean mustConferm;
	protected E toConfermTo;
	protected ArrayList<int[]> walkedAlong;
	
	protected String boardName;
	
	public FieldBase(Fanorona parrent, String board) {
		//this.parrent = parrent;
		boardName = board;
		MoveDirection[][] dgrid = null;
		try {
			dgrid = Parser.parseDirection(System.getProperty("user.dir") + "\\data\\" + boardName);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		directionsGrid = dgrid;
		actualPieceGrid = makeEmptyActualGrid();
		selected = null;
		currentPlayer = Piece.getColor('W', parrent);
		moved = false;
		walkedAlong = new ArrayList<int[]>();
	}
	
	protected abstract E[][] makePieceGrid();
	
	protected abstract E[][] makeEmptyActualGrid();
	
	protected void populatePieceGrid() {
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
	
	public int isVictory() {
		E found = null;
		for (E[] row : pieceGrid) {
			for (E p : row) {
				if (found == null && p.isActive()) {
					found = p;
				} else if (p.isActive() && p.getColor() != found.getColor()) {
					return 0;
				}
			}
		}
		return found.getColor();
	}
	
	public static int[] createPos(int a, int b) {
		return new int[] {a,b};
	}
	
	public MoveDirection[][] getDirections() {
		return directionsGrid;
	}
	
	public E[][] getActualPieceGrid() {
		return actualPieceGrid;
	}
	
	public boolean mustBeCapture() {
		/*
		 * Returns if there is at least one piece that can capture another one i.e.
		 * if the current move has to be a capturing one.
		 */
		for (E[] row : pieceGrid) {
			for (E p : row) {
				if (p.getColor() == currentPlayer && p.isActive()) {
					if (p.canCapture()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public E getPiece(int row, int col) {
		if (row >= 0 && row < actualPieceGrid.length && col >= 0 && col < actualPieceGrid[0].length) {
			return actualPieceGrid[row][col];
		} else {
			return null;
		}
	}
	
	
	
	public void dissablePiece(int row, int col) {
		pieceGrid[row][col].setActive(false);
	}
	
	public void dissablePiece(int[] pos) {
		dissablePiece(pos[0], pos[1]);
	}
	
	protected void makeMove(E toMoveTo, Fanorona parrent) {
		if (selected.canMoveTo(toMoveTo) && !mustConferm) {
			if (mustBeCapture()) {
				if (selected.isCaptureMove(toMoveTo)) {
					move(selected, toMoveTo, parrent);
				}
			} else {
				int tempPlayer = currentPlayer;
				move(selected, toMoveTo, parrent);
				if (tempPlayer == currentPlayer) {
					nextTurn(parrent);
				}
			}
		}
	}
	
	public E getCorospondingPiece(E p) {
		if (p != null && p.getPos()[0] >= 0 && p.getPos()[0] < pieceGrid.length &&
						 p.getPos()[1] >= 0 && p.getPos()[1] < pieceGrid[0].length){
			return pieceGrid[p.getPos()[0]][p.getPos()[1]];
		} else {
			return null;
		}
	}
	
	protected void move(E from, E to, Fanorona parrent) {
		if (from.isCaptureMove(to)) {
			from.capture(to);
		}
		int[] fActualPos = from.getPos();
		int[] tActualPos = to.getPos();
		int[] fPos = actualPieceGrid[fActualPos[0]][fActualPos[1]].getPos();
		int[] tPos = actualPieceGrid[tActualPos[0]][tActualPos[1]].getPos();
		int[] direction = {(tActualPos[0] - fActualPos[0])/2, (tActualPos[1] - fActualPos[1])/2};
		int[] linePos = {fActualPos[0] + direction[0], fActualPos[1] + direction[1]};
		walkedAlong.add(linePos);
		pieceGrid[fPos[0]][fPos[1]] = to;
		pieceGrid[tPos[0]][tPos[1]] = from;
		to.setPos(fActualPos);
		from.setPos(tActualPos);
		E temp = actualPieceGrid[fActualPos[0]][fActualPos[1]];
		actualPieceGrid[fActualPos[0]][fActualPos[1]] = actualPieceGrid[tActualPos[0]][tActualPos[1]];
		actualPieceGrid[tActualPos[0]][tActualPos[1]] = temp;
		actualPieceGrid[fActualPos[0]][fActualPos[1]].setPos(fPos);
		temp.setPos(tPos);
		mustConferm = from.requiresConfermation();
		moved = true;
		if (from.canCapture()) {
			lastMoved = from;
		} if (mustConferm) {
			toConfermTo = from;
		} if (!from.canCapture() && !mustConferm) {
			nextTurn(parrent);
		}
	}
	
	protected void nextTurn(Fanorona parrent) {
		lastMoved = null;
		currentPlayer = (currentPlayer == Piece.getColor('W', parrent)) ? Piece.getColor('B', parrent) : Piece.getColor('W', parrent);
		for (E[] row : pieceGrid) {
			for (E p : row) {
				p.resetMovement();
			}
		}
		
		for (E[] row : actualPieceGrid) {
			for (E p : row) {
				if (p != null) {
					p.resetMovement();
				}
			}
		}
		walkedAlong.clear();
	}
	
	protected boolean containsPos(int[] pos) {
		for (int[] p : walkedAlong) {
			if (p[0] == pos[0] && p[1] == pos[1]) {
				return true;
			}
		}
		 return false;
	}
}
