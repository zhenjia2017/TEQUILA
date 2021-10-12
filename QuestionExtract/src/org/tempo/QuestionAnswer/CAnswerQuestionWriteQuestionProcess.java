package org.tempo.QuestionAnswer;

import java.util.List;

import org.tempo.DataFileReaders.TextDataFileWriter;
import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;


public class CAnswerQuestionWriteQuestionProcess  extends CNodeProcessorBase<CSentenceAnalysisReport>{
	TextDataFileWriter qaServer;
	String writeToFile;
	CAnswerFilterContext queryQuestionContext;
	
	public CAnswerQuestionWriteQuestionProcess(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);		
		this.qaServer = new TextDataFileWriter();
		//this.writeToFile = writetoFile1;
	}
	
	public void OpenFile(String writetoFile){
		this.qaServer.AppendMode = true;
		this.qaServer.OpenFile(writetoFile);
	}
	
	public void CloseFile(){
		this.qaServer.Close();
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!= null && context.Document!=null);
	}

	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		
		CAnswerFilterContext answerCtx = new CAnswerFilterContext(this.Context );
		this.queryQuestionContext = answerCtx;
		if(answerCtx.IsTempoQuestion()){
			AnswerTempoQuestion(answerCtx);
		}else{
			AnswerOtherQuestion(answerCtx);
		}
		
	}

	protected void AnswerTempoQuestion(CAnswerFilterContext context) {
		String firstQuestion = this.ConstructQuestion(context.SentenceReport.getFirstQuestion(), this.getFirstQuestionType());
		this.qaServer.WriteLine(firstQuestion);
		
		//if context is question that contain a sub question, 
		//should answer sub-question first to get time constraint;		
		CLabelTag tag = null;
		if(this.queryQuestionContext.HasDateTag()){
			tag = GetTimeConstraint();
		}
		
		if (queryQuestionContext.hasSubQuestion()) {
			if (tag == null) {
				GetTimeConstraintFromSuQuestion();
			}
		}
		
	
	}
	
	
	
	protected void AnswerOtherQuestion(CAnswerFilterContext context) {		
		String question = this.ConstructQuestion(queryQuestionContext.SentenceReport.Text, 0);
		this.qaServer.WriteLine(question);
	}
	
	int getFirstQuestionType(){
		if(this.queryQuestionContext.HasDateTag() || this.queryQuestionContext.HasOrdinalTag() || this.queryQuestionContext.hasSubQuestion()){
			return 1;
		}else
			return 0;
	}
	
	private CLabelTag GetTimeConstraint() {
		// TODO Auto-generated method stub
		
		CLabelTag tag = null;		
		if(this.queryQuestionContext.SentenceReport.TimeReports.size() > 0){
			tag = this.getMostPossibleTag(this.queryQuestionContext.hasSubQuestion(), this.queryQuestionContext.SentenceReport.TimeReports);
		}
		return tag;
	}
	
	CLabelTag getMostPossibleTag(boolean afterSignal, List<? extends CLabelTag> tagSet){
		CLabelTag selectTag = null;
		
		for(CLabelTag tag : tagSet ){
			if( afterSignal){
				if( tag.IsTagStartAfterPos(this.queryQuestionContext.getSignalEndPos())){
					if(selectTag == null)
						selectTag = tag;
					else if (tag.getBeginPos()< selectTag.getBeginPos())
						selectTag = tag;
						
				}
			}else{
				if( tag.IsTagStartBeforePos(this.queryQuestionContext.getSignalBegingPos())){
					if(selectTag==null)
						selectTag = tag;
					else if(tag.getEndPos()> selectTag.getEndPos())
						selectTag = tag;
				}
			}
				
		}
		
		return selectTag;
		
	}		
	
	private void GetTimeConstraintFromSuQuestion() {
		// TODO Auto-generated method stub
		String question2= this.ConstructSQ2Question(queryQuestionContext.SentenceReport.SubQuestions.SubQuestion);
		this.qaServer.WriteLine(question2);
	}
	
	String ConstructQuestion(String sourceQuestion, int qaType){

		if(qaType == 0 && this.queryQuestionContext.IsWhenQuestion()){
				return this.ConstructWhenQuestion(sourceQuestion);
		}else if(qaType == 1){
			return this.ConstructSQ1Question(sourceQuestion);
		}else if(qaType == 2){
			return this.ConstructSQ2Question(sourceQuestion);
		}else{
			return sourceQuestion;
		}
			
	}
		
	String ConstructWhenQuestion(String question){
		return "###" + question.trim();
	}
	
	String ConstructSQ2Question(String question){
		return "|||" + question.trim();
	}
	
	String ConstructSQ1Question(String question){
		return "%%%" + question.trim();
	}	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
