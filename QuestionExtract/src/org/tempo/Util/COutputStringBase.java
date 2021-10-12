package org.tempo.Util;

/** 
* @ClassName: COutputStringBase 
* @Description: a base class to output strings.
*  
*/
class COutputStringBase implements IOutputString{

	IOutputString nextHandle;
	
	public COutputStringBase(IOutputString next){
		this.nextHandle = next;
	}
	

	/**   
	 * <p>Title: Write</p>   
	 * <p>Description: write without new line ending </p>   
	 * @param s   
	 * @see org.tempo.Util.IOutputString#Write(java.lang.String)   
	 */ 
	public void Write(String s) {
		// TODO Auto-generated method stub
		if(nextHandle != null)
			this.nextHandle.Write(s);
	}


	/**   
	 * <p>Title: WriteLine</p>   
	 * <p>Description: write string with a new line ending</p>   
	 * @param s   
	 * @see org.tempo.Util.IOutputString#WriteLine(java.lang.String)   
	 */ 
	public void WriteLine(String s) {
		// TODO Auto-generated method stub
		if(nextHandle != null)
			this.nextHandle.WriteLine(s);		
	}
	
}
