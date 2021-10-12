package org.tempo.QuestionAnswer;

import org.tempo.SentenceAnalysis.TimeTool.CDateValue;
import org.tempo.SentenceAnalysis.TimeTool.CTimeX3ToDateValue;

/** 
* @ClassName: CDataInProperty 
* @Description: a class to store Date In Property
*  
*/
public class CDataInProperty {
	static final String mid_Head = "mid:(";
	static final String name_Head = "name:(";	
	static final String tp_Head = "tp:(";	
	static final String date_Head = "date:(";	
	//static final String sim_Head = "sim:(";	
	static final String item_ending = ")";	
	
	public String Name;
	public String MID;
	
	public String getAnswerString(){
		return String.format("%s(%s)", this.Name, this.MID);
	}

	public String TagName;
	public CDateValue Date;
	
	protected CDataInProperty(String name, String mid, String tp, String date){
		this.Name = name;
		this.MID = mid;
		this.TagName= tp;
		this.Date = CTimeX3ToDateValue.GetDateTimeValue(date);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  ( "["+ this.MID + "," + this.Name + ","+ this.Date +","+ this.TagName + "]");
	}	
	
	/** 
	* @Title: Parse 
	* @Description: parse string to CDataInProperty object
	* @param text
	* @return   
	*/
	public static CDataInProperty  Parse(String text){
		String mid = GetItem(mid_Head, item_ending, text);
		String name = GetItem(name_Head, item_ending, text);
		String tag = GetItem(tp_Head, item_ending, text);
		String date = GetItem(date_Head, item_ending, text);
		
		return (new CDataInProperty(name,mid, tag, date));
	}	
	
	/** 
	* @Title: GetItem 
	* @Description: get text of item
	* @param head
	* @param end
	* @param text
	* @return   
	*/
	static String GetItem(String head, String end, String text){
		int pos1 = text.indexOf(head);		
		int pos2 = text.indexOf(end, pos1+1);		
		if(pos1<1 || pos2<1)
			return "";
		
		if(pos2 < (text.length()-end.length())){
			if( text.substring(pos2+end.length()).startsWith(end))
				pos2 += end.length();
		}
		
		return (text.substring(pos1+head.length(), pos2));
	}
	
	
}
