package org.tempo.SentenceAnalysis.Decompose;

/** 
* @ClassName: RewriteQuestions 
* @Description: the result of question decomposition
*  
*/
public class CRewriteQuestionResult {
		/** QuestionType: the process name of question decomposition  */  
		public String QuestionType;
		
		/** FirstQuestion: first question after decomposition */  
		public String FirstQuestion;		
		
		/** SignalWord:signal word */  
		public String SignalWord;
		
		/** SignalPos: signal word index */  
		public int SignalPos=-1;
		
		/** SubQuestion: sub-question */  
		public String SubQuestion;

}
