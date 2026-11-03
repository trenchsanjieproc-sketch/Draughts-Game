package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/*The class represents a checkers game board state.
 * Standard checkers is played on an 8x8 grid (64 tiles), alternating light/dark.
 * Checkers only occupy the 32 dark tiles and move diagonally.
 */
public class Board {
	
	// ID indicating a coordinate point is outside the board or on a light tile.
	public static final int INVALID = -1;

	// ID of an empty checkerboard dark tile.
	public static final int EMPTY = 0;

	// ID of a normal black checker piece.
	public static final int BLACK_CHECKER = 6;
	
	// ID of a normal white checker piece.
	public static final int WHITE_CHECKER = 4;

	// ID of a black king checker piece.
	public static final int BLACK_KING = 7;
	
	// ID of a white king checker piece.
	public static final int WHITE_KING = 5;

	// Array holding 32 integers to store board state for 32 dark tiles.
	private int[] state;
	
	// Constructs a new checkerboard initialized with the standard starting piece layout.
	public Board() {
		reset();
	}
	
	/* Creates an exact clone of this board state.
	 * a new independent copy of this Board object
	 */
	public Board copy() {
		Board copy = new Board();
		copy.state = state.clone();
		return copy;
	}
	
	/* Resets the checkerboard to initial game setup:
	 * 12 black checkers on indices 0-11 (top rows) and 12 white checkers on indices 20-31 (bottom rows).
	 */
	public void reset() {
		this.state = new int[32];
		for (int i = 0; i < 12; i++) {
			set(i, BLACK_CHECKER);
			set(31 - i, WHITE_CHECKER);
		}
	}
	
	/* Searches the board for all dark tiles matching a specific piece ID.
	 * id piece ID to search for
	 * List of Point coordinates containing the matching piece ID
	 */
	public List<Point> find(int id) {
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < 32; i++) {
			if (get(i) == id) {
				points.add(toPoint(i));
			}
		}
		return points;
	}
	
	/* Sets the piece ID at specified 2D board coordinates (x, y).
	 * x-coordinate on board (0 to 7)
	 * y-coordinate on board (0 to 7)
	 * id new piece ID to set
	 */
	public void set(int x, int y, int id) {
		set(toIndex(x, y), id);
	}
	
	/* Sets the piece ID at a specific dark tile index (0 to 31).
	 * index tile index (0 to 31)
	 * id piece ID to assign
	 */
	public void set(int index, int id) {
		if (!isValidIndex(index)) {
			return;
		}
		if (id < 0) {
			id = EMPTY;
		}
		this.state[index] = id;
	}
	
	/* Gets the piece ID at specified 2D board coordinates (x, y).
	 * x-coordinate (0 to 7)
	 * y y-coordinate (0 to 7)
	 * piece ID or if off board / on light tile
	 */
	public int get(int x, int y) {
		return get(toIndex(x, y));
	}
	
	/* Gets the piece ID at a specific dark tile index (0 to 31).
	 * index tile index (0 to 31)
	 * piece ID or if index is out of bounds
	 */
	public int get(int index) {
		if (!isValidIndex(index)) {
			return INVALID;
		}
		return state[index];
	}
	
	/* Maps a 1D dark tile index (0 to 31) to 2D grid coordinates Point(x, y).
	 * index tile index (0 to 31)
	 * Point(x, y) or Point(-1, -1) if index is invalid
	 */
	public static Point toPoint(int index) {
		if (!isValidIndex(index)) {
			return new Point(-1, -1);
		}
		int y = index / 4;
		int x = 2 * (index % 4) + (y + 1) % 2;
		return new Point(x, y);
	}
	
	/* Maps 2D grid coordinates (x, y) to a 1D dark tile index (0 to 31).
	 * x x-coordinate (0 to 7)
	 * y y-coordinate (0 to 7)
	 * index (0 to 31) or -1 if invalid/light tile
	 */
	public static int toIndex(int x, int y) {
		if (!isValidPoint(new Point(x, y))) {
			return -1;
		}
		return y * 4 + x / 2;
	}
	
	/* Maps a Point object to a 1D dark tile index.
	 * p Point(x, y)
	 * index (0 to 31) or -1 if invalid
	 */
	public static int toIndex(Point p) {
		return (p == null) ? -1 : toIndex(p.x, p.y);
	}
	
	/* Calculates the middle tile Point between two jump positions.
	 * p1 start position
	 * p2 end position after jump
	 * middle tile Point or Point(-1, -1) if invalid jump
	 */
	public static Point middle(Point p1, Point p2) {
		if (p1 == null || p2 == null) {
			return new Point(-1, -1);
		}
		return middle(p1.x, p1.y, p2.x, p2.y);
	}
	
	/* Calculates middle tile Point from 1D start and end indices.
	 * index1 start index (0 to 31)
	 * index2 end index (0 to 31)
	 *  middle tile Point
	 */
	public static Point middle(int index1, int index2) {
		return middle(toPoint(index1), toPoint(index2));
	}
	
	/* Calculates middle tile Point given raw start and end coordinates.
	 * x1 start x
	 * y1 start y
	 * x2 end x
	 * y2 end y
	 * middle tile Point
	 */
	public static Point middle(int x1, int y1, int x2, int y2) {
		int dx = x2 - x1, dy = y2 - y1;
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
			return new Point(-1, -1);
		} else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) {
			return new Point(-1, -1);
		} else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
			return new Point(-1, -1);
		}
		return new Point(x1 + dx / 2, y1 + dy / 2);
	}
	
	/* Checks if a 1D index is within valid dark tile range (0 to 31 inclusive).
	 * testIndex index to check
	 * true if valid dark tile index
	 */
	public static boolean isValidIndex(int testIndex) {
		return testIndex >= 0 && testIndex < 32;
	}
	
	/* Checks if a Point(x, y) lies on a playable dark tile within board boundaries.
	 * testPoint Point to check
	 * true if valid dark tile coordinate
	 */
	public static boolean isValidPoint(Point testPoint) {
		if (testPoint == null) {
			return false;
		}
		final int x = testPoint.x, y = testPoint.y;
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		if (x % 2 == y % 2) { // Light tile condition
			return false;
		}
		return true;
	}

	/* Checks if a piece ID represents a black checker (normal or king).
	 * id piece ID to check
	 * true if black piece
	 */
	public static boolean isBlackChecker(int id) {
		return id == Board.BLACK_CHECKER || id == Board.BLACK_KING;
	}

	/* Checks if a piece ID represents a white checker (normal or king).
	 * id piece ID to check
	 * true if white piece
	 */
	public static boolean isWhiteChecker(int id) {
		return id == Board.WHITE_CHECKER || id == Board.WHITE_KING;
	}

	/* Checks if a piece ID represents a king checker (black king or white king).
	 * id piece ID to check
	 * true if king piece
	 */
	public static boolean isKingChecker(int id) {
		return id == Board.BLACK_KING || id == Board.WHITE_KING;
	}
	
	@Override
	public String toString() {
		StringBuilder obj = new StringBuilder(getClass().getName() + "[");
		for (int i = 0; i < 31; i++) {
			obj.append(get(i)).append(", ");
		}
		obj.append(get(31)).append("]");
		return obj.toString();
	}
}
