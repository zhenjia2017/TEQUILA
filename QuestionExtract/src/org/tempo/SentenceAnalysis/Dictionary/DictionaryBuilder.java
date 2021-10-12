package org.tempo.SentenceAnalysis.Dictionary;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.IFileVistor;
import org.tempo.DataFileReaders.TextDataFileReader;


/** 
* @ClassName: DictionaryBuilder 
* @Description: tool to build event dictionary.
*  
*/
public class DictionaryBuilder implements IFileVistor{

	CEntityDictionary dict;
	StringBuilder buf;
	
	Pattern number_pattern;
	
	static String symbleString="~!@#$%^&*()_+{}|:\"<>?-=[];',./'\\";

	public CEntityDictionary getDict(){
		return dict;
	}

	DictionaryBuilder(){
		dict = new CEntityDictionary();
		buf = new StringBuilder(4096);
		number_pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
	}


	public void Visit(DataFileLineInfo lineInfo) {
		// TODO Auto-generated method stub
		ParseByStringSplite(lineInfo);		
	}
	
	/** 
	* @Title: IsMatch 
	* @Description: check the event is valid 
	* @param words
	* @return   
	*/
	boolean IsMatch(String[] words){
		if(words.length < 1)
			return false;
		
		if(words.length == 1){
			//not a number , and not a symbol
			if(this.number_pattern.matcher(words[0]).matches()  || symbleString.contains(words[0])  )
				return false;
		}
		
		return true;
	}
	
	/** 
	* @Title: ParseByStringSplite 
	* @Description: parse line text of file , and save the envent to dictionary
	* @param lineInfo   
	*/
	void ParseByStringSplite(DataFileLineInfo lineInfo){
		String text = lineInfo.Text.trim();
		String[] words = text.split(" ");
		if(IsMatch(words) == false)
			return;
		
		List<Integer> lstEntity = new ArrayList<Integer>();
		for(String tk : words){
			Integer id = this.dict.SaveWord(tk);
			lstEntity.add(id);
		}
		this.dict.SaveEntity(text, (int)lineInfo.FileLineNo);
		SavtEntityToTree(lstEntity, (int)lineInfo.FileLineNo);	
	}
	


	/** 
	* @Title: SavtEntityToTree 
	* @Description: save entity id to dictionary
	* @param lstEntity
	* @param extraData   
	*/
	void SavtEntityToTree(List<Integer> lstEntity, int extraData) {		
		this.getDict().GetDictionary().insert(lstEntity, extraData);
		
		//PrintEntity(lstEntity);
	}



	public void EndVisitFile() {
		// TODO Auto-generated method stub
		
		
		
	}


	public void StartVisitFile() {
		
	}
	
	
	
	/** 
	* @Title: LoadDictionaryFile 
	* @Description: static method to create  dictionary from file.
	* @param dicFile
	* @return   
	*/
	public static CEntityDictionary LoadDictionaryFile(String dicFile){
		
		DictionaryBuilder dicBuilder = new DictionaryBuilder();
		System.out.println("Loading Dictionary " + dicFile + "....");
		PrintStream err = System.err;

		// now make all writes to the System.err stream silent 
		System.setErr(new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		}));
		

		TextDataFileReader r = new TextDataFileReader();
		r.ReadFile(dicFile, "utf-8", dicBuilder);
		
		System.setErr(err); 	
		
		System.out.println("Load Dictionary Complete");
		
		return dicBuilder.getDict();
			
	}

} 
