package MuTorere;

/**Our implementation of the Player abstract class.
  * COSC326 Etude 1
  * Daniel Blaikie
  * Blake MacDade
  * Sam Fern
  * Theo Molteno
  */
  class CustomPlayer extends Player{
    
    //ARRAY of board pieces, array index 8 is always the centre peice.
    private Board.Piece[] boardArray = new Board.Piece[9];


    /**Constructor - creates a new custom player in the same way the original Player class does.*/
    public CustomPlayer(BoardReader boardReader, Board.Piece playerID){
      super(boardReader, playerID);
    }
    
    
    /*
     Need to implement this.
     Return the index of the piece that you want to move.
     If the result is not a valid move, you lose.
     If there are no valid moves, just return something - don't leave us hanging!
     */
    public int getMove(){
        return 2147483647;
    }
    
    private void rotateBoardArray(int numPlaces){

      for(int i = 0; i < boardArray.length - 1; i++){
        
      }
    }

    private void loadBoardArray(){
        if(BoardReader != null){
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
  }