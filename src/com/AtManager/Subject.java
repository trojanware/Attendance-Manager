package com.AtManager;

public class Subject {
	
	private String name;	
	private int totalClases, noPresent;
	
	/*public Subject(String name, int total, int present){
		this.name = name;
		totalClases = total;
		noPresent = present;
	}*/
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setTotalClases(int total){
		totalClases = total;
	}
	
	public void setNoPresent(int present){
		noPresent = present;
	}
	
	public String getSubjectName(){
		return name;
	}
	
	public int getTotalClases(){
		return totalClases;
	}
	
	public int getNoClasesPresent(){
		return noPresent;
	}
	
}
