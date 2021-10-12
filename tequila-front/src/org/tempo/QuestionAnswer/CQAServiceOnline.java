package org.tempo.QuestionAnswer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tempo.Util.CGlobalConfiguration;

/** 
* @ClassName: CQAService 
* @Description: a class to operate QA Service
*  
*/
public class CQAServiceOnline implements IQAService {

	String qaServerUrl;
	static final String QUERY_PARAM ="query";

	public CQAServiceOnline(String severurl){
		this.qaServerUrl = severurl;
	}
	
	
	/**   
	 * <p>Title: QueryAnswer</p>   
	 * <p>Description: get answer of a question </p>   
	 * @param question
	 * @return   
	 * @see org.tempo.QuestionAnswer.IQAService#QueryAnswer(java.lang.String)   
	 */ 
	@Override
	public CQAResult QueryAnswer( String question){
		CQAResult result = QueryAnswer_jsoup( QUERY_PARAM,  question);
		if( result != null) {
			result.Question = question;
			System.out.println(result.Question);
			System.out.println(result.getSparql());
			System.out.println(result.getDefaultAnswers());}
		return result;
	}
	
	/** 
	* @Title: QueryAnswer_jsoup 
	* @Description: post question to online service  by jsoup
	* @param paramName
	* @param paramValue
	* @return   
	*/
	CQAResult QueryAnswer_jsoup(String paramName, String paramValue) {
		Document doc = postQuery_jsoup(paramName, paramValue);
		return this.getAnswerContent(doc);
	}
	
	/** 
	* @Title: QueryAnswer_httpurltool 
	* @Description: post question by httmpurl tool
	* @param paramName
	* @param paramValue
	* @return   
	*/
	CQAResult QueryAnswer_httpurltool(String paramName,String paramValue) {
		String doc = postQuery_htmlurlconnection(paramName, paramValue);
		return this.getAnswerContent(doc);
	}
	
	/** 
	* @Title: postQuery_jsoup 
	* @Description: post question by jsoup
	* @param paramName
	* @param paramValue
	* @return   
	*/
	Document postQuery_jsoup( String paramName, String paramValue){
		
		Document answerDoc  =null;
		
		try{		
			answerDoc = Jsoup.connect(this.qaServerUrl)
					  .data(paramName, paramValue)
					  .userAgent("Mozilla")
					  .timeout(CGlobalConfiguration.HttpTimeout)
					  .post();
			
			
		}catch (Exception e) {
			System.out.println("Post Error!" + e);
			e.printStackTrace();
			
		}
		return answerDoc;
		
		
	}
	

	
	
    /** 
    * @Title: postQuery_htmlurlconnection 
    * @Description: post question by http tool
    * @param paramName
    * @param paramValue
    * @return   
    */
    String postQuery_htmlurlconnection( String paramName, String paramValue) {
    	HttpURLConnection connection=null;
    	DataOutputStream out=null ;
    	BufferedReader reader=null;
    	StringBuilder ResponseBuf = new StringBuilder();
    	
		try {
			URL postUrl = new URL(this.qaServerUrl);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			// Read from the connection. Default is true.
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();
			out = new DataOutputStream(connection.getOutputStream());
			String content = paramName.trim() + "=" + URLEncoder.encode(paramValue, "UTF-8");
			out.writeBytes(content);
			out.flush();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				ResponseBuf.append(line);
			}

		} catch (Exception e) {
			System.out.println("Post Error!" + e);
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				
				if (reader != null) {
					reader.close();
				}
				
				if(connection != null)
					connection.disconnect();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}      
        
       return ResponseBuf.toString();
    }	

    
    /** 
    * @Title: getAnswerContent 
    * @Description: parse answer document to CQAResult
    * @param htmlDocString
    * @return   
    */
    public CQAResult getAnswerContent(String htmlDocString){
    	if(htmlDocString==null || htmlDocString.length() <1)
    		return null;
    	
    	Document doc = Jsoup.parse(htmlDocString);
    	return getAnswerContent(doc);
    }
    
    /** 
    * @Title: getAnswerContent 
    * @Description: parse answer document
    * @param htmlDoc
    * @return   
    */
    CQAResult getAnswerContent(Document htmlDoc){
    	if(htmlDoc == null)
    		return null;
    	
    	Elements divs = htmlDoc.select("dd.link > div");
    	
    	if( divs.size() < 3)
    		return null;

    	
    	Element[]divArray= new Element[28];    	
    	divArray = divs.toArray(divArray);
    	
    	String [] sections = GetDivTexts(divArray);
    	String str = sections[0];
    	System.out.println(str);
    	
    	
    	CQAResult result = CQAResult.Parse( sections);
    	

    	return result;    
 
    }
    
    /** 
    * @Title: GetDivTexts 
    * @Description: get div text
    * @param div
    * @return   
    */
    String [] GetDivTexts(Element[] div){
    	String[] strs = new String[div.length];
    	for(int i =0; i<div.length; i++){
    		strs[i] = getDivText(div[i]);
    	}
    	return strs;
    }
    
    String getDivText(Element div){
    	if( div != null)
    		return div.text();
    	else
    		return "";
    }
    



}





