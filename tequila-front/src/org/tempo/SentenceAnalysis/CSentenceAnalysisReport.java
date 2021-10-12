package org.tempo.SentenceAnalysis;

import java.util.*;


import org.tempo.SentenceAnalysis.Decompose.DecomposeTree;
import org.tempo.QuestionAnswer.CDataInProperty;
import org.tempo.SentenceAnalysis.Decompose.CRewriteQuestionResult;
import org.tempo.SentenceAnalysis.TimeTool.CTempralTag;
import org.tempo.Util.CGlobalConfiguration;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;


/** 
* @ClassName: CSentenceAnalysisReport 
* @Description: this class store all information in the process of parse ,analysis,rewrite.
*  
*/
public class CSentenceAnalysisReport implements IDocumentContext{
	public class QuestionAnswerRank{
		
		public String SQ1_Answer="";
		public String SQ1_Entity = "";
		public String SQ1_Predicate="";
		public String SQ1_SparqlQuery = "";		
		public String SQ1_SparqlUpdate="";
		public String SQ1_Relation = "";
		public String SQ1_BestDatePredicate="";
		public String SQ1_dateInCVT = "";
		public String SQ1_dateInProperty = "";
		public List<CDataInProperty> SQ1_DateInPpropertyCVT_List = new ArrayList<CDataInProperty>();
	}

	public class QuestionAnswerReport{
		
		public String SQ1="";
		public QuestionAnswerRank[] AnswerRank;
		public String SQ2="";
		public String SQ2_Answer="";	
		public String SQ2_Entity="";
		public String SQ2_Predicate="";
		public String SQ2_Sparql="";			
		public String SQ2_Relation="";
		public String SQ2_SparqlUpdate="";	
		
		public String GodenAnswer="";	
		public String TempoAnswer="";
		public String Reason="";
		public String TempoAnswerReason = "";
		public String SignalWord = "";
		public String TimeConstraint = "";
		public String TemporalExpression = "";
		
		public List<String> Answer;
		public List<String> QA_Answer;
		
		public int AnswerFromRank =0;
		
		public String AnswerChecking="";
		
		public QuestionAnswerReport(){
			
			this.AnswerRank = new QuestionAnswerRank[3];
			for(int i = 0; i<3; i++)
				this.AnswerRank[i] = new QuestionAnswerRank();
			
		}
		
		
		public QuestionAnswerRank getDefaultRank(int defaultRank){
			
			return this.AnswerRank[defaultRank-1];
		}
		
	}
	
	public QuestionAnswerReport  AnswerReport;
	
	public String PaserName;
	public String OriginalText;
	public String Text;
	public CoreMap  TextTokens;
	public Annotation Document;
	
	public List<String> ExplicitTemporalText;
	public List<String> ExplicitDateText;
	public List<String> WhenTagsText;
	public List<String> OrdinalsText;
	public String ImplicitTemporalText;
	public boolean RemoveDateTag;
	public boolean AlwaysAddNoDateAnswers;
	public boolean OnlyLatestOneByTimeFilter;
	public int SelectedRank;

	public SentenceSectionDescription baseSection;
	public List<SentenceSectionDescription > Sections;
	public List<CTempralTag>  TimeReports;	
	public List<CEventTag> Events;	
	public List<CNEREntityTag> NEREntities;
	public List<CLabelTag> Dates;
	public List<CLabelTag> Ordinals;
	public List<CLabelTag> WhenTags;
	
	public DecomposeTree  decomposeTree;
	
	public CRewriteQuestionResult  SubQuestions;
	
	public int ErrorCode;
	@Override
	public int GetErrorCode() {
		return this.ErrorCode;
	}
	
	public String ErrorInfo;

	@Override
	public String GetErrorInfo() {
		return this.ErrorInfo;
	}

	@Override
	public Annotation getDocument() {
		return this.Document;
	}

	@Override
	public String getSentence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CoreLabel> getTextTokens(int sentenceOrder ) {
		// TODO Auto-generated method stub
		if( this.Document != null)
			return NLPTool.GetTokens(this.Document, sentenceOrder);		

		return null;
	}
	
	
	public CSentenceAnalysisReport(){
		this("", true, false, false, 1);
	}
	
	public CSentenceAnalysisReport(String text,boolean RemoveDateTag, boolean AlwaysAddNoDateAnswers, boolean OnlyLatestOneByTimeFilter, int SelectedRank){
		this.Text = text;
		this.TextTokens = null;
		this.Sections = new ArrayList<SentenceSectionDescription >();
		this.PaserName ="";
		this.ErrorCode = 0;
		this.ErrorInfo ="";
		this.ImplicitTemporalText = "";
		this.OriginalText = this.Text;
		 
		this.SelectedRank = SelectedRank;
		
		this.ExplicitTemporalText = new ArrayList<String>();
		this.ExplicitDateText  = new ArrayList<String>();
		this.WhenTagsText = new ArrayList<String>();
		this.OrdinalsText = new ArrayList<String>();
		this.RemoveDateTag = RemoveDateTag;
		this.AlwaysAddNoDateAnswers = AlwaysAddNoDateAnswers;
		this.OnlyLatestOneByTimeFilter = OnlyLatestOneByTimeFilter;
		this.NEREntities = new ArrayList<CNEREntityTag>();
		this.Events = new ArrayList<CEventTag>();	
		this.Dates = new ArrayList<CLabelTag>();
		this.Ordinals = new ArrayList<CLabelTag>();
		this.TimeReports = new ArrayList<CTempralTag>();
		this.WhenTags = new ArrayList<CLabelTag>(); 		
		this.AnswerReport = new QuestionAnswerReport();
		this.AnswerReport.getDefaultRank(this.SelectedRank);
	}   
	
	
	/** 
	* @Title: SetAnswer 
	* @Description: set question answers
	* @param tempoAnswer
	* @param qaAnswer   
	*/
	public void SetAnswer(List<String> tempoAnswer, List<String> qaAnswer) {
		// TODO Auto-generated method stub
		this.AnswerReport.Answer = new ArrayList<String>();		
		if(tempoAnswer != null && tempoAnswer.size()>0 ){			
			this.AnswerReport.Answer.addAll(tempoAnswer);
		}
		
		this.AnswerReport.QA_Answer = new ArrayList<String>();
		if(qaAnswer != null && qaAnswer != null){
			this.AnswerReport.QA_Answer.addAll(qaAnswer);
		}
	}
	
	
	/** 
	* @Title: GetMatchedTimes 
	* @Description: get rule match times
	* @return   
	*/
	public int GetMatchedTimes(){
		if( this.Sections == null || this.Sections.size() < 1)
			return 0;
		
		int nTotal = 0;
		for(SentenceSectionDescription sec: this.Sections){
			if(sec.HasMatchedRule()) nTotal++;
		}
		return nTotal;
	}
	
	
	/** 
	* @Title: GetSentenceText 
	* @Description: get shaped string for question 
	* @return   
	*/
	public String GetSentenceText(){
		int nStart = this.Sections.get(0).Start;
		int nEnd = this.Sections.get(this.Sections.size() - 1 ).End;
		
		return (NLPTool.GetTextOfTokens(this.getTextTokens(0), nStart, nEnd)); 
	
	}
	
	/**   
	 * <p>Title: GetTextOfSection</p>   
	 * <p>Description:get string in a section </p>   
	 * @param sec
	 * @return   
	 * @see org.tempo.SentenceAnalysis.IDocumentContext#GetTextOfSection(org.tempo.SentenceAnalysis.SentenceSectionDescription)   
	 */ 
	public String GetTextOfSection(SentenceSectionDescription sec){
		if( this.Document == null)
			return "";
		
		return (NLPTool.GetTextOfTokens(this.getTextTokens(0), sec.Start, sec.End));
	}	
	
	
	
	/** 
	* @Title: HasSubQuestion 
	* @Description: has subquestion after rewrite
	* @return   
	*/
	public boolean HasSubQuestion(){
		
		return (this.SubQuestions!=null && this.SubQuestions.SubQuestion!=null);
	}
		
	
	/** 
	* @Title: IsTempoQuestion 
	* @Description: check it's a question for tempo system
	* @return   
	*/
	public boolean IsTempoQuestion() {
		// TODO Auto-generated method stub
		return (this.Ordinals.size()>0 || this.Dates.size()>0 || this.WhenTags.size() > 0
				|| this.TimeReports.size()>0 || this.HasSubQuestion());
	}
	
	/** 
	* @Title: GetDateText 
	* @Description: get temporal expression from original text
	* @return   
	*/
	public List<String> getDateText(){
		
		if( this.Dates.size()>0 )
			for (int i=0; i<this.Dates.size(); i++) {
				CLabelTag labelTag = this.Dates.get(i);
				if (labelTag.getBeginPos() >=0 && labelTag.getEndPos() >= 0) {
				this.ExplicitDateText.add(this.OriginalText.substring(labelTag.getBeginPos(), labelTag.getEndPos()));
			}
			}
			
		return this.ExplicitDateText;
	}

	/** 
	* @Title: GetExplicitTemporalText 
	* @Description: get temporal expression from original text
	* @return   
	*/
	

	public List<String> getExplicitTemporalText(){
		if( this.TimeReports.size()>0 ) {
			for (int i=0; i<this.TimeReports.size(); i++) {
				CLabelTag labelTag = this.TimeReports.get(i);
				if (labelTag.getBeginPos() >=0 && labelTag.getEndPos() >= 0) {
					this.ExplicitTemporalText.add(this.OriginalText.substring(labelTag.getBeginPos(), labelTag.getEndPos()));
			}
			}
		}
		
		else if (this.Dates.size()>0 ) {
			for (int i=0; i<this.Dates.size(); i++) {
				CLabelTag labelTag = this.Dates.get(i);
				if (labelTag.getBeginPos() >=0 && labelTag.getEndPos() >= 0) {
					this.ExplicitTemporalText.add(this.OriginalText.substring(labelTag.getBeginPos(), labelTag.getEndPos()));
		}
		}
	}
		return this.ExplicitTemporalText;
	}
	
	
	/** 
	* @Title: getOridnalText 
	* @Description: get oridnal expression from original text
	* @return   
	*/
	public List<String> getOridnalText(){
		
		if( this.Ordinals.size()>0 )
			for (int i=0; i<this.Ordinals.size(); i++) {
				CLabelTag labelTag = this.Ordinals.get(i);
				if (labelTag.getBeginPos() >= 0 && labelTag.getEndPos() >= 0) {
					this.OrdinalsText.add(this.OriginalText.substring(labelTag.getBeginPos(), labelTag.getEndPos()));
			}
			}
			
		return this.OrdinalsText;
	}
	/** 
	* @Title: GetWhenTag 
	* @Description: get temporal expression from original text
	* @return   
	*/
	
	
	
	public List<String> getWhenTagText(){
		
		if( this.WhenTags.size()>0 )
			for (int i=0; i<this.WhenTags.size(); i++) {
				CLabelTag labelTag = this.WhenTags.get(i);
				if (labelTag.getBeginPos() >=0 && labelTag.getEndPos() >= 0) {
				this.WhenTagsText.add(this.OriginalText.substring(labelTag.getBeginPos(), labelTag.getEndPos()));
			}
			}
		return this.WhenTagsText;
	}
	
	
	/** 
	* @Title: GetLastSection 
	* @Description: get last section
	* @return   
	*/
	public SentenceSectionDescription GetLastSection(){
		if( this.Sections.size() < 1)
			return null;
		else
			return this.Sections.get(this.Sections.size() - 1);
			
	}
	
	/**   
	 * <p>Title: getTextTokens</p>   
	 * <p>Description: get corelable in section </p>   
	 * @param sec
	 * @return   
	 * @see org.tempo.SentenceAnalysis.IDocumentContext#getTextTokens(org.tempo.SentenceAnalysis.SentenceSectionDescription)   
	 */ 
	public List<CoreLabel> getTextTokens(SentenceSectionDescription sec) {
		// TODO Auto-generated method stub
		return this.getTextTokens(0).subList(sec.Start, sec.End);
	}
	
	/** 
	* @Title: getToken 
	* @Description: get NLP token at a char position
	* @param charPos
	* @return   
	*/
	public int getToken(int charPos){
		return NLPTool.Locate(this.getTextTokens(0), charPos);
	}
	
	
	/** 
	* @Title: getFirstQuestion 
	* @Description: get first question in rewrite result
	* @return   
	*/
	public String getFirstQuestion(){
		if(this.SubQuestions != null ) {
			return this.SubQuestions.FirstQuestion;}
		else
			return this.Text;
	}
	
	/** 
	* @Title: getSecondQuestion 
	* @Description:  get second question in rewrite result
	* @return   
	*/
	public String getSecondQuestion(){
		if(this.SubQuestions != null && this.SubQuestions.SubQuestion!=null )
			return this.SubQuestions.SubQuestion;
		else
			return "";
		
	}

	

}


