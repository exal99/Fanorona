package bases;

import java.util.ArrayList;
import java.util.Arrays;

import fanorona.MoveDirection;
import fanorona.PlayingField;
import processing.core.PApplet;

public abstract class PieceBase<E extends FieldBase<T, E>, T extends PieceBase<E,T>>  implements Cloneable<T, E>{
	protected int[] pos;
	protected boolean active;
	protected E grid;
	protected int color;
	protected ArrayList<int[]> visited;
	protected int[] lastDirection;
	protected boolean requireConferm;
	protected T[] canConfermWith;
	protected boolean confermOption;
	
	public PieceBase(int color, int[] pos, E grid) {
		this.color   = color;
		this.pos     = pos;
		this.grid 	 = grid;
		active 		 = true;
		visited 	 = new ArrayList<int[]>();
		lastDirection = null;
		requireConferm = false;
		canConfermWith = makeConfermPair();
	}
	
	public ArrayList<int[]> getVisited() {
		return visited;
	}

	public int[] getLastDirection() {
		return lastDirection;
	}

	public boolean isRequireConferm() {
		return requireConferm;
	}

	public T[] getCanConfermWith() {
		return canConfermWith;
	}

	public boolean isConfermOption() {
		return confermOption;
	}

	/**
	 * Creates a new 2 element piece-array used for the confirmation pair
	 * @return a 2 element piece-array.
	 */
	protected abstract T[] makeConfermPair();
	
	/**
	 * Sets the activ state of the piece
	 * @param newActive the active state
	 */
	public void setActive(boolean newActive) {
		active = newActive;
	}
	
	/**
	 * Returns if the piece require a confirmation
	 * @return <code>requireConferm</code>
	 */
	public boolean requiresConfermation() {
		return requireConferm;
	}
	
	/**
	 * Gets the position of the piece
	 * @return the position
	 */
	public int[] getPos() {
		return pos;
	}
	
	/**
	 * Gets the color of the piece
	 * @return the color of the piece
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Sets the position of the piece. This is also counts indirectly as a move adding the new position
	 * to the visited fields and saving the direction of the move.
	 * @param newPos the new position
	 */
	public void setPos(int[] newPos) {
		visited.add(pos);
		lastDirection = PlayingField.createPos(newPos[0] - pos[0], newPos[1] - pos[1]);
		pos = newPos;
	}
	
	/**
	 * Resets the movement of the piece, clearing the path (<code>visited</code>) and the last direction.
	 */
	public void resetMovement() {
		visited.clear();
		lastDirection = null;
	}
	
	/**
	 * Returns if the piece is active or not
	 * @return <code>true</code> if the piece is active else <code>false</code>
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Returns if the current piece can make a capturing move.
	 * @return <code>true</code> if the piece can make a capturing move else <code>false</code>.
	 */
	public boolean canCapture() {
		ArrayList<int[]> possibleMoves = getAllPossibleMoves();
		for (int[] newPos : possibleMoves) {
			if (isCaptureMove(newPos[0], newPos[1])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns if the piece can move.
	 * @return <code>true</code> if the piece can move else <code>false</code>
	 */
	public boolean canMove() {
		return getAllPossibleMoves().size() > 0;
	}
	
	/**
	 * Gets all possible moves the piece can make as a list of new position that the piece could be in.
	 * @return An <code>ArrayList</code> containing all possible position the piece could move too.
	 */
	protected ArrayList<int[]> getAllPossibleMoves() {
		MoveDirection[][] directions = grid.getDirections();
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		for (int dRow = -1; dRow < 2; dRow++) {
			for (int dCol = -1; dCol < 2; dCol++) {
				if (!(dRow == 0 && dCol == 0) && pos[0] + dRow >= 0 && pos[0] + dRow < directions.length && pos[1] + dCol >= 0 && pos[1] + dCol < directions[0].length) {
					int[] directionPos = PlayingField.createPos(pos[0] + dRow, pos[1] + dCol);
					int[] newPos = directions[pos[0] + dRow][pos[1] + dCol].getNewPos(directionPos, pos);
					if (newPos != null && isValidMove(newPos[0], newPos[1])) {
						possibleMoves.add(newPos);
					}
				}
			}
		}
		return possibleMoves;
	}
	
	/**
	 * Checks if the piece could move to the new position (a <code>Piece</code>).
	 * @param p The target <code>Piece</code> to move too.
	 * @return <code>true</code> if it is a valid move else <code>false</code>
	 */
	public boolean canMoveTo(T p) {
		if (!p.isActive()) {
			return isValidMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the piece could move to the given <code>newRow</code> and <code>newCol</code>.
	 * @param newRow the target row to move too.
	 * @param newCol the target column to move too.
	 * @return <code>true</code> if it is a valid move else <code>false</code>
	 */
	protected boolean isValidMove(int newRow, int newCol) {
		int[] direction = {newRow - pos[0], newCol - pos[1]};
		MoveDirection move = grid.getDirections()[pos[0] + direction[0] / 2][pos[1] + direction[1] / 2];
		if (move.validMove(PlayingField.createPos(pos[0] + direction[0] / 2,pos[1] + direction[1] / 2), pos) &&
			!grid.getActualPieceGrid()[newRow][newCol].isActive() && !containsPos(PlayingField.createPos(newRow, newCol)) &&
			validDirection(direction)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the given position has already been visited.
	 * @param pos a two element array in the form <code>{row, col}</code>
	 * @return <code>true</code> if the position has been visited, else <code>false</code>
	 */
	protected boolean containsPos(int[] pos) {
		for (int[] posToCheck : visited) {
			if (posToCheck[0] == pos[0] && posToCheck[1] == pos[1]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the move direction is valid based on the <code>lastDirection</code> i.e. that the directions is
	 * not the same as the last move.
	 * @param direction a two element array of the format <code>{rowDelta, columndDelta}</code>.
	 * @return <code>true</code> if it is a valid direction else <code>false</code>.
	 */
	protected boolean validDirection(int[] direction) {
		return (lastDirection == null) || (lastDirection[0] != direction[0] || lastDirection[1] != direction[1]);
	}
	
	/**
	 * Checks if the move to <code>newRow</code>, <code>newCol</code> is a capturing move. The position should be the position
	 * in the <b><code>actualPieceGrid</code></b>!
	 * @param newRow the new row in the <code>actualPieceGrid</code>.
	 * @param newCol the new column in the <code>actualPieceGrid</code>.
	 * @return <code>true</code> if it is a capturing move else <code>false</code>.
	 */
	public boolean isCaptureMove(int newRow, int newCol) {
		int[] direction = {newRow - pos[0], newCol - pos[1]};
		T pushPiece = grid.getPiece(newRow + direction[0], newCol + direction[1]);
		T pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if ((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) ||
			(pullPiece != null && pullPiece.isActive() && pullPiece.color != color)) {
			return true;
		}
		return false;
	}
	
	/**
	 * See the {@link #isCaptureMove(int, int) isCaptureMove} method.
	 * @param p the target piece in the <b><code>pieceGrid</code></b>!
	 * @return <code>true</code> if it is a capturing move, else <code>false</code>.
	 */
	public boolean isCaptureMove(T p) {
		if (!p.isActive()) {
			return isCaptureMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	/**
	 * Captures all the pieces along the path of movement from the current position to the position of given piece.
	 * @param p the target piece. The piece should come from the <b><code>pieceGrid</code></b>!
	 */
	public void capture(T p) {
		int newX = p.getPos()[0];
		int newY = p.getPos()[1];
		int[] direction = {newX - pos[0], newY - pos[1]};
		T pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		T pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if (!((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) && (pullPiece != null && pullPiece.isActive() && pullPiece.color != color))){
			if (pushPiece != null && pushPiece.isActive() && pushPiece.color != color) {
				T currPiece = pushPiece;
				int multiplyer = 2;
				while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
					grid.dissablePiece(currPiece.getPos());
					currPiece.setActive(false);
					currPiece = grid.getPiece(newX + multiplyer * direction[0], newY + multiplyer * direction[1]);
					multiplyer += 1;
				}
			}
			if (pullPiece != null && pullPiece.isActive() && pullPiece.color != color) {
				T currPiece = pullPiece;
				int multiplyer = 2;
				while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
					grid.dissablePiece(currPiece.getPos());
					currPiece.setActive(false);
					currPiece = grid.getPiece(pos[0] - multiplyer * direction[0], pos[1] - multiplyer * direction[1]);
					multiplyer += 1;
				}
			}
		} else {
			requireConferm = true;
			canConfermWith[0] = grid.getCorospondingPiece(pullPiece);
			canConfermWith[1] = grid.getCorospondingPiece(pushPiece);
			canConfermWith[0].confermOption = true;
			canConfermWith[1].confermOption = true;
		}
	}
	
	/**
	 * Chooses the piece as the confirm option (if it is one of the possibilities) and captures all required pieces.
	 * @param p the confirm piece. This piece should come from <b><code>pieceGrid</code></b>.
	 */
	public void conferm(T p) {
		if (canConfermWith[0].equals(p) || canConfermWith[1].equals(p)) {
			int newX = p.getPos()[0];
			int newY = p.getPos()[1];
			int[] direction = {newX - pos[0], newY - pos[1]};
			if (direction[0] == -4 || direction[0] == 4 || direction[1] == -4 || direction[1] == 4) {
				direction = PlayingField.createPos(direction[0] / 2, direction[1] / 2);
			}
			T currPiece = p;
			int multiplyer = 1;
			while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
				currPiece.setActive(false);
				grid.getPiece(currPiece.getPos()[0], currPiece.getPos()[1]).setActive(false);
				currPiece = grid.getCorospondingPiece(grid.getPiece(newX + multiplyer * direction[0], newY + multiplyer * direction[1]));
				multiplyer += 1;
			}
			requireConferm = false;
			canConfermWith[0].confermOption = false;
			canConfermWith[1].confermOption = false;
			canConfermWith = makeConfermPair();
		}
	}
	
	/**
	 * Returns a string representation of the piece.
	 * @param parrent the <code>Fanorona</code> parent.
	 * @return Either " ", "B" or "W" depending on the color and active state of the piece.
	 */
	public String toString(PApplet parrent) {
		if (!active) {
			return " ";
		}
		if (color == parrent.color(0)) {
			return "B";
		}else {
			return "W";
		}
	}
	
	/**
	 * Converts a character to the color of that piece. The recognised characters are: 'B' and 'W'. Everything else is just 0.
	 * @param letter the letter to convert.
	 * @param applet a <code>PApplet</code> so that an appropriate color can be returned.
	 * @return the color corresponding to the letter: 'B' is black, 'W' is white and everything else is 0.
	 */
	public static int getColor(char letter, PApplet applet) {
		switch (letter) {
		case 'B':
			return applet.color(0);
		case 'W':
			return applet.color(255);
		default:
			return 0;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PieceBase) {
			return Arrays.equals(((PieceBase) obj).pos, pos);
		} return false;
	}
	
	//public abstract T clone(E grid);
	
	public abstract T copy();

}
