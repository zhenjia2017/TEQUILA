package org.tempo.Util;

import java.util.HashMap;
import java.util.Map;

import org.tempo.DataFileReaders.CFileVisitorBase;
import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.TextDataFileReader;

public class CMIDInfoProvider extends CFileVisitorBase{
	static CMIDInfoProvider _provider;
	
	public static CMIDInfoProvider getInstance(){
		return _provider;
	}
	
	public static boolean LoadDictionaryFile(String file){
		if(_provider == null){			
			_provider = new CMIDInfoProvider();
		}else{
			_provider.Clear();
		}
		
		TextDataFileReader r = new TextDataFileReader();
		r.ReadFile( file, "utf-8", _provider);
		System.out.println("Load item:" + _provider.getItemCount());
		return (_provider.getItemCount() > 0);
	}
	
	private CMIDInfoProvider(){
		this.midDictionary = new HashMap<String, CMIDInfo>(40000);
	}
	
	
	Map<String, CMIDInfo>  midDictionary;

	private void Clear(){
		this.midDictionary.clear();
	}
	
	public int getItemCount(){
		return this.midDictionary.size();
	}
	
	public CMIDInfo findMID(String mid){
		String key =  getKeyStandardFormat(mid);
		return this.midDictionary.getOrDefault(key, null);
	}
	
	private static String getKeyStandardFormat(String key){
		return key.toLowerCase().trim();
	}
	
	public void Visit(DataFileLineInfo lineInfo) {
		// TODO Auto-generated method stub
		//System.out.println( lineInfo.FileLineNo + "\t: " + lineInfo.Text);
		
		String[] items = lineInfo.Text.split("\\t");
	
		
		if( items != null && items.length >= 2){
			String key = this.getKeyStandardFormat(items[0]);
			if( this.midDictionary.containsKey(key) == false){
				CMIDInfo mid =  new CMIDInfo(key,items[1].trim(), items.length>2 ? items[2].trim():"", items.length>3? items[3].trim():"");
				this.midDictionary.put(key, mid);
			}
		}else{
			System.out.println("Can't analyze line:"+lineInfo.FileLineNo +  "  [" + lineInfo.Text +"]");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CMIDInfoProvider.LoadDictionaryFile(CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\TempQuestions_truecase_allsubquestion_aqqu_mid_name_wiki_des.txt");
		
		String[] keys= new String[]{
				"m.0h_4v3y","m.0h_4v6s","m.0h_j0w8","m.0h_7qww", "skeneksld"
		};
		
		for(String key : keys){
			CMIDInfo item = CMIDInfoProvider.getInstance().findMID(key);
			if(item != null)
				System.out.println(item.toString());
			else
				System.out.println("no info for key:" + key);
		}
		

	}

}
