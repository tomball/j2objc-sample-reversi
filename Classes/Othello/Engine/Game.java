
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
 * The class Game represents a complete or incomplete Othello game. It uses
 * the classes Score and Move (and internally  Position).
 * You can make moves, take back one move at a time, reset to initial position
 * and get certain data on the current position.
 * @see Score
 * @see Move
 */

public class Game
{
  private Position m_positions[];
  private int m_movenumber;

  /**
   * Creates a game with the initial position.
   */

  public Game()
  {
    m_positions = new Position[61];
    m_positions[0] = new Position();
    m_movenumber = 0;
  }


  /**
   * Resets to the initial position.
   */

  public void Reset()
  {
    m_positions = new Position[61];
    m_positions[0] = new Position();
    m_movenumber = 0;
  }


  /**
   * Makes the move m. Returns false if the move is not legal or when called
   * with a move where the player is Score.NOBODY.
   */

  public boolean MakeMove(Move m)
  {
    if (m.GetPlayer() == Score.NOBODY) return false;
    if (GetWhoseTurn() != m.GetPlayer()) return false;
    if (! m_positions[m_movenumber].MoveIsLegal(m)) return false;

    m_positions[m_movenumber+1] = new Position(m_positions[m_movenumber], m);
    m_movenumber++;

    return true;
  }


  /**
   * Takes back a move. Returns true if not at the initial position.
   */

  public boolean TakeBackMove()
  {
    if (m_movenumber <= 0) return false;

    m_positions[m_movenumber--] = null;
  
    return true;
  }


  /**
   * Returns the piece at (x, y). Returns Score.NOBODY if the square is not
   * occupied.
   */

  public int GetSquare(int x, int y)
  {
    return m_positions[m_movenumber].GetSquare(x, y);
  }


  /**
   * Returns the score for player.
   */

  public int GetScore(int player)
  {
    return m_positions[m_movenumber].GetScore(player);
  }


  /**
   * Returns the last move. Returns null if at the initial position.
   */

  public Move GetLastMove() { return m_positions[m_movenumber].GetLastMove(); }


  /**
   * Returns the moves made in the game, null if parameter i is invalid.
   */

  public Move GetMove(int i)
  {
    if (i <=0 || i > m_movenumber) return null;

    return m_positions[i].GetLastMove();
  }


  /**
   * Checks if move m is legal.
   */

  public boolean MoveIsLegal(Move m)
  {
    return m_positions[m_movenumber].MoveIsLegal(m);
  }


  /**
   * Checks if there is a legal move for player.
   */

  public boolean MoveIsPossible(int player)
  {
    return m_positions[m_movenumber].MoveIsPossible(player);
  }


  /**
   * Checks if there are any legal moves at all.
   */

  public boolean MoveIsAtAllPossible()
  {
    return m_positions[m_movenumber].MoveIsAtAllPossible();
  }


  /**
   * Returns move number of last move.
   */

  public int GetMoveNumber() { return m_movenumber; }


  /**
   * Returns the player in turn to play (if there are no legal moves
   * Score.NOBODY is returned).
   */

  public int GetWhoseTurn()
  {
    if (m_movenumber <= 0) return Score.BLACK;

    int player = GetLastMove().GetPlayer();
    int opponent = Score.GetOpponent(player);

    if (MoveIsPossible(opponent)) return opponent;

    if (MoveIsPossible(player)) return player;

    return Score.NOBODY;
  }


  /**
   * Returns a vector of the squares that were changed by the last move.
   * The move that was actually played is at index 0. At the initial
   * position the length of the vector returned is zero. (Could be used
   * for faster updates of a graphical board).
   */

  public Move[] TurnedByLastMove()
  {
    if (m_movenumber <= 0) return new Move[0];

    Move lastmove = GetLastMove();
    Move m[] = new Move[28];
    int n = 0;

    m[n++] = new Move(lastmove);

    for (int i=1; i<9; i++)
    for (int j=1; j<9; j++)
      if ((i != lastmove.GetX() || j != lastmove.GetY()) &&
	  m_positions[m_movenumber].GetSquare(i, j) !=
	  m_positions[m_movenumber-1].GetSquare(i, j))
	m[n++] = new Move(i, j, lastmove.GetPlayer());

    Move retval[] = new Move[n];

    for (int i = 0; i < n; i++) retval[i] = new Move(m[i]);

    return retval;
  }


  /**
   * Tells if exactly the same moves were played in two games
   */

  public boolean Identical(Game g)
  {
    if (m_movenumber != g.m_movenumber) return false;

    for (int i=1; i<m_movenumber; i++)
      if (m_positions[i].GetLastMove().GetX() !=
	    g.m_positions[i].GetLastMove().GetX() ||
	  m_positions[i].GetLastMove().GetY() !=
	    g.m_positions[i].GetLastMove().GetY())
	return false;

    return true;
  }
};
