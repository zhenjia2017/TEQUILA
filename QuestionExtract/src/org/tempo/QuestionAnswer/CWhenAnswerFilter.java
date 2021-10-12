package org.tempo.QuestionAnswer;

import org.tempo.SentenceAnalysis.INodeProcessor;

/** 
* @ClassName: CWhenAnswerFilter 
* @Description: filter answer for When question
*  
*/
public class CWhenAnswerFilter extends CAnswerFilterBase{
	public CWhenAnswerFilter(INodeProcessor<CAnswerFilterContext> next){
		super(next);
	}

	boolean IsMatchTypeQuestion(CAnswerFilterContext context){
		return (context.SentenceReport.WhenTags.size()>0);
	}	
	
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		//for when type question, it will ignore any sub-question answers.
		if( context.QuestionAnwser.FilterCandidates.size() > 0)
			this.Context.FinalAnswer.add(context.QuestionAnwser.FilterCandidates.get(0).GetAnswer());
	}
}