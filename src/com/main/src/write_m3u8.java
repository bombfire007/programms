package com.main.src;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class write_m3u8 {
	String		ext_x_header="#EXTM3U";
	String		ext_x_targetduration="#EXT-X-TARGETDURATION";
	String		ext_x_media_sequence = "#EXT-X-MEDIA-SEQUENCE ";
	String		ext_x_program_date_time = "#EXT-X-PROGRAM-DATE-TIME";
	String		ext_x_media = "#EXT-X-MEDIA";
	String		ext_x_playlist_type = "#EXT-X-PLAYLIST-TYPE";
	String		ext_x_key = "#EXT-X-KEY";
	String		ext_x_stream_inf = "#EXT-X-STREAM-INF";
	String		ext_x_version = "#EXT-X-VERSION";
	String		ext_x_allow_cache = "#EXT-X-ALLOW-CACHE";
	String		ext_x_endlist = "#EXT-X-ENDLIST";
	String		extinf = "#EXTINF";
	String		ext_i_frames_only = "#EXT-X-I-FRAMES-ONLY";
	String		ext_x_byterange = "#EXT-X-BYTERANGE";
	String		ext_x_i_frame_stream_inf = "#EXT-X-I-FRAME-STREAM-INF";
	String		ext_x_discontinuity = "#EXT-X-DISCONTINUITY";
	String		ext_x_cue_out_start = "#EXT-X-CUE-OUT";
	String		ext_x_cue_out = "#EXT-X-CUE-OUT-CONT" ;
	String		ext_is_independent_segments = "#EXT-X-INDEPENDENT-SEGMENTS";
	String		ext_x_scte35 = "#EXT-OATCLS-SCTE35";
	String		ext_x_cue_start = "#EXT-X-CUE-OUT";
	String		ext_x_cue_end = "#EXT-X-CUE-IN";
	String		ext_x_cue_span = "#EXT-X-CUE-SPAN";
	String		ext_x_map = "#EXT-X-MAP";
	String		ext_x_start = "#EXT-X-START";
	
	@SuppressWarnings({ "unchecked", "resource" })
	public void write_playlist(String path,String destpath) throws IOException
	{
		File fold=new File(path);
		ArrayList<File> list=new ArrayList<File>();
		if(fold.isDirectory())
		{
			for(File p : fold.listFiles())
			{
				String name=p.getName();
				if(name.endsWith(".ts"))
				list.add(p);
			}
		}
		Collections.sort(list, new myfunction());
		//for(int i=0;i<list.size();i++)
		//	System.out.println(list.get(i).getName());
		File dest=new File(destpath);
		FileOutputStream fout=new FileOutputStream(dest);
		DataOutputStream dout=new DataOutputStream(fout);
		String newline = System.getProperty("line.separator");
		dout.writeBytes(ext_x_header);
		dout.writeBytes(newline);
		dout.writeBytes(ext_x_media_sequence+":"+0);
		dout.writeBytes(newline);
		dout.writeBytes(ext_x_program_date_time+":"+LocalDateTime.now());
		dout.writeBytes(newline);
		
		for(int i=0;i<list.size();i++)
		{
			dout.writeBytes(extinf+" "+list.get(i).getName());
			dout.writeBytes(newline);
			dout.writeBytes(list.get(i).getPath());
			dout.writeBytes(newline);
		}
		dout.writeBytes(ext_x_endlist);
		
		System.out.println("Playlist Prepered : "+destpath);
	}
	
	public static void main(String args[]) throws IOException
	{
		String path="F:\\IVIS_PROJECT\\TestData";
		String destination="F:\\playlist.m3u8";
		write_m3u8 play=new write_m3u8();
		play.write_playlist(path,destination);
		
	}
}