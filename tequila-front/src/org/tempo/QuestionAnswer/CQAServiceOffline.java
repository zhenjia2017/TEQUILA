package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tempo.DataFileReaders.CFileVisitorBase;
import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.TextDataFileReader;



/** 
* @ClassName: CQAServiceOffline 
* @Description: QAservice for offline (answer from file)
*  
*/
public class CQAServiceOffline implements IQAService{
	Map<String, CQAResult> resultsMap;
	
	
	public CQAServiceOffline(){
		this.resultsMap = new HashMap<String, CQAResult>();
		
	}
	
	/** 
	* @Title: ParseResultsFile 
	* @Description: parse answer from answer file
	* @param file   
	*/
	public void ParseResultsFile(String file){
		//create answer file parser
		CQAResultsFileParser parse = new CQAResultsFileParser();
		TextDataFileReader r = new TextDataFileReader();
		r.ReadFile(file, "utf-8", parse);
		
		for(CQAResult result : parse.qaResults){
			String key = result.Question.toLowerCase().trim();
			if( this.resultsMap.containsKey(key) == false)
				this.resultsMap.put(key, result);
		}
	}
	
	
	/**   
	 * <p>Title: QueryAnswer</p>   
	 * <p>Description: find question answer </p>   
	 * @param question
	 * @return   
	 * @see org.tempo.QuestionAnswer.IQAService#QueryAnswer(java.lang.String)   
	 */ 
	public CQAResult QueryAnswer( String question){
		String key = question.toLowerCase().trim();
		return this.resultsMap.getOrDefault(key, null);
	}


}

/** 
* @ClassName: CQAResultsFileParser 
* @Description: the class is to visit answer file
*  
*/
class CQAResultsFileParser extends CFileVisitorBase{
	String[] qaTempBuffer1=new String[28];
	String[] qaTempBuffer2 = new String[7];
	int CurrentTemplate =0;
	
	
	String[] templateItemNames1=new String[]{ "question",
			"answer_1","question_entity_1","SPARQL_1","relation_1","updated_SPARQL_1","predicate_1","best_date_predicate_1","date_in_CVT_1","date_in_pro_1",
			"answer_2","question_entity_2","SPARQL_2","relation_2","updated_SPARQL_2","predicate_2","best_date_predicate_2","date_in_CVT_2","date_in_pro_2",
			"answer_3","question_entity_3","SPARQL_3","relation_3","updated_SPARQL_3","predicate_3","best_date_predicate_3","date_in_CVT_3","date_in_pro_3",
		};
	
	String[] templateItemNames2=new String[]{ 
					"question","answer","question_entity","SPARQL",
					"relation","SPARQL_update","predicate"	};
	
	List<CQAResult> qaResults;
	List<String>  linesBuffer;
	String _CurrentItemName;
	int Status; //0:first line of object; 1:line item£» 2£º multiline items;
	
	
	public CQAResultsFileParser(){
		this.qaResults = new ArrayList<CQAResult>(512);
		this.linesBuffer = new ArrayList<String>();
		this._CurrentItemName="";
		this.Status = 0;
	
	}

	@Override
	public void EndVisitFile() {
		// TODO Auto-generated method stub
		StoreObjectInfo();
	}

	@Override
	public void StartVisitFile() {
		// TODO Auto-generated method stub
		CleanTemplate(this.qaTempBuffer1);
		CleanTemplate(this.qaTempBuffer2);
		this.Status = 0;
		this._CurrentItemName = "";
	}

	/**   
	 * <p>Title: Visit</p>   
	 * <p>Description: parse text line </p>   
	 * @param lineInfo   
	 * @see org.tempo.DataFileReaders.CFileVisitorBase#Visit(org.tempo.DataFileReaders.DataFileLineInfo)   
	 */ 
	@Override
	public void Visit(DataFileLineInfo lineInfo) {
		if(lineInfo.Text.length()<1)
			return;
		
		//if read last line of a answer
		if(IsObjectBorderFlag(lineInfo.Text)){
			StoreObjectInfo(); // convert to  CQAResult Object
			return;
		}
		
		//if read part of the answer, not complete
		if( this.IsMultiLineStatus() && NeedKeepMultiStatus(lineInfo.Text)){
			this.linesBuffer.add(lineInfo.Text);
			return;
		}
		
		//get item text
		ReadItemFromText(lineInfo.Text);
		
	}
	
	/** 
	* @Title: IsTemplateEmpty 
	* @Description: check if the template is empty
	* @param template
	* @return   
	*/
	boolean IsTemplateEmpty(String[] template){
		return (template[0].length()<2 );// && template[1].length()<1);
	}

	/** 
	* @Title: findFlagPos 
	* @Description: find flag index
	* @param findflag
	* @param flags
	* @return   
	*/
	int findFlagPos(String findflag  , String[]flags){
		for(int i = 0; i < flags.length; i++){
			if(findflag.compareToIgnoreCase(flags[i]) == 0)
				return i;
		}
		return -1;
			
	}
	
	
	/** 
	* @Title: IsObjectBorderFlag 
	* @Description: read end of the text section
	* @param lineInfo
	* @return   
	*/
	boolean IsObjectBorderFlag(String lineInfo){
		return (lineInfo!=null && lineInfo.length()>0 && lineInfo.contains("-----------"));
	}
	
	/** 
	* @Title: CleanItem 
	* @Description: clean buffer   
	*/
	void CleanItem(){
		this.linesBuffer.clear();
		this._CurrentItemName = "";
		this.Status=0;
		
		
	}
	
	/** 
	* @Title: StoreObjectInfo 
	* @Description: convert buffer to CQAResult Object   
	*/
	void StoreObjectInfo(){
		String []template = this.GetCurrentTemplate();
		if( this.IsTemplateEmpty( template))
			return;
		
		CQAResult result = CQAResult.Parse(template);
		if(result != null)
			this.qaResults.add(result);
		
		CleanItem();
		this.Status = 0;
		
	}
	
	/** 
	* @Title: IsMultiLineStatus 
	* @Description: check if it's multi-line
	* @return   
	*/
	boolean IsMultiLineStatus(){
		return (this.Status == 2);
	}
	
	/** 
	* @Title: NeedKeepMultiStatus 
	* @Description: check if need keep multi line status.
	* @param lineText
	* @return   
	*/
	boolean NeedKeepMultiStatus(String lineText){
		int npos = lineText.indexOf(':');
		if(npos < 0) return true;

		return(this.FindFlag(lineText.substring(0, npos).trim())<0);
	}
	
	/** 
	* @Title: FindFlag 
	* @Description: get index of flag
	* @param item
	* @return   
	*/
	int FindFlag(String item){
		String[] items = this.GetCurrentTemplateItemNames();
		for(int i = 0; i < items.length; i++){
			if(items[i].compareToIgnoreCase(item) == 0)
				return i;
		}
		return -1;
	}
	
	
	/** 
	* @Title: ReadItemFromText 
	* @Description: read text of item
	* @param text   
	*/
	void ReadItemFromText(String text){
		int npos = text.indexOf(':');
		if(npos<1){
			if(this.Status == 0){
				StoreQuestion(text);
				this.Status = 1;
			}
			return;
		}
		
		String item = text.substring(0, npos).trim();
		String itemContent = text.substring(npos +1).trim();
		
		if(this.IsMultiLineStatus() && IsMultiFlag(item)==false){
			SaveMultiItem();
		}
		
		
		if( IsMultiFlag(item)){
			this.Status = 2;
			this.SetMultiLineItem(item, itemContent);
		}else{
			this.Status = 1;
			this.SetItem(item, itemContent);
		}
		
		
	}
	
	/** 
	* @Title: SetMultiLineItem 
	* @Description: add item to buffer
	* @param item
	* @param itemContent   
	*/
	void SetMultiLineItem(String item, String itemContent){
		linesBuffer.clear();
		linesBuffer.add(itemContent);
		this._CurrentItemName = item;
	}
	
	/** 
	* @Title: SaveMultiItem 
	* @Description: save template item   
	*/
	void SaveMultiItem(){
		if(this.IsMultiLineStatus() && this.IsMultiFlag(this._CurrentItemName)){
			String itemText = getBufferString(this.linesBuffer);
			this.SetItem(this._CurrentItemName, itemText);
		}
	}
	
	/** 
	* @Title: SetItem 
	* @Description: set template item value
	* @param itemName
	* @param text   
	*/
	void SetItem(String itemName, String text){
		int nIndex = this.FindNameIndexInTemplate(itemName);
		
		if( nIndex < 0)
			return;
		
		String[] template = this.GetCurrentTemplate();
		template[nIndex] = text;	
		this._CurrentItemName = itemName;
	}
	
	/** 
	* @Title: FindNameIndexInTemplate 
	* @Description: get name index in template
	* @param itemName
	* @return   
	*/
	int FindNameIndexInTemplate(String itemName){
		String[] names = this.GetCurrentTemplateItemNames();
		for(int i = 1; i<names.length; i++){
			if(itemName.compareToIgnoreCase(names[i]) ==0)
				return i;
		}
		return -1;
	}
	
	
	/** 
	* @Title: getBufferString 
	* @Description: add string list to buffer.
	* @param texts
	* @return   
	*/
	String getBufferString(List<String> texts){
		StringBuilder buf = new StringBuilder();
		for(String str : texts){
			buf.append(str);
		}
		return buf.toString();
	}
	
	
	
	/** 
	* @Title: IsMultiFlag 
	* @Description: check if it's multi-line
	* @param item
	* @return   
	*/
	boolean IsMultiFlag(String item){
		return (item.contains("SPARQL"));
	}
	
	/** 
	* @Title: StoreQuestion 
	* @Description: save question to template
	* @param question   
	*/
	void StoreQuestion(String question){
		String[] template = SetCurrentTemplate(question);
		template[0] = question;
		this._CurrentItemName = "question";
	}
	
	/** 
	* @Title: GetCurrentTemplateItemNames 
	* @Description: select template
	* @return   
	*/
	String[] GetCurrentTemplateItemNames(){
		if( this.CurrentTemplate == 1)
			return this.templateItemNames1;
		else
			return this.templateItemNames2;
	}
	
	/** 
	* @Title: GetCurrentTemplate 
	* @Description: get current template
	* @return   
	*/
	String[] GetCurrentTemplate(){
		if( this.CurrentTemplate == 1)
			return qaTempBuffer1;
		else
			return qaTempBuffer2;		
	}
	
	/** 
	* @Title: SetCurrentTemplate 
	* @Description: set current template
	* @param question
	* @return   
	*/
	String[] SetCurrentTemplate(String question){
		CleanTemplate(this.qaTempBuffer1);
		CleanTemplate(this.qaTempBuffer2);	
		this.CurrentTemplate = this.GetTemplateIndex(question);
		return this.GetCurrentTemplate();
	}
	
	/** 
	* @Title: CleanTemplate 
	* @Description: clean template
	* @param template   
	*/
	void CleanTemplate(String[] template){
		for(int i = 0; i<template.length; i++){
			template[i] = "";
		}
	}
	
	
	/** 
	* @Title: GetTemplateIndex 
	* @Description: get matched template index
	* @param question
	* @return   
	*/
	int GetTemplateIndex(String question){
		if( question.startsWith("%%%")){
			return 1;
		}else { // "|||", "###";
			return 2;
		}
	}

	
}
