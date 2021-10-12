package org.tempo.SentenceAnalysis.TimeTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.tempo.Util.CGlobalConfiguration;


/** 
* @ClassName: TimeTagProcessBase 
* @Description: a process base class to convert Time tag(Heildtime Tag, SUTime Tag) to CTempralTag tag
*  
*/
public class TimeTagProcessBase {

	protected  DateTime baselineDate ;
	
	public  Date getBaselineDate() {
		return baselineDate.toDate();
	}

	public  void setBaselineDate(DateTime baselineDate) {
		this.baselineDate = baselineDate;
	}
	
	protected List<CTempralTag> TempralTags;

	public List<CTempralTag> getTempralTags() {
		return TempralTags;
	}

	public TimeTagProcessBase(){
		this.setBaselineDate( this.getDate((CGlobalConfiguration.CurrentRefDate)));
		this.TempralTags = new ArrayList<CTempralTag>();
	}
	
	DateTime getDate(String timeString){
		    
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        try
	        {
	            Date date = simpleDateFormat.parse(timeString);
	            return (new DateTime(date));
	           // System.out.println("date : "+simpleDateFormat.format(date));
	        }
	        catch (ParseException ex)
	        {
	            return DateTime.now();
	        }
	}
	
	
	public void Initialize(){
	
	}
	public String Process(String document){
		return "";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
