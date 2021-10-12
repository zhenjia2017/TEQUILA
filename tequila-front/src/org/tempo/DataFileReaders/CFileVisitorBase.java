

package org.tempo.DataFileReaders;


/** 
* @ClassName: CFilePaserBase 
* @Description: base class for visitor to a file 
*  
*/
public class CFileVisitorBase implements IFileVistor {


	public CFileVisitorBase(){

	}

/**
 * 
 * <p>Title: EndVisitFile</p>   
 * <p>Description: when stop visiting the file</p>      
 * @see org.tempo.DataFileReaders.IFileVistor#EndVisitFile()
 */
	public void EndVisitFile() {
		// TODO Auto-generated method stub
		
	}



	/**   
	 * <p>Title: StartVisitFile</p>   
	 * <p>Description: when start to visit file</p>      
	 * @see org.tempo.DataFileReaders.IFileVistor#StartVisitFile()   
	 */ 
	public void StartVisitFile() {
		// TODO Auto-generated method stub
		
	}


	
	/**   
	 * <p>Title: Visit</p>   
	 * <p>Description: deal with text line from file</p>   
	 * @param lineInfo :  the text line information of file  
	 * @see org.tempo.DataFileReaders.IFileVistor#Visit(org.tempo.DataFileReaders.DataFileLineInfo)   
	 */ 
	public void Visit(DataFileLineInfo lineInfo) {
		// TODO Auto-generated method stub

	}
		
}
