package org.tempo.QuestionAnswer;

/** 
* @ClassName: CAnswerCandidate 
* @Description: the class is to describe Answer candidate part
*  
*/
public class CAnswerCandidate {

	public String Answer;
	public String MID;
	private boolean AnwserMID;
	public boolean IsNaswerMID(){
		return this.AnwserMID;
	}
	
	public String getAnswerString(){
		return String.format("%s(%s)", this.Answer, this.MID);
	}
	
	protected CAnswerCandidate(String answer, String mid){
		this.Answer = answer;
		this.MID = mid;
		this.AnwserMID = true;
	}
	
	
	protected CAnswerCandidate(String answer){
		this.Answer = answer;
		this.MID = "";
		this.AnwserMID = false;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(this.AnwserMID)
			return  (this.Answer + "(" + this.MID + ")" );
		else
			return ( this.Answer );
	}	
	
	/** 
	* @Title: Parse 
	* @Description: parse by answer string
	* @param answertext
	* @return   
	*/
	public static CAnswerCandidate  Parse(String answertext){
		if( answertext.contains("("))
			return getAnswerMID(answertext);
		else
			return getSQ2Answer(answertext);
	}
	
	/** 
	* @Title: getAnswerMID 
	* @Description: contains only 1 answer without id
	* @param answertext
	* @return   
	*/
	static CAnswerCandidate getAnswerMID(String answertext){

		int pos1 = answertext.indexOf("(");
		int pos2 = answertext.lastIndexOf(")");
		
		if(pos1 < 0 || pos2<1)
			return null;
		
		return (new CAnswerCandidate(answertext.substring(0, pos1).trim(),
							answertext.substring(pos1+1, pos2)));
	}
	
	/** 
	* @Title: getSQ2Answer 
	* @Description: contains  answer and ID
	* @param answertext
	* @return   
	*/
	static CAnswerCandidate getSQ2Answer(String answertext){

		int pos1 = answertext.indexOf("<");
		int pos2 = answertext.lastIndexOf(">");
		
		if(pos1 < 0 || pos2<1)
			return null;
		
		return (new CAnswerCandidate(answertext.substring(pos1+1, pos2)));
	}	

	

}
