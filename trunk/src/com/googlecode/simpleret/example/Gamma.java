package com.googlecode.simpleret.example;

public class Gamma {

	public void start() {
		System.out.println("... in Gamma start");
		inside1();
	}
	
	public void inside1() {
		System.out.println("... in Gamma inside1");
		try {
			inside2();
		} catch (Exception e) {
			System.out.println("... catch Exception in Gamma inside1");
		}
	}

	public void inside2() throws Exception{
		System.out.println("... in Gamma inside2");
		//throw new Exception("test1");
	}

}
