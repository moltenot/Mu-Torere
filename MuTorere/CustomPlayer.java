package MuTorere;
import java.util.ArrayList;
import java.util.HashMap;

/**Our implementation of the Player abstract class.
  * COSC326 Etude 1
  * Daniel Blaikie
  * Blake MacDade
  * Sam Fern
  * Theo Molteno
  */
  class CustomPlayer extends Player{
    
    //ARRAY of board pieces, array index 8 is always the centre piece.
    private Board.Piece[] boardArray;
    private int numKawai;
    private ArrayList<Integer> previousTransformations;

    private HashMap<Board.Piece[], Integer> moveMap = new HashMap<>();


    /**Constructor - creates a new custom player in the same way the original Player class does.*/
    public CustomPlayer(BoardReader boardReader, Board.Piece playerID){
      super(boardReader, playerID);
      boardArray  = new Board.Piece[9];
      numKawai = boardArray.length - 1;
      previousTransformations = new ArrayList<Integer>();
      loadHashMap();
    }
    
    
    /*
     Need to implement this.
     Return the index of the piece that you want to move.
     If the result is not a valid move, you lose.
     If there are no valid moves, just return something - don't leave us hanging!
     */
    public int getMove(){
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

      loadBoardArray();
      normaliseBoard();

      int move = getNormalisedMove();
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

    private int getNormalisedMove(){
      return 0;
    }

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
        }
        //Rotate by longest streak length - 1
        rotateBoardArray(length - 1);
      }else{
        rotateBoardArray(blankLocation);
        //If the board is oriented towards the wrong side, flip it.
        if(getCentreOfGravity() < 0){
          flipBoardArray();
        }
      }
    }

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

    
    

    
    private void rotateBoardArray(int numPlaces){
      if(numPlaces == 0){return;}
      Board.Piece[] boardArrayClone = boardArray.clone();

      for(int i = 0; i < numKawai; i++){
        boardArray[i] = boardArrayClone[(i + numPlaces) % numKawai];
      }
      //Add "rotate n places" transformation
      previousTransformations.add(Integer.valueOf(numPlaces));
    }

    private void flipBoardArray(){
      for(int i = 1; i < numKawai / 2; i++){
        swapArrayPositions(i, numKawai - i);
      }
      //Add 0 for a "flip" transformation
      previousTransformations.add(Integer.valueOf(0));
    }

    private void swapArrayPositions(int i, int j){
      Board.Piece temp = boardArray[i];
      boardArray[i] = boardArray[j];
      boardArray[j] = temp;
    }

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


  private void loadHashMap(){
    Board.Piece friendly, enemy, blank;
    friendly = playerID;
    blank = Board.Piece.BLANK;
    if(friendly == Board.Piece.ONE){
        enemy = Board.Piece.TWO;
    } else{enemy = Board.Piece.ONE;}

    //0 through 7 is clockwise around circle, 8 is centre
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,enemy,friendly,enemy,enemy,friendly}, 0);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,enemy,enemy,friendly,enemy,friendly}, 0);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,friendly,enemy,enemy,enemy,friendly}, 0);
    //Winning move: move 1 -> 0
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,friendly,enemy,enemy,enemy,enemy,friendly}, 0);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,enemy,friendly,friendly,enemy,enemy,friendly,enemy}, 0);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,friendly,enemy,enemy,enemy,friendly,enemy}, 0);
    //Non-Losing Move:
    moveMap.put(new Board.Piece[]{blank, friendly,friendly,enemy,friendly,enemy,enemy,friendly,enemy}, 0);
  }
}