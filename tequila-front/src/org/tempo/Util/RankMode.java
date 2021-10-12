package org.tempo.Util;

/** 
* @Description: Rank option.
* 
*/
public enum RankMode {
	Rank1(1),  //sub-question 1 answer is top-1 candidate of underlying KB-QA
	Rank2(2),  //sub-question 1 answer is top-2 candidate of underlying KB-QA
	Rank3(3);  //sub-question 1 answer is top-3 candidate of underlying KB-QA
	
	 private int rank;

		private RankMode(int rank) { 
	    	this.rank = rank;
         
     }  
		
		public int getMode() {  
	        return rank;  
	    }

}
