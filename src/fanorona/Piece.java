package fanorona;

import bases.PieceBase;
import processing.core.PApplet;
import processing.core.PVector;

public class Piece extends PieceBase<PlayingField, Piece>{
	private PApplet parrent;
	private PVector displayPos;
	private PVector newPos;
	private float radius;
	private float decreesingRadius;
	private boolean selected;
	private boolean canBeSelected;
	
	public Piece(PApplet parrent, int color, int[] pos, PlayingField grid) {
		super(color, pos, grid);
		this.parrent = parrent;
		displayPos   = new PVector(0,0);
		selected     = false;
		canBeSelected = false;
		newPos = new PVector();
		confermOption = false;
	}
	
	public void setDisplayPos(PVector newPos) {
		this.newPos = newPos;
	}
	
	public void setRadius(float newRadius) {
		radius = newRadius;
	}
	
	@Override
	public void setActive(boolean newActive) {
		super.setActive(newActive);
		if (!newActive) {
			decreesingRadius = radius;
		}
	}
	
	public void setCanSelect(boolean newVal) {
		canBeSelected = newVal;
	}
	
	
	public void draw() {
		if (active) {
			parrent.fill(color);
			parrent.noStroke();
			float moveSpeed = PApplet.dist(0, 0, parrent.width, parrent.height)/2.5f * 1/parrent.frameRate;
			if (selected) {
				parrent.strokeWeight(PApplet.dist(0, 0, parrent.width, parrent.height)/150);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, radius * 2, radius * 2);
			if (canBeSelected) {
				int color = (getColor('W', parrent) == this.color) ? getColor('B', parrent) : getColor('W', parrent);
				parrent.fill(color);
				parrent.noStroke();
				parrent.ellipse(displayPos.x, displayPos.y, radius/2, radius/2);
			}
			if (confermOption) {
				int color = (getColor('W', parrent) == this.color) ? getColor('B', parrent) : getColor('W', parrent);
				parrent.fill(color);
				parrent.textSize(12);
				
				float textHeight = parrent.textAscent() + parrent.textDescent();
				float percentOfHeight = textHeight/((radius * 2) * 0.9f);
				parrent.textSize(12 * 1/percentOfHeight);
				float textWidth = parrent.textWidth("?");
				float x = (displayPos.x - radius) + ((radius * 2) - textWidth)/2;
				float y = (displayPos.y - radius) + ((radius * 2) - (parrent.textAscent() + parrent.textDescent()))/2 + parrent.textAscent();
				parrent.text("?", x, y);

			}
			if ((displayPos.x - newPos.x < -moveSpeed/2 || displayPos.x - newPos.x > moveSpeed/2) ||
				 displayPos.y - newPos.y < -moveSpeed/2 || displayPos.y - newPos.y > moveSpeed/2) {
				PVector vect = PVector.sub(newPos, displayPos);
				vect.normalize();
				vect.mult(moveSpeed);
				displayPos.add(vect);
			} else if (displayPos.x != newPos.x || displayPos.y != newPos.y) {
				displayPos.x = newPos.x;
				displayPos.y = newPos.y;
			}
		} else if (decreesingRadius > 0) {
			parrent.fill(color);
			parrent.noStroke();
			if (selected) {
				parrent.strokeWeight(PApplet.dist(0, 0, parrent.width, parrent.height)/150);
				parrent.stroke(255 - parrent.brightness(color));
			}
			parrent.ellipse(displayPos.x, displayPos.y, decreesingRadius * 2, decreesingRadius * 2);
			decreesingRadius -= radius * 0.15;
		}
	}
	
	public PVector getDisplayPos() {
		return newPos;
	}
	
	
	public void setSelected(boolean newSelected) {
		selected = newSelected;
	}
	
	public boolean isClicked(int mouseX, int mouseY) {
		if (PApplet.dist(mouseX, mouseY, newPos.x, newPos.y) <= radius) {
			selected = !selected;
			return true;
		} else {
			return false;
		}
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
		return super.toString(parrent);
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

	@Override
	protected Piece[] makeConfermPair() {
		return new Piece[2];
	}
}
