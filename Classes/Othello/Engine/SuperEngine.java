
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;

import java.util.Random;


/**
 * The class SuperEngine is a super class for engines that produce moves.
 * It implements functionality that move engines have in common, which is
 * useful if you want to use several different engines in the same program
 * (for instance when you are test playing different strategies against each
 * other).
 * <P>
 * SuperEngine implements:
 * <P>
 * Random number handling (engines should not play exactly the same games
 * repeatedly).
 * <P>
 * Setting playing strength level.
 * <P>
 * Functionality for telling the engine to interrupt calculation.
 */

public abstract class SuperEngine
{
  /**
   * The programs current strenght level.
   * Is set and read by SetStrength() and GetStrength().
   */

  protected int m_strength;


  /**
   * Could (and should in a good engine) be used to prevent the engine from
   * repeating itself, always playing the same moves in the same positions.
   * If this is not done, winning once would make it possible to play the
   * same moves and win every time against the program.
   */

  private Random m_random;


  /**
   *  Is set and read by SetInterrupt() and GetInterrupt().
   */

  private boolean m_interrupt;


  /**
   * Creates an engine playing at level st. All integers greater than 0
   * should be possible levels. There need not be any actual difference in
   * playing strength between levels, but if there is, higher number should
   * mean greater playing strength.
   */

  public SuperEngine(int st)
  {
    m_strength = st;
    m_random = new Random();
    m_interrupt = false;
  }


  /**
   * The same as SuperEngine(int st), but uses sd as the seed for the random
   * generator. This means that the engine always behaves in exactly the same
   * way (practical when testing).
   */

  public SuperEngine(int st, int sd)
  {
    m_strength = st;
    m_random = new Random(sd);
    m_interrupt = false;
  }


  /**
   * Tells the engine to interrupt calculation as
   * soon as possible and return null from ComputeMove(). This function could
   * be called when ComputeMove() is executing. 
   */

  public synchronized final void SetInterrupt(boolean intr)
  {
    m_interrupt =intr;
  }


  /**
   * Returns true when SetInterrupt() has been called. Should be called
   * with short intervals from ComputeMove().
   */

  public synchronized final boolean GetInterrupt() { return m_interrupt; }


  /**
   * Sets playing strength level.
   */

  public void SetStrength(int st) { m_strength = st; }


  /**
   *  public int GetStrength()
   *     Gets playing strength level.
   */

  public int GetStrength() { return m_strength; }


  /**
   * Changes the random seed.
   */

  public void SetSeed(int sd) { m_random = new Random(sd); }


  /**
   * Creates a random number.
   */

  protected int GetRandom() { return m_random.nextInt(); }


  /**
   * This function should produce a move. If SetInterrupt() is called
   * during its execution it should return null as soon as possible.
   */

  public abstract Move ComputeMove(Game g);
}
