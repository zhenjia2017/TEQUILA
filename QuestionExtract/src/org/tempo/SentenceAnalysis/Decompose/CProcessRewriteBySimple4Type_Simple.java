package org.tempo.SentenceAnalysis.Decompose;

import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;

/** 
* @ClassName: CProcessRewriteBySimple4Type_Simple 
* @Description:  rewrite simple question by the method which have 4  rewrite types.
*  
*/
public class CProcessRewriteBySimple4Type_Simple extends CSentenceRewriteBase {
	
	protected CProcessRewriteBySimple4Type_Simple(){
		super(null);
		
	}	
	
	public CProcessRewriteBySimple4Type_Simple(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	
	}

	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		if( context == null || context.decomposeTree == null)
			return false;
		
		//if the question is a simple question, and has at least one date ,time or ordinal tag
		return( context.decomposeTree.Root.getReleationDescription().length()<1 && 
					(context.Dates.size()>0 || context.TimeReports.size()>0 || context.Ordinals.size()>0));
	}
	
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		this.Context.SubQuestions = this.CreateSubQuestions();
	}
	
	/** 
	* @Title: CreateSubQuestions 
	* @Description: remove time tags
	* @return   
	*/
	public CRewriteQuestionResult  CreateSubQuestions(){
		String subQuestion = this.RemoveDateOrdinalTagString(Context.Text);
		
		return this.CreateRewriteQuestions("Simple_Remove_TimeOrdinal", subQuestion);
	}
	
	/** 
	* @Title: CreateRewriteQuestions 
	* @Description: create rewrite result
	* @param qestionType
	* @param subQuestion
	* @return   
	*/
	CRewriteQuestionResult CreateRewriteQuestions(String qestionType, String subQuestion) {
		CRewriteQuestionResult rq = new CRewriteQuestionResult();
		rq.QuestionType = qestionType;
	
		//appned "?" at the end of question.
	    if(subQuestion.length()>0 && subQuestion.endsWith("?") == false){
			rq.FirstQuestion = subQuestion + "?";
		}else{
			rq.FirstQuestion = subQuestion;
		}

		return rq;
	}	

	
}
