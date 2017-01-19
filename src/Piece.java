import processing.core.PApplet;
import processing.core.PVector;

public class Piece {
	private PApplet parrent;
	private PVector displayPos;
	private float radius;
	private int color;
	private int[] pos;
	private boolean active;
	
	public Piece(PApplet parrent, int color, int[] pos) {
		this.parrent = parrent;
		this.color   = color;
		this.pos     = pos;
		active 		 = true;
		displayPos = new PVector(0,0);
	}
	
	public void setDisplayPos(PVector newPos) {
		displayPos = newPos;
	}
	
	public void setRadius(float newRadius) {
		radius = newRadius;
	}
	
	public void setActive(boolean newActive) {
		active = newActive;
	}
	
	public void draw() {
		if (active) {
			parrent.fill(color);
			parrent.noStroke();
			parrent.ellipse(displayPos.x, displayPos.y, radius * 2, radius * 2);
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
	
	@Override
	public Piece clone() {
		int[] newPos = {pos[0], pos[1]};
		return new Piece(parrent, color, newPos);
	}
	
	@Override
	public String toString() {
		if (!active) {
			return " ";
		}
		if (color == -1) {
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
