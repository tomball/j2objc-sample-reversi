
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
 * The class Score maintains the score for two players. The constants
 * BLACK and WHITE are used to access the scores for each player. The constant
 * NOBODY can be used in any context where a value other than WHITE and BLACK
 * is meaningful. The function GetOpponent() transforms WHITE to BLACK and
 * vice versa. The other funcions are self explanatory.
 */

public class Score
{
  private int m_score[];

  public static final int NOBODY = 0;
  public static final int WHITE  = 1;
  public static final int BLACK  = 2;

  public Score() { m_score = new int[2];  m_score[0] = 0; m_score[1] = 0; }


  public Score(int whitescore, int blackscore)
  {
    m_score = new int[2];
    m_score[WHITE-1] = whitescore;
    m_score[BLACK-1] = blackscore;
  }


  public static int GetOpponent(int player) { return player % 2 + 1; }


  public void ScoreCopy(Score s)
  {
    m_score[0] = s.m_score[0];
    m_score[1] = s.m_score[1];
  }


  public int GetScore(int player) { return m_score[player-1]; }


  public void SetScore(int player, int score) { m_score[player-1] = score; }


  public final void ScoreAdd(int player, int amount)
  {
    m_score[player-1] += amount;
  }


  public final void ScoreSubtract(int player, int amount)
  {
    m_score[player-1] -= amount;
  }
}
