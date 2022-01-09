import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class MoveManager {
    String botSide, movingNowSide;
    boolean paused;
    private static MoveManager instance = null;

    private MoveManager() {
        botSide = "BLACK";
        movingNowSide = "WHITE";
        paused = false;
    }

    public static MoveManager getInstance() {
        if (instance == null)
            instance = new MoveManager();
        return instance;
    }

    /**
     * Function that changes the current moving side from WHITE to BLACK and from BLACK to WHITE
     */
    public void nextMoveSide() {
        if (movingNowSide.equalsIgnoreCase("WHITE"))
            movingNowSide = "BLACK";
        else if (movingNowSide.equalsIgnoreCase("BLACK"))
            movingNowSide = "WHITE";
    }

    /**
     * Function that forces the bot to stop thinking
     */
    public void force() {
        paused = true;
    }

    /**
     * Function that forces the bot to start thinking with the current side
     */
    public void go() {
        paused = false;
        botSide = movingNowSide;

        sendMove();
    }

    /**
     * Function that receives a move, updates the board and sends a response if the bot is not paused
     *
     * @param move - move received to be updated in the board
     */
    public void receiveMove(String move) {
        updateBoard(move);
        nextMoveSide();
        if (!paused)
            sendMove();
    }

    /**
     * Generates the best move, updates the board and then sends the move
     */
    public void sendMove() {
        String nextMove = piecesMove();

        if (nextMove == null)
            resign();
        else {
            updateBoard(nextMove);
            CommandManager.getInstance().send("move " + nextMove);
            nextMoveSide();
        }
    }

    /**
     * Function that checks if a piece has a take move and prioritize it
     *
     * @param pieces - LinkedList of pieces
     * @param moves - Linked list of possible moves of piece parameter
     * @param piece - the piece that we will add in pieces list
     * @return - true ( if the piece has a take move) or false (otherwise)
     */
    private boolean prioritizePiece(LinkedList<Piece> pieces, LinkedList<Pair<Integer, Integer>> moves, Piece piece) {
        ChessBoard chessBoard = ChessBoard.getInstance();

        if (piece.getClass().getName().equalsIgnoreCase("PAWN")){
            for (Pair<Integer, Integer> move : moves)
                if (!piece.getCurrentPosition().getSecond().equals(move.getSecond())) {
                    pieces.addFirst(piece);
                    return true;
                }
        } else if (piece.getClass().getName().equalsIgnoreCase("KING")) {
            for (Pair<Integer, Integer> move : moves) {
                // If castling exists, it should be prioritised
                if (Math.abs(piece.getCurrentPosition().getSecond() - move.getSecond()) == 2 ||
                        chessBoard.getPiece(move) != null) {
                    pieces.addFirst(piece);
                    return true;
                }
            }
        } else {
            for (Pair<Integer, Integer> move : moves)
                if (chessBoard.getPiece(move) != null) {
                    pieces.addFirst(piece);
                    return true;
                }

        }
        pieces.addLast(piece);

        return false;
    }

    /**
     * Function that computes piece move.
     *
     * @return - string representing the move.
     */
    public String piecesMove() {
        Piece[][] board = ChessBoard.getInstance().getBoard();
        ChessBoard chessBoard = ChessBoard.getInstance();
        LinkedList<Piece> pieces = new LinkedList<>();
        boolean isTakePieceMove = false;

        for (int i = 0; i < board.length && !isTakePieceMove; i++) {
            for (int j = 0; j < board.length && !isTakePieceMove; j++) {
                if (board[j][i] != null && board[j][i].getColor().equalsIgnoreCase(botSide) &&
                        !board[j][i].getClass().getName().equalsIgnoreCase("KING")) {
                    LinkedList<Pair<Integer, Integer>> possibleMoves = board[j][i].getPossibleMoves();
                    boolean check = false;
                    if (possibleMoves != null && possibleMoves.size() != 0)
                        check = prioritizePiece(pieces, possibleMoves, board[j][i]);
                    if (check)
                        isTakePieceMove = true;
                }
            }
        }

        // Prioritise KING last
        {
            LinkedList<Pair<Integer, Integer>> possibleMoves = chessBoard.getKing(botSide).getPossibleMoves();
            if (possibleMoves != null && possibleMoves.size() != 0)
                if (prioritizePiece(pieces, possibleMoves, chessBoard.getKing(botSide)))
                    isTakePieceMove = true;
        }

        if (pieces.size() != 0) {
            Pair<Integer, Integer> startPos;
            Pair<Integer, Integer> endPos;

            // if exist a take move on the board
            if (isTakePieceMove) {
                startPos = pieces.get(0).getCurrentPosition();
                endPos = pieces.get(0).getPossibleMoves().get(0);
            } else {

                // else we take a random piece and a random move.
                Random rand = new Random();
                int randomPiece = rand.nextInt(pieces.size());
                LinkedList<Pair<Integer, Integer>> possibleMoves = pieces.get(randomPiece).getPossibleMoves();
                int randomMove = rand.nextInt(possibleMoves.size());

                startPos = pieces.get(randomPiece).getCurrentPosition();
                endPos = possibleMoves.get(randomMove);
            }
            String move = IntToStringCoordinate(startPos) + IntToStringCoordinate(endPos);

            if (chessBoard.getPiece(startPos).getClass().getName().equalsIgnoreCase("PAWN")) {
                if (botSide.equalsIgnoreCase("BLACK") && endPos.getFirst() == 7)
                    move += "q";
                else if (botSide.equalsIgnoreCase("WHITE") && endPos.getFirst() == 0)
                    move += "q";
            }
            return move;
        }

        return null;
    }


    /**
     * Function that resets the game
     */
    public void newGame() {
        botSide = "BLACK";
        movingNowSide = "WHITE";
        paused = false;
        ChessBoard.getInstance().resetBoard();
    }

    /**
     * Function that updates the board and promotes a pawn if needed
     *
     * @param move - update the board with the given move
     */
    private void updateBoard(String move) {
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> coordinates = getCoordinates(move);
        ChessBoard chessBoard = ChessBoard.getInstance();

        // Move piece on ChessBoard
        Piece piece = chessBoard.getPiece(coordinates.getFirst());
        piece.moveTo(coordinates.getSecond());

        // Handle rook update for castling
        if (piece.getClass().getName().equalsIgnoreCase("KING") &&
                Math.abs(coordinates.getSecond().getSecond() - coordinates.getFirst().getSecond()) == 2) {
            Piece rook;

            if (coordinates.getSecond().equals(new Pair<>(0, 6))) {
                rook = chessBoard.getPiece(new Pair<>(0, 7));
                rook.moveTo(new Pair<>(0, 5));
            } else if (coordinates.getSecond().equals(new Pair<>(0, 2))) {
                rook = chessBoard.getPiece(new Pair<>(0, 0));
                rook.moveTo(new Pair<>(0, 3));
            } else if (coordinates.getSecond().equals(new Pair<>(7, 6))) {
                rook = chessBoard.getPiece(new Pair<>(7, 7));
                rook.moveTo(new Pair<>(7, 5));
            } else if (coordinates.getSecond().equals(new Pair<>(7, 2))) {
                rook = chessBoard.getPiece(new Pair<>(7, 0));
                rook.moveTo(new Pair<>(7, 3));
            }
        }

        // Log the move into move history
        if (movingNowSide.equalsIgnoreCase("BLACK"))
            chessBoard.addBlackMove(coordinates.getFirst(), coordinates.getSecond());
        else
            chessBoard.addWhiteMove(coordinates.getFirst(), coordinates.getSecond());

        // Handle pawn promotion
        if (move.length() == 5) {
            switch (move.getBytes()[4]) {
                case 'q':
                    ((Pawn) piece).promote("QUEEN");
                    break;
                case 'r':
                    ((Pawn) piece).promote("ROOK");
                    break;
                case 'k':
                    ((Pawn) piece).promote("KNIGHT");
                    break;
                case 'b':
                    ((Pawn) piece).promote("BISHOP");
                    break;
            }
        }
    }

    /**
     * Function that converts a String move into cartesian coordinates
     *
     * @param move - move in String format (eg. "a1a2", "h3g7")
     * @return A source-destination pair of cartesian coordinates pairs (eg. "b8c6" -> Pair<Pair<0, 1>, Pair<2, 2>>)
     */
    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getCoordinates(String move) {
        if (move.matches("^[a-h][1-8][a-h][1-8].?")) {
            return new Pair<>(StringToIntCoordinate(move.substring(0, 2)), StringToIntCoordinate(move.substring(2, 4)));
        }

        System.out.println("Invalid move!");
        return null;
    }

    /**
     * Function that converts a coordinate into a pair of cartesian coordinates
     *
     * @param coordinate - coordinate in String format (eg. "g2", "c3")
     * @return A pair of cartesian coordinates (eg. "f5" -> Pair<3, 5>)
     */
    public Pair<Integer, Integer> StringToIntCoordinate(String coordinate) {
        int first = 7 - ((int) (coordinate.getBytes()[1] - '1'));
        int second = ((int) (coordinate.getBytes()[0] - 'a'));
        return new Pair<>(first, second);
    }

    /**
     * Function that converts a pair of cartesian coordinates into a coordinate
     *
     * @param coordinate - coordinate in Pair format (eg. "Pair<0, 7>", "Pair<4, 2>")
     * @return A pair of cartesian coordinates (eg. "Pair<3, 5>" -> f5)
     */
    public String IntToStringCoordinate(Pair<Integer, Integer> coordinate) {
        int first = coordinate.getFirst();
        int second = coordinate.getSecond();
        StringBuilder move = new StringBuilder();

        move.append((char) ('a' + second));
        move.append((char) ('1' + (7 - first)));

        return move.toString();
    }

    /**
     * Function that computes the mirror of a move
     *
     * @param move - move in String format (eg. "e1e3", "a5e1")
     * @return Mirror of the provided move (eg. "a7a5" -> "h2h4")
     */
    public String mirrorMove(String move) {
        if (move.matches("^[a-h][1-8][a-h][1-8].?")) {
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> coordinates = getCoordinates(move);
            Pair<Integer, Integer> start = coordinates.getFirst();
            Pair<Integer, Integer> end = coordinates.getSecond();

            start.setFirst(7 - start.getFirst());
            start.setSecond(7 - start.getSecond());
            end.setFirst(7 - end.getFirst());
            end.setSecond(7 - end.getSecond());

            return IntToStringCoordinate(start) + IntToStringCoordinate(end);
        }

        System.out.println("Invalid move!");
        return null;
    }

    /**
     * Function that sends resign
     */
    public void resign() {
        CommandManager.getInstance().send("resign");
    }
}

class Pair<T, K> {
    private T first;
    private K second;

    Pair() {
        this.first = null;
        this.second = null;
    }

    Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(K second) {
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}