package ai;

import java.util.ArrayList;
import java.util.Arrays;

import ai.PlayingFieldTree.Move;
import bases.PieceBase;
import fanorona.Fanorona;

public class TreePiece extends PieceBase<PlayingFieldTree, TreePiece>{
	
	private Fanorona parrent;

	public TreePiece(int color, int[] pos, PlayingFieldTree grid, Fanorona parrent) {
		super(color, pos, grid);
		this.parrent = parrent;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TreePiece(PieceBase piece, PlayingFieldTree grid, Fanorona parrent) {
		this(piece.getColor(), piece.getPos().clone(), grid, parrent);
		active = piece.isActive();
		visited = new ArrayList<int[]>();
		ArrayList<int[]> l = piece.getVisited();
		for(int[] visit : l) {
			visited.add(visit.clone());
		}
		lastDirection = (piece.getLastDirection() != null) ? piece.getLastDirection().clone() : null;
		requireConferm = piece.requiresConfermation();
		canConfermWith = makeConfermPair();
		if (piece.getCanConfermWith()[0] != null) {
			canConfermWith[0] = new TreePiece(piece.getCanConfermWith()[0], grid, parrent);
			canConfermWith[1] = new TreePiece(piece.getCanConfermWith()[1], grid, parrent);
		}
		confermOption = piece.isConfermOption();
	}
	
	public TreePiece(TreePiece p, PlayingFieldTree tree) {
		super(p.color, p.pos.clone(), tree);
		this.parrent = p.parrent;
		active = p.active;
		visited = new ArrayList<int[]>();
		for (int[] pos : p.visited) {
			visited.add(new int[] {pos[0], pos[1]});
		}
		lastDirection = (p.lastDirection != null) ? new int[] {p.lastDirection[0], p.lastDirection[1]} : null;
		requireConferm = p.requireConferm;
		if (p.canConfermWith[0] != null) {
			canConfermWith[0] = p.canConfermWith[0].clone(tree);
			canConfermWith[1] = p.canConfermWith[1].clone(tree);
		}
		confermOption = p.confermOption;
	}
	
	/**
	 * Returns a new <code>PlayingFieldTree</code> where the given position has been used to conferm the movement.
	 * @param pos the position in <b><code>actualPieceGrid</code></b>
	 * @return a new <code>PlayingFieldTree</code> with the given confermation executed.
	 */
	public PlayingFieldTree conferm(int[] pos, Move move) {
		PlayingFieldTree newTree = new PlayingFieldTree(grid, move);
		TreePiece toConfermTo = newTree.getToConfermTo();
		toConfermTo.conferm(newTree.getCorospondingPiece(newTree.getPiece(pos[0], pos[1])));
		toConfermTo.requireConferm = false;
		newTree.setMustConferm(false);
		return newTree;
	}
	
	public void setRequireConfermation(boolean newVal) {
		requireConferm = newVal;
	}
	
	
	public void setGrid(PlayingFieldTree grid) {
		this.grid = grid;
	}


	@Override
	protected TreePiece[] makeConfermPair() {
		return new TreePiece[2];
	}
	
	@Override
	public TreePiece clone(PlayingFieldTree tree) {
		return new TreePiece(this, tree);
	}
	
	public ArrayList<int[]> getAllPossibleMoves() {
		return (active) ? super.getAllPossibleMoves() : new ArrayList<int[]>();
	}
	
	public TreePiece[] getConfermPossibilities() {
		return canConfermWith;
	}
	
	@Override
	public String toString() {
		return super.toString(parrent);
	}

	@Override
	public TreePiece copy() {
		int[] newPos = {pos[0], pos[1]};
		TreePiece res = new TreePiece(color, newPos, grid, parrent);
		res.active = active;
		return res;
	}
	
	public static void main(String[] args) {
		int[] a = {1,2,3};
		int[] b = a;
		int[] c = a.clone();
		a[0] = 10;
		System.out.println(Arrays.toString(a) + Arrays.toString(b) + Arrays.toString(c));
		System.out.println((a == b) + " " + (a == c) + " " + (b == c));
	}

}
