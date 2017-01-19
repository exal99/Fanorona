import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
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
		try {
			List<String> lines = Files.readAllLines(Paths.get(file));
			pieces = new Piece[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				for (int pos = 0; pos < lines.get(line).length(); pos++) {
					if (pos > pieces[0].length) {
						throw new ParseException("Uneaven length of lines at line: " + line, line);
					}
					int color = Piece.getColor(lines.get(line).charAt(pos), parrent);
					System.out.println(lines.get(line).charAt(pos) + " " + color);
					if (color != 0) {
						int[] posInArray = {line, pos};
						pieces[line][pos] = new Piece(parrent, color, posInArray);
					} else {
						pieces[line][pos] = null;
					}
					if (pos == lines.get(line).length() - 1 && pos < pieces[0].length - 1 && pos != 0) {
						throw new ParseException("Uneaven length of lines at line: " + line, line);
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return pieces;
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
