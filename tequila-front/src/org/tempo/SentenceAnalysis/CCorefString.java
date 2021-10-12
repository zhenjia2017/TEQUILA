package org.tempo.SentenceAnalysis;

/** 
* @ClassName: CCorefString 
* @Description: coref string 
*  
*/
public class CCorefString {
	public String Source; //source text
	public String CorefString; //coref string
	
	public CCorefString(String src, String coref){
		this.Source = src;
		this.CorefString = coref;
	}
}
