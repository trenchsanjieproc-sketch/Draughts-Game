
package logic;

import java.awt.Point;
import java.util.List;

import model.Board;
import model.Game;

public class MoveLogic {

	// Determines if a proposed move in the current game state is legal.
	public static boolean isValidMove(Game game, int startIndex, int endIndex) {
		return game != null && isValidMove(game.getBoard(), game.isP1Turn(), startIndex, endIndex, game.getSkipIndex());
	}
	
	// Determines if a proposed move is legal given board state, turn player, and multi-jump lock state.
	public static boolean isValidMove(Board board, boolean isP1Turn, int startIndex, int endIndex, int skipIndex) {
		if (board == null || !Board.isValidIndex(startIndex) || !Board.isValidIndex(endIndex)) {
			return false;
		} else if (startIndex == endIndex) {
			return false;
		} else if (Board.isValidIndex(skipIndex) && skipIndex != startIndex) {
			return false;
		}
		
		if (!validateIDs(board, isP1Turn, startIndex, endIndex)) {
			return false;
		} else if (!validateDistance(board, isP1Turn, startIndex, endIndex)) {
			return false;
		}
		
		return true;
	}
	
	// Validates piece ownership and target tile vacancy.
	private static boolean validateIDs(Board board, boolean isP1Turn, int startIndex, int endIndex) {
		if (board.get(endIndex) != Board.EMPTY) {
			return false;
		}
		
		int id = board.get(startIndex);
		if ((isP1Turn && !Board.isBlackChecker(id)) || (!isP1Turn && !Board.isWhiteChecker(id))) {
			return false;
		}
		
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		if (midID != Board.INVALID && ((!isP1Turn && !Board.isBlackChecker(midID)) || (isP1Turn && !Board.isWhiteChecker(midID)))) {
			return false;
		}
		
		return true;
	}
	
	// Validates diagonal movement vectors and enforces mandatory capture jumps.
	private static boolean validateDistance(Board board, boolean isP1Turn, int startIndex, int endIndex) {
		Point start = Board.toPoint(startIndex);
		Point end = Board.toPoint(endIndex);
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		
		if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
			return false;
		}
		
		int id = board.get(startIndex);
		if ((id == Board.WHITE_CHECKER && dy > 0) || (id == Board.BLACK_CHECKER && dy < 0)) {
			return false;
		}
		
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		
		// If attempting a normal 1-step move (midID < 0), check if any of player's pieces can jump
		if (midID < 0) {
			List<Point> checkers;
			if (isP1Turn) {
				checkers = board.find(Board.BLACK_CHECKER);
				checkers.addAll(board.find(Board.BLACK_KING));
			} else {
				checkers = board.find(Board.WHITE_CHECKER);
				checkers.addAll(board.find(Board.WHITE_KING));
			}
			
			// If a capture jump is available anywhere for this player, normal moves are invalid
			for (Point p : checkers) {
				int index = Board.toIndex(p);
				if (!MoveGenerator.getSkips(board, index).isEmpty()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
