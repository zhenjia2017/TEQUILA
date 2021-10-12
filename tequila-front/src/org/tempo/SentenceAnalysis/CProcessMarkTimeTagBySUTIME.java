package org.tempo.SentenceAnalysis;

import java.util.List;

import org.tempo.SentenceAnalysis.TimeTool.CTempralTag;

import java.util.ArrayList;

import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.util.CoreMap;


/** 
* @ClassName: CProcessMarkTimeTagBySUTIME 
* @Description: this process node is to get all timpral tag by SUTime;
*  
*/
public class CProcessMarkTimeTagBySUTIME extends CNodeProcessorBase<CSentenceAnalysisReport> {

	public CProcessMarkTimeTagBySUTIME(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	}
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport contextReport) {
		// TODO Auto-generated method stub
	      List<CoreMap> timexAnnsAll = contextReport.Document.get(TimeAnnotations.TimexAnnotations.class);
	      
	      
	      if(timexAnnsAll == null || timexAnnsAll.size() < 1)
	    	  return ;
	 
	      List<CTempralTag> timeTags = new ArrayList<CTempralTag>();
	      //convert sutime tag to TempralTag
	      for (CoreMap cm : timexAnnsAll) {
	    	  CTempralTag tmptag = CTempralTag.ConvertToTempralTag(cm);
	    	  if( tmptag != null){

	    		  timeTags.add(tmptag);
	    	  }
	    	  
	      }
	      
	      ILabelTag.UpdateTagsWordIndex(timeTags, this.Context.getTextTokens(0));
	      
	      contextReport.TimeReports.addAll(timeTags);
	}


}
