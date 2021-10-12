package org.tempo.SentenceAnalysis.TimeTool;

import java.util.List;

import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.EnumTagType;

import de.unihd.dbs.uima.types.heideltime.Timex3;
import de.unihd.dbs.uima.types.heideltime.Timex3Interval;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

/** 
* @ClassName: CTempralTag 
* @Description: Tempral Tag to record Time label
*  
*/
public class CTempralTag extends CLabelTag{

	public TempralTagType TagType;
	public String SubType;
//	public String Text;
	public String id;
	public String BeginTime;
	public String EndTime;
	public String Time;
	public String Quant;
	public String Freq;
	public String Mod;
	
	public CTempralTag(TempralTagType tagtype){
		super(EnumTagType.TimeHeilderTime);
		this.TagType = tagtype;
		this.SubType = "";
		this.id = "";
		this.Text="";
		this.BeginTime = "";
		this.EndTime ="";
		this.Time = "";
		this.Quant = "";
		this.Freq="";
		this.Mod ="";
	}

	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder buf = new StringBuilder();
		if(this.TagType == TempralTagType.Interval){
			buf.append("Interval, ");
			buf.append("id=\"" + this.id + "\", ");
			buf.append("type=\"" + this.SubType + "\", ");
			buf.append("BeginPos=\"" + this.StartChar + "\", ");
			buf.append("EndPos=\"" + this.EndChar + "\", ");	
			buf.append("Time=\"" + this.Time +"\", " );
			if( this.BeginTime.length() >1 ){
				buf.append("BeginTime=\"" + this.BeginTime +"\", " );
				buf.append("EndTime=\"" + this.EndTime +"\"" );	
			}
			else
				buf.append("Time=\"" + this.Time +"\"" );	
		}
		else if ( this.TagType == TempralTagType.Time){
			buf.append("Time, ");
			buf.append("id=\"" + this.id + "\", ");
			buf.append("type=\"" + this.SubType + "\", ");
			buf.append("BeginPos=\"" + this.StartChar + "\", ");
			buf.append("EndPos=\"" + this.EndChar + "\", ");
			buf.append("Time=\"" + this.Time +"\", " );
			if(this.Quant!=null && this.Quant.length()>0)
				buf.append("Quant=\"" + this.Quant +"\", " );			
			if(this.Freq!=null && this.Freq.length()>0)
				buf.append("Freq=\"" + this.Freq +"\", " );		
			if(this.Mod!=null && this.Mod.length()>0)
				buf.append("Mod=\"" + this.Mod +"\"" );		
			}
		
		return buf.toString();
	}
	
	

	/** 
	* @Title: ConvertToTempralTag 
	* @Description: convert Timex3Interval lable to CTempralTag
	* @param srcTag
	* @param document
	* @return   
	*/
	public static CTempralTag  ConvertToTempralTag(Timex3Interval srcTag, String document){
		CTempralTag tag = new CTempralTag(TempralTagType.Interval);
		tag.id = "";
		
		tag.Time = srcTag.getTimexValue();
		tag.StartChar = srcTag.getBegin();
		tag.EndChar = srcTag.getEnd();
		tag.Text = document.substring(tag.StartChar, tag.EndChar);
		tag.BeginTime = srcTag.getTimexValueEB();
		tag.EndTime = srcTag.getTimexValueLE();		
		
		return tag;
	}
	
	/** 
	* @Title: ConvertToTempralTag 
	* @Description: convert Timex3 lable to CTempralTag
	* @param srcTag
	* @param document
	* @return   
	*/
	public static CTempralTag  ConvertToTempralTag(Timex3 srcTag, String document){
		CTempralTag tag = new CTempralTag(TempralTagType.Time);
		tag.SubType = srcTag.getTimexType();
		tag.id = srcTag.getTimexId();
		tag.Time = srcTag.getTimexValue();
		tag.StartChar = srcTag.getBegin();
		tag.EndChar = srcTag.getEnd();	
		tag.Text = document.substring(tag.StartChar, tag.EndChar);
		tag.Time = srcTag.getTimexValue();
		tag.Quant = srcTag.getTimexQuant();
		tag.Freq=srcTag.getTimexFreq();
		tag.Mod =srcTag.getTimexFreq();

		
		return tag;
	}	
	
	/** 
	* @Title: ConvertToTempralTag 
	* @Description: convert NLP sumtime tag to TempralTag
	* @param cm
	* @return   
	*/
	public static CTempralTag  ConvertToTempralTag(CoreMap cm){
		CTempralTag tag;
		SUTime.Temporal srcTag = cm.get(TimeExpression.Annotation.class).getTemporal();
		List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);	
		
		tag = new CTempralTag(TempralTagType.Time);
		tag.SubType = srcTag.getTimexType().toString();
		tag.id = srcTag.timeLabel;
		tag.Time = srcTag.getTimexValue();
		tag.StartChar = tokens.get(0).beginPosition();
		tag.EndChar = tokens.get(tokens.size()-1).endPosition();


		tag.Time = ( srcTag.getTimexValue()!=null? srcTag.getTimexValue():"");
		tag.Quant = "";
		tag.Freq="";
		tag.Mod =( srcTag.getMod()!=null? srcTag.getMod():"");

		return tag;
	}		
	
	/** 
	* @Title: Compare 
	* @Description: compare two Tempral Tag
	* @param srcTag
	* @param targetTag
	* @return   
	*/
	public static boolean  Compare(CTempralTag srcTag, CTempralTag targetTag){
		if ( srcTag.TagType != targetTag.TagType)
			return false;
	
		if(srcTag.Time.equalsIgnoreCase(targetTag.Time)==false)
			return false;

		if(srcTag.StartChar != targetTag.StartChar)
			return false;

		if(srcTag.EndChar != targetTag.EndChar)
			return false;

		
		return true;
	}	
	
}
