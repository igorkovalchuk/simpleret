package com.googlecode.simpleret.example;

public class Gamma {

	public void start() {
		System.out.println("... in Gamma start");
		inside1();
	}
	
	public void inside1() {
		System.out.println("... in Gamma inside1");
		inside2();
	}

	public void inside2() {
		System.out.println("... in Gamma inside2");
	}

}
