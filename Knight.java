import java.util.LinkedList;

public class Knight extends Piece {
    public Knight(Pair<Integer, Integer> currentPosition, String color) {
        super(currentPosition, color);
    }

    /**
     * Function that creates and returns an array of possible and valid moves.
     *
     * @return - array of moves represented by a pair of Integer coordinates.
     */
    @Override
    public LinkedList<Pair<Integer, Integer>> getPossibleMoves() {
        LinkedList<Pair<Integer, Integer>> result = new LinkedList<>();

        Pair<Integer, Integer> move1R = generateMove(-2, 1);
        Pair<Integer, Integer> move2R = generateMove(2, 1);
        Pair<Integer, Integer> move3R = generateMove(-1, 2);
        Pair<Integer, Integer> move4R = generateMove(1, 2);

        Pair<Integer, Integer> move1L = generateMove(-2, -1);
        Pair<Integer, Integer> move2L = generateMove(2, -1);
        Pair<Integer, Integer> move3L = generateMove(-1, -2);
        Pair<Integer, Integer> move4L = generateMove(+1, -2);

        prioritization(result, move1R);
        prioritization(result, move2R);
        prioritization(result, move3R);
        prioritization(result, move4R);

        prioritization(result, move1L);
        prioritization(result, move2L);
        prioritization(result, move3L);
        prioritization(result, move4L);

        return result;
    }

    /**
     * Function that adds a move at the start ( if it takes an opponent piece) or
     * at the end ( if not).
     *
     * @param result
     * @param move
     */
    public void prioritization(LinkedList<Pair<Integer, Integer>> result, Pair<Integer, Integer> move) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        if (isValidMove(move) && !isKingCheckedAfterTempMove(move)) {
            if (chessBoard.getPiece(move) != null)
                result.addFirst(move);
            else
                result.addLast(move);
        }
    }

    /**
     * Function checks if a move is valid or not.
     *
     * @param move - represents where we want to move the knight.
     * @return - true if the given move is a valid one, false otherwise.
     */
    @Override
    public boolean isValidMove(Pair<Integer, Integer> move) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        if (move.getFirst() >= 0 && move.getFirst() <= 7
                && move.getSecond() >= 0 && move.getSecond() <= 7) {

            if (chessBoard.getPiece(move) == null || !chessBoard.getPiece(move)
                    .getColor().equalsIgnoreCase(getColor()))
                return true;
        }
        return false;
    }
}