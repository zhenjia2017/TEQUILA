package org.tempo.SentenceAnalysis;

import java.util.List;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.util.CoreMap;

/** 
* @ClassName: CProcessShapeSentence 
* @Description: this process node use NLP Truecase to rewrite sentence.
*  
*/
public class CProcessShapeSentence  extends CNodeProcessorBase<CSentenceAnalysisReport> {

	AnnotationPipeline nlptool;
	static final String STR_SYMPLE = "~!@#$%^&*()_+{}|:<>?,./;[]\\=-'\"";
	
	AnnotationPipeline  getNLPTool(){
		return nlptool;
	}
	
	public CProcessShapeSentence(){
		super(null);
		CreateNLPHandle();
	}
	
	void CreateNLPHandle(){
		this.nlptool = NLPTool.GetTruecaseNLP();			
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!=null && context.Text != null );
	}
		
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		StringBuilder buf1 = new StringBuilder();

		//append "?" at the end of question.
		String strNew = AppendQASymbleEnd(context.Text.trim().toLowerCase());
		Annotation document1 = new Annotation(strNew);
	
		//deal with question by NLP with truecase option
	    this.getNLPTool().annotate(document1);
	    List<CoreMap> sentences = document1.get(SentencesAnnotation.class);
	    
	    //rewrite sentence by true case.
	    for(CoreMap sentence: sentences) {
	    	  buf1.append(NLPTool.GetTextOfTruecase(sentence.get(TokensAnnotation.class), 0, -1, " "));
	    	  buf1.append(" ");
	    }
	    

	    context.OriginalText = context.Text;
	    context.Text = buf1.toString().trim();		
	    
	}
	
	
	/** 
	* @Title: AppendQASymbleEnd 
	* @Description: if the question has not "?" symble , append it.
	* @param text
	* @return   
	*/
	String AppendQASymbleEnd(String text){
		String lastWord = text.substring(text.length() -1);
		if(STR_SYMPLE.contains(lastWord)){
			return text;
		}else {
			return text.trim()+"?";
		}
	}
	
	
}
