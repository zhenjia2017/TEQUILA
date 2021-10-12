package org.tempo.SentenceAnalysis.TimeTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/** 
* @ClassName: CDateValue 
* @Description: a class is to operate a date , or a date range.
*  
*/
public class CDateValue {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(this.endDate == null || this.startDate.compareTo(this.endDate) == 0 ){
			return (this.getStartDateString());
		}else{
			return ( "from " + this.getStartDateString() + " to " + this.getEndDateString());
		}
	}
	
		/** dateFormate: the formatter to format date to string */  
	static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");  
	
	/** startDate:the start date */  
	Date startDate;
	public Date getStartDate() {
		return startDate;
	}	
	
	/** 
	* @Title: getStartDateString 
	* @Description: get the string of start date
	* @return   
	*/
	public String getStartDateString() {
		return DateToString(this.startDate);
	}
	
	/** endDate: the end date for a date range */  
	Date endDate;
	public Date getEndDate() {
		return endDate;
	}

	/** 
	* @Title: getEndDateString 
	* @Description: get the end date string.
	* @return   
	*/
	public String getEndDateString() {
		return DateToString(endDate);
	}


	/** 
	* <p>Title: </p> 
	* <p>Description: construct  as a date </p> 
	* @param start 
	*/
	public CDateValue(String start){
		this.startDate = StringToDate(start);
	}	
	
	/** 
	* <p>Title: </p> 
	* <p>Description:construct as a date range by  string</p> 
	* @param start
	* @param end 
	*/
	public CDateValue(String start, String end){
		this.startDate = StringToDate(start);
		this.endDate = StringToDate(end);
	}	
	
	
	/** 
	* <p>Title: </p> 
	* <p>Description:construct as a date range </p> 
	* @param start
	* @param end 
	*/
	public CDateValue(Date start, Date end){
		this.startDate = start;
		this.endDate = end;
	}
	
	/** 
	* @Title: DateToString 
	* @Description: convert date to string
	* @param dt
	* @return   
	*/
	String DateToString(Date dt){
		if(dt == null)
			return "";
		
		return dateFormate.format(dt);
	}
	
	/** 
	* @Title: StringToDate 
	* @Description: convert a string to Date
	* @param dateString
	* @return   
	*/
	Date StringToDate(String dateString) {
		if(dateString==null || dateString.length()<2)
			return null;
		
		Date date = null; 
		try {  
		    date = dateFormate.parse(dateString); 
		} catch (ParseException e) {  
		    e.printStackTrace();  
		}
		
		return date;
	}
		
	/** 
	* @Title: IsDateRange 
	* @Description: Is this object a date range
	* @return   
	*/
	public boolean IsDateRange(){
		return (this.endDate !=null);
	}
	
	/** 
	* @Title: Before 
	* @Description: is first date before the second date 
	* @param firstDate
	* @param secondDate
	* @return   
	*/
	public static boolean Before(CDateValue firstDate, CDateValue secondDate){
		return firstDate.Before(secondDate.getStartDate(), secondDate.getEndDate());
	}
	
	
	/** 
	* @Title: After 
	* @Description: is first date After the second date 
	* @param firstDate
	* @param secondDate
	* @return   
	*/
	public static boolean After(CDateValue firstDate, CDateValue secondDate){
		return firstDate.After(secondDate.getStartDate(), secondDate.getEndDate());
	}
	
	
	
	/** 
	* @Title: Before 
	* @Description: judge if this date or date range is before the giving date  or date range
	* @param from_Date
	* @param end_Date
	* @return   
	*/
	public boolean Before(Date from_Date, Date end_Date){
		boolean breturn = true;
		
		if(from_Date != null)
			breturn = breturn && this.BeforeDate(this.endDate,from_Date);
		
		if(end_Date != null)
			breturn = breturn && this.BeforeDate(this.endDate, end_Date);
		
		return breturn;
		
	}
	/** 
	* @Title: After 
	* @Description: judge if this date or date range is after the giving date  or date range
	* @param from_Date
	* @param end_Date
	* @return   
	*/
	public boolean After(Date from_Date, Date end_Date){
		boolean breturn = true;
		
		if(from_Date != null)
			breturn = breturn && this.AfterDate(this.startDate, from_Date);
		
		if(end_Date != null)
			breturn = breturn && this.AfterDate(this.startDate, end_Date);
		
		return breturn;
	}
	
	/** 
	* @Title: During 
	* @Description: judge if this date or date range intersects the giving date  or date range
	* @param from_Date
	* @param end_Date
	* @return   
	*/
	public boolean During(Date from_Date, Date end_Date){
		if( from_Date !=null && end_Date==null){
			
			//return (this.BeforeDate(from_Date, this.endDate) && this.AfterDate(from_Date, this.startDate));
			return (this.BeforeDate(from_Date, this.endDate) );
			
		}else if(from_Date ==null && end_Date!=null){
			
			//return (this.AfterDate( end_Date, this.startDate) && this.BeforeDate(end_Date, this.endDate));
			return (this.AfterDate( end_Date, this.startDate) );
			
		}else{		
	
			if(this.BeforeDate(this.startDate, from_Date) && this.AfterDate(this.endDate,from_Date)
				&& this.BeforeDate(this.startDate, end_Date) && this.AfterDate(this.endDate,end_Date)){
				return true;
			}
			
			if(this.AfterDate(this.endDate, from_Date) && this.BeforeDate(this.endDate,end_Date))
				return true;
		
			if(this.AfterDate(this.startDate, from_Date) && this.BeforeDate(this.startDate,end_Date))
				return true;
			
			
			return false;
		}
		
	}
	
	/** 
	* @Title: getMiniIntervalDays 
	* @Description: get the interval days between two date range
	* @param from_Date
	* @param end_Date
	* @return   
	*/
	public long getIntervalDays(Date from_Date, Date end_Date){
		long fromInternal = -1, endInternal = -1;
		if(from_Date != null){
			//the interval days between two start dates.
			fromInternal = this.getDaysBetweenDate(this.startDate, from_Date);			
		}
		
		if(end_Date != null){
			// the interval days between two end dates.
			endInternal = this.getDaysBetweenDate(this.endDate, end_Date);	
		}
		//return the  little one
		if(fromInternal>0 && endInternal>0){
			return ( (fromInternal < endInternal) ? fromInternal: endInternal);
		}else if(endInternal>0)
			return endInternal;
		else
			return fromInternal;
		
	}
	
	/** 
	* @Title: getDaysBetweenDate 
	* @Description: get the days between two date
	* @param beginTime
	* @param endTime
	* @return   
	*/
	long getDaysBetweenDate(Date beginTime, Date endTime){
		return((long) Math.abs(((endTime.getTime() - beginTime.getTime()) / (1000 * 60 * 60 *24) + 0.5))); 
	}
	
	/** 
	* @Title: AfterDate 
	* @Description: compare two date
	* @param srcDate
	* @param cmpDate
	* @return   
	*/
	boolean  AfterDate(Date srcDate, Date cmpDate){
		if( srcDate == null ) 
			return false;		
		if(cmpDate == null)	
			return true;
		
		return (srcDate.getTime() >= cmpDate.getTime());
	}
	
	/** 
	* @Title: BeforeDate 
	* @Description: compare two dates
	* @param srcDate
	* @param cmpDate
	* @return   
	*/
	boolean BeforeDate(Date srcDate, Date cmpDate){
		if( srcDate == null ) 
			return false;		
		if(cmpDate == null)	
			return true;
		return (srcDate.getTime() <= cmpDate.getTime());
	}
	

}
