package org.tempo.QuestionAnswer;

import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;



/** 
* @ClassName: CAnswerQuestionProcess 
* @Description: the class is to ask QA questions and filter answers from QA
*  
*/
public class CAnswerQuestionProcess extends CNodeProcessorBase<CSentenceAnalysisReport>{
	IQAService qaServer;
	CAnswerSelectForTempoPolicy qaAnswerHandle;  // handlers for tempo question 
	CAnswerSelectPolicyBase otherAnswerHandle;  //handlers for non-tempo question
	

	
	public CAnswerQuestionProcess(String service, INodeProcessor<CSentenceAnalysisReport> next){
		super(next);		
		this.qaServer = CQAServiceFactory.CreateQAService(service); //create QA service
		InitQuestionAnswerHandle(this.qaServer);
	}
		
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!= null && context.Document!=null);
	}

	/** 
	* @Title: LoadPairMapFile 
	* @Description: Load  pair names for answer.
	* @param file   
	*/
	public void LoadPairMapFile(String file){
		this.qaAnswerHandle.LoadPairDictionFile(file);
	}


	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		CAnswerFilterContext answerCtx = new CAnswerFilterContext(this.Context );

		//select tempo question handlers to deal with the context
		if(answerCtx.IsTempoQuestion()){ 
			this.qaAnswerHandle.QueryAnswer(answerCtx);
		}else{
			this.otherAnswerHandle.QueryAnswer(answerCtx);
		}
		
		//write report to context;
		WriteAnswerReport(answerCtx);
	}
	
	/** 
	* @Title: WriteAnswerReport 
	* @Description: output answers to context
	* @param answerCtx   
	*/
	void WriteAnswerReport(CAnswerFilterContext answerCtx){
		this.Context.AnswerReport.SQ1 = this.Context.getFirstQuestion();
		this.Context.AnswerReport.TimeConstraint = this.GetQuestionTimeConstraint(answerCtx);
		if(this.Context.SubQuestions != null)
			this.Context.AnswerReport.SignalWord = this.Context.SubQuestions.SignalWord;
		
		if(answerCtx.QuestionAnwser != null){			
			this.WriteRank(answerCtx.QuestionAnwser.AnswerRanks[0], this.Context.AnswerReport.AnswerRank[0]);
			
			if(answerCtx.QuestionAnwser.AnswerRanks[1] != null){
				this.WriteRank(answerCtx.QuestionAnwser.AnswerRanks[1], this.Context.AnswerReport.AnswerRank[1]);			}
			
			if(answerCtx.QuestionAnwser.AnswerRanks[2] != null){
				this.WriteRank(answerCtx.QuestionAnwser.AnswerRanks[2], this.Context.AnswerReport.AnswerRank[2]);
			}		
		}
		
		if(this.Context.HasSubQuestion()){
			this.Context.AnswerReport.SQ2 = this.Context.getSecondQuestion();
			this.Context.AnswerReport.SQ2_Answer =this.GetQuestionTimeConstraint(answerCtx);
			this.Context.AnswerReport.SQ2_Predicate = this.GetSubQuestionPredicate(answerCtx);
			this.Context.AnswerReport.SQ2_Relation = this.GetSubQuestionRelation(answerCtx);
			this.Context.AnswerReport.SQ2_Sparql = this.GetSubQuestionSparql(answerCtx);
			this.Context.AnswerReport.SQ2_Entity = this.GetSubQuestionEntity(answerCtx);
			this.Context.AnswerReport.SQ2_SparqlUpdate = this.GetSubQuestionSparqlUpdate(answerCtx);
			

		}
		this.GetTempoAnswerReason(answerCtx);		
		
		this.Context.SetAnswer(answerCtx.FinalAnswer, null);
	
	}
	
	void WriteRank(AnswerDataSection rankSrc, CSentenceAnalysisReport.QuestionAnswerRank dstOutRank){
		dstOutRank.SQ1_Answer = rankSrc.getQAAnswerString();
		dstOutRank.SQ1_SparqlQuery =rankSrc.SPARQL;
		dstOutRank.SQ1_Predicate =rankSrc.getPredicate();
		dstOutRank.SQ1_BestDatePredicate = rankSrc.getBestPredicate();
		dstOutRank.SQ1_dateInCVT =rankSrc.getDateInCVTString();
		dstOutRank.SQ1_dateInProperty =rankSrc.getDateInPropertyString();
		dstOutRank.SQ1_DateInPpropertyCVT_List.clear();
		dstOutRank.SQ1_DateInPpropertyCVT_List.addAll(rankSrc.getDate_in_property());
		dstOutRank.SQ1_Entity =rankSrc.getEntity();
		dstOutRank.SQ1_Relation =rankSrc.getRelation();
		dstOutRank.SQ1_SparqlUpdate =rankSrc.getUpdateSPARQL();		
	}
	

	/** 
	* @Title: GetAnswerReason 
	* @Description: get reason of the answer
	* @param answerCtx   
	*/
	void GetTempoAnswerReason(CAnswerFilterContext answerCtx){
		if(answerCtx.QuestionAnwser == null || (answerCtx.QuestionAnwser.HasQASystemAnswer() == false)){
			this.Context.AnswerReport.TempoAnswerReason="no answer" ;		
		}else if(answerCtx.QuestionAnwser.HasDateCandidate()  && answerCtx.HasFinalAnswer() == false){
			this.Context.AnswerReport.TempoAnswerReason="not satisfy constraint";			
		}else if(answerCtx.QuestionAnwser.HasDateCandidate() == false){
			this.Context.AnswerReport.TempoAnswerReason="no date of answer";			
		}else{
			this.Context.AnswerReport.TempoAnswerReason="";
		}
		
	}
	/** 
	* @Title: GetSubQuestionPredicate 
	* @Description: get predicate of sub question
	* @param answerCtx
	* @return   
	*/
	private String GetSubQuestionPredicate(CAnswerFilterContext answerCtx){
		if(answerCtx.SubQuestionAnwser != null){
			return	answerCtx.SubQuestionAnwser.getPredicate();
		}
		return "";
	}
	
	
	private String GetSubQuestionRelation(CAnswerFilterContext answerCtx){
		if(answerCtx.SubQuestionAnwser != null){
			return	answerCtx.SubQuestionAnwser.getRealtion();
		}
		return "";
	}

	
	private String GetSubQuestionEntity(CAnswerFilterContext answerCtx){
		if(answerCtx.SubQuestionAnwser != null){
			return	answerCtx.SubQuestionAnwser.getEntity();
		}
		return "";
	}
	
	private String GetSubQuestionSparql(CAnswerFilterContext answerCtx){
		if(answerCtx.SubQuestionAnwser != null){
			return	answerCtx.SubQuestionAnwser.getSparql();
		}
		return "";
	}
	
	private String GetSubQuestionSparqlUpdate(CAnswerFilterContext answerCtx){
		if(answerCtx.SubQuestionAnwser != null){
			return	answerCtx.SubQuestionAnwser.getSparqlUpdate();
		}
		return "";
	}

	/** 
	* @Title: GetSubQuestionAnswer 
	* @Description: get time constraint of the question
	* @param answerCtx
	* @return   
	*/
	private String GetQuestionTimeConstraint(CAnswerFilterContext answerCtx){
		 CDateValue dt = answerCtx.getTimeConstraint();
		 if(dt != null)
			 return dt.toString();
		 
		 return "";
	}
	
	/** 
	* @Title: InitQuestionAnswerHandle 
	* @Description: create tempo and non-tempo question handlers.
	*/
	private  void	InitQuestionAnswerHandle(IQAService qaServer1){
		CQAAnswerFilter qaService = new CQAAnswerFilter(qaServer1, null);		
		this.qaAnswerHandle = new  CAnswerSelectForTempoPolicy (qaService );			
		this.otherAnswerHandle =  new  CAnswerSelectPolicyForNotTempo ( qaService);
	}
	

}





