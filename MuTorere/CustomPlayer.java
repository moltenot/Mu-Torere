package MuTorere;

import java.util.ArrayList;

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

    /**Constructor - creates a new custom player in the same way the original Player class does.*/
    public CustomPlayer(BoardReader boardReader, Board.Piece playerID){
      super(boardReader, playerID);
      boardArray  = new Board.Piece[9];
      numKawai = boardArray.length - 1;
      previousTransformations = new ArrayList<Integer>();
    }
    
    
    /*
     Need to implement this.
     Return the index of the piece that you want to move.
     If the result is not a valid move, you lose.
     If there are no valid moves, just return something - don't leave us hanging!
     */
    public int getMove(){
      loadBoardArray();
      normaliseBoard();

      return 2147483647;
    }

    private void normaliseBoard(){
      //Figure out how much we need to rotate
      int blankLocation = boardReader.board.blankLocation;
      if(blankLocation == 8){
        //Get longest streak in the right spot
        //Make COG positive
        //Rotate by longest streak length - 1
      }else{
        rotateBoardArray(blankLocation);
        //If the board is oriented towards the wrong side, flip it.
        if(getCentreOfGravity() < 0){
          flipBoardArray();
        }
      }
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

  }