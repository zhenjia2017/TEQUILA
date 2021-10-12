package org.tempo.SentenceAnalysis;
import java.util.List;

import org.tempo.SentenceAnalysis.TimeTool.*;

/** 
* @ClassName: CProcessMarkTimeTagByHeildeTime 
* @Description: this process node is to create TimepralTag by HeilderTime.
*  
*/
public class CProcessMarkTimeTagByHeildeTime extends CNodeProcessorBase<CSentenceAnalysisReport> {
	HeildeTimeWrapper heilderTime;
	
	
	public HeildeTimeWrapper getHeilderTime() {
		return heilderTime;
	}

	public void setHeilderTime(HeildeTimeWrapper heilderTime) {
		this.heilderTime = heilderTime;
	}

	public CProcessMarkTimeTagByHeildeTime(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	}
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		
		//run heilderTime to analyze the sentence.
		this.heilderTime.Process(context.Text);
		
		//get tempralTags.
		List<CTempralTag> timetags = this.heilderTime.getTempralTags();
		ILabelTag.UpdateTagsWordIndex(timetags, this.Context.getTextTokens(0));		
		
		context.TimeReports.addAll(timetags);
		
		
		
	}


}
