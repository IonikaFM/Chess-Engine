import java.util.LinkedList;

public class Queen extends Piece {
    public Queen(Pair<Integer, Integer> currentPosition, String color) {
        super(currentPosition, color);
    }

    /**
     * Function checks if the move coordinates are inside the board size.
     *
     * @param move - pair of x and y coordinates
     * @return - true or false
     */
    @Override
    public boolean isValidMove(Pair<Integer, Integer> move) {

        return move.getFirst() >= 0 && move.getFirst() <= 7
                && move.getSecond() >= 0 && move.getSecond() <= 7;
    }

    /**
     * Function that adds to an array possible and valid moves for a given side: LEFT,
     * RIGHT, UP, DOWN, etc.
     *
     * @param moves - array of possible and valid moves
     * @param signX - int value which can be 1, 0, -1
     * @param signY - int value which can be 1, 0, -1
     */
    public void addSideMoves(LinkedList<Pair<Integer, Integer>> moves, Integer signX, Integer signY) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        for (int i = 1; i <= 8; i++) {
            if (isValidMove(generateMove(signX * i, signY * i))
                    && !isKingCheckedAfterTempMove(generateMove(signX * i, signY * i))) {
                if (chessBoard.getPiece(generateMove(signX * i, signY * i)) != null) {
                    if (!chessBoard.getPiece(generateMove(signX * i, signY * i)).getColor()
                            .equalsIgnoreCase(getColor())) {
                        moves.addFirst(generateMove(signX * i, signY * i));
                    }
                    break;

                } else {
                    moves.addLast(generateMove(signX * i, signY * i));
                }
            } else {
                break;
            }
        }
    }

    /**
     * Function that creates and returns an array of possible and valid moves.
     *
     * @return - array of moves represented by a pair of Integer coordinates.
     */
    @Override
    public LinkedList<Pair<Integer, Integer>> getPossibleMoves() {
        LinkedList<Pair<Integer, Integer>> moves = new LinkedList<>();

        // LEFT
        addSideMoves(moves, 0, -1);

        // RIGHT
        addSideMoves(moves, 0, 1);

        // UP
        addSideMoves(moves, -1, 0);

        // DOWN
        addSideMoves(moves, 1, 0);

        // UPPER LEFT DIAG
        addSideMoves(moves, -1, -1);

        // UPPER RIGHT DIAG
        addSideMoves(moves, -1, 1);

        // LOWER LEFT DIAG
        addSideMoves(moves, 1, -1);

        // LOWER RIGHT DIAG
        addSideMoves(moves, 1, 1);

        return moves;
    }

}
