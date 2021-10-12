package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;
import org.tempo.Util.CGlobalConfiguration;

/** 
* @ClassName: CTimeX3AnswerFilter 
* @Description: filter for Time reasoning
*  
*/
public class CTimeX3AnswerFilter extends CAnswerFilterBase{

	
	public CTimeX3AnswerFilter(INodeProcessor<CAnswerFilterContext> next){
		super(next);
	}

	boolean IsMatchTypeQuestion(CAnswerFilterContext context){
		//return (context.HasDateTag() && context.getTimeConstraint() != null );
		
		return (context.getTimeConstraint() != null &&  context.getAnswersCount()>0);	
	}	
	
	
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		//get time constraint
		CDateValue dateConstraint = this.Context.getTimeConstraint();
		
		//reasoning process
		ReasoningAnswer(dateConstraint, context.GetSignalWord().toLowerCase().trim());
		
	}
	
	
	/** 
	* @Title: ReasoningAnswer 
	* @Description: according to the signal word , filter the answer by date constraint
	* @param dateConstraint
	* @param signalString   
	*/
	void ReasoningAnswer(CDateValue dateConstraint, String signalString){
		if(signalString.compareTo("before") == 0 || signalString.compareTo("prior to") == 0){
			FilterByBefore(dateConstraint);
			
		}else if(signalString.compareTo("after") == 0 || signalString.compareTo("since") == 0){
			FilterByAfter(dateConstraint);
		}else /* if("during, while, when, since, until, in,".contains(signalString+","))*/{
			FilterByDuring(dateConstraint);
		}
		
		SetReasoningString(dateConstraint, signalString);
		
	}
	
	private void SetReasoningString(CDateValue dateConstraint, String signalString) {
		// TODO Auto-generated method stub
		if( this.Context.SentenceReport != null){
			String strInfo = String.format("Time rule: %s (%s) ;", dateConstraint.toString(), signalString);
			this.Context.SentenceReport.AnswerReport.Reason += strInfo; 
		}
	}

	/** 
	* @Title: FilterByBefore 
	* @Description: process for "before" date constraint
	* @param dateConstraint   
	*/
	void FilterByBefore(CDateValue dateConstraint){
		List<CPairAnswerCandidate> matched = new ArrayList<CPairAnswerCandidate>();
		
		//filter answer candidates  before date constraint
		for(CPairAnswerCandidate cpa: this.Context.QuestionAnwser.FilterCandidates){
			if(dateConstraint.After(cpa.getStartDate(), cpa.getEndDate())){
				this.AddMatch(matched, cpa);
			}
		}
		
		//select only one answer if get lastest option is opened
		 this.Context.QuestionAnwser.FilterCandidates.clear();
		 
	//	 if(CGlobalConfiguration.OnlyLatestOneByTimeFilter && matched.size()>1 ){
		 if(this.Context.GetOnlyLatestOneByTimeFilter()&& matched.size()>1 ){
			 List<CPairAnswerCandidate> cpas = getLastestOne(dateConstraint, matched);
			 this.Context.QuestionAnwser.FilterCandidates.addAll(cpas);	 
		 }else{
			 this.Context.QuestionAnwser.FilterCandidates.addAll(matched);
		 }
	}
	


	/** 
	* @Title: FilterByAfter 
	* @Description:  process for "after" date constraint
	* @param dateConstraint   
	*/
	void FilterByAfter(CDateValue dateConstraint){
		List<CPairAnswerCandidate> matched = new ArrayList<CPairAnswerCandidate>();
		
		//filter candidates after date constraint
		for(CPairAnswerCandidate cpa: this.Context.QuestionAnwser.FilterCandidates){
			if(dateConstraint.Before(cpa.getStartDate(), cpa.getEndDate())){
				this.AddMatch(matched, cpa);
			}
		}
		
		 this.Context.QuestionAnwser.FilterCandidates.clear();
		 
	//	 if(CGlobalConfiguration.OnlyLatestOneByTimeFilter && matched.size()>1 ){
		 if(this.Context.GetOnlyLatestOneByTimeFilter() && matched.size()>1 ){
			 List<CPairAnswerCandidate> cpas = getLastestOne(dateConstraint, matched);
			 this.Context.QuestionAnwser.FilterCandidates.addAll(cpas);	 
		 }else{
			 this.Context.QuestionAnwser.FilterCandidates.addAll(matched);
		 }
		 
	}
	
	/** 
	* @Title: FilterByDuring 
	* @Description: filter candidates during date constraint
	* @param dateConstraint   
	*/
	void FilterByDuring(CDateValue dateConstraint){
		List<CPairAnswerCandidate> matched = new ArrayList<CPairAnswerCandidate>();
		
		for(CPairAnswerCandidate cpa: this.Context.QuestionAnwser.FilterCandidates){
			System.out.println("Time Constraint: " + dateConstraint.toString() + " ----  " + cpa.toString());
			if(dateConstraint.During(cpa.getStartDate(), cpa.getEndDate())){
				System.out.println("match ok!");
				this.AddMatch(matched, cpa);
			}
		}
		
		 this.Context.QuestionAnwser.FilterCandidates.clear();
		 this.Context.QuestionAnwser.FilterCandidates.addAll(matched);			
	}
	

	
	/** 
	* @Title: AddMatch 
	* @Description: add item to list
	* @param matched
	* @param item   
	*/
	void AddMatch(List<CPairAnswerCandidate> matched, CPairAnswerCandidate item){
		matched.add(item);
		
	}
	
	/** 
	* @Title: getLastestOne 
	* @Description: get lastest one from date constraint in the list
	* @param dateConstraint
	* @param matched
	* @return   
	*/
	private List<CPairAnswerCandidate> getLastestOne(CDateValue dateConstraint, List<CPairAnswerCandidate> matched) {
		// TODO Auto-generated method stub
		List<CPairAnswerCandidate> lastestAnswer=new ArrayList<CPairAnswerCandidate>();
		long lastestInternal = -1;
		for(CPairAnswerCandidate cpa : matched){
			long curInternal = dateConstraint.getIntervalDays(cpa.getStartDate(),cpa.getEndDate());
			if( lastestAnswer.size() < 1 || curInternal < lastestInternal ){
				lastestAnswer.clear();
				lastestAnswer.add(cpa);				
				lastestInternal = curInternal;
			}else if( curInternal == lastestInternal ){
				lastestAnswer.add(cpa);
			}
		}
		
		return lastestAnswer;
	}
	
}
