
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
 * Called when CommandInterface has finished a computation.
 */

public interface CommandInterfaceListener
{
  public void ComputationFinished(Move m);
}
