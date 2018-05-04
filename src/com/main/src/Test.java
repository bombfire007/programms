package com.main.src;
final class Test
{
	final int age;
	final String name;
	Test(int age,String name)
	{
		this.name=name;
		this.age=age;
	}
	public void show() {
		System.out.println(age);
		System.out.println(name);
	}
public static void main(String[] args)
{
	Test t=new Test(23,"arshad hasan");
	Test t1=new Test(24,"ARSHAD HASAN");
	t.show();
	t=t1;
	t.show();
			
}
}