
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
 * The class Move is used to represent an Othello move with a player value
 * (see class Score) and a pair of coordinates on an 8x8 Othello board.
 * @see Score
 */

public class Move
{
  private int m_x, m_y;
  private int m_player;

  public Move(Move m) { m_x = m.m_x; m_y = m.m_y; m_player = m.m_player; }
  public Move(int x, int y, int player) { m_x = x; m_y = y; m_player =player; }

  public int GetX() { return m_x; }
  public int GetY() { return m_y; }
  public int GetPlayer() { return m_player; }
}
