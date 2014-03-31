
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
* The class Position is used to represent an Othello position as white and
* black pieces and empty squares (see class Score) on an 8x8 Othello board.
* It also stores information on the move that lead to the position.
*/

public class Position
{
  private int m_board[][];
  private Move m_last_move;
  private Score m_score;


  /**
   * Creates an initial position.
   */

  public Position()
  {
    m_board = new int[10][10];
    m_score = new Score(2, 2);
    m_last_move = null;

    for (int i=0; i<10; i++)
    for (int j=0; j<10; j++)
      m_board[i][j] = Score.NOBODY;

    m_board[4][4] = Score.WHITE;
    m_board[5][5] = Score.WHITE;
    m_board[5][4] = Score.BLACK;
    m_board[4][5] = Score.BLACK;
  }


  /**
   * Creates the position that arise when the Move m is applied to the
   * Position p (m must be a legal move).
   */

  public Position(Position p, Move m)
  {
    m_board = new int[10][10];
    m_score = new Score();

    for (int i=0; i<10; i++)
      System.arraycopy(p.m_board[i], 0, m_board[i], 0, 10);

    m_score.ScoreCopy(p.m_score);

    int player = m.GetPlayer();
    int opponent = Score.GetOpponent(player);

    m_board[m.GetX()][m.GetY()] = player;
    m_score.ScoreAdd(player, 1);

    for (int xinc=-1; xinc<=1; xinc++)
    for (int yinc=-1; yinc<=1; yinc++)
    if (xinc != 0 || yinc != 0)
    {
      int x, y;

      for (x = m.GetX()+xinc, y = m.GetY()+yinc; m_board[x][y] == opponent;
	   x += xinc, y += yinc)
	;

      if (m_board[x][y] == player)
	for (x -= xinc, y -= yinc; x != m.GetX() || y != m.GetY();
	     x -= xinc, y -= yinc)
	{
	  m_board[x][y] = player;
	  m_score.ScoreAdd(player, 1);
	  m_score.ScoreSubtract(opponent, 1);
	}
    }

    m_last_move = new Move(m);
  }


  /**
   * Returns the color of the piece at the square (x, y) (Score.WHITE,
   * Score.BLACK or Score.NOBODY).
   */

  public int GetSquare(int x, int y) { return m_board[x][y]; }


  /**
   * Returns the the current number of pieces of color player.
   */

  public int GetScore(int player) { return m_score.GetScore(player); }


  /**
   * Returns the last move.
   */

  public Move GetLastMove() { return m_last_move; }


  /**
   * Checks if a move is legal.
   */

  public boolean MoveIsLegal(Move m)
  {
    if (m_board[m.GetX()][m.GetY()] != Score.NOBODY) return false;

    int player = m.GetPlayer();
    int opponent = Score.GetOpponent(player);

    for (int xinc=-1; xinc<=1; xinc++)
    for (int yinc=-1; yinc<=1; yinc++)
    if (xinc != 0 || yinc != 0)
    {
      int x, y;

      for (x = m.GetX()+xinc, y = m.GetY()+yinc; m_board[x][y] == opponent;
	   x += xinc, y += yinc)
	;

      if (m_board[x][y] == player &&
	  (x - xinc != m.GetX() || y - yinc != m.GetY()))
        return true;
    }

    return false;
  }


  /**
   * Checks if there is a legal move for player.
   */

  public boolean MoveIsPossible(int player)
  {
    for (int i=1; i<9; i++)
    for (int j=1; j<9; j++)
      if (MoveIsLegal(new Move(i, j, player))) return true;

    return false;
  }


  /**
   * Checks if there are any legal moves at all.
   */

  public boolean MoveIsAtAllPossible()
  {
    return MoveIsPossible(Score.WHITE) || MoveIsPossible(Score.BLACK);
  }
};
