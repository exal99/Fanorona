package fanorona;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	private static ArrayList<Character> CHARACTERS;
	static {
		CHARACTERS = new ArrayList<Character>();
		CHARACTERS.add('W');
		CHARACTERS.add('B');
		CHARACTERS.add('-');
		CHARACTERS.add('|');
		CHARACTERS.add('/');
		CHARACTERS.add('\\');
		CHARACTERS.add(' ');
	}
	
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
	
	public static GridPair parsePieces(String file) throws ParseException {
		char pieces[][] = null;
		int poses[][][] = null;
		ArrayList<ArrayList<Character>> listPieces = new ArrayList<ArrayList<Character>>();
		ArrayList<ArrayList<Integer[]>> listPoses = new ArrayList<ArrayList<Integer[]>>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(file));
			pieces = new char[lines.size()][lines.get(0).length()];
			for (int line = 0; line < lines.size(); line++) {
				//int tempColor = Piece.getColor(lines.get(line).charAt(0), parrent);
				char tempChar = lines.get(line).charAt(0);
				if (tempChar == 'B' || tempChar == 'W' || tempChar == ' ') {
					listPieces.add(new ArrayList<Character>());
					listPoses.add(new ArrayList<Integer[]>());
					for (int pos = 0; pos < lines.get(line).length(); pos++) {
						if (pos > pieces[0].length) {
							throw new ParseException("Uneaven length of lines at line: " + line, line);
						}
						//int color = Piece.getColor(lines.get(line).charAt(pos), parrent);
						char currentChar = lines.get(line).charAt(pos);
						Integer[] arrayPos = {line, pos};
						if (currentChar == 'B' || currentChar == 'W') {
							listPieces.get(listPieces.size() - 1).add(currentChar);
							listPoses.get(listPoses.size() - 1).add(arrayPos);
						} else if (currentChar == ' ') {
							listPieces.get(listPieces.size() - 1).add(currentChar);
							listPoses.get(listPoses.size() - 1).add(arrayPos);
							//listPieces.get(listPieces.size() - 1).get(listPieces.get(listPieces.size() - 1).size() - 1).setActive(false);
						} else if (!CHARACTERS.contains((Character) lines.get(line).charAt(pos))){
							throw new ParseException("Invalid character at line: " + line + " column: " + pos + " \"" + lines.get(line).charAt(pos) + "\"", line);
						}
						if (pos == lines.get(line).length() - 1 && pos < pieces[0].length - 1 && pos != 0) {
							throw new ParseException("Uneaven length of lines at line: " + line, line);
						}
					}
				}
			}
			pieces = new char[listPieces.size()][listPieces.get(0).size()];
			poses = new int[listPoses.size()][listPoses.get(0).size()][2];
			for (int row = 0; row < listPieces.size(); row++) {
				for (int col = 0; col < listPieces.get(0).size(); col++) {
					pieces[row][col] = listPieces.get(row).get(col);
					poses[row][col] = Arrays.stream(listPoses.get(row).get(col)).mapToInt(Integer::intValue).toArray();
				}
			}
			GridPair pair = new GridPair();
			pair.chars = pieces;
			pair.poses = poses;
			return pair;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static class GridPair {
		public char[][] chars;
		public int[][][] poses;
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
