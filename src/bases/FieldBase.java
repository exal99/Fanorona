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
	
	/**
	 * Selects a piece from the <b><code>pieceGrid</b></code>
	 * @param row the row in the <code>pieceGrid</code>
	 * @param col the column in the <code>pieceGrid</code>
	 */
	public void select(int row, int col) {
		selected = pieceGrid[row][col];
	}
	
	public E[][] getPieceGrid() {
		return pieceGrid;
	}

	public E getSelected() {
		return selected;
	}

	public E getLastMoved() {
		return lastMoved;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean isMoved() {
		return moved;
	}

	public boolean isMustConferm() {
		return mustConferm;
	}

	public E getToConfermTo() {
		return toConfermTo;
	}

	public ArrayList<int[]> getWalkedAlong() {
		return walkedAlong;
	}

	public String getBoardName() {
		return boardName;
	}
	
	/**
	 * Creates and returns the <code>pieceGrid</code> used in the initialisation. Must be manually called!
	 */
	protected abstract E[][] makePieceGrid();
	
	/**
	 * Creates and returns an empty <code>pieceGrid</code>. Used for the <code>actualPieceGrid</code>.
	 * Is automatically called during initialisation.
	 */
	protected abstract E[][] makeEmptyActualGrid();
	
	/**
	 * Populates the <code>actualPieceGrid</code> based on the <code>directionsGrid</code>
	 * and <code>pieceGrid</code>. Must be manually called!
	 */
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
					actualPieceGrid[row][col] = pieceGrid[actualRow][actualCol].copy();
					actualPieceGrid[row][col].setPos(pos);
					
					actualCol++;
				}
			}
		}
	}
	
	/**
	 * Checks if one player is victorious and returns the players color if that is the case
	 * @return The color of the winning player if there is one, else <code>0</code>
	 */
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
	
	/**
	 * Creates a array containing the two elements
	 * @param a fist parameter
	 * @param b second parameter
	 * @return <code>{a, b}</code>
	 */
	public static int[] createPos(int a, int b) {
		return new int[] {a,b};
	}
	
	/**
	 * Gets the directionsGrid
	 * @return <code>directionsGrid</code>
	 */
	public MoveDirection[][] getDirections() {
		return directionsGrid;
	}
	
	/**
	 * Gets the actualPieceGrid
	 * @return <code>actualPieceGrid</code>
	 */
	public E[][] getActualPieceGrid() {
		return actualPieceGrid;
	}
	
	
	/**
	 * Checks if the move must be a capturing one.
	 * @return <code>true</code> if the move must capture a piece else <code>false</code>
	 */
	public boolean mustBeCapture() {
		
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
	
	/**
	 * Gets the piece at position <code>{row, col}</code> in the <code>actualPieceGrid</code>.
	 * @param row the row of the piece
	 * @param col the column of the piece
	 * @return the piece at position <code>{row, col}</code>
	 */
	public E getPiece(int row, int col) {
		if (row >= 0 && row < actualPieceGrid.length && col >= 0 && col < actualPieceGrid[0].length) {
			return actualPieceGrid[row][col];
		} else {
			return null;
		}
	}
	
	/**
	 * Disables the piece at position <code>{row, col}</code>.
	 * @param row the row of the piece
	 * @param col the col of the piece
	 */
	public void dissablePiece(int row, int col) {
		pieceGrid[row][col].setActive(false);
	}
	
	/**
	 * Disables the piece at position <code>pos</code>.
	 * @param pos a two element array of the format <code>{row, col}</code>
	 */
	public void dissablePiece(int[] pos) {
		dissablePiece(pos[0], pos[1]);
	}
	
	/**
	 * Moves the selected piece to <code>toMoveTo</code> if it is a valid move based on the current
	 * game state.
	 * @param toMoveTo the target piece to move to
	 * @param parrent the <code>Fanorona</code> parrent
	 */
	public void makeMove(E toMoveTo, Fanorona parrent) {
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
	
	/**
	 * Gets the corresponding piece from the <code>pieceGrid</code> i.e. converts a piece from the
	 * <code>actualPieceGrid</code> to <code>pieceGrid</code>. <p>
	 * 
	 * <code>actualPieceGrid</code> --{@literal >} <code>pieceGrid</code>
	 * @param p a piece from the <code>actualPieceGrid</code>
	 * @return a piece from the <code>pieceGrid</code> corresponding to <code>p</code>
	 */
	public E getCorospondingPiece(E p) {
		if (p != null && p.getPos()[0] >= 0 && p.getPos()[0] < pieceGrid.length &&
						 p.getPos()[1] >= 0 && p.getPos()[1] < pieceGrid[0].length){
			return pieceGrid[p.getPos()[0]][p.getPos()[1]];
		} else {
			return null;
		}
	}
	
	/**
§	 * Moves piece <code>from</code> to <code>to</code> capturing pieces if the move is a capturing one.
	 * Also advances the turns if the piece cannot capture any pieces anymore. <p>
	 * 
	 * <b>This method does not make any checks to see if the move is valid!</b>
	 * @param from the piece that moves.
	 * @param to the target of the move.
	 * @param parrent the <code>Fanorona</code> parrent.
	 */
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
	
	/**
	 * Makes everything ready for the next turn, i.e. reseting last movements and updating
	 * the current player.
	 * @param parrent the <code>Fanorona</code> parrent.
	 */
	public void nextTurn(Fanorona parrent) {
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
	
	/**
	 * Checks if <code>walkedAlong</code> contains the given position i.e. if the is a element <code>p</code>
	 * such that <code>p[0] == pos[0] {@literal &&} p[1] == pos[1]</code>.
	 * @param pos a two element array.
	 * @return <code>true</code> if <code>pos</code> is in <code>walkedAlong</code> else <code>false</code>.
	 */
	protected boolean containsPos(int[] pos) {
		for (int[] p : walkedAlong) {
			if (p[0] == pos[0] && p[1] == pos[1]) {
				return true;
			}
		}
		 return false;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (int row = 0; row < actualPieceGrid.length; row++) {
			StringBuilder rowString = new StringBuilder();
			for (int col = 0; col < actualPieceGrid[0].length; col++) {
				if (actualPieceGrid[row][col] != null) {
					rowString.append(actualPieceGrid[row][col]);
				} else {
					rowString.append(directionsGrid[row][col]);
				}
			}
			rowString.append("\n");
			res.append(rowString);
		}
		return res.toString();
	}
	
	public void setMustConferm(boolean newConferm) {
		mustConferm = newConferm;
		if (!newConferm) {
			toConfermTo = null;
		}
	}
	
	public void clearToConfermTo() {
		toConfermTo = null;
	}
	
	public void conferm(E p, Fanorona parrent) {
		toConfermTo.conferm(p);
		if (lastMoved != null && toConfermTo.requiresConfermation() != lastMoved.requiresConfermation()) {
			lastMoved.conferm(p);
		}
		mustConferm = toConfermTo.requiresConfermation();
		if (!mustConferm) {
			toConfermTo = null;
		}
		if (!mustConferm) {
			nextTurn(parrent);
		}
	}

}
