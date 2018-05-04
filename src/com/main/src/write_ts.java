package com.main.src;
import java.io.*;
class write_ts
{
@SuppressWarnings("resource")
public void writeData(byte[] buffer,int index) throws FileNotFoundException,IOException
{ 
	String path="F:\\testData"+index+".ts";
	File f=new File(path);
	FileOutputStream fout=new FileOutputStream(f,true);
	fout.write(buffer);
		
}
public static void main(String args[])
{
	
}
}
	