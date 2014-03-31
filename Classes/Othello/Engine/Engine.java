
// This code is freely distributable, but may neither be sold nor used or
// included in any product sold for profit without permission from
// Mats Luthman.

package Othello.Engine;


/**
 * Implements a
 * stack entry that is used by Engine to store pieces that are turned during
 * searching (see ComputeMove()).
 */

class SquareStackEntry
{
  public int m_x, m_y;

  public SquareStackEntry() { m_x = 0; m_y = 0; }
}


/**
 * Implements a
 * stack that is used by Engine to store pieces that are turned during
 * searching.
 */

class SquareStack
{
  private SquareStackEntry m_squarestack[];
  private int m_top;

  public SquareStack(int size)
  {
    m_squarestack = new SquareStackEntry[size];
    m_top = 0;

    for (int i=0; i<size; i++) m_squarestack[i] = new SquareStackEntry();
  }

  public final SquareStackEntry Pop() { return m_squarestack[--m_top]; }


  public final void Push(int x, int y)
  {
    if (m_squarestack.length <= m_top) grow(m_squarestack.length * 2);

    m_squarestack[m_top].m_x = x;
    m_squarestack[m_top++].m_y = y;
  }


  private final void grow(int size)
  {
    if (m_squarestack.length >= size) return;

    SquareStackEntry tmp_squarestack[];
    tmp_squarestack = new SquareStackEntry[size];

    int i=0;

    for (i=0; i<m_squarestack.length; i++)
      tmp_squarestack[i] = m_squarestack[i];

    m_squarestack = tmp_squarestack;

    for (; i<size; i++)
      m_squarestack[i] = new SquareStackEntry();
  }
}


/**
 * Used by Engine to store all possible moves
 * at the first level and the values that were calculated for them.
 * This makes it possible to select a random move among those with equal
 * or nearly equal value after the search is completed.
 */

class MoveAndValue
{
  public int m_x, m_y, m_value;

  public MoveAndValue(int x, int y, int value)
  {
    m_x = x; m_y = y; m_value = value;
  }
}


/**
* The class Engine produces moves from a Game object through calls to the
* function ComputeMove().
* <P>
* First of all: this is meant to be a simple example of a game playing
* program. Not everything is done in the most clever way, particularly not
* the way the moves are searched, but it is hopefully made in a way that makes
* it easy to understand. The function ComputeMove2() that does all the work
* is actually not much more than a hundred lines. Much could be done to
* make the search faster though, I'm perfectly aware of that. Feel free
* to experiment.
* <P>
* The method used to generate the moves is called minimax tree search with
* alpha-beta pruning to a fixed depth. In short this means that all possible
* moves a predefined number of moves ahead are either searched or refuted
* with a method called alpha-beta pruning. A more thorough explanation of
* this method could be found at the world wide web at
* http://yoda.cis.temple.edu:8080/UGAIWWW/lectures96/search/minimax/alpha-beta.html
* at the time this was written. Searching for "minimax" would also point
* you to information on this subject. It is probably possible to understand
* this method by reading the source code though, it is not that complicated.
* <P>
* At every leaf node at the search tree, the resulting position is evaluated.
* Two things are considered when evaluating a position: the number of pieces
* of each color and at which squares the pieces are located. Pieces at the
* corners are valuable and give a high value, and having pieces at squares
* next to a corner is not very good and they give a lower value. In the
* beginning of a game it is more important to have pieces on "good" squares,
* but towards the end the total number of pieces of each color is given a
* higher weight. Other things, like how many legal moves that can be made in a
* position, and the number of pieces that can never be turned would probably
* make the program stronger if they were considered in evaluating a position,
* but that would make things more complicated (this was meant to be very
* simple example) and would also slow down computation (considerably?).
*/

public class Engine extends SuperEngine
{
  private static final int LARGEINT = 99999;
  private static final int ILLEGAL_VALUE = 888888;
  private static final int BC_WEIGHT = 3;

  /**
   * Holds the current position during the
   * computation. It is initiated at the start of ComputeMove() and
   * every move that is made during the search is made on this board. It should
   * be noted that 1 to 8 is used for the actual board, but 0 and 9 can be
   * used too (they are always empty). This is practical when turning pieces
   * when moves are made on the board. Every piece that is put on the board
   * or turned is saved in the stack m_squarestack (see class SquareStack) so
   * every move can easily be reversed after the search in a node is completed.
   */

  private int m_board[][];

  /**
   * Holds board control values for each square
   * and is initiated by a call to the function private void SetupBcBoard()
   * from Engines constructor. It is used in evaluation of positions except
   * when the game tree is searched all the way to the end of the game.
   */

  private int m_bc_board[][];

  /**
   * Holds the number of pieces of each color during the search
   * (this is faster than counting at every leaf node).
   */

  private Score m_score;

  /**
   * Holds the sum of the board control values for each color during the search
   * (this is faster than counting at every leaf node).
   */

  private Score m_bc_score;

  /**
   * A stack that is used by Engine to store pieces that are turned during
   * searching.
   */

  private SquareStack m_squarestack;
  private int m_depth;
  private int m_coeff;
  private int m_nodes_searched;
  private boolean m_exhaustive;

  /**
   * Used to
   * speed up the tree search. This goes against the principle of keeping things
   * simple, but to understand the program you do not need to understand them
   * at all. They are there to make it possible to throw away moves where
   * the piece that is played is not adjacent to a piece of opposite color
   * at an early stage (because they could never be legal). It should be
   * pointed out that not all moves that pass this test are legal, there will
   * just be fewer moves that have to be tested in a more time consuming way.
   */

  private long m_coord_bit[][];

  /**
   * Used to
   * speed up the tree search. This goes against the principle of keeping things
   * simple, but to understand the program you do not need to understand them
   * at all. They are there to make it possible to throw away moves where
   * the piece that is played is not adjacent to a piece of opposite color
   * at an early stage (because they could never be legal). It should be
   * pointed out that not all moves that pass this test are legal, there will
   * just be fewer moves that have to be tested in a more time consuming way.
   */

  private long m_neighbor_bits[][];

  public Engine(int st, int sd) { super(st, sd); SetupBcBoard(); SetupBits(); }


  public Engine(int st) { super(st); SetupBcBoard(); SetupBits(); }


  public Engine() { super(5); SetupBcBoard(); SetupBits(); }


  /**
   * Calcuates the next move.
   * @param g An incomplete game from which the next move should be calculated.
   * @return The calculated move.
   */

  public Move ComputeMove(Game g)
  {
    m_exhaustive = false;

    int player = g.GetWhoseTurn();

    if (player == Score.NOBODY) return null;

    m_score = new Score(g.GetScore(Score.WHITE), g.GetScore(Score.BLACK));
    if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) == 4)
      return ComputeFirstMove(g);

    m_board = new int[10][10];
    m_squarestack = new SquareStack(200);
    m_depth = m_strength;
    if (m_depth == 0) m_depth = 1; // m_strength == 0 is very low strength

    if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth + 4 >= 64)
      m_depth =
        64 - m_score.GetScore(Score.WHITE) - m_score.GetScore(Score.BLACK);
    else if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth + 7 >= 64)
      m_depth += 3;
    else if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth + 9 >= 64)
      m_depth += 2;
    else if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth + 11 >= 64)
      m_depth++;

    if (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth >= 64) m_exhaustive = true;

    m_coeff =
      100 - (100*
        (m_score.GetScore(Score.WHITE) + m_score.GetScore(Score.BLACK) +
        m_depth - 4))/60;

    m_nodes_searched = 0;

    for (int x=0; x<10; x++)
    for (int y=0; y<10; y++)
      m_board[x][y] = Score.NOBODY;

    for (int x=1; x<9; x++)
    for (int y=1; y<9; y++)
      m_board[x][y] = g.GetSquare(x, y);

    m_bc_score = new Score(CalcBcScore(Score.WHITE), CalcBcScore(Score.BLACK));

    long playerbits = ComputeOccupiedBits(player);
    long opponentbits = ComputeOccupiedBits(Score.GetOpponent(player));

    int maxval = -LARGEINT;
    int max_x = 0;
    int max_y = 0;

  /**
   * Used by Engine to store all possible moves
   * at the first level and the values that were calculated for them.
   * This makes it possible to select a random move among those with equal
   * or nearly equal value after the search is completed.
   */

    MoveAndValue moves[] = new MoveAndValue[60];
    int number_of_moves = 0;
    int number_of_maxval = 0;

    SetInterrupt(false);

    // long starttime = System.currentTimeMillis();

    for (int x=1; x<9; x++)
    for (int y=1; y<9; y++)
    if (m_board[x][y] == Score.NOBODY &&
      (m_neighbor_bits[x][y] & opponentbits) != 0)
    {
      int val = ComputeMove2(x, y, player, 1, maxval, playerbits,opponentbits);

      if (val != ILLEGAL_VALUE)
      {
	moves[number_of_moves++] = new MoveAndValue(x, y, val);

	if (val > maxval)
	{
	  maxval = val;
	  max_x = x;
	  max_y = y;
	  number_of_maxval = 1;
	}
	else if (val == maxval) number_of_maxval++;
      }

      if (GetInterrupt()) break;
    }

    // long endtime = System.currentTimeMillis();

    if (number_of_maxval > 1)
    {
      int r = 0;

      // if m_strength == 0, select any move at random:

      if (m_strength != 0)
        r = GetRandom() % number_of_maxval + 1;
      else
	r = GetRandom() % number_of_moves + 1;

      int i;

      for (i=0; i < number_of_moves; i++)
	if ((m_strength == 0 || moves[i].m_value == maxval) && --r <= 0) break;

      max_x = moves[i].m_x;
      max_y = moves[i].m_y;
    }


/********
    System.out.print("Number of pos. with highest score: ");
    System.out.println(number_of_maxval);

    System.out.print("Nodes searched:     ");
    System.out.println(m_nodes_searched);

    System.out.print("Total time:         ");
    System.out.println((endtime - starttime) / 1000.0);

    System.out.print("Time per node (ms): ");
    System.out.println((float) (endtime-starttime) / (float) m_nodes_searched);

    System.out.print("Position value:     ");
    if (maxval != -LARGEINT) System.out.println(maxval);

    System.out.println("");
********/

    if (GetInterrupt()) return null;
    else if (maxval != -LARGEINT) return new Move(max_x, max_y, player);
    else return null;
  }


  private Move ComputeFirstMove(Game g)
  {
    int r;
    int player = g.GetWhoseTurn();

    r = GetRandom() % 4 + 1;

    if (player == Score.WHITE)
    {
      if (r == 1) return new Move(3, 5, player);
      else if (r == 2) return new Move(4, 6, player);
      else if (r == 3) return new Move(5, 3, player);
      else return new Move(6, 4, player);
    }
    else
    {
      if (r == 1) return new Move(3, 4, player);
      else if (r == 2) return new Move(5, 6, player);
      else if (r == 3) return new Move(4, 3, player);
      else return new Move(6, 5, player);
    }
  }


  private final int ComputeMove2(int xplay, int yplay, int player, int level,
    int cutoffval, long playerbits, long opponentbits)
  {
    int number_of_turned = 0;
    SquareStackEntry mse;
    int opponent = Score.GetOpponent(player);

    m_nodes_searched++;

    m_board[xplay][yplay] = player;
    playerbits |= m_coord_bit[xplay][yplay];
    m_score.ScoreAdd(player, 1);
    m_bc_score.ScoreAdd(player, m_bc_board[xplay][yplay]);

    ///////////////////
    // Turn all pieces:
    ///////////////////

    for (int xinc=-1; xinc<=1; xinc++)
    for (int yinc=-1; yinc<=1; yinc++)
    if (xinc != 0 || yinc != 0)
    {
      int x, y;

      for (x = xplay+xinc, y = yplay+yinc; m_board[x][y] == opponent;
           x += xinc, y += yinc)
        ;

      if (m_board[x][y] == player)
        for (x -= xinc, y -= yinc; x != xplay || y != yplay;
             x -= xinc, y -= yinc)
        {
          m_board[x][y] = player;
	  playerbits |= m_coord_bit[x][y];
	  opponentbits &= ~m_coord_bit[x][y];
          m_squarestack.Push(x, y);
          m_bc_score.ScoreAdd(player, m_bc_board[x][y]);
          m_bc_score.ScoreSubtract(opponent, m_bc_board[x][y]);
          number_of_turned++;
        }
    }

    int retval = -LARGEINT;

    if (number_of_turned > 0)
    {
      //////////////
      // Legal move:
      //////////////

      m_score.ScoreAdd(player, number_of_turned);
      m_score.ScoreSubtract(opponent, number_of_turned);

      if (level >= m_depth) retval = EvaluatePosition(player); // Terminal node
      else
      {
	int maxval = TryAllMoves(opponent, level, cutoffval, opponentbits,
          playerbits);

        if (maxval != -LARGEINT) retval = -maxval;
        else
	{
	  ///////////////////////////////////////////////////////////////
	  // No possible move for the opponent, it is players turn again:
	  ///////////////////////////////////////////////////////////////

	  retval= TryAllMoves(player, level, -LARGEINT, playerbits,
            opponentbits);

	  if (retval == -LARGEINT)
	  {
	    ///////////////////////////////////////////////
	    // No possible move for anybody => end of game:
	    ///////////////////////////////////////////////

	    int finalscore =
	      m_score.GetScore(player) - m_score.GetScore(opponent);

	    if (m_exhaustive) retval = finalscore;
	    else
	    {
	      // Take a sure win and avoid a sure loss (may not be optimal):

	      if (finalscore > 0) retval = LARGEINT - 65 + finalscore;
	      else if (finalscore < 0) retval = -(LARGEINT - 65 + finalscore);
	      else retval = 0;
	    }
	  }
	}
      }

      m_score.ScoreAdd(opponent, number_of_turned);
      m_score.ScoreSubtract(player, number_of_turned);
    }

    /////////////////
    // Restore board:
    /////////////////

    for (int i = number_of_turned; i > 0; i--)
    {
      mse = m_squarestack.Pop();
      m_bc_score.ScoreAdd(opponent, m_bc_board[mse.m_x][mse.m_y]);
      m_bc_score.ScoreSubtract(player, m_bc_board[mse.m_x][mse.m_y]);
      m_board[mse.m_x][mse.m_y] = opponent;
    }

    m_board[xplay][yplay] = Score.NOBODY;
    m_score.ScoreSubtract(player, 1);
    m_bc_score.ScoreSubtract(player, m_bc_board[xplay][yplay]);

    if (number_of_turned < 1 || GetInterrupt()) return ILLEGAL_VALUE;
    else return retval;
  }


  private final int TryAllMoves(int opponent, int level, int cutoffval,
    long opponentbits, long playerbits)
  {
    int maxval = -LARGEINT;

    for (int x=1; x<9; x++)
    {
      for (int y=1; y<9; y++)
      if (m_board[x][y] == Score.NOBODY &&
        (m_neighbor_bits[x][y] & playerbits) != 0)
      {
	int val = ComputeMove2(x, y, opponent, level+1, maxval, opponentbits,
          playerbits);

	if (val != ILLEGAL_VALUE && val > maxval)
	{
	  maxval = val;
	  if (maxval > -cutoffval || GetInterrupt()) break;
	}
      }

      if (maxval > -cutoffval || GetInterrupt()) break;
    }

    if (GetInterrupt()) return -LARGEINT;
    return maxval;
  }


  private final int EvaluatePosition(int player)
  {
    int retval;

    int opponent = Score.GetOpponent(player);
    int score_player = m_score.GetScore(player);
    int score_opponent = m_score.GetScore(opponent);

    if (m_exhaustive) retval = score_player - score_opponent;
    else
    {
      retval = (100-m_coeff) *
	(m_score.GetScore(player) - m_score.GetScore(opponent)) +
	m_coeff * BC_WEIGHT *
	(m_bc_score.GetScore(player)-m_bc_score.GetScore(opponent));
    }

    return retval;
  }


  private void SetupBcBoard()
  {
    m_bc_board = new int[9][9];

    for (int i=1; i < 9; i++)
    for (int j=1; j < 9; j++)
    {
      if (i == 2 || i == 7) m_bc_board[i][j] = -2; else m_bc_board[i][j] = 0;
      if (j == 2 || j == 7) m_bc_board[i][j] -= 2;
    }

    m_bc_board[1][1] = 20;
    m_bc_board[8][1] = 20;
    m_bc_board[1][8] = 20;
    m_bc_board[8][8] = 20;

    m_bc_board[1][2] = -2;
    m_bc_board[2][1] = -2;
    m_bc_board[1][7] = -2;
    m_bc_board[7][1] = -2;
    m_bc_board[8][2] = -2;
    m_bc_board[2][8] = -2;
    m_bc_board[8][7] = -2;
    m_bc_board[7][8] = -2;
  }


  private void SetupBits()
  {
    m_coord_bit = new long[9][9];
    m_neighbor_bits = new long[9][9];

    long bits = 1;

    for (int i=1; i < 9; i++)
    for (int j=1; j < 9; j++)
    {
      m_coord_bit[i][j] = bits;
      bits *= 2;
    }

    for (int i=1; i < 9; i++)
    for (int j=1; j < 9; j++)
    {
      m_neighbor_bits[i][j] = 0;

      for (int xinc=-1; xinc<=1; xinc++)
      for (int yinc=-1; yinc<=1; yinc++)
      if (xinc != 0 || yinc != 0)
	if (i + xinc > 0 && i + xinc < 9 && j + yinc > 0 && j + yinc < 9)
	  m_neighbor_bits[i][j] |= m_coord_bit[i + xinc][j + yinc];
    }
  }


  private final int CalcBcScore(int player)
  {
    int sum = 0;

    for (int i=1; i < 9; i++)
    for (int j=1; j < 9; j++)
      if (m_board[i][j] == player) sum += m_bc_board[i][j];

    return sum;
  }


  private long ComputeOccupiedBits(int player)
  {
    long retval = 0;

    for (int i=1; i < 9; i++)
    for (int j=1; j < 9; j++)
      if (m_board[i][j] == player) retval |= m_coord_bit[i][j];

    return retval;
  }
}
