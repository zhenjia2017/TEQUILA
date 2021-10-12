package org.tempo.SentenceAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;

/** 
* @ClassName: CProcessNLP 
* @Description: this process node start NLP instance to parse sentence.
*  
*/
public class CProcessNLP extends CNodeProcessorBase<CSentenceAnalysisReport>{

	AnnotationPipeline nlptool;
	
	public CProcessNLP(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
		
		this.nlptool = NLPTool.GetStandfordNLPIntance();
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context != null && context.Document == null);
	}

	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		context.Document = new Annotation(context.Text);
		this.nlptool.annotate(context.Document);
		//getCorefChains();
	}
	
	/** 
	* @Title: getCorefChains 
	* @Description: get coref List by NLP   
	*/
	@Deprecated
	void getCorefChains(){
	      Map<Integer, CorefChain> corefChains =  this.Context.getDocument().get(CorefCoreAnnotations.CorefChainAnnotation.class);

	      //List<CoreMap> sentences = this.Context.getDocument().get(CoreAnnotations.SentencesAnnotation.class);
	      
	     List<  List<CorefChain.CorefMention> >corefList = new ArrayList< List<CorefChain.CorefMention> >();
	      
	      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
	    	  //System.out.println("Chain " + entry.getKey());
	    	  
	    	  List<CorefChain.CorefMention> m = entry.getValue().getMentionsInTextualOrder();
	    	  if(m.size() > 1)
	    		   corefList.add(m);

	      }
	      
	      //this.Context.CorefList = corefList;
	}


}
