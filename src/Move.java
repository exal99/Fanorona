import processing.core.PVector;

public class Move {
	private PlayingField field;
	private Piece piece;
	private int[] direction;
	private boolean valid;
	private boolean validSet;
	
	public Move(PlayingField f, Piece p, int[] d) {
		field = f;
		piece = p;
		direction = d;
		validSet = false;
	}
	
	public boolean isValid() {
		if (validSet) {
			return valid;
		} else {
			int[] pos = piece.getPos();
			PVector pPos = new PVector(pos[0], pos[1]);
			MoveDirection[][] directionGrid = field.getDirections();
			int[] movePos = {pos[0] + direction[0],pos[1] + direction[1]};
			PVector pMovePos = new PVector(movePos[0], movePos[1]);
			while (directionGrid[(int) pMovePos.x][(int) pMovePos.y] != MoveDirection.CONNECTION) {
				if (directionGrid[(int) pMovePos.x][(int) pMovePos.y].validMove(
						PlayingField.createPos((int) pMovePos.x, (int) pMovePos.y),
						PlayingField.createPos((int) pPos.x, (int) pPos.y))) {
					pPos = pMovePos.copy();
					pMovePos.add(new PVector(direction[0], direction[1]));
				} else {
					validSet = true;
					valid = false;
					return false;
				}
			}
			Piece potentialGood = field.getActualPieceGrid()[(int) pMovePos.x][(int) pMovePos.y];
			if (potentialGood.isActive()) {
				validSet = true;
				valid = true;
				return true;
			} else {
				validSet = true;
				valid = false;
				return false;
			}
		}
	}
}
