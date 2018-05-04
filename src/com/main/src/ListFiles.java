package com.main.src;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
public class ListFiles 
{
	
	public File[] readFiles()throws IOException
	{
		//String name="tsfile123.ts";
		File folder_name=new File("F:\\IVIS_PROJECT\\TestData\\frames");
		File []FolderFiles=folder_name.listFiles();
		sortFiles(FolderFiles);
		return FolderFiles;
	}
	public  void sortFiles(File[] folderFiles) 
	{
	Arrays.sort(folderFiles,new Comparator<File>() 
	{

		@Override
		public int compare(File arg0, File arg1) {
			int num1=getnumber(arg0.getName());
			int num2=getnumber(arg1.getName());
			
			return num1-num2;
		}
		public int getnumber(String name)
		{
			int i;
			int e=name.lastIndexOf('.');
			int s=0;
			while(s<name.length())
			{
				if(name.charAt(s)>=48 && name.charAt(s)<=57)
					break;
				else
					s++;
			}
			String c=name.substring(s, e);
			i=Integer.parseInt(c);
			return i;
		}
		
	});
		
	}
}