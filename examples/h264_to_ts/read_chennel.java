package h264_to_ts;
import java.io.*;
import java.security.DigestException;
import java.util.ArrayList;
public class read_chennel 
{
	public static void main(String[] args) throws IOException
	{
	File f=new File("F:\\IVIS_PROJECT\\TestData\\frames\\f0.bin");
	FileInputStream fin=new FileInputStream(f);
	System.out.println(f.length());
	}
	
}
    