package org.tempo.SentenceAnalysis.Dictionary;
/* See LICENSE for licensing and NOTICE for copyright. */
/**
 * Implementation of a node contained in a ternary tree.
 *
 * @author  Middleware Services
 */

public class TernaryNode
{

  /** character of this node. */
  private int splitchar;

  /** whether this character is the end of a word. */
//  private boolean endOfWord;

  /** low child of this node. */
  private TernaryNode lokid;

  /** equal child of this node. */
  private TernaryNode eqkid;

  /** high child of this node. */
  private TernaryNode hikid;

  /** character of this node. */
  private int extraData; 
  

  public int getExtraData() {
	return extraData;
}


public void setExtraData(int extraData) {
	this.extraData = extraData;
}


/**
   * Create a new ternary node with the supplied character.
   *
   * @param  c  character
   */
  public TernaryNode(final int c)
  {
    splitchar = c;
    this.extraData=0;
  }


  /**
   * Returns the split character.
   *
   * @return  character
   */
  public int getSplitChar()
  {
    return splitchar;
  }


  /**
   * Sets the split character.
   *
   * @param  c  character
   */
  public void setSplitChar(final int c)
  {
    splitchar = c;
  }


  /**
   * Returns whether this node is at the end of a word.
   *
   * @return  whether this node is at the end of a word
   */
  public boolean isEndOfWord()
  {
    return (this.extraData>0);
  }


  /**
   * Sets whether this node is at the end of a word.
   *
   * @param  b  whether this node is at the end of a word
   */
//  public void setEndOfWord(final boolean b)
//  {
//    endOfWord = b;
//  }


  /**
   * Returns the lokid node in relation to this node.
   *
   * @return  ternary node
   */
  public TernaryNode getLokid()
  {
    return lokid;
  }


  /**
   * Sets the lokid node in relation to this node.
   *
   * @param  node  ternary node
   */
  public void setLokid(final TernaryNode node)
  {
    lokid = node;
  }


  /**
   * Returns the eqkid node in relation to this node.
   *
   * @return  ternary node
   */
  public TernaryNode getEqkid()
  {
    return eqkid;
  }


  /**
   * Sets the eqkid node in relation to this node.
   *
   * @param  node  ternary node
   */
  public void setEqkid(final TernaryNode node)
  {
    eqkid = node;
  }


  /**
   * Returns the hikid node in relation to this node.
   *
   * @return  ternary node
   */
  public TernaryNode getHikid()
  {
    return hikid;
  }


  /**
   * Sets the hikid node in relation to this node.
   *
   * @param  node  ternary node
   */
  public void setHikid(final TernaryNode node)
  {
    hikid = node;
  }
}
