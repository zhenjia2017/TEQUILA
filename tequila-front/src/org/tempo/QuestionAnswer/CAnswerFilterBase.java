package org.tempo.QuestionAnswer;

import java.util.List;

import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.INodeProcessor;


/** 
* @ClassName: CAnswerFilterBase 
* @Description: a base class to send  question to QA system ,and filter answers 
*/
public class CAnswerFilterBase  extends CNodeProcessorBase<CAnswerFilterContext>{

	
	public CAnswerFilterBase(INodeProcessor<CAnswerFilterContext> next){
		super(next);
	}
	
	@Override
	protected boolean CanProcess(CAnswerFilterContext context) {
		// TODO Auto-generated method stub
		if  ( context == null || context.SentenceReport ==  null || context.QuestionAnwser == null  )
			return false;
		
		return (IsMatchTypeQuestion(context));
	}
	
	boolean IsMatchTypeQuestion(CAnswerFilterContext context){
		return false;
	}
	
	
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		
	}
	
	
	/** 
	* @Title: getMostPossibleTag 
	* @Description: get tag near the signal.
	* @param afterSignal
	* @param tagSet
	* @return   
	*/
	CLabelTag getMostPossibleTag(boolean afterSignal, List<? extends CLabelTag> tagSet){
		CLabelTag selectTag = null;
		
		for(CLabelTag tag : tagSet ){
			if( afterSignal){
				if( tag.IsTagStartAfterPos(this.Context.getSignalEndPos())){
					if(selectTag == null)
						selectTag = tag;
					else if (tag.getBeginPos()< selectTag.getBeginPos())
						selectTag = tag;
						
				}
			}else{
				if( tag.IsTagStartBeforePos(this.Context.getSignalBegingPos())){
					if(selectTag==null)
						selectTag = tag;
					else if(tag.getEndPos()> selectTag.getEndPos())
						selectTag = tag;
				}
			}
				
		}
		
		return selectTag;
		
	}	
	
	/** 
	* @Title: PrepareFilterCandidates 
	* @Description: clean filter buffer.   
	*/
	void PrepareFilterCandidates(){
			Context.QuestionAnwser.FilterCandidates.clear();
			Context.QuestionAnwser.FilterCandidates.addAll(Context.QuestionAnwser.PairCandidates);
	}

}
	