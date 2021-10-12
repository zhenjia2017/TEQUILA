package org.tempo.SentenceAnalysis.Dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
* @ClassName: CEntityDictionary 
* @Description: Entity dictionary 
*  
*/

public class CEntityDictionary implements IDictionary{
	final Integer DefaultKeyID=-1;
	
	/** WordDict:  Word to ID map */  
	HashMap<String, Integer> WordDict;	
	
	/** IDDict:ID  to String map */  
	HashMap<Integer,String> IDDict;
	
	
	/** Entities:  Entity ID Map*/  
	HashMap<Integer,String> Entities;
	
	TernaryTree ternaryDict;
	StringBuilder _buf;
	int IndexPos;
	
	public CEntityDictionary(){
		this.WordDict = new HashMap<String, Integer>(500000);
		this.IDDict = new HashMap<Integer,String>(500000);
		this.Entities = new HashMap<Integer,String>(500000);
		
		this.IndexPos = 1000;
		this.ternaryDict = new TernaryTree();
		this._buf = new StringBuilder();
	}

	public TernaryTree GetDictionary(){
		return this.ternaryDict;
	}
	
	
	public void Init(){
		this.IndexPos = 1000;
		this.WordDict.clear();
	}
	
	/** 
	* @Title: GetWordByID 
	* @Description: get a Word by word id(integer)
	* @param id
	* @return   
	*/
	public String GetWordByID(Integer id){
		if( id <= 0 )
			return "";
		
		return this.IDDict.getOrDefault(id, "");		
	}
	
	/** 
	* @Title: SaveEntity 
	* @Description: save a Entity 
	* @param rule
	* @param id   
	*/
	public void SaveEntity(String rule , int id){
		if( this.Entities.containsKey(id) == false)
			this.Entities.put(id, rule);
	}
	
	/** 
	* @Title: GetEntityByID 
	* @Description: find Entity by id
	* @param id
	* @return   
	*/
	public String GetEntityByID(int id){
		return this.Entities.getOrDefault(id, "");
	}
	
	/** 
	* @Title: SaveWord 
	* @Description: save word string 
	* @param word
	* @return   
	*/
	public Integer SaveWord(String word){
		if( this.WordDict.containsKey(word))
			return WordDict.get(word);
		
		this.WordDict.put(word, this.IndexPos); // update word to id map
		this.IDDict.put(this.IndexPos, word);   // update id to word map
		
		this.IndexPos++;
		return(this.IndexPos-1);
	}
	
	public Integer GetWordId(String word){
		return this.WordDict.getOrDefault(word, this.DefaultKeyID);
	}
	
	/** 
	* @Title: TranslateFromTokens 
	* @Description: translate word array to ID list
	* @param tokens
	* @return   
	*/
	public List<Integer> TranslateFromTokens(String[] tokens){
		
		List<Integer> idlst = new ArrayList<Integer>();	
		for(String tk : tokens){
			Integer id = this.GetWordId( tk );			
			idlst.add(id);		
		}
		
		return idlst;
	}	

	
	/** 
	* @Title: Translate 
	* @Description: translate a string to ID list
	* @param words
	* @return   
	*/
	List<Integer> Translate(String words){
		String[] tokens = words.split(" ");
		return this.TranslateFromTokens(tokens);
	}

	
	/** 
	* @Title: Translate 
	* @Description: convert Tree search result to text 
	* @param lstWords
	* @return   
	*/
	TernaryTreeSearchResult[] Translate(List< TernaryTreeSearchResult >  lstWords){
		if( lstWords==null || lstWords.size()<1)
			return (new TernaryTreeSearchResult[]{});
		
		for(int i = 0; i<lstWords.size(); i++){
			TernaryTreeSearchResult r = lstWords.get(i);
			r.WordString = this.TranslateToString(r.getWord());			
		}
		return lstWords.toArray(new TernaryTreeSearchResult[]{});
	}
	
	/** 
	* @Title: TranslateToString 
	* @Description: translate Id list to words
	* @param word
	* @return   
	*/
	String TranslateToString(List<Integer> word){
		this._buf.setLength(0);
		for(Integer id: word){
			this._buf.append(this.GetWordByID(id));
			this._buf.append(" ");
		}
		return this._buf.toString();
	}
	
	
	/** 
	* @Title: IsValid 
	* @Description: check the Id list is valid.
	* @param idLst
	* @return   
	*/
	boolean IsValid(List<Integer> idLst){
		for(Integer id : idLst){
			if( id <= 0)
				return false;
		}
		
		return true;
	}


	/**   
	 * <p>Title: search</p>   
	 * <p>Description: search a word  in the dictionary</p>   
	 * @param word
	 * @return   
	 * @see org.tempo.SentenceAnalysis.Dictionary.IDictionary#search(java.lang.String)   
	 */ 
	public boolean search(String word) {
		// TODO Auto-generated method stub
		List<Integer> lst = this.Translate(word); //translate words to id list
		
		if(lst != null && IsValid(lst))
			return (this.ternaryDict.search(lst) != null);

		return false;
	}

	
	/** 
	* @Title: search 
	* @Description: search word in the dictionary
	* @param words
	* @return   
	*/
	public TernaryTreeSearchResult search(String[] words) {
		// TODO Auto-generated method stub
		List<Integer> lst = this.TranslateFromTokens(words);		
		if(lst != null && IsValid(lst))
			return (this.ternaryDict.search(lst));

		return null;
	}	
	


	/**   
	 * <p>Title: prefixSearch</p>   
	 * <p>Description: search word by prefix search</p>   
	 * @param word
	 * @return   
	 * @see org.tempo.SentenceAnalysis.Dictionary.IDictionary#prefixSearch(java.lang.String)   
	 */ 
	public TernaryTreeSearchResult[] prefixSearch(String word) {
		// TODO Auto-generated method stub
		List<Integer> lst = this.Translate(word);
		
		if(lst != null && IsValid(lst)){
			List<TernaryTreeSearchResult > result = this.ternaryDict.prefixSearch(lst);
			return (this.Translate(result));
			
		}
		else
			return (new TernaryTreeSearchResult[]{});
	}


	/**   
	 * <p>Title: size</p>   
	 * <p>Description: get dictionary size</p>   
	 * @return   
	 * @see org.tempo.SentenceAnalysis.Dictionary.IDictionary#size()   
	 */ 
	public long size() {
		// TODO Auto-generated method stub
		return this.ternaryDict.size();
	}

}

