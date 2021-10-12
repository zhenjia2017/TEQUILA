package org.tempo.SentenceAnalysis;

import java.util.List;
import org.tempo.SentenceAnalysis.TimeTool.CTempralTag;
import org.tempo.SentenceAnalysis.TimeTool.TempralTagType;
import org.tempo.Util.COutputToConsole;
import org.tempo.Util.IOutputString;


/** 
* @ClassName: CReportOutput 
* @Description: output CSentenceAnalysisReport information
*  
*/
public class CReportOutput extends CNodeProcessorBase<CSentenceAnalysisReport>{

	IOutputString outputHandle;
	
	public void setOutputHandle(IOutputString outputHandle) {
		this.outputHandle = outputHandle;
	}

	public CReportOutput(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
		
		this.outputHandle = new COutputToConsole(null); //default output is console 
		
	}
	
	/** 
	* @Title: Write 
	* @Description: write a string
	* @param str   
	*/
	public void Write(String str){
		this.outputHandle.Write(str);
	}
	
	/** 
	* @Title: WriteLine 
	* @Description: write a string with line ending
	* @param str   
	*/
	public void WriteLine(String str){
		this.outputHandle.WriteLine(str);
	}
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		PrintReport(context);
	}
	
	
	/** 
	* @Title: PrintReport 
	* @Description: output report
	* @param rp   
	*/
	protected void PrintReport(CSentenceAnalysisReport rp){
		if(rp == null)	return ;
		

//		OutputQuestion(rp);		
		OutputLabelTag(rp.Events,"Event");
		OutputNEREntity(rp.NEREntities);
	    OutputTempralTag(rp.TimeReports);		
		OutputLabelTag(rp.Dates,"Time");
		OutputLabelTag(rp.Ordinals,"ORDINAL");
		
		
		OutputAnswer(rp);
		//OutputQASystemAnswer(rp);
		//OutputCandidateAnswer(rp);
		
		OutputQuestionType(rp);		
	}
	
	
	/** 
	* @Title: OutputAnswer 
	* @Description: output answers.
	* @param rp   
	*/
	protected void OutputAnswer(CSentenceAnalysisReport rp){
		//if(rp.AnswerReport.GodenAnswer != null)
		WriteLine("Gold answer: " + rp.AnswerReport.GodenAnswer );
	//	WriteLine("temporal predicate of sq1 answer:" + rp.AnswerReport.Top1Similarity );
		
		Write("QUINT+TEQUILA answer: ");
		WriteLine(this.getTempoAnswer(rp));
//		WriteLine("Reason for Errors: " + rp.AnswerReport.Reason );
//		WriteLine("Process:" + (rp.IsTempoQuestion()? "TEMPO" : " NOT TEMPO"));
		
		if(rp.SubQuestions != null ){
			//ZHEN change the code		    
			WriteLine("Sub-question 1: " + rp.AnswerReport.SQ1 );
			WriteLine("Sub-question 1 SPARQL query: " + rp.AnswerReport.getDefaultRank().SQ1_SparqlQuery );
			WriteLine("Sub-question 1 predicate: " + rp.AnswerReport.getDefaultRank().SQ1_Predicate );
			
			WriteLine("Sub-question 1 best date predicate: " + rp.AnswerReport.getDefaultRank().SQ1_BestDatePredicate );
			WriteLine("Sub-question 1 answer: " + rp.AnswerReport.getDefaultRank().SQ1_Answer );
		//	WriteLine("Sq1 Predicate2:" + rp.AnswerReport.SQ1_predicate2 );
		//	WriteLine("Sq1 Answer2:" + rp.AnswerReport.SQ1_Answer2 );
		//	WriteLine("Sq1 Predicate3:" + rp.AnswerReport.SQ1_predicate3 );
		//	WriteLine("Sq1 Answer3:" + rp.AnswerReport.SQ1_Answer3 );	
			if(rp.SubQuestions.SubQuestion != null){
				WriteLine("Sub-question 2: " + rp.AnswerReport.SQ2 );
				WriteLine("Sub-question 2 SPARQL query: " + rp.AnswerReport.SQ2_Sparql );
				WriteLine("Sub-question 2 predicate: " + rp.AnswerReport.SQ2_Predicate );
				WriteLine("Sub-question 2 answer: " + rp.AnswerReport.SQ2_Answer );
			}
			else{
				WriteLine("Sub-question 2: " + "NULL" );
				WriteLine("Sub-question 2 SPARQL query: " + "NULL" );
				WriteLine("Sub-question 2 predicate: " + "NULL" );
				WriteLine("Sub-question 2 answer: " + "NULL" );
			}
		}else{
			WriteLine("Sub-question 1: " + rp.AnswerReport.SQ1 );
			WriteLine("Sub-question 1 SPARQL query: " + rp.AnswerReport.getDefaultRank().SQ1_SparqlQuery);
			WriteLine("Sub-question 1 predicate: " + rp.AnswerReport.getDefaultRank().SQ1_Predicate );
			WriteLine("Sub-question 1 best date predicate: " + "" );
			WriteLine("Sub-question 1 answer: " + rp.AnswerReport.getDefaultRank().SQ1_Answer );
			WriteLine("Sub-question 2: " + "NULL" );
			WriteLine("Sub-question 2 SPARQL query: " + "NULL" );
			WriteLine("Sub-question 2 predicate: " + "NULL" );
			WriteLine("Sub-question 2 answer: " + "NULL" );			
		}
		
		//if(rp.AnswerReport.GodenAnswer != null)
		WriteLine("Gold Answer:" + rp.AnswerReport.GodenAnswer );
	//	WriteLine("temporal predicate of sq1 answer:" + rp.AnswerReport.Top1Similarity );
		Write("TEMPO Answer:");
		WriteLine(this.getTempoAnswer(rp));
		WriteLine("Reason Filter: " + rp.AnswerReport.Reason );
		WriteLine("Tempo Answer Reason: " + rp.AnswerReport.TempoAnswerReason );
		WriteLine("Process:" + (rp.IsTempoQuestion()? "TEMPO" : " NOT TEMPO"));
	}	


	/** 
	* @Title: OutputQASystemAnswer 
	* @Description: output QA answers
	* @param rp   
	*/
	protected void OutputQASystemAnswer(CSentenceAnalysisReport rp){
		WriteLine("QA System Answer:" + (rp.AnswerReport.QA_Answer!=null? rp.AnswerReport.QA_Answer: ""));
	}
	
	/** 
	* @Title: getTempoAnswer 
	* @Description: output tempo answers
	* @param rp
	* @return   
	*/
	protected String getTempoAnswer(CSentenceAnalysisReport rp){
		StringBuilder buf = new StringBuilder();
		StringBuilder bufLowCase = new StringBuilder();
		
		if(rp.AnswerReport.Answer != null && rp.AnswerReport.Answer.size() > 0){
			buf.append("[" );
			for(int i = 0; i < rp.AnswerReport.Answer.size(); i++){
				String item = getNameString(rp.AnswerReport.Answer.get(i).toString());
				if(bufLowCase.indexOf(item.toLowerCase())>=0) {//if find dulpicate item in buffer, don't add it;
					System.out.println("Duplicate answer : " + item);
					continue;
				}
				
				String itemString = "\"" + item + "\"";
				
				bufLowCase.append(itemString.toLowerCase());
				buf.append(itemString);
				
				if(i < (rp.AnswerReport.Answer.size()-1)){
					bufLowCase.append(",");
					buf.append(",");						
				}
			}

			buf.append("]");
		}
		
		return buf.toString();
	}
	
	

	private String getNameString(String item) {
		// TODO Auto-generated method stub
		if(item == null || item.length() < 1)
			return "";
		
		int pos1 = item.lastIndexOf("(");
		if( pos1>0)
			return item.substring(0,pos1);
		else
			return item;
		
	}

	/** 
	* @Title: OutputQuestionType 
	* @Description: output question type information.
	* @param rp   
	*/
	protected void OutputQuestionType(CSentenceAnalysisReport rp){
		WriteLine("QuestionType:" + getQuestionTypeString(rp));
	}

	/** 
	* @Title: getQuestionTypeString 
	* @Description: get question type string by the report
	* @param rp
	* @return   
	*/
	protected String getQuestionTypeString(CSentenceAnalysisReport rp){
		StringBuilder strQAType = new StringBuilder();
		if( rp.Dates.size()>0 || rp.TimeReports.size()>0)
			strQAType.append("Explicit; ");//("TIMEX3; ");
		
		if(rp.WhenTags.size()>0)
			strQAType.append("Temp.Ans; ");//("WHEN; ");		
		
		if(rp.HasSubQuestion())
			strQAType.append("Implicit; ");//("SQ2; ");			

		if(rp.Ordinals.size()>0)
			strQAType.append("Ordinal; ");//("ORDINAL; ");			
		
//		
//		if(rp.Dates.size() > 0)
//			strQAType.append("DATE; ");
//		
//		if(rp.TimeReports.size()>0)
//			strQAType.append("TIMEX3; ");	
//		
//		if(rp.Ordinals.size()>0)
//			strQAType.append("ORDINAL; ");			
//		
//		if(rp.Events.size()>0)
//			strQAType.append("EVENT; ");	
//		
//		if(rp.WhenTags.size()>0)
//			strQAType.append("WHEN; ");	
//		
//		if(rp.SubQuestions != null ){
//			strQAType.append("SIGNAL(");
//			strQAType.append(rp.SubQuestions.QuestionType);
//			strQAType.append(")");
//		}
		
		return strQAType.toString().trim();
	}
	
	/** 
	* @Title: OutputQuestion 
	* @Description: output rewrite  information
	* @param rp   
	*/
	protected void OutputQuestion(CSentenceAnalysisReport rp){
		if( rp.SubQuestions == null){
			WriteLine("Question:" + rp.Text);
		}else{
			if(rp.SubQuestions.FirstQuestion != null && rp.SubQuestions.FirstQuestion.length() > 0)
				WriteLine("SubQuestion1:" + rp.SubQuestions.FirstQuestion);
			
			if(rp.SubQuestions.SignalWord != null && rp.SubQuestions.SignalWord.length() > 0)
				WriteLine("SignalWord:" + rp.SubQuestions.SignalWord);
			
			if(rp.SubQuestions.SubQuestion != null && rp.SubQuestions.SubQuestion.length() > 0)
				WriteLine("SubQuestion2:" + rp.SubQuestions.SubQuestion);
		}
		
	}
	
	 /** 
	* @Title: OutputTempralTag 
	* @Description: output temparal tags
	* @param tags   
	*/
	void OutputTempralTag(List<CTempralTag> tags){
		if(tags.size()<1)
			return;
//		for(CTempralTag tg: tags){
//			WriteLine(tg.toString());		
//		}
		Write("TIMEX3:");
		String strout ;
		for(CTempralTag tg: tags){
			if(tg.TagType == TempralTagType.Interval)
				strout = String.format("<%s (%s : %s)>",tg.Text, tg.BeginTime, tg.EndTime);		
			else
				strout = String.format("<%s (%s) >",tg.Text, tg.Time);	
			
			Write( strout );
			Write(" ");
			
		}
		WriteLine("");
	}
	
	/** 
	* @Title: OutputLabelTag 
	* @Description: output lable 
	* @param tags
	* @param tagName   
	*/
	void OutputLabelTag(List<? extends CLabelTag> tags, String tagName){
		if(tags==null || tags.size()<1)
			return;
		
		Write( tagName );
		Write( ":" );
		for(CLabelTag tag: tags){
			Write( "[");
			Write(tag.Text);
			Write( "]");
			Write(" ");
		}
		WriteLine("");
	}

	/** 
	* @Title: OutputNEREntity 
	* @Description: output entity
	* @param tags   
	*/
	void OutputNEREntity(List<CNEREntityTag> tags){
		if(tags.size() < 1) return;
			
		Write("Entity:");
		for(CNEREntityTag tag:tags){
			String str = String.format("<%s:%s>",tag.NERObjectType, tag.Text);
			Write(str);
			Write(" ");
		}
		WriteLine("");
	}

	
}





