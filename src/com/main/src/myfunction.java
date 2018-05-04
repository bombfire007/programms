package com.main.src;
import java.io.File;
import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class myfunction implements Comparator {

	private int getnumber(String s)
	{
		int end=s.lastIndexOf(".");
		int start=0;
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)>=48 && s.charAt(i)<=58)
			{
				break;
			}
			else
				start++;
		}
		if(start==0 && start==end)
			return 0;
		String num=s.substring(start, end);
		return Integer.parseInt(num);
	}
		
	public int compare(Object arg0, Object arg1) {
		
		File f1=(File)arg0;
		File f2=(File)arg1;
		int num1=getnumber(f1.getName());
		int num2=getnumber(f2.getName());
		return num1-num2;
		
	}

}
