package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;

import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.EnumTagType;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;
import org.tempo.SentenceAnalysis.TimeTool.CTempralTag;
import org.tempo.SentenceAnalysis.TimeTool.CTimeX3ToDateValue;

/** 
* @ClassName: CAnswerFilterContext 
* @Description: a context class for answer filter processes
*  
*/
public class CAnswerFilterContext{
	
	public CQAResult QuestionAnwser; //first question answer
	public CQAResult SubQuestionAnwser; //second question answer
	
	public CSentenceAnalysisReport SentenceReport; //related report
	public List<String> FinalAnswer; //final answer to report
	
	boolean _isSubQuestion;  //do this have a subquestion
	public boolean IsSubQuestion(){
		return this._isSubQuestion;
	}
	
	CDateValue _timeConstraint; //time constraint
	String _signalWord; //signal word
	
	
	/** 
	* @Title: setTimeConstraint 
	* @Description: add time constraint by Heilder time tag
	* @param accordingTag
	* @throws Exception   
	*/
	public void setTimeConstraint( CLabelTag accordingTag) throws Exception {
		if(accordingTag.TagType != EnumTagType.TimeHeilderTime)
			throw new Exception("Incorrect tag Type");
		
		//convert the tag to a CDateValue object
		this._timeConstraint =  CTimeX3ToDateValue.GetDateTimeValue( ( (CTempralTag) accordingTag).Time);
	}
	
	public void setTimeConstraint(CDateValue timeConstraint) {
		this._timeConstraint = timeConstraint;
	}
	public CDateValue getTimeConstraint(){
		return this._timeConstraint;
	}
	
	public void SetSignalWord(CLabelTag accordingTag){
		this._signalWord = this._getSignalWord(accordingTag);
	}
	
	public void SetSignalWord(String signalWord){
		this._signalWord = signalWord;
	}
	
	public String GetSignalWord(){
		return this._signalWord;
	}
	


	public CAnswerFilterContext( CSentenceAnalysisReport report){
		this.SentenceReport = report;
		this._isSubQuestion = false;
		this.FinalAnswer = new ArrayList<String>();
	}
	
	public CAnswerFilterContext(CSentenceAnalysisReport report, boolean isSubQuestion){
		this.SentenceReport = report;
		this._isSubQuestion = isSubQuestion;
		this.FinalAnswer = new ArrayList<String>();
	}	
	

	/** 
	* @Title: _getSignalWord 
	* @Description: get signal word string
	* @param tag
	* @return   
	*/
	String _getSignalWord(CLabelTag tag){
		CLabelTag dateTag = null;
		if(this.SentenceReport.Dates.size()>0)
			dateTag = this.findContainTag(this.SentenceReport.Dates, tag.Text);
		
		
		if(dateTag != null){
			String[] dates = dateTag.Text.split(" ");
			if(dates != null && dates.length>0)
				return dates[0];
		}
		

		return "during";		
	}
	
	/** 
	* @Title: findContainTag 
	* @Description: find a tag by a string
	* @param tags
	* @param word
	* @return   
	*/
	CLabelTag findContainTag(List<? extends CLabelTag> tags, String word){
		for(CLabelTag tag: tags){
			if(tag.Text.contains(word))
				return tag;
		}
		
		return null;
	}
	
	
	/** 
	* @Title: AddFinalAnswer 
	* @Description: append final answer
	* @param answer   
	*/
	public void AddFinalAnswer(String answer){
		this.FinalAnswer.add(answer);
	}
	
	/** 
	* @Title: getSignalBegingPos 
	* @Description: get signal begin char position
	* @return   
	*/
	public int getSignalBegingPos(){
		if(this.SentenceReport.SubQuestions != null){
			return this.SentenceReport.SubQuestions.SignalPos;
		}
		return -1;
	}
	
	
	/** 
	* @Title: getSignalEndPos 
	* @Description: get signal end char position
	* @return   
	*/
	public int getSignalEndPos(){
		if(this.SentenceReport.SubQuestions != null){
			return this.SentenceReport.SubQuestions.SignalPos + this.SentenceReport.SubQuestions.SignalWord.length();
		}
		return -1;
	}
	
	/** 
	* @Title: IsTempoQuestion 
	* @Description: check if a tempo question
	* @return   
	*/
	public boolean IsTempoQuestion() {
		// TODO Auto-generated method stub
		return (this.SentenceReport.IsTempoQuestion());
	}

	
	/** 
	* @Title: hasSubQuestion 
	* @Description: check if it has subquestion.
	* @return   
	*/
	public boolean hasSubQuestion(){
		return (this.SentenceReport.HasSubQuestion());
	}
	
	/** 
	* @Title: HasDateTag 
	* @Description: check if it has date tag.
	* @return   
	*/
	public boolean HasDateTag(){
		return (this.SentenceReport.TimeReports.size()>0 || this.SentenceReport.Dates.size()>0);//|| this.SentenceReport.Dates.size()>0);
	}
	
	/** 
	* @Title: HasOrdinalTag 
	* @Description: check if it has ordinal tag.
	* @return   
	*/
	public boolean HasOrdinalTag(){
		return (this.SentenceReport.Ordinals.size()>0 );
	}
	
	/** 
	* @Title: IsWhenQuestion 
	* @Description: check if it  is when question.
	* @return   
	*/
	public boolean IsWhenQuestion(){
		return (this.SentenceReport.WhenTags.size()>0);
	}
	
	/** 
	* @Title: getAnswersCount 
	* @Description: get answer count in current rank
	* @return   
	*/
	public int getAnswersCount(){
		if (this.QuestionAnwser == null || this.QuestionAnwser.get_currentDateRankSection() == null)
			return 0;
		else
			return this.QuestionAnwser.get_currentDateRankSection().getAnswers().size();
	}
	

	/** 
	* @Title: HasFilterResult 
	* @Description: check filter result has a answer
	* @return   
	*/
	public boolean HasFilterResult(){
		return (this.QuestionAnwser != null && this.QuestionAnwser.FilterCandidates.size() > 0);
	}
	
	/** 
	* @Title: IfGetQAAnswer 
	* @Description: check if QA has answers
	* @return   
	*/
	public boolean IfGetQAAnswer(){
		return (this.QuestionAnwser.getDate_in_propertySize()< 1 &&  this.QuestionAnwser.getAnswerSize() > 0);
	}
	
	/** 
	* @Title: HasDateInfo 
	* @Description: check if answer has Date in property
	* @return   
	*/
	public boolean HasDateInfo(){
		return (this.QuestionAnwser != null && this.QuestionAnwser.getDate_in_propertySize()>0);
	}
	
	/** 
	* @Title: HasFinalAnswer 
	* @Description: check if it has final answer.
	* @return   
	*/
	public boolean HasFinalAnswer(){
		return (this.FinalAnswer != null && this.FinalAnswer.size()>0);
	}
}



