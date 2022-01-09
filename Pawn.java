import java.util.LinkedList;
import java.util.Objects;

public class Pawn extends Piece {
    private boolean madeFirstMove;

    /**
     * madeFirst - false if the Pawn wasn't moved, true otherwise.
     *
     * @param currentPosition - the current position of the Pawn.
     * @param color           - color of the Pawn(BLACK or WHITE).
     */
    public Pawn(Pair<Integer, Integer> currentPosition, String color) {
        super(currentPosition, color);
        this.madeFirstMove = false;
    }

    /**
     * Function that creates and returns an array of possible and valid moves.
     *
     * @return - array of moves represented by a pair of Integer coordinates.
     */
    @Override
    public LinkedList<Pair<Integer, Integer>> getPossibleMoves() {
        LinkedList<Pair<Integer, Integer>> possibleMoves = new LinkedList<>();
        ChessBoard chessBoard = ChessBoard.getInstance();
        Pair<Integer, Integer> oneStep, leftStep, rightStep;
        Pair<Integer, Integer> twoStep = new Pair<>(-1, -1);
        Pair<Integer, Integer> enPassant = new Pair<>(-1, -1);

        if (this.getColor().equalsIgnoreCase("BLACK")) {
            // if the Pawn wasn't moved and the cell in front of it is null
            if (!madeFirstMove && chessBoard.getPiece(generateMove(1, 0)) == null)
                twoStep = generateMove(2, 0);

            // en passant move for BLACK
            if (getCurrentPosition().getFirst().equals(4)) {
                Piece leftPiece = null;
                Piece rightPiece = null;
                Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> whiteLastMove = chessBoard.getWhiteMoves().
                        get(chessBoard.getWhiteMoves().size() - 1);

                if (getCurrentPosition().getSecond() - 1 >= 0)
                    leftPiece = chessBoard.getPiece(generateMove(0, -1));

                if (getCurrentPosition().getSecond() + 1 <= 7)
                    rightPiece = chessBoard.getPiece(generateMove(0, 1));

                if (leftPiece != null && leftPiece.getClass().getName().equalsIgnoreCase("PAWN") &&
                        leftPiece.getColor().equalsIgnoreCase("WHITE") &&
                        whiteLastMove.getFirst().equals(generateMove(2, -1)) &&
                        whiteLastMove.getSecond().equals(leftPiece.getCurrentPosition()))
                    enPassant = generateMove(1, -1);

                if (rightPiece != null && rightPiece.getClass().getName().equalsIgnoreCase("PAWN") &&
                        rightPiece.getColor().equalsIgnoreCase("WHITE") &&
                        whiteLastMove.getFirst().equals(generateMove(2, 1)) &&
                        whiteLastMove.getSecond().equals(rightPiece.getCurrentPosition()))
                    enPassant = generateMove(1, 1);

            }

            leftStep = generateMove(1, -1);
            rightStep = generateMove(1, 1);
            oneStep = generateMove(1, 0);

        } else {
            if (!madeFirstMove && chessBoard.getPiece(generateMove(-1, 0)) == null)
                twoStep = generateMove(-2, 0);

            // en passant move for WHITE
            if (getCurrentPosition().getFirst().equals(3)) {
                Piece leftPiece = null;
                Piece rightPiece = null;
                Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> blackLastMove = chessBoard.getBlackMoves().
                        get(chessBoard.getBlackMoves().size() - 1);

                if (getCurrentPosition().getSecond() - 1 >= 0)
                    leftPiece = chessBoard.getPiece(generateMove(0, -1));

                if (getCurrentPosition().getSecond() + 1 <= 7)
                    rightPiece = chessBoard.getPiece(generateMove(0, 1));

                if (leftPiece != null && leftPiece.getClass().getName().equalsIgnoreCase("PAWN") &&
                        leftPiece.getColor().equalsIgnoreCase("BLACK") &&
                        blackLastMove.getFirst().equals(generateMove(-2, -1)) &&
                        blackLastMove.getSecond().equals(leftPiece.getCurrentPosition()))
                    enPassant = generateMove(-1, -1);

                if (rightPiece != null && rightPiece.getClass().getName().equalsIgnoreCase("PAWN") &&
                        rightPiece.getColor().equalsIgnoreCase("BLACK") &&
                        blackLastMove.getFirst().equals(generateMove(-2, 1)) &&
                        blackLastMove.getSecond().equals(rightPiece.getCurrentPosition()))
                    enPassant = generateMove(-1, 1);
            }

            leftStep = generateMove(-1, -1);
            rightStep = generateMove(-1, 1);
            oneStep = generateMove(-1, 0);
        }

        if (enPassant.getFirst() != -1 && enPassant.getSecond() != -1 && !isKingCheckedAfterTempMove(enPassant))
            possibleMoves.addFirst(enPassant);
        if (isValidMove(twoStep) && !isKingCheckedAfterTempMove(twoStep))
            possibleMoves.addLast(twoStep);
        if (isValidMove(leftStep) && !isKingCheckedAfterTempMove(leftStep))
            possibleMoves.addFirst(leftStep);
        if (isValidMove(rightStep) && !isKingCheckedAfterTempMove(rightStep))
            possibleMoves.addFirst(rightStep);
        if (isValidMove(oneStep) && !isKingCheckedAfterTempMove(oneStep))
            possibleMoves.addLast(oneStep);

        return possibleMoves;
    }

    /**
     * Function checks if a move is valid or not.
     *
     * @param move - represents where we want to move the pawn.
     * @return - true if the given move is a valid one, false otherwise.
     */
    @Override
    public boolean isValidMove(Pair<Integer, Integer> move) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        if (move.getFirst() >= 0 && move.getFirst() <= 7
                && move.getSecond() >= 0 && move.getSecond() <= 7) {

            // one or two step move.
            if (this.getCurrentPosition().getSecond().equals(move.getSecond()))
                return chessBoard.getPiece(move) == null;

            else { // take move
                return chessBoard.getPiece(move) != null &&
                        !(chessBoard.getPiece(move).getColor().equals(this.getColor()));
            }
        }
        return false;
    }

    /**
     * Function that promotes a Pawn to one of 4 pieces: Queen, Rook, Knight, Bishop.
     * Creates a new piece and adds it to the board.
     *
     * @param pieceName - name of the piece that we want to promote the pawn.
     */
    public void promote(String pieceName) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        if (pieceName.equalsIgnoreCase("BISHOP"))
            chessBoard.addPiece(this.getCurrentPosition(),
                    new Bishop(this.getCurrentPosition(), this.getColor()));

        else if (pieceName.equalsIgnoreCase("KNIGHT"))
            chessBoard.addPiece(this.getCurrentPosition(),
                    new Knight(this.getCurrentPosition(), this.getColor()));

        else if (pieceName.equalsIgnoreCase("ROOK"))
            chessBoard.addPiece(this.getCurrentPosition(),
                    new Rook(this.getCurrentPosition(), this.getColor()));
        else
            chessBoard.addPiece(this.getCurrentPosition(),
                    new Queen(this.getCurrentPosition(), this.getColor()));
    }

    public boolean getMadeFirstMove() {
        return madeFirstMove;
    }

    public void setMadeFirstMove(boolean firstMove) {
        madeFirstMove = firstMove;
    }

    @Override
    public void moveTo(Pair<Integer, Integer> destination) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        // check for en passant move.
        if (chessBoard.getPiece(destination) == null && !getCurrentPosition().getSecond().equals(destination.getSecond())) {
            if (getColor().equalsIgnoreCase("BLACK"))
                chessBoard.removePiece(new Pair<>(destination.getFirst() - 1, destination.getSecond()));
            else
                chessBoard.removePiece(new Pair<>(destination.getFirst() + 1, destination.getSecond()));
        }
        super.moveTo(destination);
        setMadeFirstMove(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pawn pawn = (Pawn) o;
        return madeFirstMove == pawn.madeFirstMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), madeFirstMove);
    }
}
