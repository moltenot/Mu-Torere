package MuTorere;

/**Our implementation of the Player abstract class.
  * COSC326 Etude 1
  * Daniel Blaikie
  * Blake MacDade
  * Sam Fern
  * Theo Molteno
  */
  class CustomPlayer extends Player{
    
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
  }