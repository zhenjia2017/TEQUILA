package org.tempo.QuestionAnswer;

import java.util.Date;

/** 
* @ClassName: CPairAnswerCandidate 
* @Description: class to store Pair candidates
*  
*/
public class CPairAnswerCandidate{

	public CDataInProperty FirstAnswer;
	public CDataInProperty SecondAnswer;
	
	private boolean Pair;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: has a pair candidates </p> 
	* @param first
	* @param second 
	*/
	public CPairAnswerCandidate(CDataInProperty first, CDataInProperty second){
		this.FirstAnswer = first;
		this.SecondAnswer = second;
		this.Pair = true;
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: only 1 candidate without pair</p> 
	* @param first 
	*/
	public CPairAnswerCandidate(CDataInProperty first){
		this.FirstAnswer = first;
		this.SecondAnswer = null;
		this.Pair = false;
	}
	
	public boolean IsPair(){
		return (this.Pair);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(this.IsPair()){
				return ("[" + this.FirstAnswer + "|||" + (this.SecondAnswer != null? this.SecondAnswer:"") +"]");
		}else{
				return ("[" + this.FirstAnswer + "]");
		}
		
	}
	
	/** 
	* @Title: GetAnswer 
	* @Description: get answer
	* @return   
	*/
	public String GetAnswer(){
		if(this.FirstAnswer != null)
			return this.FirstAnswer.getAnswerString();
		else if(this.SecondAnswer != null)
			return this.SecondAnswer.getAnswerString();
		else
			return "";
	}
		
	
	
	/** 
	* @Title: getStartDate 
	* @Description: get start date
	* @return   
	*/
	public Date getStartDate(){
		if(this.IsPair()){
			if( this.FirstAnswer != null && this.FirstAnswer.Date != null){
				return this.FirstAnswer.Date.getStartDate();
			}else{
				return null;
			}
				
		}else{
			
			if( this.FirstAnswer != null && this.FirstAnswer.Date != null){
				return this.FirstAnswer.Date.getStartDate();
			}else if( this.SecondAnswer != null && this.SecondAnswer.Date != null){
				return this.SecondAnswer.Date.getStartDate();	
			}else{
				return null;
			}
		}
	}

	/** 
	* @Title: getEndDate 
	* @Description: get end date
	* @return   
	*/
	public Date getEndDate(){
		if(this.IsPair()){
			if( this.SecondAnswer != null && this.SecondAnswer.Date != null){
				return this.SecondAnswer.Date.getEndDate();
			}else{
				return null;
			}
		}else{
			
			if( this.SecondAnswer != null && this.SecondAnswer.Date != null){
				return this.SecondAnswer.Date.getEndDate();
			}else if( this.FirstAnswer != null && this.FirstAnswer.Date != null){
				return this.FirstAnswer.Date.getEndDate();	
			}else{
				return null;
			}		
	
		}
	}
	
	
}
