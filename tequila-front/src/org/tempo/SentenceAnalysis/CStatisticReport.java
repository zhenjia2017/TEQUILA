package org.tempo.SentenceAnalysis;

import java.util.HashMap;
import java.util.Map;


/** 
* @ClassName: CStatisticReport 
* @Description: a class to record statistic information
*  
*/
public class CStatisticReport {
	
	public int SentenceTotal =0; //total sentence 
	public int SentenceRewriteCount=0; //total rewritten sentence 
	public HashMap<String, Integer> SentenceTypeCount; //sentence Type counter
	public HashMap<String, Integer> SingalWordCount;  //signal word counter
	public HashMap<String, Integer> SingalTypeCount; //signal type counter
	
	public int NoneTypeSentenceCount=0;  //question no any type
	public int EntityCount=0;  //entity counter
	public int EntitySentenceCount=0; //counter for sentence which has entity 
	public int OneEnitySentenceCount = 0;//counter for sentence without 1 entity
	public int TwoEntitySentenceCount = 0;//counter for sentence without 2 entity
	public int MoreEntitySentenceCount = 0;//counter for sentence without more than 2 entity
	
	public HashMap<String, Integer> NEREntityCount; //counter for enitity
	public int EventCount=0; //counter for event
	public int EventSentenceCount=0;	//counter for event sentence
	public int DateTagSentenceCount=0; //counter for date tag sentence
	public int DateTagCount=0;  //counter for date tags
	public int OrdinalTagCount=0;	 //counter for ordinal tags
	public int OrdinalSentenceCount=0;	//counter for ordinal sentence 
	public int TimeX3Count=0;	 //counter for timeX3 tag
	public int TimeX3SentenceCount=0;	//counter for timex3 sentence
	public int[] AnswerFromRankCount = new int[]{0,0,0}; //counter for answer in rank
	
	
	public CStatisticReport(){
		this.SentenceTypeCount = new HashMap<String, Integer>();
		this.SingalWordCount =  new HashMap<String, Integer>();
		this.SingalTypeCount =  new HashMap<String, Integer>();
		this.NEREntityCount =  new HashMap<String, Integer>();
	}
	
	/** 
	* @Title: AddTypeMapCount 
	* @Description: create hash set for a type
	* @param typeName
	* @param hashSet   
	*/
	public void AddTypeMapCount(String typeName,HashMap<String, Integer> hashSet ){
		if(hashSet.containsKey(typeName)){
			hashSet.put(typeName, hashSet.get(typeName) + 1);
		}else{
			hashSet.put(typeName, 1);
		}
	}
	
	/**   
	 * <p>Title: toString</p>   
	 * <p>Description: convert to string </p>   
	 * @return   
	 * @see java.lang.Object#toString()   
	 */ 
	@Override
	public String toString() {
		String line= "\r\n";
		// TODO Auto-generated method stub
		StringBuilder buf = new StringBuilder();
		
		buf.append("Sentence Total:" + this.SentenceTotal);
		buf.append(line);

		buf.append("None Type SentenceCount:" + this.NoneTypeSentenceCount);
		buf.append(line);
		
		buf.append("Sentence Rewrite Sentence Count:" + this.SentenceRewriteCount);	
		buf.append(line);
		
		getMapString(this.SentenceTypeCount, buf, "Sentenc Type \"%s\":%d");
		getMapString(this.SingalTypeCount, buf,"Signal Type %s:%d");
		getMapString(this.SingalWordCount, buf,"Signal Word \"%s\":%d");
		

		buf.append("Entity Tag Count:" + this.EntityCount);	
		buf.append(line);
		buf.append("Entity Tag Sentence Count:" + this.EntitySentenceCount);
		buf.append("\t(non-Entity Sentence Count:" + (this.SentenceTotal -this.EntitySentenceCount));
		
		String str1 = String.format("\t 1 Entity Sentence:%d ,\t 2 Entity Sentence:%d,\t More Entity Sentence:%d)",
						this.OneEnitySentenceCount, this.TwoEntitySentenceCount, this.MoreEntitySentenceCount);
		buf.append(str1);
		
		buf.append(line);

		
		getMapString(this.NEREntityCount, buf, "NER Entity Type \"%s\":%d");		
		
		buf.append("Date Tag Count:" + this.DateTagCount);
		buf.append(line);
		buf.append("Date Tag Sentence Count:" + this.DateTagSentenceCount);	
		buf.append(line);
		
		buf.append("Event Tag Count:" + this.EventCount);
		buf.append(line);
		buf.append("Event Tag Sentence Count:" + this.EventSentenceCount);	
		buf.append(line);
		

		buf.append("Ordinal Tag Count:" + this.OrdinalTagCount);	
		buf.append(line);
		buf.append("Ordinal Tag Sentence Count:" + this.OrdinalSentenceCount);	
		buf.append(line);
		
		buf.append("TimeX3 Tag Count:" + this.TimeX3Count);	
		buf.append(line);
		buf.append("TimeX3 Tag Sentence Count:" + this.TimeX3SentenceCount);	
		buf.append(line);
	
		buf.append("Answer From Rank: Rank1(" + this.AnswerFromRankCount[0] 
							+ "); Rank2( " +  this.AnswerFromRankCount[1] 
							+ "); Rank3( " +  this.AnswerFromRankCount[2] +")");
		buf.append(line);
		
		return buf.toString().trim();
	}

	void getMapString(HashMap<String,Integer> hashSet, StringBuilder buf, String formatString){
		for( Map.Entry<String, Integer> v : hashSet.entrySet() ){
			if(v.getValue()>0){
				String str = String.format(formatString, v.getKey(), v.getValue());

				buf.append(str);
				buf.append("\r\n");
			}
			
		}
	}


}
