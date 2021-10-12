package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;

/** 
* @ClassName: COrdinalAnswerFilter 
* @Description: ordinal filter
*/
public class COrdinalAnswerFilter  extends CAnswerFilterBase{
	public COrdinalAnswerFilter(INodeProcessor<CAnswerFilterContext> next){
		super(next);
	}

	boolean IsMatchTypeQuestion(CAnswerFilterContext context){
		return( context.SentenceReport.Ordinals.size() > 0 	&&  context.getAnswersCount()>0);
	}	
	
	
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		SortDateCandidates();	
		ReasoningAnswer();
	}
	
	/** 
	* @Title: ReasoningAnswer 
	* @Description: reasoning answer with ordinal information
	*/
	void ReasoningAnswer(){
	
		//get ordinal tag
		CLabelTag ordinalTag = null;
		if(this.Context.QuestionAnwser !=null){
			ordinalTag = this.getMostPossibleTag(false, this.Context.SentenceReport.Ordinals);			
		}	
		
		String ordinalText = ordinalTag.Text.toLowerCase().trim();
		
		//get sequence number by the ordinal tag
		CPairAnswerCandidate aw = null;
		int nOrder = this.getOrderInteger(ordinalText, Context.QuestionAnwser.FilterCandidates.size()) - 1;
		
		//if has a candidate , add it to filter result.
		if(Context.QuestionAnwser.FilterCandidates != null  && Context.QuestionAnwser.FilterCandidates.size()>0){
			if(Context.QuestionAnwser.FilterCandidates.size()>nOrder)
				aw= Context.QuestionAnwser.FilterCandidates.get(nOrder);
			else
				aw = Context.QuestionAnwser.FilterCandidates.get(Context.QuestionAnwser.FilterCandidates.size()-1);
			
			//add candidates which has same time with the selected answer;
			List<CPairAnswerCandidate> result = new ArrayList<CPairAnswerCandidate>();
			for(int i = 0; i<Context.QuestionAnwser.FilterCandidates.size();i++){
				CPairAnswerCandidate item = Context.QuestionAnwser.FilterCandidates.get(i);
				if( item != null && this.compare(item, aw) == 0){
					result.add(item);
					System.out.println("Ordinal Match OK!: " + item.toString());
				}else{
					System.out.println("Ordinal filter out " + item.toString());
				}
				
			}
			
			Context.QuestionAnwser.FilterCandidates.clear();
			Context.QuestionAnwser.FilterCandidates.addAll(result);
		}
		
		SetReasoningString(ordinalText);
		
	}
	
	private void SetReasoningString(String ordinalText) {
		// TODO Auto-generated method stub
		if( this.Context.SentenceReport != null){
			String strInfo = String.format("Ordinal rule: %s ;", ordinalText);
			this.Context.SentenceReport.AnswerReport.Reason += strInfo; 
		}
	}
	
	String[] _ordinalWords = new String[]{
			" %%%% ", "first","second","third","fourth","fifth","sixth","seventh","eighth","ninth", "tenth", 
			"eleventh", "twelfth","thirteenth","fourteen","fifteenth","sixteenth","seventeen","eighteenth","nineteenth","twentieth"
			};
	
	/** 
	* @Title: findOrdinalWord 
	* @Description: find ordinal string in array
	* @param word
	* @return   
	*/
	int findOrdinalWord(String word){
		for(int i =1; i<this._ordinalWords.length; i++ ){
			if( word.compareTo(this._ordinalWords[i]) == 0)
				return i;				
		}
		
		return -1;
	}
	
	
	/** 
	* @Title: getOrderInteger 
	* @Description: get the index of ordinal tag string
	* @param label
	* @param max
	* @return   
	*/
	int getOrderInteger(String label, int max){
		
		int n = this.findOrdinalWord(label);
		if( n > 0){
			return n;		
		}else if(label.compareTo("last") ==0){
			return max;
		}else if(label.endsWith("th")){
			return getIntFromString(label.substring(0, label.length()-2));
		}else{
			return 1;
		}
	}

	/** 
	* @Title: getIntFromString 
	* @Description: parse string to Integer
	* @param text
	* @return   
	*/
	int getIntFromString(String text){
	
		int n = 0;
		try {
			n = Integer.parseInt(text);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return n;
		}
	}
	
	
	/** 
	* @Title: OrderDateCandidates 
	* @Description: sort candidates  by date 
	*/
	void SortDateCandidates(){
		Collections.sort(Context.QuestionAnwser.FilterCandidates, new Comparator<CPairAnswerCandidate>() {

			public int compare(CPairAnswerCandidate d1, CPairAnswerCandidate d2) {
				Date dt1 = d1.getStartDate();
				Date dt2 = d2.getStartDate();
				
				if(dt1==null && dt2 == null)
					return 0;
				
				if( dt1 == null && dt2 != null)
					return 1;

				if( dt1 != null && dt2 == null)
					return -1;
								
				if (dt1.getTime() < dt2.getTime()){
					return -1;
				}else if(dt1.getTime() == dt2.getTime()){
					return 0;
				}else{
					return 1;					
				}
			}

		});	
	}
	
	
	int compare(CPairAnswerCandidate d1, CPairAnswerCandidate d2) {
		Date dt1 = d1.getStartDate();
		Date dt2 = d2.getStartDate();
		
		if(dt1==null && dt2 == null)
			return 0;
		
		if( dt1 == null && dt2 != null)
			return 1;

		if( dt1 != null && dt2 == null)
			return -1;
						
		if (dt1.getTime() < dt2.getTime()){
			return -1;
		}else if(dt1.getTime() == dt2.getTime()){
			return 0;
		}else{
			return 1;					
		}
	}

	
}
