import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class Parser {
	
	public static MoveDirection[][] parseDirection(String file) throws ParseException {
		MoveDirection directions[][] = null;
		try {
			List<String> lines = Files.readAllLines(Paths.get(file));
			directions = new MoveDirection[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				for (int pos = 0; pos < lines.get(line).length(); pos++) {
					if (pos > directions[0].length) {
						throw new ParseException("Uneaven length of lines at line: " + line, line);
					}
					MoveDirection token = MoveDirection.getToken(lines.get(line).charAt(pos));
					if (token != null) {
						directions[line][pos] = token;
					} else {
						throw new ParseException("Invalid token at line: " + line + " position: " + pos, pos);
					}
					if (pos == lines.get(line).length() - 1 && pos < directions[0].length - 1 && pos != 0) {
						throw new ParseException("Uneaven length of lines at line: " + line, line);
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return directions;
	}
	
	public static Piece[][] parsePieces(String file, PApplet parrent) throws ParseException {
		Piece pieces[][] = null;
		ArrayList<ArrayList<Piece>> listPieces = new ArrayList<ArrayList<Piece>>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(file));
			pieces = new Piece[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				int tempColor = Piece.getColor(lines.get(line).charAt(0), parrent);
				if (tempColor != 0) {
					listPieces.add(new ArrayList<Piece>());
					for (int pos = 0; pos < lines.get(line).length(); pos++) {
						if (pos > pieces[0].length) {
							throw new ParseException("Uneaven length of lines at line: " + line, line);
						}
						int color = Piece.getColor(lines.get(line).charAt(pos), parrent);
						if (color != 0) {
							int[] arrayPos = {line, pos};
							listPieces.get(listPieces.size() - 1).add(new Piece(parrent, color, arrayPos));
						}
						if (lines.get(line).charAt(pos) == ' ') {
							int[] arrayPos = {line, pos};
							listPieces.get(listPieces.size() - 1).add(new Piece(parrent, color, arrayPos));
							listPieces.get(listPieces.size() - 1).get(listPieces.get(listPieces.size() - 1).size() - 1).setActive(false);
						}
						if (pos == lines.get(line).length() - 1 && pos < pieces[0].length - 1 && pos != 0) {
							throw new ParseException("Uneaven length of lines at line: " + line, line);
						}
					}
				}
			}
			pieces = new Piece[listPieces.size()][listPieces.get(0).size()];
			for (int row = 0; row < listPieces.size(); row++) {
				for (int col = 0; col < listPieces.get(0).size(); col++) {
					pieces[row][col] = listPieces.get(row).get(col);
				}
			}
			return pieces;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		try {
			MoveDirection[][] res = parseDirection("C:\\Users\\Alexander\\Documents\\Javaprogram\\Fanorona\\src\\board.txt");
			for (MoveDirection[] line : res) {
				for (MoveDirection token : line) {
					System.out.print(token.name() + " ");
				}
				System.out.println();
			}
			int[] fromPos = {0,0};
			int[] thisPos = {0,1};
			System.out.println(res[1][0].validMove(thisPos, fromPos));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
