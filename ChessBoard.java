import java.util.ArrayList;

public class ChessBoard {
    private static ChessBoard boardInstance = null;
    private Piece [][]board;

    /**
     * for the passant move for Pawn.
     * whiteMoves keeps track of the moves made by WHITE.
     * blackMoves keeps track of the moves made by BLACK.
     */
    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> whiteMoves;
    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> blackMoves;

    /**
     * Reference of the kings.
     */
    private King blackKing;
    private King whiteKing;

    public King getBlackKing() {
        return blackKing;
    }

    public King getWhiteKing() {
        return whiteKing;
    }

    /**
     * Function that returns the king of the specified color
     * @param color - color of the king, could be either BLACK or WHITE
     * @return - reference to the BLACK/WHITE king
     */
    public King getKing(String color) {
        if (color.equalsIgnoreCase("WHITE"))
            return whiteKing;
        else
            return blackKing;
    }

    private ChessBoard() {
        resetBoard();
    }


    public static ChessBoard getInstance()
    {
        if (boardInstance == null)
            boardInstance = new ChessBoard();

        return boardInstance;
    }

    /**
     * resets the board to its initial state.
     */
    public void resetBoard() {
        board = new Piece[8][8];
        whiteMoves = new ArrayList<>();
        blackMoves = new ArrayList<>();

        //PAWN
        for(int i = 0; i < board.length; i++) {
            board[1][i] = new Pawn(new Pair<>(1, i), "BLACK");
            board[6][i] = new Pawn(new Pair<>(6, i), "WHITE");
        }

        //ROOK
        board[0][0] = new Rook(new Pair<>(0, 0), "BLACK");
        board[0][7] = new Rook(new Pair<>(0, 7), "BLACK");
        board[7][0] = new Rook(new Pair<>(7, 0), "WHITE");
        board[7][7] = new Rook(new Pair<>(7, 7), "WHITE");

        //KNIGHT
        board[0][1] = new Knight(new Pair<>(0, 1), "BLACK");
        board[0][6] = new Knight(new Pair<>(0, 6), "BLACK");
        board[7][1] = new Knight(new Pair<>(7, 1), "WHITE");
        board[7][6] = new Knight(new Pair<>(7, 6), "WHITE");

        //BISHOP
        board[0][2] = new Bishop(new Pair<>(0, 2), "BLACK");
        board[0][5] = new Bishop(new Pair<>(0, 5), "BLACK");
        board[7][2] = new Bishop(new Pair<>(7, 2), "WHITE");
        board[7][5] = new Bishop(new Pair<>(7, 5), "WHITE");

        //QUEEN
        board[0][3] = new Queen(new Pair<>(0, 3), "BLACK");
        board[7][3] = new Queen(new Pair<>(7, 3), "WHITE");

        //KING
        board[0][4] = new King(new Pair<>(0, 4), "BLACK");
        board[7][4] = new King(new Pair<>(7, 4), "WHITE");

        //Reference to the kings
        blackKing = (King)board[0][4];
        whiteKing = (King)board[7][4];

    }

    /**
     * @return the board - a 8 by 8 matrix.
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Function that adds a piece at a specified position.
     * @param position - pair of Integer (x, y) coordinates.
     * @param piece - piece that we want to add.
     */
    public void addPiece(Pair<Integer, Integer> position, Piece piece) {
        board[position.getFirst()][position.getSecond()] = piece;
    }

    /**
     * Function that removes a piece from a specified position.
     * @param position - pair of Integer (x, y) coordinates.
     */
    public void removePiece(Pair<Integer, Integer> position) {
        board[position.getFirst()][position.getSecond()] = null;
    }

    /**
     * Function that returns a piece from a given position.
     * @param piecePosition - pair of Integer (x, y) coordinates.
     * @return - piece from the given position.
     */
    public Piece getPiece(Pair<Integer, Integer> piecePosition) {
        return board[piecePosition.getFirst()][piecePosition.getSecond()];
    }

    /**
     * @return - array of moves made by WHITE.
     */
    public ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getWhiteMoves() {
        return whiteMoves;
    }

    /**
     * Function that adds a move made by WHITE
     * @param currentPos - starting position
     * @param move - ending position
     */
    public void addWhiteMove(Pair<Integer, Integer> currentPos, Pair<Integer, Integer> move) {
        whiteMoves.add(new Pair<>(currentPos, move));
    }

    /**
     * @return - array of moves made by BLACK.
     */
    public ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getBlackMoves() {
        return blackMoves;
    }

    /**
     * Function that adds a move made by BLACK
     * @param currentPos - starting position
     * @param move - ending position
     */
    public void addBlackMove(Pair<Integer, Integer> currentPos, Pair<Integer, Integer> move) {
        blackMoves.add(new Pair<>(currentPos, move));
    }
}
