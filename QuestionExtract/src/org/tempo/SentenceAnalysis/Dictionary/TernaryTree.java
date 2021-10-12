package org.tempo.SentenceAnalysis.Dictionary;

/* See LICENSE for licensing and NOTICE for copyright. */

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Implementation of a ternary tree. Methods are provided for inserting strings and searching for strings. The
 * algorithms in this class are all recursive, and have not been optimized for any particular purpose. Data which is
 * inserted is not sorted before insertion, however data can be inserted beginning with the median of the supplied data.
 *
 * @author  Middleware Services
 */

public class TernaryTree
{

  // CheckStyle:JavadocVariable OFF
  /** Case sensitive comparator. */
  protected static final Comparator<Integer> CASE_SENSITIVE_COMPARATOR = (a, b) -> {
    int result = 0;
    final int c1 = a;
    final int c2 = b;
    if (c1 < c2) {
      result = -1;
    } else if (c1 > c2) {
      result = 1;
    }
    return result;
  };

  // CheckStyle:JavadocVariable ON

  /** File system line separator. */
  private static final int LINE_SEPARATOR = 0;

  /** Character comparator. */
  protected final Comparator<Integer> comparator;

  /** root node of the ternary tree. */
  private TernaryNode root;


  /** Creates an empty case sensitive ternary tree. */
  public TernaryTree()
  {
    this(true);
  }


  /**
   * Creates an empty ternary tree with the given case sensitivity.
   *
   * @param  caseSensitive  whether this ternary tree should be case sensitive.
   */
  public TernaryTree(final boolean caseSensitive)
  {
      comparator = CASE_SENSITIVE_COMPARATOR;
  }


  /**
   * Inserts the supplied word into this tree.
   *
   * @param  word  to insert
   */
  public void insert(final List<Integer> word)
  {
    if (word != null) {
      root = insertNode(root, word, 0, 1);
    }
  }

  public void insert(final List<Integer> word, int extraData)
  {
	 if(extraData <= 0) extraData = 1;
	  
     if (word != null) {
       root = insertNode(root, word, 0, extraData);
    }
  }


  /**
   * Inserts the supplied array of words into this tree.
   *
   * @param  words  to insert
   */
//  public void insert(final List< List<Integer> > words)
//  {
//    if (words != null) {
//      for (List<Integer> s : words) {
//        insertList(s);
//      }
//    }
//  }


  /**
   * Returns whether the supplied word has been inserted into this ternary tree.
   *
   * @param  word  to search for
   *
   * @return  whether the word was found
   */

  public TernaryTreeSearchResult search(final List<Integer> word)
  {
     TernaryNode nd = this.findNode(word);
     if( nd == null || nd.isEndOfWord() == false)
    	 return null;
     
     return (new TernaryTreeSearchResult(word, nd.getExtraData()));
  }


  /**
   * Returns an array of strings which partially match the supplied word. word should be of the format '.e.e.e' Where
   * the '.' character represents any valid character. Possible results from this query include: Helene, delete, or
   * severe Note that no substring matching occurs, results only include strings of the same length. If the supplied
   * word does not contain the '.' character, then a regular search is performed.
   *
   * <p><strong>NOTE</strong> This method is not supported for case insensitive ternary trees. Since the tree is built
   * without regard to case any words returned from the tree may or may not match the case of the supplied word.</p>
   *
   * @param  word  to search for
   *
   * @return  array of matching words
   *
   * @throws  UnsupportedOperationException  if this is a case insensitive ternary tree
   */
  public List< List<Integer>  > partialSearch(final List<Integer> word)
  {

    final List< List<Integer> > matches = partialSearchNode(root, new ArrayList< List<Integer> >(), new ArrayList<Integer>(), word, 0);
    if (matches != null) {
    	  return matches;
    } else {
    	return( new ArrayList< List<Integer> > ());
    }
    
  }


  /**
   * Return an array of strings which are near to the supplied word by the supplied distance. For the query
   * nearSearch("fisher", 2): Possible results include: cipher, either, fishery, kosher, sister. If the supplied
   * distance is not &gt; 0, then a regular search is performed.
   *
   * <p><strong>NOTE</strong> This method is not supported for case insensitive ternary trees. Since the tree is built
   * without regard to case any words returned from the tree may or may not match the case of the supplied word.</p>
   *
   * @param  word  to search for
   * @param  distance  for valid match
   *
   * @return  array of matching words
   *
   * @throws  UnsupportedOperationException  if this is a case insensitive ternary tree
   */
  public List< List<Integer> > nearSearch(final List<Integer> word, final int distance)
  {

    final List< List<Integer> > matches = nearSearchNode(root, distance, new ArrayList< List<Integer> >(), new ArrayList<Integer>(),word, 0);
    if (matches == null) {
      return ( new ArrayList< List<Integer> >());
    } else {
      return matches;
    }

  }


  /**
   * Returns a list of all the words in this ternary tree. This is a very expensive operation, every node in the tree is
   * traversed. The returned list cannot be modified.
   *
   * @return  unmodifiable list of words
   */
  public List< List<Integer> > getWords()
  {
    final List<List<Integer>> words = traverseNode(root, new ArrayList<Integer>(), new ArrayList<>());
    //return Collections.unmodifiableList(words);
    return words;
  }


  /**
   * Prints an ASCII representation of this ternary tree to the supplied writer. This is a very expensive operation,
   * every node in the tree is traversed. The output produced is hard to read, but it should give an indication of
   * whether or not your tree is balanced.
   *
   * @param  out  to print to
   *
   * @throws  IOException  if an error occurs
   */
  public void print(final Writer out)
    throws IOException
  {
    //out.write(printNode(root, "", 0));
  }


  /**
   * Recursively inserts a word into the ternary tree one node at a time beginning at the supplied node.
   *
   * @param  node  to put character in
   * @param  word  to be inserted
   * @param  index  of character in word
   *
   * @return  ternary node to insert
   */
  private TernaryNode insertNode(

    // CheckStyle:FinalParametersCheck OFF
    TernaryNode node,
    // CheckStyle:FinalParametersCheck ON
    final List<Integer> word,
    final int index,
    final int extraData)
  {
    if (index < word.size()) {
      final Integer c = word.get(index);
      if (node == null) {
        // CheckStyle:ParameterAssignmentCheck OFF
        node = new TernaryNode(c);
        // CheckStyle:ParameterAssignmentCheck ON
      }

      final Integer split = node.getSplitChar();
      final int cmp = comparator.compare(c, split);
      if (cmp < 0) {
        node.setLokid(insertNode(node.getLokid(), word, index, extraData));
      } else if (cmp == 0) {
        if (index == word.size() - 1) {
          //node.setEndOfWord(true);
          node.setExtraData(extraData);
        }
        node.setEqkid(insertNode(node.getEqkid(), word, index + 1, extraData));
      } else {
        node.setHikid(insertNode(node.getHikid(), word, index, extraData));
      }
    }
    return node;
  }


  /**
   * Recursively searches for a word in the ternary tree one node at a time beginning at the supplied node.
   *
   * @param  node  to search in
   * @param  word  to search for
   * @param  index  of character in word
   *
   * @return  whether or not the word was found
   */
  // CheckStyle:ReturnCount OFF
//  private boolean searchNode(final TernaryNode node, final List<Integer> word, final int index)
//  {
//    boolean success = false;
//    if (node != null && index < word.size()) {
//      final int c = word.get(index);
//      final int split = node.getSplitChar();
//      final int cmp = comparator.compare(c, split);
//      if (cmp < 0) {
//        return searchNode(node.getLokid(), word, index);
//      } else if (cmp > 0) {
//        return searchNode(node.getHikid(), word, index);
//      } else {
//        if (index == word.size() - 1) {
//          if (node.isEndOfWord()) {
//            success = true;
//          }
//        } else {
//          return searchNode(node.getEqkid(), word, index + 1);
//        }
//      }
//    }
//    return success;
//  }
  // CheckStyle:ReturnCount ON


  /**
   * Recursively searches for a partial word in the ternary tree one node at a time beginning at the supplied node.
   *
   * @param  node  to search in
   * @param  matches  of partial matches
   * @param  match  the current word being examined
   * @param  word  to search for
   * @param  index  of character in word
   *
   * @return  list of matches
   */
  private List<List<Integer>> partialSearchNode(
    final TernaryNode node,
    // CheckStyle:FinalParametersCheck OFF
    List<List<Integer>> matches,
    // CheckStyle:FinalParametersCheck ON
    final List<Integer> match,
    final List<Integer> word,
    final int index)
  {
    if (node != null && index < word.size()) {
      final int c = word.get(index);
      final int split = node.getSplitChar();
      final int cmp = comparator.compare(c, split);
      if (c == 0 || cmp < 0) {
        // CheckStyle:ParameterAssignmentCheck OFF
        matches = partialSearchNode(node.getLokid(), matches, match, word, index);
        // CheckStyle:ParameterAssignmentCheck ON
      }
      if (c == 0 || cmp == 0) {
        if (index == word.size() - 1) {
          if (node.isEndOfWord()) {
        	  match.add(split);
        	  matches.add(match);
          }
        } else {
          // CheckStyle:ParameterAssignmentCheck OFF
        	match.add(split);
          matches = partialSearchNode(node.getEqkid(), matches, match, word, index + 1);
         // CheckStyle:ParameterAssignmentCheck ON
        }
      }
      if (c == 0 || cmp > 0) {
        // CheckStyle:ParameterAssignmentCheck OFF
        matches = partialSearchNode(node.getHikid(), matches, match, word, index);
        // CheckStyle:ParameterAssignmentCheck ON
      }
    }
    return matches;
  }
  
  
  private TernaryNode findNode(List<Integer> word){
      TernaryNode node = this.root;
      int index =0;
      while (node != null && index < word.size())
      {
	        final int c = word.get(index);
	        final int split = node.getSplitChar();
	        final int cmp = comparator.compare(c, split);
	    
	        if (cmp < 0){
	              node = node.getLokid();
	        } else if (cmp>0){
	              node = node.getHikid();
	        }else{
	            if (++index == word.size()){
	            	//if (node.isEndOfWord())
	            		return node;
	            	//else
	            		//return node.getEqkid();
	            }else{
	                node = node.getEqkid();
	            }
	        }
      }

      return null;
  }

  private void DFS( TernaryNode node,
		  		List<TernaryTreeSearchResult> matches,     
		  		List<Integer> match)
  {
      if (node != null)
      {
    	  int c = node.getSplitChar();   
    	  
          if (node.isEndOfWord())
          {
        	  final List<Integer> newMatch = new ArrayList<Integer>(match);
        	  newMatch.add(c);
        	  matches.add(new TernaryTreeSearchResult(newMatch, node.getExtraData()));        	  
          }
          
      	  DFS(node.getLokid(), matches, match);
      	  if( node.getEqkid() != null){
        	  final List<Integer> newMatch = new ArrayList<Integer>(match);
        	  newMatch.add(c);      		  
      		  DFS(node.getEqkid(), matches, newMatch);
      	  }
       	  DFS(node.getHikid(), matches, match);
      }
  }
  
  public List<TernaryTreeSearchResult> prefixSearch(List<Integer> word)
  {
	  
	  List<TernaryTreeSearchResult> matches = new ArrayList<TernaryTreeSearchResult>();
	  if(word.size() < 1)
		  return matches;
	  
	  TernaryNode node = this.findNode(word);
	  if( node != null){
		  if( node.isEndOfWord()){
			  matches.add(new TernaryTreeSearchResult(word, node.getExtraData()) );
		  }
		  
		  this.DFS(node.getEqkid(), matches, word);
	  }
	  
      return matches;
  }

  /**
   * Recursively searches for a near match word in the ternary tree one node at a time beginning at the supplied node.
   *
   * @param  node  to search in
   * @param  distance  of a valid match, must be > 0
   * @param  matches  list of near matches
   * @param  match  the current word being examined
   * @param  word  to search for
   * @param  index  of character in word
   *
   * @return  list of matches
   */
  private List< List<Integer> > nearSearchNode(
    final TernaryNode node,
    final int distance,
    // CheckStyle:FinalParametersCheck OFF
    List< List<Integer> > matches,
    // CheckStyle:FinalParametersCheck ON
    final List<Integer> match,
    final List<Integer> word,
    final int index)
  {
    if (node != null && distance >= 0) {

      final int c;
      if (index < word.size()) {
        c = word.get(index);
      } else {
        c = (char) -1;
      }

      final int split = node.getSplitChar();
      final int cmp = comparator.compare(c, split);

      if (distance > 0 || cmp < 0) {
        // CheckStyle:ParameterAssignmentCheck OFF
        matches = nearSearchNode(node.getLokid(), distance, matches, match, word, index);
        // CheckStyle:ParameterAssignmentCheck ON
      }

      final List<Integer> newMatch = new ArrayList<Integer>(match);
      newMatch.add(split);
      if (cmp == 0) {

        if (node.isEndOfWord() && distance >= 0 && newMatch.size() + distance >= word.size()) {
          matches.add(newMatch);
        }

        // CheckStyle:ParameterAssignmentCheck OFF
        matches = nearSearchNode(node.getEqkid(), distance, matches, newMatch, word, index + 1);
        // CheckStyle:ParameterAssignmentCheck ON
      } else {

        if (node.isEndOfWord() && distance - 1 >= 0 && newMatch.size() + distance - 1 >= word.size()) {
          matches.add(newMatch);
        }

        // CheckStyle:ParameterAssignmentCheck OFF
        matches = nearSearchNode(node.getEqkid(), distance - 1, matches, newMatch, word, index + 1);
        // CheckStyle:ParameterAssignmentCheck ON
      }

      if (distance > 0 || cmp > 0) {
        // CheckStyle:ParameterAssignmentCheck OFF
        matches = nearSearchNode(node.getHikid(), distance, matches, match, word, index);
        // CheckStyle:ParameterAssignmentCheck ON
      }
    }
    return matches;
  }

  private static Integer s_count;

  
  public Integer size()
  {
	s_count = 0;
    traverseNodeCount(root);
    //return Collections.unmodifiableList(words);
    return s_count;
  }
 
  
  private void traverseNodeCount( final TernaryNode node) {
	    if (node != null) {
		      if(node.isEndOfWord()){
		    	  s_count++;
		      }
	    	  traverseNodeCount(node.getLokid());
	    	  traverseNodeCount(node.getEqkid());
	    	  traverseNodeCount(node.getHikid());
		}
		  
  }


  
  
  /**
   * Recursively traverses every node in the ternary tree one node at a time beginning at the supplied node. The result
   * is a string representing every word, which is delimited by the LINE_SEPARATOR character.
   *
   * @param  node  to begin traversing
   * @param  s  string of words found at the supplied node
   * @param  words  which will be returned (recursive function)
   *
   * @return  string containing all words from the supplied node
   */
  private List< List<Integer> > traverseNode(
    final TernaryNode node,
    final List<Integer> s,
    // CheckStyle:FinalParametersCheck OFF
    List< List<Integer> > words)
  // CheckStyle:FinalParametersCheck ON
  {
    if (node != null) {

      // CheckStyle:ParameterAssignmentCheck OFF
      words = traverseNode(node.getLokid(), s, words);
      // CheckStyle:ParameterAssignmentCheck ON

      final Integer c = node.getSplitChar();
      if (node.getEqkid() != null) {
        // CheckStyle:ParameterAssignmentCheck OFF
    	  s.add(c);
    	  words = traverseNode(node.getEqkid(),s , words);
        // CheckStyle:ParameterAssignmentCheck ON
      }

      if (node.isEndOfWord()) {
    	  s.add(c);
          words.add(s);
      }

      // CheckStyle:ParameterAssignmentCheck OFF
      words = traverseNode(node.getHikid(), s, words);
      // CheckStyle:ParameterAssignmentCheck ON
    }
    return words;
  }


  /**
   * Recursively traverses every node in the ternary tree one node at a time beginning at the supplied node. The result
   * is an ASCII string representation of the tree beginning at the supplied node.
   *
   * @param  node  to begin traversing
   * @param  s  string of words found at the supplied node
   * @param  depth  of the current node
   *
   * @return  string containing all words from the supplied node
   */
//  private String printNode(final TernaryNode node, final String s, final int depth)
//  {
//    final StringBuilder buffer = new StringBuilder();
//    if (node != null) {
//      buffer.append(printNode(node.getLokid(), " <-", depth + 1));
//
//      final String c = String.valueOf(node.getSplitChar());
//      final StringBuilder eq = new StringBuilder();
//      if (node.getEqkid() != null) {
//        eq.append(printNode(node.getEqkid(), s + c + "--", depth + 1));
//      } else {
//        int count = (new StringTokenizer(s, "--")).countTokens();
//        if (count > 0) {
//          count--;
//        }
//        for (int i = 1; i < depth - count - 1; i++) {
//          eq.append("   ");
//        }
//        eq.append(s).append(c).append(TernaryTree.LINE_SEPARATOR);
//      }
//      buffer.append(eq);
//
//      buffer.append(printNode(node.getHikid(), " >-", depth + 1));
//    }
//    return buffer.toString();
//  }
}
