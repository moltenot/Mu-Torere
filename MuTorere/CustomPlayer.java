package MuTorere;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**Our implementation of the Player abstract class.
  * COSC326 Etude 1
  * Daniel Blaikie    7708554
  * Blake MacDade     8548310
  * Sam Fern          8555433
  * Theo Molteno      7615853
  */
  class CustomPlayer extends Player{
    
    //ARRAY of board pieces, array index 8 is always the centre piece.
    private Board.Piece[] boardArray;
    private int numKawai;
    private ArrayList<Integer> previousTransformations;
    private Random rng;
    static boolean verbose = false; // set to true if we want to print abstracted board and if we moved randomly

    private HashMap<Board.Piece[], Integer> moveMap = new HashMap<>();


    /**Constructor - creates a new custom player in the same way the original Player class does.*/
    public CustomPlayer(BoardReader boardReader, Board.Piece playerID){
      super(boardReader, playerID);
      boardArray  = new Board.Piece[9];
      numKawai = boardArray.length - 1;
      previousTransformations = new ArrayList<Integer>();
      loadHashMap();
      rng = new Random();
    }

    /**
     * Returns the symbol to print based on the type of Board.Piece required
     * 
     * @param board the board containing the symbol
     * @param index the index of the symbol in the board we want to print
     * @return the correct symbol
     */
    public char getSymbol(Board.Piece[] board, int index) {
      if (board[index] == Board.Piece.ONE) {
        return '1';
      } else if (boardArray[index] == Board.Piece.TWO) {
        return '2';
      } else {
        return '_';
      }
    }

    /**
     * Prints a Board.Piece array nicely
     * 
     * @param board the board to print
     */
    public void printBoard(Board.Piece[] board) {
      String row1 = "  " + getSymbol(board, 7) + "   " + getSymbol(board, 0); 
      String row2 = "" + getSymbol(board, 6) + "       " + getSymbol(board, 1);
      String row3 = "    " + getSymbol(board, 8);
      String row4 = "" + getSymbol(board, 5) + "       " + getSymbol(board, 2);
      String row5 = "  " + getSymbol(board, 4) + "   " + getSymbol(board, 3); 
      System.out.println(row1);
      System.out.println(row2);
      System.out.println(row3);
      System.out.println(row4);
      System.out.println(row5);
    }
    
    /**
     * Returns a move based on the current board. We do this with the following steps
     * 1. normalise the board so mirroring and rotation are not considered
     * 2. see if we know what to do with that board position by checking our LUT of 
     *    board position -> move
     * 3. if we have it we return that move (abstracted back to the real board)
     * 4. if we don't we move randomly
     */
    public int getMove(){
      loadBoardArray();
      normaliseBoard();

      int move = testMap(boardArray);
      if (move==-1) {
        if(verbose) {
          System.out.println("("+playerID+") moving randomly");
        }
        move = getRandomMove();
      } else {
        if (verbose) {
          System.out.println("("+playerID+") moving deliberatly");
        }
        move = abstractMove(move);
      }
      System.out.println(super.boardReader.board.getClass());

      if (verbose){
        System.out.println("normalised board ---");
        printBoard(boardArray);
        System.out.println("---------------- ---");
      }

      return move;
    }

    /**
     * Finds all of the valid moves given a senario and returns on of them
     * 
     * @return integer move 
     */
    int getRandomMove() {
      ArrayList<Integer> validMoves = new ArrayList<Integer>();
        for (int i = 0; i < 9; ++i) {
          for (int j = 0; j < 9; ++j) {
            if (isValid(i,j)) {
              validMoves.add(i);
              continue;
            }
          }
        }
        if (validMoves.isEmpty()) {
          return 0;
        } else if (validMoves.size() == 1){
          return validMoves.get(0);
        }
      return validMoves.get(rng.nextInt(validMoves.size()));
    }

    /**
     * Using the list of transforms created by normalising the board, transform
     * a move back to the original board position.
     * 
     * @param move what move we made on the normalised board
     * @return a move to return to the game
     */
    int abstractMove(int move) {
      //Ignore the following if we're moving the centre piece
      if(move != numKawai){
        /*Iterate back through the previous transformations
        and apply them in reverse to the move*/
        int transformation;
        for(int i = previousTransformations.size() - 1; i >= 0; i--){
          transformation = previousTransformations.get(i).intValue();
          if(transformation == 0){
            //Undo the flip
            move = numKawai - move;
          }else{
            //Undo the rotation
            move = (move + transformation) % numKawai;
          }
        }
      }
      return move;
    }

    /**
     * Check if out moveMap contains an entry for the given board, returns -1 if we can't find the move
     * and the move if we do have it.
     * 
     * @param board is the board to look up in the LUT
     * @return the move in the LUT, or -1 if the board wasn't present in out LUT
     */
    private int testMap(Board.Piece[] board) {
      for (Map.Entry<Board.Piece[], Integer> entry : moveMap.entrySet()) {
        Board.Piece[] key = entry.getKey();
        Integer move = entry.getValue();

        // test if board == key
        boolean isSame=true; // and set false when a piece is different
        for (int i =0; i<=numKawai; i++) {
          Board.Piece mapPiece = key[i]; // piece in the map
          Board.Piece boardPiece = board[i]; // piece in board
          if (mapPiece != boardPiece) {
            isSame=false;
            break;
          }
        }
        if (isSame) {
          return move;
        }
      }
      return -1;
    }

    /**
     * If the hole is on the edge, move it to index 0 and make sure our centre of gravity
     * is positive. If the hole is in the middle, move the longest streak to 0 and make 
     * sure the COG is positive. We record the transformations in 
     * this.previousTransformations.
     */
    private void normaliseBoard(){
      //Figure out how much we need to rotate
      int blankLocation = boardReader.board.blankLocation;
      if(blankLocation == 8){
        //Get longest streak in the right spot
        int[] arr = longestStreakPosLength();
        int pos = arr[0], length = arr[1];

        rotateBoardArray(pos);
        //Make COG positive
        if(getCentreOfGravity() < 0){
          flipBoardArray();
          //Rotate by longest streak length - 1
          rotateBoardArray(length - 1);
        }
      }else{
        rotateBoardArray(blankLocation);
        //If the board is oriented towards the wrong side, flip it.
        if(getCentreOfGravity() < 0){
          flipBoardArray();
        }
      }
    }

    /**
     * Helper method for normaliseing the board
     * 
     * @return int array of [longest streak position, longest streak]
     */
    private int[] longestStreakPosLength(){
      int firstStreak = 0, longestStreak = 0, currentStreak = 0,
      longestStreakPos = -1, currentStreakStart = 0;
      for(int i = 0; i < numKawai; i++){
        if(boardArray[i] == playerID){
          //Continue current streak
          currentStreak++;
        }else{
          //End current streak
          if(longestStreak == 0){
            firstStreak = currentStreak;
          }
          if(currentStreak > longestStreak){
            longestStreak = currentStreak;
            longestStreakPos = currentStreakStart;
          }
          currentStreak = 0;
          //Start next streak
          currentStreakStart = i + 1;
        }
      }
      if(currentStreak > 0){
        //If we've still got a streak going on, go back to the start of the board.
        currentStreak += firstStreak;
      }
      if(currentStreak > longestStreak){
        return new int[] {currentStreakStart, currentStreak};
      }
      return new int[] {longestStreakPos, longestStreak};
    }

    /**Gets the current board's centre of gravity.*/
    private int getCentreOfGravity(){
      int cog = 0; 
      for(int i = 0; i < numKawai; i++){
        //Update the centre if the player is on the current piece
        if(boardArray[i] == playerID){
          cog += getCogWeight(i);
        }
      }
      return cog;
    }

    /**
     * Return the weight of a putahi given it's index in the board 
     * 
     * @param i the index of the putahi
     * @return the weight of that putahi
     */
    private int getCogWeight(int i){
      int multiplier;
      if(i < numKawai / 2){
        multiplier = 1;
      }else{
        multiplier = -1;
        i -= numKawai / 2;
      }
      int base;
      if(i == 0){
        base = 0;
      }else if(i == numKawai / 4){
        base = 3;
      }else{
        base = 2;
      }
      return base * multiplier;
    }

    /**
     * rotate this.boardArray by a certain number of places
     * 
     * @param numPlaces the number of places to rotate by
     */
    private void rotateBoardArray(int numPlaces){
      if(numPlaces == 0){return;}
      Board.Piece[] boardArrayClone = boardArray.clone();

      for(int i = 0; i < numKawai; i++){
        boardArray[i] = boardArrayClone[(i + numPlaces) % numKawai];
      }
      //Add "rotate n places" transformation
      previousTransformations.add(Integer.valueOf(numPlaces));
    }

    /**
     * Mirror this.boardArray along the axis throught index 0 and 4
     */
    private void flipBoardArray(){
      for(int i = 1; i < numKawai / 2; i++){
        swapArrayPositions(i, numKawai - i);
      }
      //Add 0 for a "flip" transformation
      previousTransformations.add(Integer.valueOf(0));
    }

    /**
     * Helper swap method for board indicies
     */
    private void swapArrayPositions(int i, int j){
      Board.Piece temp = boardArray[i];
      boardArray[i] = boardArray[j];
      boardArray[j] = temp;
    }

    /**
     * loads the board array from the boardReader
     */
    private void loadBoardArray(){
      previousTransformations.clear();

        if(boardReader != null){
          for(int i = 0; i < boardArray.length; i ++){
              boardArray[i] = boardReader.pieceAt(i); 
          }
        }
    }

    /* Copied from NaivePlayer.java */
    boolean isValid(int moveFrom, int moveTo) {	
		
      if (boardReader.pieceAt(moveTo) != Board.Piece.BLANK) {
        return false;
      }
      
      if (boardReader.pieceAt(moveFrom) != playerID) {
        return false;
      }
      if (moveTo == 8) {
        // Move to centre, check for valid neighbour
        int prev = moveFrom - 1;
        if (prev < 0) prev = 7;
        int next = moveFrom + 1;
        if (next > 7) next = 0;
        if (boardReader.pieceAt(prev) == playerID && boardReader.pieceAt(next) == playerID) {
          return false;
        }
      } else {
        // Either move from centre to kewai...
        if (moveFrom == 8) {
          return true;
        }
        // ... or from one kewai to next, make sure they are neighbours
        int prev = moveFrom - 1;
        if (prev < 0) prev = 7;
        int next = moveFrom + 1;
        if (next > 7) next = 0;
        if (boardReader.pieceAt(prev) != Board.Piece.BLANK &&
          boardReader.pieceAt(next) != Board.Piece.BLANK) {
          return false;
        }					
      }
      return true;
    }

  
    /**
     * Hard code board positions and where to move from given that board position.
     * This information is put into this.moveMap
     */
  private void loadHashMap(){
    Board.Piece friendly, enemy, blank;
    friendly = playerID;
    blank = Board.Piece.BLANK;
    if(friendly == Board.Piece.ONE){
        enemy = Board.Piece.TWO;
    } else{enemy = Board.Piece.ONE;}

    //0 through 7 is clockwise around circle, 8 is centre
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,enemy,friendly,enemy,enemy,friendly}, 1);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,enemy,enemy,friendly,enemy,friendly}, 1);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,friendly,enemy,enemy,enemy,friendly}, 1);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,friendly,enemy,enemy,enemy,enemy,friendly}, 1);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,enemy,friendly,friendly,enemy,enemy,friendly,enemy}, 1);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,friendly,enemy,enemy,enemy,friendly,enemy}, 1);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,friendly,enemy,enemy,friendly,enemy}, 1);

    moveMap.put(new Board.Piece[] {blank,friendly,friendly,enemy,enemy,friendly,enemy,friendly,enemy}, 1);
  }
}
