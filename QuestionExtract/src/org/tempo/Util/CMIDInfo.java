package org.tempo.Util;

public class CMIDInfo {


	public String MID;
	public String Name;
	public String HttpLink;
	public String Info;
	
	public CMIDInfo(){
		this.MID = "";
		this.HttpLink ="";
		this.Info = "";
		this.Name = "";
	}
	
	public CMIDInfo(String mid, String name,String link, String info){
		this.MID = mid;
		this.Name = name;
		this.HttpLink = link;
		this.Info = info ;		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("mid:%s, name:%s,httplink:%s, info:%s", this.MID, this.Name, this.HttpLink, this.Info);
	}
	

}
