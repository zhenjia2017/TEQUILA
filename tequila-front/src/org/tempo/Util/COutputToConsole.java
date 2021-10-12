package org.tempo.Util;

/** 
* @ClassName: COutputToConsole 
* @Description: output the string to console.
*  
*/
public class COutputToConsole  extends COutputStringBase{

	public COutputToConsole(IOutputString next){
		super(next);
	}
	
	@Override
	public void Write(String s) {
		// TODO Auto-generated method stub
		System.out.print(s);		
		super.Write(s);		
	}

	@Override
	public void WriteLine(String s) {
		// TODO Auto-generated method stub
		System.out.println(s);		
		super.WriteLine(s);		
	}
	
}
