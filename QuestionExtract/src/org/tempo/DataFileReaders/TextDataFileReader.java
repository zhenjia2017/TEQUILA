package org.tempo.DataFileReaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/** 
* @ClassName: TextDataFileReader 
* @Description: TODO(....) 
*  
*/
public class TextDataFileReader {
	/** m_filename: current reading file name. */  
	String m_filename;
	
	/** m_buf:a buffer to store read text */  
	BufferedReader m_buf;
	
	
	/** Encoding:file encoding, default is utf-8 */  
	String Encoding;
	
	/** m_CurLineNo: current read line No. */  
	long m_CurLineNo;
	
	/** m_CurLineInfo: current line text.*/  
	String m_CurLineInfo;
	
	/** MaxLineNumber: can read maximum lines. */  
	long MaxLineNumber = -1;

	public void setMaxLineNumber(long maxLineNumber) {
		MaxLineNumber = maxLineNumber;
	}
/**
 * SkipLines : the reader will skip the first lines of a file.
 */
	long SkipLines =-1;
	public void setSkipLines(long skipLines) {
		SkipLines = skipLines;
	}	

	/** PrintPerLine: every lines number when print line info . -1 means never print. */  
	long PrintPerLine = -1;
	

	/** 
	* @Title: setPrintPerLine 
	* @Description: set the interval lines for print
	* @param printPerLine   
	*/
	public void setPrintPerLine(int printPerLine) {
		PrintPerLine = printPerLine;
	}


	public TextDataFileReader(){
		this.m_filename ="";
		this.m_buf = null;
		this.Encoding = "utf-8";
	}	
	

	/** 
	* @Title: ReadFile 
	* @Description: TODO
	* @param filePath, the file path.
	* @param encoding, file encoding
	* @param vistor, file visitor object.
	* @return boolean , true for success, false for fail.
	*/
	public boolean ReadFile(String filePath, String encoding, IFileVistor vistor){
		this.Encoding = encoding;
		if( ! _OpenFile(filePath))
			return false;
		
		//trigger the start file function.
		vistor.StartVisitFile();
		DataFileLineInfo lineInfo = null;
		
		//read every line to end;
		do{
			lineInfo = ReadLine();
			
			if(lineInfo != null){
				if(this.SkipLines>0 && lineInfo.FileLineNo <= this.SkipLines )
					continue;
				
				//check if read maximum lines.
				if( this.MaxLineNumber >0 && lineInfo.FileLineNo > this.MaxLineNumber)
					break;
				
				//print the line info if have set PrintPerLine 
				if( this.PrintPerLine > 0 &&  lineInfo.FileLineNo % this.PrintPerLine  == 0 )
					System.out.println("read " + lineInfo.FileLineNo);
				
				vistor.Visit(lineInfo);
			}
				
		}while(lineInfo != null);
		
		//Trigger the end file function.
		vistor.EndVisitFile();
		_Close();
		
		return true;
	}
	
	
	/** 
	* @Title: OpenFile 
	* @Description: internal open file function.
	* @param filePath
	* @return  boolean, true for success, false for fail.
	*/
	boolean _OpenFile(String filePath){
		 File file=new File(filePath);
		 if( file.isFile() && file.exists() == false)
				 return false;		 
		 try{		 
	        m_filename= filePath;
	        InputStreamReader read = new InputStreamReader(new FileInputStream(file),this.Encoding);
	        m_buf = new BufferedReader(read);
		 }        
      catch(Exception e){
          e.printStackTrace();
      }
		 
	//reset current  info variable
	  this.m_CurLineNo = 0;
	  
	  return (m_buf != null);
	}
	

	/** 
	* @Title: _Close 
	* @Description: internal close file function.
	* @param    
	* @return void    
	* @throws 
	*/
	void _Close()	{
		if(m_buf != null)
		{
			try{
				m_buf.close();
			}
			catch(Exception e){
	            //System.out.println("read file error!");
	            e.printStackTrace();				
			}
			finally{
				m_buf = null;
				m_filename = "";
				m_CurLineNo=0;
				m_CurLineInfo="";
			}
		}
	}
	

	/** 
	* @Title: ReadLine 
	* @Description: create a line info object for current line.
	* @param @return   
	* @return DataFileLineInfo    
	* @throws 
	*/
	DataFileLineInfo ReadLine( ){
		DataFileLineInfo item = null;
		
		try{
	        this.m_CurLineInfo = m_buf.readLine();
	        this.m_CurLineNo++;
	        
	        if (this.m_CurLineInfo == null)
	        	return null;
	        
	        item = new DataFileLineInfo();
	        item.FileLineNo = this.m_CurLineNo;
	        item.Text = this.m_CurLineInfo;
	        item.FileName = this.m_filename;
	        
		 } catch (Exception e) {
	            System.out.println("read file error!");
	            e.printStackTrace();
	        }
		
		return item;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TextDataFileReader r = new TextDataFileReader();
		r.setMaxLineNumber(1000);
		r.ReadFile("C:\\temp\\compose_test_resut_3233_simple4ruleNew.txt", "utf-8", new PrintLineToConsoleVistor());		
	}
	
}





