package org.tempo.SentenceAnalysis.TimeTool;

import java.util.Calendar;


import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.Util.CGlobalConfiguration;

/** 
* @ClassName: CTimeX3ConvertContext 
* @Description: a context class to convert Timex3 label
*  
*/
class CTimeX3ConvertContext{
	public YMD  fromDate;
	public YMD  endDate;
	public String TimeX3String;
	public String TimeX3Type;
	public CTimeX3ConvertContext(String x3String, String typeString){
		this.TimeX3String = x3String;
		this.TimeX3Type = typeString;
	}
}


/** 
* @ClassName: YMD 
* @Description: a class to store year, month, day
*  
*/
class YMD{
	public int Year;
	public int Month;
	public int Day;
	public YMD(int y, int m, int d){
		this.Year = y;
		this.Month = m;
		this.Day = d;
	}
	
	public String ToDateString(){
		return (this.Year + "-" + this.Month + "-" + this.Day);
	}
}



class CYearToDate extends CTimeDateConvertBase {
	public CYearToDate(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.Year, next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {

		context.fromDate = this.getYMD(context.TimeX3String);
		context.endDate = new YMD(context.fromDate.Year, 12, 31);		
	}
}


class CYearMonToDate extends CTimeDateConvertBase {
	public CYearMonToDate(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.YearMon,  next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		context.fromDate = this.getYMD(context.TimeX3String);
		int lastMonDay = this.getLastDayOfMonth(context.fromDate.Year, context.fromDate.Month);
		context.endDate = new YMD(context.fromDate.Year, context.fromDate.Month, lastMonDay);
		
	}
}

class CYearMonDayToDate extends CTimeDateConvertBase {
	public CYearMonDayToDate(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.YearMonDay, next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		String day = this.RemoveEndingChar(context.TimeX3String);
		
		context.fromDate = this.getYMD(day);
		//context.endDate = context.fromDate;
	}
	
	String RemoveEndingChar(String X3String){
		String strDay = X3String;
		char c =  strDay.charAt(strDay.length()-1);
		while( Character.isAlphabetic(c) ){
			strDay = strDay.substring(0, strDay.length()-1);	
			c =  strDay.charAt(strDay.length()-1);
		}
		return strDay;
	}
}

class CYearToYear extends CTimeDateConvertBase {
	public CYearToYear(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.YearToYear, next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		String[]years = context.TimeX3String.split("-");
		YMD firstYear = this.getYMD(years[0]);
		YMD secondYear = this.getYMD(years[1]);
		
		context.fromDate = new YMD(firstYear.Year, 1, 1);
		context.endDate = new YMD(secondYear.Year, 12, 31);
		
	}
}

class CYearSeason extends CTimeDateConvertBase {
	public CYearSeason( INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.YearSeazon, next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		String[]year_season = context.TimeX3String.split("-");
		
		YMD year = this.getYMD(year_season[0]);
		int[] season = GetSeasonMonth(year_season[1]);
		
		context.fromDate = new YMD(year.Year, season[0], 1);
		context.endDate = new YMD(year.Year, season[1], this.getLastDayOfMonth(year.Year, season[1]));		
	}
	
	int [] GetSeasonMonth(String season){
		int [] monthStartEnd = new int[2];
		if(season.compareTo("SP") == 0){
			monthStartEnd[0]=1;
			monthStartEnd[1]=3;			
		}else if(season.compareTo("SU") == 0){
			monthStartEnd[0]=4;
			monthStartEnd[1]=6;				
		}else if(season.compareTo("AU") == 0){
			monthStartEnd[0]=7;
			monthStartEnd[1]=9;			
		}else { //"WI"
			monthStartEnd[0]=10;
			monthStartEnd[1]=12;			
		}
		
		return monthStartEnd;		
	}
	
	
}

class CYearBCAD extends CTimeDateConvertBase {
	public CYearBCAD(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.BC_ADYear,next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		String adbc = context.TimeX3String.substring(0,2);
		String yearString = context.TimeX3String.substring(2);
		
		YMD year = this.getYMD(yearString);
		if(adbc.compareTo("BC") == 0)
			year.Year = - year.Year;
		
		context.fromDate = new YMD(year.Year, 1, 1);
		context.endDate = new YMD(year.Year, 12, 31);		
	}
}



class CRefDate extends CTimeDateConvertBase {
	public CRefDate(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.RefDate,next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		//DateTime dt = DateTime.now();
		context.fromDate = this.getYMD(CGlobalConfiguration.CurrentRefDate);
		//context.endDate = context.fromDate;	
	}
}

class CExplicitDate extends CTimeDateConvertBase {
	public CExplicitDate(INodeProcessor<CTimeX3ConvertContext> next){
		super(CTimeX3ToDateValue.ExplicitDate,next);
	}

	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
		
		
	}
}


/** 
* @ClassName: CTimeDateConvertBase 
* @Description: base class to convert timex3 string
*  
*/
/** 
* @ClassName: CTimeDateConvertBase 
* @Description: TODO
*/
class CTimeDateConvertBase  extends CNodeProcessorBase<CTimeX3ConvertContext>{
	protected String matchTypeString;
	
	public CTimeDateConvertBase(String matchtype, INodeProcessor<CTimeX3ConvertContext> next){
		super(next);
		this.matchTypeString = matchtype;
	}
	
	/**   
	 * <p>Title: CanProcess</p>   
	 * <p>Description: Judge if the context match the condition  </p>   
	 * @param context
	 * @return   
	 * @see org.tempo.SentenceAnalysis.CNodeProcessorBase#CanProcess(java.lang.Object)   
	 */ 
	@Override
	protected boolean CanProcess(CTimeX3ConvertContext context) {
		// TODO Auto-generated method stub
		if  ( context == null || context.TimeX3String ==null || context.TimeX3String.length()< 1)
			return false;
		
		if(matchTypeString == null || context.TimeX3Type == null || context.fromDate != null)
			return false;
		
		return (context.TimeX3Type.compareTo(matchTypeString) == 0);
	}
	
	@Override
	protected void NodeProcess(CTimeX3ConvertContext context) {
	}
	
	/** 
	* @Title: getYMD 
	* @Description: convert the year-month-day to YMD object.
	* @param yearMonDay
	* @return   
	*/
	YMD getYMD(String yearMonDay){
		String[] dates=extendArray(yearMonDay.split("-"), 3);
		return getYMD(dates[0], dates[1], dates[2]);		
	}

	/** 
	* @Title: getYMD 
	* @Description: create a YMD object by year,month,day string.
	* @param year
	* @param month
	* @param day
	* @return   
	*/
	YMD getYMD(String year, String month, String day){
		
		int	nY = getInt(year,4);
		int	nM = getInt(month,2);
		if(nM>12){
			nM = 12;
		}
		
		if(nM<1)
			return (new YMD(nY, 1, 1));		
		
		int nLastMonDay = getLastDayOfMonth(nY, nM);
		int nD = getInt(day,2);
		
		if( nD<1)
			return (new YMD(nY, nM, 1));		
		
		if(nD > nLastMonDay){
			nD = nLastMonDay;
		}

		return (new YMD(nY, nM, nD));		
	}
	
	/** 
	* @Title: getInt 
	* @Description: convert a string to a integer
	* @param str
	* @param maxLen
	* @return   
	*/
	int getInt(String str, int maxLen){
		if(str != null && str.length()>0){
			if(str.length()>maxLen)
				return Integer.parseInt(str.substring(0,maxLen));
			else
				return Integer.parseInt(str);
		}
		
		return -1;
	}
	
	
	/** 
	* @Title: extendArray 
	* @Description: copy a string array to longer array.
	* @param items
	* @param maxArrayLen
	* @return   
	*/
	String[] extendArray(String[] items, int maxArrayLen){
		String[] newArray = new String[maxArrayLen];
		if(items.length >= maxArrayLen){
			for(int i = 0; i<maxArrayLen; i++)
				newArray[i]=items[i];
		}else{
			for(int i = 0; i<items.length; i++)
				newArray[i]=items[i];
			
			for(int i = items.length; i<maxArrayLen; i++){
				newArray[i]="";
			}
		}
		
		return newArray;		
	}
	
	   /** 
	* @Title: getLastDayOfMonth 
	* @Description: get the last day of a month
	* @param year
	* @param month
	* @return   
	*/
	int getLastDayOfMonth(int year,int month)
	    {
	        Calendar cal = Calendar.getInstance();

	        cal.set(Calendar.YEAR,year);
	
	        cal.set(Calendar.MONTH, month-1);
	        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	        
	        return lastDay;
	    }
	

}
