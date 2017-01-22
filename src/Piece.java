import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Piece {
	private PApplet parrent;
	private PVector displayPos;
	private float radius;
	private float decreesingRadius;
	private int color;
	private int[] pos;
	private boolean active;
	private boolean selected;
	private PlayingField grid;
	
	public Piece(PApplet parrent, int color, int[] pos, PlayingField grid) {
		this.parrent = parrent;
		this.color   = color;
		this.pos     = pos;
		this.grid 	 = grid;
		active 		 = true;
		displayPos   = new PVector(0,0);
		selected     = false;
	}
	
	public void setDisplayPos(PVector newPos) {
		displayPos = newPos;
	}
	
	public void setRadius(float newRadius) {
		radius = newRadius;
	}
	
	public void setActive(boolean newActive) {
		if (!newActive) {
			decreesingRadius = radius;
		}
		active = newActive;
	}
	
	public void draw() {
		if (active) {
			parrent.fill(color);
			parrent.noStroke();
			if (selected) {
				parrent.strokeWeight(5);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, radius * 2, radius * 2);
		} else if (decreesingRadius > 0) {
			parrent.fill(color);
			parrent.noStroke();
			if (selected) {
				parrent.strokeWeight(5);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, decreesingRadius * 2, decreesingRadius * 2);
			decreesingRadius -= 4;
		}
	}
	
	public PVector getDisplayPos() {
		return displayPos;
	}
	
	public int[] getPos() {
		return pos;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setPos(int[] newPos) {
		pos = newPos;
	}
	
	public void setSelected(boolean newSelected) {
		selected = newSelected;
	}
	
	public boolean isClicked(int mouseX, int mouseY) {
		if (PApplet.dist(mouseX, mouseY, displayPos.x, displayPos.y) <= radius) {
			selected = !selected;
			return true;
		} else {
			return false;
		}
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
	
	private ArrayList<int[]> getAllPossibleMoves() {
		MoveDirection[][] directions = grid.getDirections();
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		for (int dRow = -1; dRow < 2; dRow++) {
			for (int dCol = -1; dCol < 2; dCol++) {
				if (!(dRow == 0 && dCol == 0) && pos[0] + dRow >= 0 && pos[0] + dRow < directions.length && pos[1] + dCol >= 0 && pos[1] + dCol < directions[0].length) {
					int[] directionPos = PlayingField.createPos(pos[0] + dRow, pos[1] + dCol);
					int[] newPos = directions[pos[0] + dRow][pos[1] + dCol].getNewPos(directionPos, pos);
					if (newPos != null && grid.getPiece(newPos[0], newPos[1]) != null && !grid.getPiece(newPos[0], newPos[1]).isActive()) {
						possibleMoves.add(newPos);
					}
				}
			}
		}
		return possibleMoves;
	}
	
	public boolean canMoveTo(Piece p) {
		if (!p.isActive()) {
			return isValidMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	private boolean isValidMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		if (grid.getDirections()[pos[0] + direction[0] / 2][pos[1] + direction[1] / 2].validMove(PlayingField.createPos(pos[0] + direction[0] / 2,pos[1] + direction[1] / 2), pos)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isValidMove(Piece p) {
		if (!p.isActive()) {
			return isValidMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	private boolean isCaptureMove(int newX, int newY) {
		int[] direction = {newX - pos[0], newY - pos[1]};
		Piece pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		Piece pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if ((pushPiece != null && pushPiece.isActive() && pushPiece.color != color) ||
			(pullPiece != null && pullPiece.isActive() && pullPiece.color != color)) {
			return true;
		}
		return false;
	}
	
	public boolean isCaptureMove(Piece p) {
		if (!p.isActive()) {
			return isCaptureMove(p.pos[0], p.pos[1]);
		} else {
			return false;
		}
	}
	
	public void capture(Piece p) {
		int newX = p.getPos()[0];
		int newY = p.getPos()[1];
		int[] direction = {newX - pos[0], newY - pos[1]};
		Piece pushPiece = grid.getPiece(newX + direction[0], newY + direction[1]);
		Piece pullPiece = grid.getPiece(pos[0] - direction[0], pos[1] - direction[1]);
		if (pushPiece != null && pushPiece.isActive() && pushPiece.color != color) {
			Piece currPiece = pushPiece;
			int multiplyer = 2;
			while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
				grid.dissablePiece(currPiece.getPos());
				currPiece.setActive(false);
				currPiece = grid.getPiece(newX + multiplyer * direction[0], newY + multiplyer * direction[1]);
				multiplyer += 1;
			}
		}
		if (pullPiece != null && pullPiece.isActive() && pullPiece.color != color) {
			Piece currPiece = pullPiece;
			int multiplyer = 2;
			while(currPiece != null && currPiece.isActive() && currPiece.color != color) {
				grid.dissablePiece(currPiece.getPos());
				currPiece.setActive(false);
				currPiece = grid.getPiece(pos[0] - multiplyer * direction[0], pos[1] - multiplyer * direction[1]);
				multiplyer += 1;
			}
		}
	}
	
	private void pullCapture(int newX, int newY) {
		
	}
	
	private void pushCapture(int newX, int newY) {
		
	}
	
	@Override
	public Piece clone() {
		int[] newPos = {pos[0], pos[1]};
		Piece res = new Piece(parrent, color, newPos, grid);
		res.active = active;
		return res;
	}
	
	@Override
	public String toString() {
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
}
