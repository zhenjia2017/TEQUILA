package org.tempo.QuestionAnswer;

import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;
import org.tempo.SentenceAnalysis.TimeTool.CTimeX3ToDateValue;

/** 
* @ClassName: CQAAnswerFilter 
* @Description: the class to send requestion to QA system
*  
*/
public class CQAAnswerFilter extends CAnswerFilterBase{
	
	IQAService _qaService;
	public IQAService getQAService(){
		return this._qaService;
	}
	
	
	public CQAAnswerFilter(IQAService qaService, INodeProcessor<CAnswerFilterContext> next){
		super(next);
		this._qaService = qaService;
	}

	
	
	
	@Override
	protected boolean CanProcess(CAnswerFilterContext context) {
		// TODO Auto-generated method stub
		if  ( context == null || context.SentenceReport ==  null  )
			return false;
		
		return true;
	}
	
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		if(context.IsTempoQuestion()){
			AnswerTempoQuestion(context);
		}else{
			AnswerOtherQuestion(context);
		}
			
	
	}
	
	protected void AnswerTempoQuestion(CAnswerFilterContext context) {
		
		//get first question
		String firstQuestion = this.ConstructQuestion(context.SentenceReport.getFirstQuestion(), this.getFirstQuestionType());
		
		//get answer 
		this.Context.QuestionAnwser = this.GetQuestionAnswer(firstQuestion);
		
		//if context has Date tag, use it as time constraint first;
		//else answer sub-question to a get time constraint;	
		
		
		CLabelTag tag = null;
		if(this.Context.HasDateTag()){
			tag = GetTimeConstraintTag();
		}
		
		
		if(tag!=null){
			try {
				this.Context.setTimeConstraint(tag);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (context.hasSubQuestion()) {
			if (tag == null) {
				this.Context.setTimeConstraint(GetTimeConstraintFromSubQuestion());
			}
			//get signal word
			this.Context.SetSignalWord(this.Context.SentenceReport.SubQuestions.SignalWord);
		} else{
			if (tag != null)
				this.Context.SetSignalWord(tag);
		}
	}
	
	/** 
	* @Title: AnswerOtherQuestion 
	* @Description: get answer of the question
	* @param context   
	*/
	protected void AnswerOtherQuestion(CAnswerFilterContext context) {		
		String question = this.ConstructQuestion(context.SentenceReport.Text, 0);
		this.Context.QuestionAnwser = this.GetQuestionAnswer(question);
	}
	
	/** 
	* @Title: GetTimeConstraint 
	* @Description: get Time 
	* @return   
	*/
	private CLabelTag GetTimeConstraintTag() {
		// TODO Auto-generated method stub
		
		CLabelTag tag = null;		
		if(this.Context.SentenceReport.TimeReports.size() > 0){
			tag = this.getMostPossibleTag(this.Context.hasSubQuestion(), this.Context.SentenceReport.TimeReports);
		}
//		}else if(this.Context.SentenceReport.Dates.size() > 0){
//			tag = this.getMostPossibleTag(this.Context.hasSubQuestion(), this.Context.SentenceReport.Dates);
//			System.out.println("&&&&&&---- Data Tag :  " + tag );
//		}
		return tag;
	}
		
	
	
	/** 
	* @Title: GetTimeConstraintFromSubQuestion 
	* @Description: get time constraint form sub question answer
	* @return   
	*/
	private CDateValue GetTimeConstraintFromSubQuestion() {
		// TODO Auto-generated method stub
		CQAResult result = this.GetQuestionAnswer(
				this.ConstructQuestion(Context.SentenceReport.SubQuestions.SubQuestion, 2) );
		
		this.Context.SubQuestionAnwser=result;
		
		if (result != null && result.getCurrentOrDefaultDateRankSection().getAnswers().size() > 0){
			String dt = result.getCurrentOrDefaultDateRankSection().getAnswers().get(0).Answer;
			return this.getSQ2AnswerToDate(dt);
		}
		
		return null;
		
	}
	
	
	/** 
	* @Title: getSQ2AnswerToDate 
	* @Description: convert string to CDateValue
	* @param strDate
	* @return   
	*/
	CDateValue  getSQ2AnswerToDate(String strDate){
		int pos = strDate.indexOf(",");
		
		if(pos<0){
			return CTimeX3ToDateValue.GetDateTimeValue(strDate);
		}else{			
			CDateValue dt1 = CTimeX3ToDateValue.GetDateTimeValue(strDate.substring(0,  pos));
			CDateValue dt2  = CTimeX3ToDateValue.GetDateTimeValue(strDate.substring(pos+1));
			
			if(CDateValue.Before(dt1, dt2)){
				return (new CDateValue(dt1.getStartDate(), dt2.getEndDate()));
			}else{
				return (new CDateValue(dt2.getStartDate(), dt1.getEndDate()));
			}			
			
		}
	}


	/** 
	* @Title: GetQuestionAnswer 
	* @Description: get question answer
	* @param question
	* @return   
	*/
	CQAResult GetQuestionAnswer(String question){
		System.out.println("Ask Question: "+ question);
		//String queryQuestion = this.Context.IsWhenQuestion()? ConstructWhenQuestion(question) : question;
		return (this.getQAService().QueryAnswer(question));
	}
	
	/** 
	* @Title: getFirstQuestionType 
	* @Description: get first question type
	* @return  1: date, ordinal or has sub question. 0: others. 
	*/
	int getFirstQuestionType(){
		if(this.Context.HasDateTag() || this.Context.HasOrdinalTag() || this.Context.hasSubQuestion()){
			return 1;
		}else
			return 0;
	}
	
	/** 
	* @Title: ConstructQuestion 
	* @Description: construct question string
	* @param sourceQuestion
	* @param qaType
	* @return   
	*/
	String ConstructQuestion(String sourceQuestion, int qaType){

		if(qaType == 0 && this.Context.IsWhenQuestion()){
				return this.ConstructWhenQuestion(sourceQuestion);
		}else if(qaType == 1){
			return this.ConstructSQ1Question(sourceQuestion);
		}else if(qaType == 2){
			return this.ConstructSQ2Question(sourceQuestion);
		}else{
			return sourceQuestion;
		}
			
	}
		
	/** 
	* @Title: ConstructWhenQuestion 
	* @Description: add "###" at the beginning
	* @param question
	* @return   
	*/
	String ConstructWhenQuestion(String question){
		return "###" + question.trim();
	}
	
	/** 
	* @Title: ConstructSQ2Question 
	* @Description: add "|||" at the beginning
	* @param question
	* @return   
	*/
	String ConstructSQ2Question(String question){
		return "|||" + question.trim();
	}
	
	/** 
	* @Title: ConstructSQ1Question 
	* @Description: add "%%%" at the beginning
	* @param question
	* @return   
	*/
	String ConstructSQ1Question(String question){
		return "%%%" + question.trim();
	}
}
