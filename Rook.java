import java.util.LinkedList;

public class Rook extends Piece {
    private boolean wasMoved;

    public Rook(Pair<Integer, Integer> currentPosition, String color) {
        super(currentPosition, color);
        this.wasMoved = false;
    }

    /**
     * Function checks if a move is valid.
     *
     * @param move the coordinates x and y where we want to move
     * @return true if the given move is valid, false otherwise
     */
    @Override
    public boolean isValidMove(Pair<Integer, Integer> move) {
        return move.getFirst() >= 0 && move.getFirst() <= 7
                && move.getSecond() >= 0 && move.getSecond() <= 7;
    }

    public void addSideMoves(LinkedList<Pair<Integer, Integer>> moves, Integer OX, Integer OY) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        for (int i = 1; i <= 8; i++) {
            if (isValidMove(generateMove(OX * i, OY * i))
                    && !isKingCheckedAfterTempMove(generateMove(OX * i, OY * i))) {
                if (chessBoard.getPiece(generateMove(OX * i, OY * i)) != null) {
                    if (!chessBoard.getPiece(generateMove(OX * i, OY * i)).getColor().equalsIgnoreCase(getColor())) {
                        moves.addFirst(generateMove(OX * i, OY * i));
                    }
                    break;
                } else {
                    moves.addLast(generateMove(OX * i, OY * i));
                }
            } else {
                break;
            }
        }
    }

    /**
     * Function that create and return an array of possible and valid moves
     *
     * @return-array of moves represented by a pair of Integer coordinates
     */
    @Override
    public LinkedList<Pair<Integer, Integer>> getPossibleMoves() {
        LinkedList<Pair<Integer, Integer>> moves = new LinkedList<>();

        // left
        addSideMoves(moves, 0, -1);

        // right
        addSideMoves(moves, 0, 1);

        // up
        addSideMoves(moves, -1, 0);

        // down
        addSideMoves(moves, 1, 0);

        return moves;
    }

    public boolean wasMoved() {
        return wasMoved;
    }

    @Override
    public void moveTo(Pair<Integer, Integer> destination) {
        super.moveTo(destination);
        this.wasMoved = true;
    }
}

