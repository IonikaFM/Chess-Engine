import java.util.LinkedList;
import java.util.Objects;

public abstract class Piece {
    private Pair<Integer, Integer> currentPosition;
    private String color;

    public Piece(Pair<Integer, Integer> currentPosition, String color) {
        this.currentPosition = currentPosition;
        this.color = color;
    }

    public abstract LinkedList<Pair<Integer, Integer>> getPossibleMoves();
    public abstract boolean isValidMove(Pair<Integer, Integer> move);

    /**
     * Generates a move from piece initial position.
     *
     * @param i - how much to add to x index;
     * @param j - how much to add to y index;
     * @return - a pair of 2 indices
     */
    public Pair<Integer, Integer> generateMove(Integer i, Integer j) {
        return new Pair<>(getCurrentPosition().getFirst() + i,
                getCurrentPosition().getSecond() + j);
    }

    /**
     * Function that move the piece temporary to destination and verify if king is checked.
     * @param destination - pair of x, y coordinates which represent the place where we want
     *                    to move the piece.
     * @return - true(if the destination is a valid move), false(otherwise).
     */
    public boolean isKingCheckedAfterTempMove(Pair<Integer, Integer> destination) {
        ChessBoard chessBoard = ChessBoard.getInstance();
        boolean isNotValid;
        Piece saveStart = this;
        Piece saveDest = chessBoard.getPiece(destination);
        Pair<Integer, Integer> saveStartPos = saveStart.currentPosition;


        // temporary move
        chessBoard.addPiece(destination, saveStart);
        chessBoard.removePiece(saveStart.getCurrentPosition());
        saveStart.setCurrentPosition(destination);

        // check for King check
        if (saveStart.getColor().equalsIgnoreCase("WHITE")) {
            isNotValid = chessBoard.getWhiteKing().isChecked();
        } else {
            isNotValid = chessBoard.getBlackKing().isChecked();
        }

        // revert changes
        chessBoard.addPiece(destination, saveDest);
        chessBoard.addPiece(saveStartPos, saveStart);
        saveStart.setCurrentPosition(saveStartPos);
        return isNotValid;
    }

    public void moveTo(Pair<Integer, Integer> destination) {
        ChessBoard chessboard = ChessBoard.getInstance();
        chessboard.removePiece(this.currentPosition);
        chessboard.addPiece(destination, this);
        this.currentPosition = destination;
    }

    public Pair<Integer, Integer> getCurrentPosition() {
        return currentPosition;
    }

    public String getColor() {
        return color;
    }

    public void setCurrentPosition(Pair<Integer, Integer> currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return Objects.equals(currentPosition, piece.currentPosition) && Objects.equals(color, piece.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPosition, color);
    }
}
