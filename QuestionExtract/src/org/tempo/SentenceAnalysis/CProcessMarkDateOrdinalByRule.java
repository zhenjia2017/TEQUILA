package org.tempo.SentenceAnalysis;

import org.tempo.SentenceAnalysis.Decompose.CQuestionSentenceJudge;
import org.tempo.SentenceAnalysis.Decompose.CTokenRule;

/** 
* @ClassName: CProcessMarkDateOrdinalByRule 
* @Description: the process node get Date, ordinal Tag by pattern rule
*  
*/
public class CProcessMarkDateOrdinalByRule extends CNodeProcessorBase<CSentenceAnalysisReport>{


	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!= null && context.Document!=null);
	}

	public CProcessMarkDateOrdinalByRule(){
		super(null);
	}
	
	public CProcessMarkDateOrdinalByRule(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	}
	
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		getDateAndOrdinalTags();
		

	}

	/** 
	* @Title: getDateAndOrdinalTags 
	* @Description: filter date and ordinal tags   
	*/
	void getDateAndOrdinalTags(){
		CQuestionSentenceJudge judge = CQuestionSentenceJudge.GetSentenceClassfication(CTokenRule.STR_Date_Ordinal);
		CSentenceAnalysisReport r = judge.MatchSentence(this.Context.getTextTokens(0));
		
	
		if(r.Sections != null && r.Sections.size()>0){
			for(SentenceSectionDescription sec: r.Sections){
					if(CTokenRule.IsRuleOf(sec.RegexRule, CTokenRule.STR_Date)){
						
						this.Context.Dates.add( 
								CLabelTag.CreateLabelTag(EnumTagType.DateFromRule, sec, Context.Text, Context.getTextTokens(0)));
				
					}else if(CTokenRule.IsRuleOf(sec.RegexRule, CTokenRule.STR_Ordinal)){
						
						this.Context.Ordinals.add( 
								CLabelTag.CreateLabelTag(EnumTagType.OrdinalFromRule,sec, Context.Text, Context.getTextTokens(0)));
				}					
			}
		}
	}	
	

}
