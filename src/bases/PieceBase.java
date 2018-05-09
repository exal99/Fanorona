package bases;

import java.util.ArrayList;

import fanorona.MoveDirection;
import fanorona.PlayingField;
import processing.core.PApplet;

public abstract class PieceBase<E extends FieldBase<T, E>, T extends PieceBase<E,T>> {
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
	
	protected abstract T[] makeConfermPair();
	
	public void setActive(boolean newActive) {
		active = newActive;
	}
	
	public boolean requiresConfermation() {
		return requireConferm;
	}
	
	public int[] getPos() {
		return pos;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setPos(int[] newPos) {
		visited.add(pos);
		lastDirection = PlayingField.createPos(newPos[0] - pos[0], newPos[1] - pos[1]);
		pos = newPos;
	}
	
	public void resetMovement() {
		visited.clear();
		lastDirection = null;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean canCapture() {
		ArrayList<int[]> possibleMoves = getAllPossibleMoves();
		for (int[] newPos : possibleMoves) {
			if (isCaptureMove(newPos[0], newPos[1])) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canMove() {
		return getAllPossibleMoves().size() > 0;
	}
	
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
	
	public boolean canMoveTo(T p) {
		if (!p.isActive()) {
			return isValidMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	protected boolean isValidMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		MoveDirection move = grid.getDirections()[pos[0] + direction[0] / 2][pos[1] + direction[1] / 2];
		if (move.validMove(PlayingField.createPos(pos[0] + direction[0] / 2,pos[1] + direction[1] / 2), pos) &&
			!grid.getActualPieceGrid()[newX][newY].isActive() && !containsPos(PlayingField.createPos(newX, newY)) &&
			validDirection(direction)) {
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean containsPos(int[] pos) {
		for (int[] posToCheck : visited) {
			if (posToCheck[0] == pos[0] && posToCheck[1] == pos[1]) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean validDirection(int[] direction) {
		return (lastDirection == null) || (lastDirection[0] != direction[0] || lastDirection[1] != direction[1]);
	}
	
	protected boolean isCaptureMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		T pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		T pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if ((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) ||
			(pullPiece != null && pullPiece.isActive() && pullPiece.color != color)) {
			return true;
		}
		return false;
	}
	
	public boolean isCaptureMove(T p) {
		if (!p.isActive()) {
			return isCaptureMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
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
	
	public void conferm(T p) {
		if (canConfermWith[0] == p || canConfermWith[1] == p) {
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
	
	public abstract T clone();

}
