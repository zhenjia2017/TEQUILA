package org.tempo.SentenceAnalysis.Decompose;

import java.util.List;

import org.tempo.SentenceAnalysis.CNEREntityTag;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;

import edu.stanford.nlp.ling.CoreLabel;

/** 
* @ClassName: CProcessRewriteBySimple4Type_Complex 
* @Description: rewrite complex (complete and incomplete ) question by the method which have 4  rewrite types.
*  
*/
public class CProcessRewriteBySimple4Type_Complex extends CSentenceRewriteBase {
	
	enum EnumSentenceTypeOfSimpleRule{COMPLETE_VERB_INTERNAL, COMPLETE_VERB_AFTER, INCOMPLETE_EVENT, INCOMPLETE_OTHER};

	protected CProcessRewriteBySimple4Type_Complex(){
		super(null);
		
	}	
	
	public CProcessRewriteBySimple4Type_Complex(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	
	}

	/**   
	 * <p>Title: CanProcess</p>   
	 * <p>Description: check if this process can deal with the context</p>   
	 * @param context
	 * @return   
	 * @see org.tempo.SentenceAnalysis.CNodeProcessorBase#CanProcess(java.lang.Object)   
	 */ 
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		if( context == null || context.decomposeTree == null)
			return false;
		
		if( context.WhenTags != null && context.WhenTags.size()>0)
			return false;
	
		//check the question is a complex question
		return( (DecomposeTree.STR_TE.compareTo(context.decomposeTree.Root.getReleationDescription()) == 0) ||
				( DecomposeTree.STR_TS.compareTo(context.decomposeTree.Root.getReleationDescription()) == 0));
	}
	
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		this.Context.SubQuestions = CreateSubQuestions();
	}
	
	/** 
	* @Title: CreateSubQuestions 
	* @Description: 
	* @return   
	*/
	public CRewriteQuestionResult  CreateSubQuestions(){
		//parse the question
		SentenceParseInfo parseInfo = this.Parse(this.Context.decomposeTree);
		
		//according the parse type, select function to rewrite question.
		switch(GetSentenceType(parseInfo)){
		case COMPLETE_VERB_AFTER:
			return this.CreateByCompleteAfter(parseInfo); //When_SimpleRewrite_Verb_AfterSignal
		case COMPLETE_VERB_INTERNAL:
			return this.CreateByCompleteInternal(parseInfo);//When_SimpleRewrite_Complete
		case INCOMPLETE_EVENT:
			return this.CreateByInCompleteEvent(parseInfo);//When_SimpleRewrite_InComplete_Event
		default:	
			return CreateByInComplete(parseInfo);//When_SimpleRewrite_InComplete
		}
	
	}
	
	/** 
	* @Title: GetSentenceType 
	* @Description: get the sentence type
	* @param parseInfo
	* @return   
	*/
	EnumSentenceTypeOfSimpleRule GetSentenceType(SentenceParseInfo parseInfo){
		//complete sentence
		if(DecomposeTree.STR_TS.compareTo(Context.decomposeTree.Root.getReleationDescription()) == 0){
			  int verbPos = parseInfo.VerbAfterSignal.getVerbFirstIndex();
			  return (( verbPos == parseInfo.signalIndexStart.index() + 1) ? 
					  EnumSentenceTypeOfSimpleRule.COMPLETE_VERB_AFTER : EnumSentenceTypeOfSimpleRule.COMPLETE_VERB_INTERNAL);
		}else{ //incomplete sentence
			return (IsTagPartAfterSignal(Context.Events,0.8f,parseInfo) ?
					 EnumSentenceTypeOfSimpleRule.INCOMPLETE_EVENT:  EnumSentenceTypeOfSimpleRule.INCOMPLETE_OTHER );
		}
	}
	
	/** 
	* @Title: Parse 
	* @Description: parse decomposition tree , and get sentence  information.
	* @param tree
	* @return   
	*/
	SentenceParseInfo Parse(DecomposeTree tree){
		//sentence basic analysis.
		SentenceParseInfo parseInfo =  this.BasicParse(tree, this.Context.Text);
		
		//get verb information
		List<VerbAuxInfo> verbs = this.GetVerbs(parseInfo.tokens, parseInfo.dependenciesGraph);		
		parseInfo.VerbBeforeSignal = this.GetVerbBefore(verbs, parseInfo.signalIndexStart.index());
		parseInfo.VerbAfterSignal = this.GetVerbAfter(verbs, parseInfo.signalIndexStart.index() );		
		
		return parseInfo;
	}
	
	/** 
	* @Title: CreateByCompleteInternal 
	* @Description: rewrite as complete sentence
	* @param parseInfo
	* @return   
	*/
	CRewriteQuestionResult   CreateByCompleteInternal(SentenceParseInfo parseInfo){
		String subQuestion = "When";
		
		subQuestion = ConnectString(subQuestion, parseInfo.Text.substring(this.getSignalEndPos(parseInfo)+1)) ;
		
		return this.CreateRewriteQuestionsSimpleRule("When_SimpleRewrite_Complete", subQuestion, parseInfo,true);
		
	}
	
	/** 
	* @Title: CreateByCompleteAfter 
	* @Description: rewrite question if verb  is after signal word.
	* @param parseInfo
	* @return   
	*/
	CRewriteQuestionResult   CreateByCompleteAfter(SentenceParseInfo parseInfo){
			String subQuestion = "When";
			
			//subQuestion = ConnectString(subQuestion, parseInfo.VerbBeforeSignal.getQuestionFirstVerb());	
			subQuestion = ConnectString(subQuestion, GetEntityTagBeforeSignal(parseInfo));
		
			subQuestion = ConnectString(subQuestion, parseInfo.Text.substring(this.getSignalEndPos(parseInfo)+1));


			return this.CreateRewriteQuestions("When_SimpleRewrite_Verb_AfterSignal", subQuestion, parseInfo);
	}
	
	/** 
	* @Title: GetEntityTagBeforeSignal 
	* @Description: if the second question part has no entity name, get entity in first part
	* @param parseInfo
	* @return   
	*/
	String GetEntityTagBeforeSignal(SentenceParseInfo parseInfo){
		if(Context.NEREntities.size() <= 0)
			return "";
		int signalChar = parseInfo.signalIndexStart.beginPosition()- 1;
		
		CNEREntityTag retTag = null;
		for(CNEREntityTag tag: Context.NEREntities){
			if( tag.getBeginPos() <= signalChar  ) { 
				if(tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Person ||
						tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Organization){
					retTag = tag;
				}else if( retTag == null){
					retTag = tag;
				}
			}
		}
		
		return (retTag == null?"":retTag.Text);
	}
	
	/** 
	* @Title: CreateByInComplete 
	* @Description: rewrite question as incompete 
	* @param parseInfo
	* @return   
	*/
	CRewriteQuestionResult CreateByInComplete(SentenceParseInfo parseInfo){
		String subQuestion = "When";

		CoreLabel verb;
		if(parseInfo.VerbBeforeSignal.auxWord != null){
			verb  = parseInfo.tokens.get(parseInfo.VerbBeforeSignal.auxWord.WordIndex-1);
		}else{
			verb  = parseInfo.tokens.get(parseInfo.VerbBeforeSignal.verbWord.WordIndex-1);
		}
		
		String str1 = parseInfo.Text.substring(verb.beginPosition(), parseInfo.signalIndexStart.beginPosition()).trim();
		String str2 = parseInfo.Text.substring(this.getSignalEndPos(parseInfo) + 1 ).trim();
		
		
		subQuestion = ConnectString(subQuestion, str2) ;
		subQuestion = ConnectString(subQuestion, str1) ;

		
		return this.CreateRewriteQuestionsSimpleRule("When_SimpleRewrite_InComplete", subQuestion, parseInfo, false);
		
	}
	
	/** 
	* @Title: CreateByInCompleteEvent 
	* @Description: rewrite for event
	* @param parseInfo
	* @return   
	*/
	CRewriteQuestionResult  CreateByInCompleteEvent(SentenceParseInfo parseInfo){	
		String subQuestion = "When";
		
		subQuestion = ConnectString(subQuestion, "did");	
		subQuestion = ConnectString(subQuestion, parseInfo.Text.substring(this.getSignalEndPos(parseInfo) + 1 ).trim());
		subQuestion = ConnectString(subQuestion, "occur");

		//subQuestion = this.RemoveDateOrdinalTagString(subQuestion);
		return this.CreateRewriteQuestions("When_SimpleRewrite_InComplete_Event", subQuestion, parseInfo);

	}	
	
	
	/** 
	* @Title: CreateRewriteQuestionsSimpleRule 
	* @Description: get rewrite result
	* @param qestionType
	* @param subQuestion
	* @param parseInfo
	* @param bNeedCoref
	* @return   
	*/
	CRewriteQuestionResult CreateRewriteQuestionsSimpleRule(String qestionType, String subQuestion,
												SentenceParseInfo parseInfo, boolean bNeedCoref) {
		CRewriteQuestionResult rq = new CRewriteQuestionResult();
		rq.QuestionType = qestionType;
		rq.SignalWord = parseInfo.signalWordString;  
		
		if(subQuestion.length() > 0){
			rq.FirstQuestion = parseInfo.Text.substring(0, parseInfo.signalIndexStart.beginPosition()).trim()+ "?";
			
		}else{
			rq.FirstQuestion = parseInfo.Text.trim();
		}
		
		rq.FirstQuestion = this.RemoveDateOrdinalTagString(rq.FirstQuestion);
		
		if(subQuestion.length()>0 && subQuestion.endsWith("?") == false){
			rq.SubQuestion = subQuestion.trim() + "?";
		}else{
			rq.SubQuestion = subQuestion.trim();
		}
		
		//rq.SubQuestion = this.RemoveDateOrdinalTagString(rq.SubQuestion);
		
		if(parseInfo.Coref != null && bNeedCoref==true){
			rq.SubQuestion = rq.SubQuestion.replace(" " + parseInfo.Coref.Source + " ", " " + parseInfo.Coref.CorefString + " ");
		}
		
		
		
		return rq;
	}	


}
