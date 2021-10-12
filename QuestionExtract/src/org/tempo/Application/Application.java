package org.tempo.Application;

import de.unihd.dbs.heideltime.standalone.CLISwitch;

public class Application {

	public static void main(String[] args) {

		for(int i = 0; i < args.length; i++) { // iterate over cli parameter tokens
			if(args[i].startsWith("-")) { // assume we found a switch
				// get the relevant enum
				CLISwitch sw = CLISwitch.getEnumFromSwitch(args[i]);
				if(sw == null) { // unsupported CLI switch
					System.out.println("Warning:Unsupported switch: "+args[i]+". Quitting.");
					System.exit(-1);
				}
				
				if(sw.getHasFollowingValue()) { // handle values for switches
					if(args.length > i+1 && !args[i+1].startsWith("-")) { // we still have an array index after this one and it's not a switch
						sw.setValue(args[++i]);
					} else { // value is missing or malformed
						System.out.println( "Warning:Invalid or missing parameter after "+args[i]+". Quitting.");
						System.exit(-1);
					}
				} else { // activate the value-less switches
					sw.setValue(null);
				}
			}
		}
		
	
	}
	
	static void InitByOnlineMode(){
		
	}
	
	static void InitByOffLineMode(){
		
	}
	
	static void InitToGenerateOffLineRequestFile(){
		
	}
	

}
