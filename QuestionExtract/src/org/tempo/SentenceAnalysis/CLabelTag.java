package org.tempo.SentenceAnalysis;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
/** 
* @ClassName: CLabelTag 
* @Description: Tag base class
*  
*/
public class CLabelTag implements ILabelTag {


	public EnumTagType TagType;
	
	/** StartChar: start char position */  
	public int StartChar;
	/** EndChar: end char position */  
	public int EndChar;
	
	/** Start:start word index */  
	public int Start;
	
	/** End:end word index */  
	public int End;

	/** Text: text of tag */  
	public String Text;	
	
	public CLabelTag( EnumTagType tagType){
		this.TagType = tagType;
		this.Start = -1;
		this.End = -1;
		this.StartChar = -1;
		this.EndChar =-1;
	}
	
	public CLabelTag( EnumTagType tagType, int start, int end, int startChar, int endChar){
		this.TagType = tagType;
		this.Start = start;
		this.End = end;
		this.StartChar = startChar;
		this.EndChar = endChar;
	}	
	

	public EnumTagType getTagType() {
		// TODO Auto-generated method stub
		return this.TagType;
	}
	
	public int getBeginPos() {
		// TODO Auto-generated method stub
		return this.StartChar;
	}
	
	

	public int getEndPos() {
		// TODO Auto-generated method stub
		return this.EndChar;
	}

	public int getStart() {
		// TODO Auto-generated method stub
		return this.Start;
	}

	public int getEnd() {
		// TODO Auto-generated method stub
		return this.End;
	}

	public int setBeginPos(int pos) {
		// TODO Auto-generated method stub
		return this.StartChar = pos;
	}

	public int setEndPos(int pos) {
		// TODO Auto-generated method stub
		return this.EndChar = pos;
	}

	public int setStart(int pos) {
		// TODO Auto-generated method stub
		return this.Start = pos;
	}

	public int setEnd(int pos) {
		// TODO Auto-generated method stub
		return this.End = pos;
	}

	/** 
	* @Title: CreateLabelTag 
	* @Description: set tag's positions, and abstract the tag text from NLP tokens
	* @param tagType
	* @param sec
	* @param text
	* @param tokens
	* @return   
	*/
	public static CLabelTag CreateLabelTag(EnumTagType tagType, SentenceSectionDescription sec,
											String text, List<CoreLabel> tokens){
		
		CLabelTag tag = new CLabelTag(tagType, sec.Start, sec.End, sec.CharStart, sec.CharEnd);
		if( sec.CharStart>=0 && sec.CharEnd>0 && tag.Start < 0)
			ILabelTag.UpdateTagsWordIndex(tag, tokens);		
		tag.Text = text.substring(sec.CharStart, sec.CharEnd);
		return tag;
	}
	
	
	/** 
	* @Title: IsTagStartAfterPos 
	* @Description: check if the tag start  after a position
	* @param pos
	* @return   
	*/
	public boolean IsTagStartAfterPos(int pos){
		if(pos < 0)
			return true;
		return (this.getBeginPos() >= pos);
	}
	
	/** 
	* @Title: IsTagStartBeforePos 
	* @Description: check if the tag start before a position
	* @param pos
	* @return   
	*/
	public boolean IsTagStartBeforePos(int pos){
		if(pos < 0) 
			return true;
		return (this.getEndPos()< pos);
	}
		
	
}
