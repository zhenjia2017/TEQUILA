package org.tempo.SentenceAnalysis;

import org.tempo.SentenceAnalysis.Decompose.CQuestionSentenceJudge;
import org.tempo.SentenceAnalysis.Decompose.CTokenRule;

/** 
* @ClassName: CProcessMarkWhenTag 
* @Description: this process node is to get all When tag which is at the beginning of the sentence.
*  
*/
public class CProcessMarkWhenTag extends CNodeProcessorBase<CSentenceAnalysisReport>{


		@Override
		protected boolean CanProcess(CSentenceAnalysisReport context) {
			// TODO Auto-generated method stub
			return (context!= null && context.Document!=null && context.WhenTags.size() < 1);
		}


		public CProcessMarkWhenTag(INodeProcessor<CSentenceAnalysisReport> next){
			super(next);
		}
		
		
		@Override
		protected void NodeProcess(CSentenceAnalysisReport context) {
			// TODO Auto-generated method stub
			//check if sentence is When question
			CQuestionSentenceJudge judge = CQuestionSentenceJudge.GetSentenceClassfication(CTokenRule.STR_When);
			CSentenceAnalysisReport r = judge.MatchSentence(this.Context.getTextTokens(0));
			if(r.Sections != null && r.Sections.size() < 1)
				return;
			
			//filter and get when tags
			for(SentenceSectionDescription sec: r.Sections){
				if(CTokenRule.IsRuleOf(sec.RegexRule, CTokenRule.STR_When)){
					this.Context.WhenTags.add( 
						CLabelTag.CreateLabelTag(EnumTagType.When, sec,Context.Text, Context.getTextTokens(0)));
				}
			}

		}	
		


}
