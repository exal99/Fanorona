import processing.core.PApplet;
import processing.core.PVector;

public class Piece {
	private PApplet parrent;
	private PVector displayPos;
	private float radius;
	private int color;
	private int[] pos;
	private boolean active;
	private boolean selected;
	
	public Piece(PApplet parrent, int color, int[] pos) {
		this.parrent = parrent;
		this.color   = color;
		this.pos     = pos;
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
