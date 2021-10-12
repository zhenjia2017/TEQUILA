package org.tempo.DataFileReaders;

public interface IFileVistor {
	
	/** 
	* @Title: EndVisitFile 
	* @Description: trigger at the end of the file    
	*/
	void EndVisitFile(); 
	/** 
	* @Title: StartVisitFile 
	* @Description: trigger at the beginning of the file.   
	*/
	void StartVisitFile() ; //
	/** 
	* @Title: Visit 
	* @Description: read each line of the file
	* @param lineInfo   
	*/
	void Visit(DataFileLineInfo lineInfo) ; 
	
}
