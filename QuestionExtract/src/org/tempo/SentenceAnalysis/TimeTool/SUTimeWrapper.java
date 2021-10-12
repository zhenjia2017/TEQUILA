package org.tempo.SentenceAnalysis.TimeTool;


import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

/** 
* @ClassName: SUTimeWrapper 
* @Description: Wrapper SUTime instance
*  
*/
public class SUTimeWrapper extends TimeTagProcessBase {
	AnnotationPipeline m_NLPpipeline;

		
	public void Initialize(){
		
		Properties props = new Properties();
	    m_NLPpipeline = new AnnotationPipeline();
	    m_NLPpipeline.addAnnotator(new TokenizerAnnotator(false));
	    m_NLPpipeline.addAnnotator(new WordsToSentencesAnnotator(false));
	    m_NLPpipeline.addAnnotator(new POSTaggerAnnotator(false));
	    m_NLPpipeline.addAnnotator(new TimeAnnotator("sutime", props));
	}
	
	public String Process(String inputSentence){
		  this.TempralTags.clear();
		  Annotation annotation = new Annotation(inputSentence);
	      annotation.set(CoreAnnotations.DocDateAnnotation.class, this.baselineDate.toLocalDate().toString());
	      this.m_NLPpipeline.annotate(annotation);
	      
	      //System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
	      List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
	      
	      if(timexAnnsAll == null || timexAnnsAll.size() < 1)
	    	  return "";
	 
	      StringBuilder infoMsg = new StringBuilder(20490);
	      for (CoreMap cm : timexAnnsAll) {
	    	  CTempralTag tmptag = CTempralTag.ConvertToTempralTag(cm);
	    	  if( tmptag != null)
	    		  this.TempralTags.add(tmptag);
	    	  
	    	  //output string 
	    	  List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
	          infoMsg.append(cm + " [from char offset " +
	                  tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
	                  " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
	                  " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());	    	  
	    	  
	      }
	      
	      return infoMsg.toString();
	      
	}
	
	
	//private static Logger logger = Logger.getLogger("SUTimeWrapper");
	
	
	
	
	public static void main(String[] args) {
		
		// Run HeidelTime

			
		// double-newstring should not be necessary, but without this, it's not running on Windows (?)
		String []exLines = {//"When did you get the doc in last month. ",
							//"who is the president of USA in 1997",
							//"How will you know during Al-Wadiah War!",
							//"If the game start at 9:00PM?",
							"Start now or next Sunday.",
							"2001-SP",
							"2001-SU",
							"2001-AU",
							"2001-WI"
		};			
		
		SUTimeWrapper sutime = new SUTimeWrapper();
		sutime.Initialize();

		
		//logger.log(Level.INFO, "start to analyze the sentences.....");
		
		for(String inString:exLines){
			String outInfo = sutime.Process(inString);
			System.out.println(inString);
			System.out.println(outInfo);
			
			//logger.log(Level.INFO, "print tag.....");
			for(CTempralTag tag: sutime.TempralTags){
				System.out.println(tag.toString());
			}
		}
	}
}
