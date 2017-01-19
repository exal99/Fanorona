import java.util.HashMap;

public enum MoveDirection {
	DIAGONAL_DOWN(1,1),
	DIAGONAL_UP(1,-1),
	HORIZONTAL(1,0),
	VERTICAL(0,1),
	CONNECTION(0,0);
	
	int deltaX, deltaY;
	MoveDirection(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}
	
	boolean validMove(int[] thisPos, int[] fromPos) {
		return (fromPos[0] + deltaX == thisPos[0] && fromPos[1] + deltaY == thisPos[1]) ||
			   (fromPos[0] - deltaX == thisPos[0] && fromPos[1] - deltaY == thisPos[1]);
	}
	
	int[] getNewPos(int[] thisPos, int[] fromPos) {
		if (validMove(thisPos, fromPos)) {
			if (fromPos[0] + deltaX == thisPos[0] && fromPos[1] + deltaY == thisPos[1]){
				int[] res = {fromPos[0] - deltaX, fromPos[1] - deltaY};
				return res;
			} else {
				int[] res = {fromPos[0] + deltaX, fromPos[1] + deltaY};
				return res;
			}			
		} else {
			return null;
		}
	}
	
	int[] getDelta() {
		int[] res = {deltaX, deltaY};
		return res;
	}
	
	static MoveDirection getToken(char c) {
		HashMap<Character, MoveDirection> directionMapping = new HashMap<Character, MoveDirection>();
		directionMapping.put('-', HORIZONTAL);
		directionMapping.put('/', DIAGONAL_UP);
		directionMapping.put('\\', DIAGONAL_DOWN);
		directionMapping.put('|', VERTICAL);
		return directionMapping.getOrDefault(c, CONNECTION);
	}
	
}
