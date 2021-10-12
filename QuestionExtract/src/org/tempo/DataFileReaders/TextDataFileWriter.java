package org.tempo.DataFileReaders;
import java.io.*;

/** 
* @ClassName: TextDataFileWriter 
* @Description: The class is to write text into a file.
*  
*/
public class TextDataFileWriter {

	
	/** __writeTimes:after write N times, writing flush to file .*/  
	int __writeTimes=0;
	
	/** writer: write file stream */  
	OutputStreamWriter writer;
	
	/** m_filename:file name to be written */  
	String m_filename;
	
	
	/** m_bufWriter:buffer writer for output */  
	BufferedWriter m_bufWriter;
	
	
	/** AppendMode: how to operate the file, true: append text to the file, false: open and clean  file */  
	public boolean AppendMode = true;
	
	public TextDataFileWriter(){
		
	}
	

	
	/**   
	 * <p>Title: OpenFile</p>   
	 * <p>Description: Open a file </p>   
	 * @param filePath
	 * @return   
	 * @see org.tempo.DataFileReaders.IDataRecorder#OpenFile(java.lang.String)   
	 */ 
	public boolean OpenFile(String filePath){
		 File file=new File(filePath);
		 if( file.exists() && file.isDirectory())
				 return false;		 
		 try{		 

			 m_filename= filePath;
			 OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, this.AppendMode),"utf-8");//¿¼ÂÇµ½±àÂë¸ñÊ½
	         this.m_bufWriter = new BufferedWriter(writer);
		 }        
     catch(Exception e){
//         System.out.println("read file error!");
         e.printStackTrace();
         //throw e;
     }
	 
	  return (this.m_bufWriter != null);
	}

	
	/**   
	 * <p>Title: Close</p>   
	 * <p>Description: close a file  </p>      
	 * @see org.tempo.DataFileReaders.IDataRecorder#Close()   
	 */ 
	public void Close()	{
		if(m_bufWriter != null)
		{
			try{
				this.m_bufWriter.flush();
				this.m_bufWriter.close();
			}
			catch(Exception e){
	            //System.out.println("read file error!");
	            e.printStackTrace();				
			}
			finally{				
				this.m_bufWriter = null;
				this.m_filename = "";
			}
		}
	}
	
	/** 
	* @Title: Flush 
	* @Description: force flush to file.   
	*/
	void Flush(){
		if(this.__writeTimes++ > 7){
			try{
				this.m_bufWriter.flush();
				this.__writeTimes = 0;
			}catch(Exception e){
	            //System.out.println("read file error!");
	            e.printStackTrace();				
			}

		}
	}
	
	/** 
	* @Title: Write 
	* @Description: write a string to file
	* @param info   
	*/
	public void Write(String info){
		if( this.m_bufWriter != null && info!=null){
			
			try{
				this.m_bufWriter.write(info);
				//this.m_bufWriter.write("\r\n");
				this.Flush();
			}catch(Exception e){
	            //System.out.println("read file error!");
	            e.printStackTrace();				
			}
			
		}
	}	
	
	/** 
	* @Title: WriteLine 
	* @Description: write a text with a line ending.
	* @param info   
	*/
	public void WriteLine(String info){
		if( this.m_bufWriter != null && info!=null){
			
			try{
				this.m_bufWriter.write(info);
				this.m_bufWriter.write("\r\n");
				this.Flush();
			}catch(Exception e){
	            //System.out.println("read file error!");
	            e.printStackTrace();				
			}
			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TextDataFileWriter tdr = new TextDataFileWriter();
		tdr.OpenFile("c:\\temp\\test_wr1.txt");
		for(int i =0; i < 100; i++){
			tdr.WriteLine("this is Line No :" + (i + 1));
		}
		
		tdr.Close();
		System.out.println("done");
		
	}
	
}
