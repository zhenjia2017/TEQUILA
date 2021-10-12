package org.tempo.DataFileReaders;

public class PrintLineToConsoleVistor extends CFileVisitorBase{
	
	/**   
	 * <p>Title: Visit</p>   
	 * <p>Description: Print each line text to console. this if just for testing.</p>   
	 * @param lineInfo   
	 * @see org.tempo.DataFileReaders.CFileVisitorBase#Visit(org.tempo.DataFileReaders.DataFileLineInfo)   
	 */ 
	public void Visit(DataFileLineInfo lineInfo) {
		// TODO Auto-generated method stub
		System.out.println( lineInfo.FileLineNo + "\t: " + lineInfo.Text);
	}
	
}