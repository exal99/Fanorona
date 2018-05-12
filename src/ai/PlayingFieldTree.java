package ai;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import bases.Cloneable;
import bases.FieldBase;
import bases.PieceBase;
import fanorona.Fanorona;
import fanorona.Parser;
import fanorona.Piece;
import fanorona.PlayingField;
import fanorona.Parser.GridPair;

public class PlayingFieldTree extends FieldBase<TreePiece, PlayingFieldTree>{
	
	private Fanorona parrent;
	private ArrayList<PlayingFieldTree> children;
	private Move moveTaken;
	private int value;

	public PlayingFieldTree(Fanorona parrent, String board) {
		super(parrent, board);
		this.parrent = parrent;
		pieceGrid = makePieceGrid();
		children = new ArrayList<PlayingFieldTree>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PlayingFieldTree(FieldBase t, Fanorona parrent) {
		this(parrent, t.getBoardName());
		PieceBase[][] pieceGrid = t.getPieceGrid();
		for (int row = 0; row < pieceGrid.length; row++) {
			for (int col = 0; col < pieceGrid[0].length; col++) {
				this.pieceGrid[row][col] = new TreePiece(pieceGrid[row][col], this, parrent);
			}
		}
		PieceBase[][] aPieceGrid = t.getActualPieceGrid();
		for (int row = 0; row < aPieceGrid.length; row++) {
			for (int col = 0; col < aPieceGrid[0].length; col++) {
				if (aPieceGrid[row][col] != null)
					this.actualPieceGrid[row][col] = new TreePiece(aPieceGrid[row][col], this, parrent);
			}
		}
//		if (t.getSelected() != null) {
//			int[] selectedPos = t.getSelected().getPos();
//			selected = getCorospondingPiece(actualPieceGrid[selectedPos[0]][selectedPos[1]]);
//		}
//		if (t.getLastMoved() != null) {
//			int[] lastMovedPos = t.getLastMoved().getPos();
//			lastMoved = getCorospondingPiece(actualPieceGrid[lastMovedPos[0]][lastMovedPos[1]]);
//		}
//		if (t.getToConfermTo() != null) {
//			int[] toConfermToPos = t.getToConfermTo().getPos();
//			toConfermTo = getCorospondingPiece(actualPieceGrid[toConfermToPos[0]][toConfermToPos[1]]);
//		}
		selected = findPiece(t.getSelected());
		lastMoved = findPiece(t.getLastMoved());
		toConfermTo = findPiece(t.getToConfermTo());
		//selected = (t.getSelected() != null) ? new TreePiece(t.getSelected(), this, parrent) : null;
		//lastMoved = (t.getLastMoved() != null) ? new TreePiece(t.getLastMoved(), this, parrent) : null;
		currentPlayer = t.getCurrentPlayer();
		moved = t.isMoved();
		mustConferm = t.isMustConferm();
		//toConfermTo = (t.getToConfermTo() != null) ? new TreePiece(t.getToConfermTo(), this, parrent) : null;
		walkedAlong = new ArrayList<int[]>();
		ArrayList<int[]> l = t.getWalkedAlong();
		for (int[] visited : l) {
			walkedAlong.add(visited.clone());
		}
	}
	
	@SuppressWarnings("rawtypes")
	private TreePiece findPiece(PieceBase p) {
		if (p != null) {
			int[] pos = p.getPos();
			return getCorospondingPiece(actualPieceGrid[pos[0]][pos[1]]);
		}
		return null;
	}
	
	public PlayingFieldTree(PlayingFieldTree t, Move moveTaken) {
		this(t.parrent, t.boardName);
		parrent = t.parrent;
		this.moveTaken = moveTaken;
		copyArray(t.actualPieceGrid, actualPieceGrid);
		pieceGrid = new TreePiece[t.pieceGrid.length][t.pieceGrid[0].length];
		copyArray(t.pieceGrid, pieceGrid);
		setGrid(pieceGrid, this);
		setGrid(actualPieceGrid, this);
		selected = findPiece(t.getSelected());
		lastMoved = findPiece(t.getLastMoved());
		toConfermTo = findPiece(t.getToConfermTo());
		//selected = (t.selected != null) ? t.selected.clone(this) : null;
		//lastMoved = (t.lastMoved != null) ? t.lastMoved.clone(this) : null;
		currentPlayer = t.currentPlayer;
		moved = t.moved;
		mustConferm = t.mustConferm;
		//toConfermTo = (t.toConfermTo != null) ? t.toConfermTo.clone(this) : null;
		walkedAlong = new ArrayList<int[]>();
		for (int[] pos : t.walkedAlong) {
			walkedAlong.add(pos.clone());
		}
	}
	
	private int minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == 0 || isVictory() != 0) {
			return evaluateValue();
		}
		MovesIterator moves = getAllPossibleMoves();
		if (maximizingPlayer) {
			int maxEval = Integer.MIN_VALUE;
			for (Move move : moves) {
				PlayingFieldTree child = executeMove(move);
				children.add(child);
				int newDepth = depth - ((child.currentPlayer != currentPlayer) ? 1 : 0);
				boolean newMaximizingPlayer = (child.currentPlayer != currentPlayer) ? !maximizingPlayer : maximizingPlayer;
				int eval = child.minimax(newDepth, alpha, beta, newMaximizingPlayer);
				maxEval = Integer.max(eval, maxEval);
				alpha = Integer.max(alpha, eval);
				if (beta <= alpha) 
					break;
			}
			value = maxEval;
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			for (Move move : moves) {
				PlayingFieldTree child = executeMove(move);
				children.add(child);
				int newDepth = depth - ((child.currentPlayer != currentPlayer) ? 1 : 0);
				boolean newMaximizingPlayer = (child.currentPlayer != currentPlayer) ? !maximizingPlayer : maximizingPlayer;
				int eval = child.minimax(newDepth, alpha, beta, newMaximizingPlayer);
				minEval = Integer.min(eval, minEval);
				beta= Integer.min(beta, eval);
				if (beta <= alpha) 
					break;
			}
			value = minEval;
			return minEval;
		}
	}
	
	public static void makeBestMove(PlayingField field, Fanorona parrent, int depth) {
		int currentPlayer = field.getCurrentPlayer();
		PlayingFieldTree current = new PlayingFieldTree(field, parrent);
		boolean maximizingPlayer = current.currentPlayer == Piece.getColor('W', parrent);
		
		current.minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, maximizingPlayer);
		ArrayList<Move> moves = current.getBestMove(maximizingPlayer);
		for (Move move : moves) {
			executeMove(field, parrent, move);
		}
		
		if (field.getCurrentPlayer() == currentPlayer) {
			field.nextTurn(parrent);
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void executeMove(FieldBase field, Fanorona parrent, Move move) {
		switch(move.type) {
		case MOVE:
			field.select(move.from[0], move.from[1]);
			field.makeMove(field.getPieceGrid()[move.too[0]][move.too[1]], parrent);
			break;
		case CONFERM_MOVE:
			field.conferm(field.getPieceGrid()[move.from[0]][move.from[1]], parrent);
			break;
		}
	}
	
	private ArrayList<Move> getBestMove(boolean maximizingPlayer) {
		ArrayList<Move> moves = new ArrayList<Move>();
		PlayingFieldTree current = this;
		do {
			current = current.findBestChild(maximizingPlayer);
			moves.add(current.moveTaken);
		} while (current.currentPlayer == currentPlayer);
		return moves;
	}
	
	private PlayingFieldTree findBestChild(boolean maximizingPlayer) {
		PlayingFieldTree bestField = children.get(0);
		for (PlayingFieldTree field : children) {
			if (maximizingPlayer && field.getValue() > bestField.getValue()) {
				bestField = field;
			} else if (!maximizingPlayer && field.getValue() < bestField.getValue()) {
				bestField = field;
			}
		}
		return bestField;
	}
	
	public int getValue() {
		return value;
	}
	
	public int evaluateValue() {
		int black = 0;
		int white = 0;
		for (TreePiece[] row : pieceGrid) {
			for (TreePiece piece : row) {
				if (piece.isActive()) {
					if (piece.getColor() == Piece.getColor('B', parrent)) {
						black++;
					} else if (piece.getColor() == Piece.getColor('W', parrent)) {
						white++;
					}
				}
			}
		}
		if (white == 0) {
			value = Integer.MIN_VALUE;
		} else if (black == 0) {
			value = Integer.MAX_VALUE;
		} else {
			value = white - black;
		}
		return value;
	}
	
	public TreePiece getToConfermTo() {
		return toConfermTo;
	}
	
	private void setGrid(TreePiece[][] pieceGrid, PlayingFieldTree field) {
		for (TreePiece[] row: pieceGrid) {
			for (TreePiece piece : row) {
				if (piece != null) 
					piece.setGrid(field);
			}
		}
	}
	
	private <T extends Cloneable<T, PlayingFieldTree>> void copyArray(T[][] copyFrom, T[][] copyTo) {
		for (int row = 0; row < copyFrom.length; row++) {
			for (int col = 0; col < copyFrom[0].length; col++) {
				if (copyFrom[row][col] != null) 
					copyTo[row][col] = (T) copyFrom[row][col].clone(this);
			}
		}
	}
	
	private PlayingFieldTree makeMove(int[] toMoveTo, Move move) {
		PlayingFieldTree newField = new PlayingFieldTree(this, move);
		newField.makeMove(newField.pieceGrid[toMoveTo[0]][toMoveTo[1]], parrent);
		return newField;
	}
	
	private PlayingFieldTree executeMove(Move move) {
		switch(move.type) {
		case MOVE: {
			TreePiece oldSelected = selected;
			selected = pieceGrid[move.from[0]][move.from[1]];
			PlayingFieldTree res = makeMove(move.too, move);
			selected = oldSelected;
			return res;
		}
		case CONFERM_MOVE:
			return conferm(move);
		}
		return null;
	}
	
	public PlayingFieldTree conferm(Move move) {
		PlayingFieldTree newField = new PlayingFieldTree(this, move);
		newField.conferm(newField.pieceGrid[move.from[0]][move.from[1]], parrent);
		return newField;
	}
	
	private MovesIterator getAllPossibleMoves() {
		return new MovesIterator();
	}
	

	@Override
	protected TreePiece[][] makePieceGrid() {
		TreePiece[][] grid = null;
		try {
			GridPair pair = Parser.parsePieces(System.getProperty("user.dir") + "\\data\\" + boardName);
			char[][] chars = pair.chars;
			int[][][] poses = pair.poses;
			
			grid = new TreePiece[chars.length][chars[0].length];
			for (int row = 0; row < chars.length; row++) {
				for (int col = 0; col < chars[0].length; col++) {
					char c = chars[row][col];
					int color = Piece.getColor(c, parrent);
					grid[row][col] = new TreePiece(color, poses[row][col], this, parrent);
					if (c == ' ') {
						grid[row][col].setActive(false);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return grid;
	}

	@Override
	protected TreePiece[][] makeEmptyActualGrid() {
		return new TreePiece[directionsGrid.length][directionsGrid[0].length];
	}
	
	private class MovesIterator implements Iterator<Move>, Iterable<Move> {
		
		private int row, col;
		private Queue<Move> foundMoves;
		
		public MovesIterator() {
			row = 0;
			col = 0;
			foundMoves = new ArrayDeque<Move>();
		}

		@Override
		public boolean hasNext() {
			if (!foundMoves.isEmpty()) {
				return true;
			}
			if (row < pieceGrid.length || col < pieceGrid[0].length) {
				updateQueue();
			}
			return !foundMoves.isEmpty();
		}

		@Override
		public Move next() {
			if (hasNext()) {
				return foundMoves.poll();
			} else {
				throw new NoSuchElementException("Iterator has no more elements");
			}
		}
		
		private ArrayList<int[]> filterMoves(int[] pos, ArrayList<int[]> moves, boolean mustCapture) {
			ArrayList<int[]> res = new ArrayList<int[]>();
			for(int[] move : moves) {
				if (pieceGrid[pos[0]][pos[1]].isCaptureMove(move[0], move[1]) == mustCapture) {
					res.add(actualPieceGrid[move[0]][move[1]].getPos());
				}
			}
			return res;
		}
		
		private void updateQueue() {
			boolean mustCapture = mustBeCapture();
			if (!mustConferm && lastMoved == null) {
				ArrayList<int[]> foundMoves = new ArrayList<int[]>();
				int[] lastPos = new int[2];
				while (foundMoves.isEmpty() && (row < pieceGrid.length && col < pieceGrid[0].length)) {
					if (pieceGrid[row][col].getColor() == currentPlayer) {
						lastPos = new int[]{row, col};
						foundMoves.addAll(filterMoves(lastPos, pieceGrid[row][col].getAllPossibleMoves(), mustCapture));
						col++;
						row += (col == pieceGrid[0].length) ? 1 : 0;
						col %= pieceGrid[0].length;
					} else {
						col++;
						row += (col == pieceGrid[0].length) ? 1 : 0;
						col %= pieceGrid[0].length;
					}
				}
				for (int[] move : foundMoves) {
					this.foundMoves.add(new Move(lastPos, move));
				}
			} else if (mustConferm){
				for (TreePiece p : toConfermTo.getConfermPossibilities()) { //p is from the pieceGrid
					int[] pos = p.getPos();
					foundMoves.add(new Move(actualPieceGrid[pos[0]][pos[1]].getPos()));
				}
				row = pieceGrid.length;
				col = pieceGrid[0].length;
			} else if (lastMoved != null) { // lastMoved is from pieceGrid
				int[] lastMovedPos = lastMoved.getPos();
				int[] lastPos = actualPieceGrid[lastMovedPos[0]][lastMovedPos[1]].getPos();
				for (int[] move : filterMoves(lastPos, lastMoved.getAllPossibleMoves(), true)) {
					this.foundMoves.add(new Move(lastPos, move));
				}
				row = pieceGrid.length;
				col = pieceGrid[0].length;
			}
		}

		@Override
		public Iterator<Move> iterator() {
			return this;
		}
		
	}
	
	
	public class Move {
		MoveType type;
		int[] from;
		int[] too;
		
		public Move(int[] from, int[] too) {
			type = MoveType.MOVE;
			this.from = from;
			this.too = too;
		}
		
		public Move(int[] conferm) {
			type = MoveType.CONFERM_MOVE;
			from = conferm;
			too = null;
		}
		
		public String toString() {
			switch (type) {
			case MOVE:
				return "{" + from[0] + "," + from[1] + "} -> {" + too[0] + "," + too[1] + "}";
			case CONFERM_MOVE:
				return "{" + from[0] + "," + from[1] + "}";
			default:
				return null;
			}
		}
	}
	
	private enum MoveType {
		CONFERM_MOVE,
		MOVE;
	}
	

}
