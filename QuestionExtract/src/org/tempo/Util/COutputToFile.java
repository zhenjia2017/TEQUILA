package org.tempo.Util;

import org.tempo.DataFileReaders.TextDataFileWriter;

/** 
* @ClassName: COutputToFile 
* @Description: output the string to a file.
*/
public class COutputToFile extends COutputStringBase{

	TextDataFileWriter  fileWriter;
	
	public TextDataFileWriter getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(TextDataFileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

	
	public COutputToFile(IOutputString next){
		super(next);
	}

	@Override
	public void Write(String s) {
		// TODO Auto-generated method stub
		if(this.fileWriter!=null){
			this.fileWriter.Write(s);			
		}
		
		super.Write(s);
	}

	@Override
	public void WriteLine(String s) {
		// TODO Auto-generated method stub
		if(this.fileWriter!=null){
			this.fileWriter.WriteLine(s);
			//this.fileWriter.
		}
		
		super.WriteLine(s);		
	}
	
}

