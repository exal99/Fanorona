import processing.core.PApplet;
import processing.core.PVector;

public class Piece {
	private PApplet parrent;
	private int color;
	private int[] pos;
	
	public Piece(PApplet parrent, int color, int[] pos) {
		this.parrent = parrent;
		this.color   = color;
		this.pos     = pos;
	}
	
	public void draw(float radius, PVector displayPos) {
		parrent.fill(color);
		parrent.noStroke();
		parrent.ellipse(displayPos.x, displayPos.y, radius * 2, radius * 2);
	}
	
	public int[] getPos() {
		return pos;
	}
	
	public int getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		if (color == 0) {
			return "B";
		} else {
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
