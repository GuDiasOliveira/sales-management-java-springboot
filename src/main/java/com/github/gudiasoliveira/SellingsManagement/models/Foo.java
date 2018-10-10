package com.github.gudiasoliveira.SellingsManagement.models;

public class Foo {
	private long id;
	private String name;
	private int age;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getPresentation() {
		return String.format("Hello! I'm %s and I am %d years old!", this.getName(), this.getAge());
	}
}
