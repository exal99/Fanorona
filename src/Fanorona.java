import processing.core.PApplet;

public class Fanorona extends PApplet {
	private PlayingField p;
	
	public void settings() {
		size(800,600);
	}
	
	public void setup() {
		fill(255);
		p = new PlayingField(this);
	}
	
	public void draw() {
		background(0,0,255);
		p.draw();
		noLoop();
	}

	public static void main(String[] args) {
		PApplet.main("Fanorona");
	}

}
