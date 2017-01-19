import processing.core.PApplet;

public class Fanorona extends PApplet {
	private PlayingField p;
	
	public void settings() {
		size(800,600);
	}
	
	public void setup() {
		fill(255);
		p = new PlayingField(this);
		surface.setResizable(true);
	}
	
	public void draw() {
		background(0,0,255);
		p.draw();
	}
	
	public void mousePressed() {
		p.mousePressed(mouseX, mouseY);
	}

	public static void main(String[] args) {
		PApplet.main("Fanorona");
	}

}
