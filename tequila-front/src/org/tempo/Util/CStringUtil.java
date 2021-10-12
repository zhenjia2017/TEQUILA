package org.tempo.Util;


public class CStringUtil {
	public static final String QASplitorString = "\\|\\|\\|";
	
	public static String[] GetWordArray(String sentence){
		if(sentence == null || sentence.length() < 1)
			return null;
		
		return ( sentence.split("[ \n\t\r.,;:!?(){}]"));
	}

	/** 
	* @Title: ReplaceWiderBlanks 
	* @Description: replace continue blank characters  to a single blank char
	* @param sentence
	* @return   
	*/
	public static String ReplaceWiderBlanks(String sentence){
		String stmp  = sentence;
		do{
			stmp = stmp.replace("  ", " ");
		}while( stmp.contains("  "));
		
		return stmp;
		
	}
	
	public static int CountInString(String sentence, char ch){
		int nCount = 0;
		for(int i=0; i<sentence.length(); i++){
			char c = sentence.charAt(i);
			if( ch == c)
				nCount++;
		}
		
		return nCount;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] a1 = new String[]{"Hello   world   ???",
				"Helll world? this is new one",
				"Hi   H  world               New world   ?"
		};
		
		for(String s : a1){
			String newS = CStringUtil.ReplaceWiderBlanks(s);
			System.out.println(newS + " ||||" + s);

		}
		
	}

}

