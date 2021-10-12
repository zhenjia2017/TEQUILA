package org.tempo.SentenceAnalysis.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tempo.SentenceAnalysis.CEventTag;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.NLPTool;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;

/** 
* @ClassName: CTagEntityFromDictionary 
* @Description: Tool to find event tag.
*  
*/
public class CTagEntityFromDictionary {
	
	CEntityDictionary dictionary; // event dictionary
	AnnotationPipeline nlptool;  //nlp tool
	
	
	protected CTagEntityFromDictionary(CEntityDictionary dict){
		this.dictionary = dict;
		this.nlptool = NLPTool.GetSimepleNLPTokenTool();
	}
	
	/** 
	* @Title: CreateTagTool 
	* @Description: build dictionary from the events file.
	* @param dictFile
	* @return   
	*/
	public static CTagEntityFromDictionary CreateTagTool(String dictFile){
		
		CEntityDictionary dict = DictionaryBuilder.LoadDictionaryFile(dictFile);
		
		if( dict != null)
			return (new CTagEntityFromDictionary(dict));
		
		return null;
	}
	
	/** 
	* @Title: MarkEntity 
	* @Description: mark event tag in the sentence.
	* @param sentence
	* @return   
	*/
	public List<CEventTag> MarkEntity(String sentence){
		Annotation doc = new Annotation(sentence);
		this.nlptool.annotate(doc);
		
		List<CoreLabel> tokens = doc.get(TokensAnnotation.class);	
		return non_overlapping_tagging(sentence.toLowerCase(),tokens, 100);		
	}
	
	/** 
	* @Title: MarkEntity 
	* @Description: 
	* @param sentence context
	* @return   
	*/
	public List<CEventTag> MarkEntity(CSentenceAnalysisReport sentence){
		if(sentence.Document == null){
			sentence.Document = new Annotation(sentence.Text);
			this.nlptool.annotate(sentence.Document);
		}
		List<CoreLabel> tokens = sentence.Document.get(TokensAnnotation.class);	
		return non_overlapping_tagging(sentence.Text.toLowerCase(), tokens, 100);		
	}
	
	
	/** 
	* @Title: IsSymbolToken 
	* @Description: check the NLP token is "SYM"
	* @param token
	* @return   
	*/
	boolean IsSymbolToken(CoreLabel token){
		String pos = token.get(PartOfSpeechAnnotation.class);			
		return ( pos.compareTo("SYM") == 0);
	}
	
	
	/** 
	* @Title: non_overlapping_tagging 
	* @Description: find events in the sentence
	* @param sentenceText
	* @param sentence
	* @param max_key_size
	* @return   
	*/
	List<CEventTag> non_overlapping_tagging(String sentenceText, List<CoreLabel> sentence, int max_key_size)
    {
		List<CEventTag> tag_sentence = new ArrayList<CEventTag>();
        int N = sentence.size();
        if (max_key_size == -1)
        {
            max_key_size = N;
        }
        int i = 0;
        while (i < N)
        {
        	if (IsSymbolToken(sentence.get(i))){
        		i++;
        		continue;
        	}
        	
            boolean tagged = false;
            int j = Math.min(i + max_key_size, N); //avoid overflow
            
            
            while (j > i)
            {
                //List<CoreLabel> literal_tokens = sentence.subList(i, j);
            	int startChar = sentence.get(i).beginPosition();
            	int endChar = sentence.get(j-1).endPosition();
            	String[] words = sentenceText.substring(startChar, endChar).split(" ");
            	
            	TernaryTreeSearchResult result = this.dictionary.search(words);
                //TernaryTreeSearchResult result = this.dictionary.search(literal_tokens);
                if (result != null)
                {
                    tag_sentence.add(new CEventTag(startChar,endChar, result.getExtraData()));
                    i = j;
                    tagged = true;
                }
                else
                {
                    j -= 1;
                }
            }
            
            i++;
            
        }
        return tag_sentence;

    }
	
	
	/** 
	* @Title: getEntityFullName 
	* @Description: get entity string by id
	* @param id
	* @return   
	*/
	public String getEntityFullName(int id){
		return this.dictionary.GetEntityByID(id);
	}
	

}
