package org.tempo.SentenceAnalysis.TimeTool;

import java.util.HashMap;
import java.util.regex.Pattern;



/** 
* @ClassName: CTimeX3ToDateValue 
* @Description: the class is to convert TimeX3 string to a CDateValue object
*  
*/
public class CTimeX3ToDateValue {

	
	public static final String  Year="YYYY";
	public static final String  YearMon="YYYY-MM";
	public static final String  YearMonDay="YYYY-MM-DD";
	public static final String  YearSeazon="YYYY-Seazon";
	public static final String  BC_ADYear="bcadYear";
	public static final String  YearToYear="YtoY";
	public static final String  RefDate="REF";
	public static final String  ExplicitDate="Explicit";
	
	
	/** TimePatternTable: time string patterns */  
	HashMap<Pattern,String> TimePatternTable;
	
	
	/** convertTimeHandle: processes to convert timex3 string  */  
	CTimeDateConvertBase  convertTimeHandle;
	
	protected CTimeX3ToDateValue(){
		InitTypePattern();
		InitTimeConvertHandles();
	}
	
	/** 
	* @Title: InitTypePattern 
	* @Description: create patterns map  
	*/
	void InitTypePattern(){
		String[][]patternRules = new String[][]{
			{Year, "^\\d{1,4}$"},
			{YearMon, "^\\d{1,4}-\\d{1,2}$"},
			{YearMonDay, "^\\d{1,4}-\\d{1,2}-\\d{1,2}.*$"},
			{YearSeazon, "^\\d{1,4}-(SP|SU|AU|WI)$"},
			{BC_ADYear, "^(BC|AD)\\d{1,4}$"},
			{YearToYear, "^\\d{2,4}-\\d{3,4}$"},
			{RefDate, "^(PRESENT_REF|PAST_REF)$"},
			{ExplicitDate, "^(EXPLICIT)$"},			
		};
		
		
		this.TimePatternTable  = new HashMap<Pattern, String>();
		for(String[] patterntxt:patternRules){
			Pattern p = Pattern.compile(patterntxt[1]);
			this.TimePatternTable.put(p,  patterntxt[0]);
		}	
	}
	
	/** 
	* @Title: InitTimeConvertHandles 
	* @Description: initial process handles list    
	*/
	void InitTimeConvertHandles(){
		this.convertTimeHandle = new CYearToDate(new CYearMonToDate(
				new CYearMonDayToDate( new CYearToYear(new CYearSeason(new CYearBCAD(new CRefDate( 
						new CExplicitDate(null) )))))));
	}
	
	/** 
	* @Title: Convert 
	* @Description: call convert handle list to convert a timex3 string to a CDateVallue
	* @param timex3String
	* @return   
	*/
	CDateValue Convert(String timex3String){
		String x3Type = this.FindTimex3Type(timex3String);
		if(x3Type == null || x3Type.length() < 1)
			return null;
		
		CTimeX3ConvertContext context = new CTimeX3ConvertContext(timex3String, x3Type);
		this.convertTimeHandle.Process(context);
		
		if( context.fromDate != null ){
			return (new CDateValue(context.fromDate.ToDateString(),
					(context.endDate != null ? context.endDate.ToDateString(): context.fromDate.ToDateString())));
		}
		 
		return null;
	}
	
	/** 
	* @Title: FindTimex3Type 
	* @Description: find the pattern that match the string
	* @param timex3String
	* @return   
	*/
	String FindTimex3Type(String timex3String){
		
		Pattern pfind = null;
		for(Pattern p: this.TimePatternTable.keySet()){
			if(p.matcher(timex3String).find()){
				pfind = p;
				break;
			}
		}
		
		if(pfind == null)
			return "";
		
		return this.TimePatternTable.getOrDefault(pfind, "");
	}
	
	/** 
	 * create a single instance.
	 */
	static CTimeX3ToDateValue _instance;
	static CTimeX3ToDateValue getInstance(){
		if(_instance == null){
			_instance = new CTimeX3ToDateValue();
		}
		return _instance;
	}
	
	
	/** 
	* @Title: GetDateTimeValue 
	* @Description: static method of converting for call
	* @param timex3String
	* @return   
	*/
	public static CDateValue  GetDateTimeValue(String timex3String){
		CTimeX3ToDateValue conveter = CTimeX3ToDateValue.getInstance();
		return conveter.Convert(timex3String);		
	}
	
	
  // test the convert class
	public static void main(String[] args) {
//		{Year, "<\\d{1,4}>"},
//		{YearMon, "<\\d{1,4}-\\d{1,2}>"},
//		{YearMonDay, "<\\d{1,4}-\\d{1,2}.+>"},
//		{YearSeazon, "<\\d{1,4}-(SP|SU|AU|WI)>"},
//		{BC_ADYear, "<(BC|AD)\\d{1,4}>"},
//		{YearToYear, "<\\d{2,4}-\\d{2,4}>"},
//		{RefDate, "<(PRESENT_REF|PAST_REF)>"},
//		{ExplicitDate, "<EXPLICIT>"},	
		
		
		// TODO Auto-generated method stub
		String[]  dateStrings = new String[]{
			"1998",
			"1998-01",	
			"1998-2",	
			"1998-12",	
			"1998-2016",	
			"1998-SP",		
			"1998-SU",
			"1998-AU",
			"1998-WI",
			"2017-06-21TNI",
			"2017-06-21T13:24",
			"AD030",			
			"BC030",	
			"PRESENT_REF",
			"PAST_REF",
			"EXPLICIT"
		};
		
		for(String x3String : dateStrings){
			CDateValue  dt = GetDateTimeValue(x3String);
			if( dt == null) {
				System.out.println(String.format("%s : Can't convert!!", x3String));	
				continue;
			}
			
			if( dt.IsDateRange()){
				System.out.println(String.format("%s : {from %s to %s}", x3String, dt.getStartDateString(), dt.getEndDateString()));	
			}else{
				System.out.println(String.format("%s :  %s", x3String, dt.getStartDateString()));	
			}
		}
		
		

	}

}

