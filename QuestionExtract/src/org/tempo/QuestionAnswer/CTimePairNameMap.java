package org.tempo.QuestionAnswer;

import java.util.HashMap;
import java.util.Map;

import org.tempo.DataFileReaders.CFileVisitorBase;
import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.TextDataFileReader;

/** 
* @ClassName: CTimePairNameMap 
* @Description: the class if ro Pair name Dictionary
*  
*/
public class CTimePairNameMap {
	
	public Map<String, Integer>  PairNameMap;
	static final String NameConectionWord = "###";
	Map<String, Integer>  NamesMap;
	
	protected CTimePairNameMap(){
		this.PairNameMap = new HashMap<String, Integer>();
		this.NamesMap = new HashMap<String, Integer>();
	}
	
	/** 
	* @Title: IsPairName 
	* @Description: check if the name is a pair name
	* @param checkName
	* @return   
	*/
	public int IsPairName(String checkName){
		String tag = getLastTagName(checkName);
		if(tag==null || tag.length() < 1 || tag.compareTo("date") ==0)
			return 0;
		
		
		if( this.NamesMap.containsKey(tag)){
			return this.NamesMap.get(tag);
		}else{
			tag = getSecondLastTagName(checkName);
			return this.NamesMap.getOrDefault(tag, 0);
		}
	}
	
	/** 
	* @Title: IsPairName 
	* @Description: check if the two names are pair
	* @param firstFullTagName
	* @param secondFullTagName
	* @return   
	*/
	public int IsPairName(String firstFullTagName, String secondFullTagName){
//		String[] firstTags = this.getTwoPartTagName(firstFullTagName);
//		if(firstTags == null)
//			return 0;
//		
//		String[] secondTags = this.getTwoPartTagName(secondFullTagName);
//		if(secondTags == null)
//			return 0;
//		
//		if( firstTags[0].compareTo(secondTags[0]) != 0 )
//			return 0;
//		
//		return this.IsPair(firstTags[1], secondTags[1]);		
		
		String[] diffParts = getDifferentParts(firstFullTagName, secondFullTagName);
		if( diffParts == null)
			return 0;
		return this.IsPair(diffParts[0], diffParts[1]);
		
	}
	
	
	/** 
	* @Title: PutPairName 
	* @Description: save pair name to dictionary
	* @param firstTagName
	* @param secondTagName   
	*/
	public void PutPairName(String firstTagName, String secondTagName){
		if(IsPair(firstTagName, secondTagName) == 0){
			this.PairNameMap.put(this.Connect(firstTagName, secondTagName), 1);
			
			if(this.NamesMap.containsKey(firstTagName) == false)
				this.NamesMap.put(firstTagName, 1);
			
			if(this.NamesMap.containsKey(secondTagName) == false)
				this.NamesMap.put(secondTagName, -1);
		}
	}
	
	/** 
	* @Title: getTwoPartTagName 
	* @Description: get pair names
	* @param word
	* @return   
	*/
	String[] getTwoPartTagName(String word){
		int npos1 = word.lastIndexOf(".");
		if( npos1 < 0)
			return null;
		
		String[] words = new String[2];
		words[0] = word.substring(0,npos1);
		words[1] = word.substring(npos1+1);
		
		return words;
	
	}
	
	
	/** 
	* @Title: getLastTagName 
	* @Description: get the pair name
	* @param word
	* @return   
	*/
	public static String getLastTagName(String word){
		int npos1 = word.lastIndexOf(".");
		if( npos1 < 0)
			return "";
		
		return word.substring(npos1+1);
	}
	
	public static String getSecondLastTagName(String word){
		int npos1 = word.lastIndexOf(".");
		if( npos1 < 0)
			return "";
		
		String str2 = word.substring(0, npos1);
		int npos2 = str2.lastIndexOf(".");
		if( npos2 < 0)
			return "";	
		
		return word.substring(npos2+1);
	}
	
	
	protected void Clear(){
		this.PairNameMap.clear();
	}
	
	/** 
	* @Title: IsPair 
	* @Description: check if the two names are pair
	* @param firstTagName
	* @param secondTagName
	* @return   
	*/
	private int IsPair(String firstTagName, String secondTagName){
		String key1 = this.Connect(firstTagName, secondTagName);
		if(PairNameMap.containsKey(key1))
			return 1;
		
		String key2 = this.Connect(secondTagName, firstTagName);
		if(PairNameMap.containsKey(key2))
			return -1;
		
		return 0;
	}
	
	/** 
	* @Title: Connect 
	* @Description: connect two name together
	* @param first
	* @param second
	* @return   
	*/
	String Connect(String first, String second){
		return (first + NameConectionWord + second);
	}
	
	/** 
	* @Title: CreateFromPairFile 
	* @Description: create dictionary from file
	* @param file
	* @return   
	*/
	public static  CTimePairNameMap CreateFromPairFile(String file){		
		CTimePairNameMap pairMap = new CTimePairNameMap();
		
		TextDataFileReader r = new TextDataFileReader();

		r.ReadFile(file, "utf-8", new CNamePairFileLoader(pairMap));		
		if (pairMap.PairNameMap.size()>0)
			return pairMap;
		
		return null;
	}

	
	public static String[] getDifferentParts(String tag1, String tag2){
		String[] tag1Words= tag1.split("\\.");
		String[] tag2Words= tag2.split("\\.");
		
		if( tag1Words==null || tag1Words.length<2 || tag2Words==null || tag2Words.length<2)
			return null;
		
		int nSize = Math.min(tag1Words.length, tag2Words.length);
		int nDiff = -1;
		for(int i = 0; i < nSize ; i++){
			if( tag1Words[i].compareTo(tag2Words[i]) != 0 ){
				nDiff = i;
				break;
			}
		}
		
		if( nDiff <0)
			return null;
		
		String[] strRet = new String[2];
		strRet[0] = getWordsString(tag1Words, nDiff, ".");
		strRet[1] = getWordsString(tag2Words, nDiff, ".");
		return strRet;
	}
	
	static String getWordsString(String[] words, int from, String splitor){
		String strTag="";
		for(int i=from; i< words.length; i++){
			strTag += words[i];
			if( i < words.length - 1){
				strTag += splitor;
			}
		}	
		
		return strTag;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CTimePairNameMap  map = CTimePairNameMap.CreateFromPairFile("D:\\TimeIdentity\\QuestionExtract\\source\\dictionary\\temporal-predicate-pairs");
		
		for(String key: map.PairNameMap.keySet()){
			System.out.println(key);
		}
			
	}
}


/** 
* @ClassName: CNamePairFileLoader 
* @Description: the class is to visit pair name file, read pair names
*  
*/
class CNamePairFileLoader extends CFileVisitorBase{
	
	CTimePairNameMap  pairNameMap;

	CNamePairFileLoader(CTimePairNameMap pairNameMap) {
		this.pairNameMap = pairNameMap;
	}

	@Override
	public void EndVisitFile() {
		// TODO Auto-generated method stub
	}

	@Override
	public void StartVisitFile() {
		// TODO Auto-generated method stub
		this.pairNameMap.Clear();
	}

	/**   
	 * <p>Title: Visit</p>   
	 * <p>Description: save pair names to dictionary</p>   
	 * @param lineInfo   
	 * @see org.tempo.DataFileReaders.CFileVisitorBase#Visit(org.tempo.DataFileReaders.DataFileLineInfo)   
	 */ 
	@Override
	public void Visit(DataFileLineInfo lineInfo) {
		// TODO Auto-generated method stub
		String[] words = lineInfo.Text.split("\t");
		if(words != null && words.length == 2){
			String[] tagDiffParts= this.pairNameMap.getDifferentParts(words[0].trim(), words[1].trim());
			if( tagDiffParts != null)
				this.pairNameMap.PutPairName( tagDiffParts[0] ,tagDiffParts[1]);		
		}
		
		
	}
	


}

