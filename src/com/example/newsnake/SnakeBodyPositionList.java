package com.example.newsnake;

import java.util.ArrayList;

public class SnakeBodyPositionList {
	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i=0; i<10; i++) {
			list.add("" + i);
		}
		System.out.println(list);
	}
}