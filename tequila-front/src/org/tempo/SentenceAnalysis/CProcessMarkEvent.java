package org.tempo.SentenceAnalysis;

import java.util.List;

import org.tempo.SentenceAnalysis.Dictionary.*;


/** 
* @ClassName: CProcessMarkEvent 
* @Description: this process get all Event tag from sentence.
*  
*/
public class CProcessMarkEvent extends CNodeProcessorBase<CSentenceAnalysisReport>{

	CTagEntityFromDictionary entityProcess;
	public CTagEntityFromDictionary getEntityProcess() {
		return entityProcess;
	}

	public void setEntityProcess(CTagEntityFromDictionary entity_process) {
		this.entityProcess = entity_process;
	}

	public CProcessMarkEvent(){
		super(null);
	}
	
	public CProcessMarkEvent(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	}
	

	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		//find all event tags from events dictionary
		List<CEventTag> tags = this.entityProcess.MarkEntity(context);
		
		//get tags' positions.
		ILabelTag.UpdateTagsWordIndex(tags, context.getTextTokens(0));
		String str  =  context.Text;
		
		//update event string to Upper String		
		for(CEventTag tag: tags){
			tag.Text = str.substring(tag.getBeginPos(), tag.getEndPos());
			str = str.substring(0, tag.getBeginPos()) + tag.Text.toUpperCase() + str.substring(tag.getEndPos());			
		}		
		
		context.Text = str;
		
		context.Events.addAll(tags);
	}
	

}
