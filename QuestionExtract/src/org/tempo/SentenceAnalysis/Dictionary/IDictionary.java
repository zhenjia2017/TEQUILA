package org.tempo.SentenceAnalysis.Dictionary;



public interface IDictionary {
	/**
   * Returns whether the supplied word exists in the dictionary.
   *
   * @param  word  to search for
   *
   * @return  whether word was found
   */
  boolean search(String word);
  TernaryTreeSearchResult[] prefixSearch( String word);

  /**
   * Returns the number of words in this dictionary
   *
   * @return  total number of words to search
   */
  long size();
}