package org.tempo.SentenceAnalysis.Dictionary;

import java.util.List;
import java.util.ArrayList;


/** 
* @ClassName: TernaryTreeSearchResult 
* @Description: tree search result.
*  
*/
public class TernaryTreeSearchResult {
	public String WordString;
	
	/** Word: save the word as a Integer list */  
	protected List<Integer> Word;
	public List<Integer> getWord() {
		return Word;
	}
	public void setWord(List<Integer> word) {
		Word = word;
	}
	
	protected int ExtraData;
	public int getExtraData() {
		return ExtraData;
	}
	public void setExtraData(int extraData) {
		ExtraData = extraData;
	}

	
	public TernaryTreeSearchResult(){
		this.Word = null;
		this.ExtraData = 0;
	}

	public TernaryTreeSearchResult(List<Integer> word, int extraData){
		this.Word = word;
		this.ExtraData = extraData;
	}	
	
	public TernaryTreeSearchResult(TernaryTreeSearchResult r){
		this.Word = new ArrayList<Integer>(r.Word);
		this.ExtraData = r.getExtraData();
		this.WordString = r.WordString;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (this.WordString + "/" + this.ExtraData);
	}
}
