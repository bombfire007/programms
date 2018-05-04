package com.main.src;
import java.io.*;

public class WriteTables
{
		public long NumTSPacketsWritten = 0;
	    public long NumTSPacketsBeforeMeta = 0;
	    public byte PATContinuityCounter = 0;
	    public byte PMTContinuityCounter = 0;
	    public byte PCRCommunityCounter=0;
	    public byte ContinuityCounter = 0; // for video
	    public int[]  CrcTable;
	    public byte[] H264AccessUnitDelimiter = { 0x00, 0x00, 0x00, 0x01, 0x09, (byte)0xf0 };
	    public byte[] H264SequenceParamSet = { 0x00, 0x00, 0x00, 0x01, 0x27, 0x42, (byte)0x80, 0x29, (byte)0x8D, (byte)0x95, 0x01, 0x40, 0x7B, 0x20 };
	    public byte[] H264PictureParamSet = { 0x00, 0x00, 0x00, 0x01, 0x28, (byte)0xDE, 0x09, (byte)0x88 };
	    public long mTimeStampIncr = 48000;
	    public long mTimeStamp = 4000;
	    public long PTSaheadOfPCRus = 500000;
	    public ByteArrayOutputStream writerToBuffer;
	    int index=0;
 byte VideoSourceIncrementContinuityCounter() 
{
	 if (++ContinuityCounter == 16) 
	 {
		 ContinuityCounter = 0;
	 }

return ContinuityCounter;
}
 public void writeProgramAssociationTable() throws IOException 
 {
	byte kdata[]= {
			 0x47,
		        0x40, 0x00, 0x10, 0x00,  
		        0x00, (byte)0xb0, 0x0d, 0x00, 
		        0x00, (byte)0xc3, 0x00, 0x00,  
		        0x00, 0x01, (byte)0xe1, (byte)0xe0,  
		        0x00, 0x00, 0x00, 0x00  
				} ;
		byte[] buffer=new byte[188];
		for(int i=0;i<188;i++)
			buffer[i]=(byte) 0xff;
		for(int i=0;i<kdata.length;i++)
			buffer[i]=kdata[i];
		kdata[3]|=PATContinuityCounter;
		if(++PATContinuityCounter==16)
		PATContinuityCounter=0;
		byte bcrc[]=bcrc(buffer,5,12);
		for(int i=0;i<4;i++)
			buffer[17+i]=bcrc[i];
		//output(buffer)
 }
 public void Write_to_file(byte[] buffer,int length) throws IOException
 {
	 File f=new File("F:\\output.ts");
	 FileOutputStream fout=new FileOutputStream(f,true);
	 fout.write(buffer);
	 fout.close();
 }
 public void writeProgramMapTable() throws IOException 
{
	 byte[] kData = {
		        0x47,
		        0x41, (byte)0xe0, 0x10, 0x00, 
		        0x02, (byte)0xb0, 0x00, 0x00,  
		        0x01, (byte)0xc3, 0x00, 0x00,  
		        (byte)0xe0, 0x00, (byte)0xf0, 0x00 };
	 byte buffer[]=new byte[188];
	 for(int i=0;i<188;i++)
		{
			buffer[i]=(byte)0XFF;
		} 
	 for(int i=0;i<kData.length;i++)
	 {
		 buffer[i]=kData[i];
	 }
	 kData[3]|=PMTContinuityCounter;
	 if(++PATContinuityCounter==16)
		 PATContinuityCounter=0;
	 int numSources = 1; 
	    int section_length = 5 * numSources + 4 + 9;
	    buffer[6] |= (byte)(section_length >> 8);
	    buffer[7] = (byte)(section_length & 0xff);
	    short kPCR_PID = 0x01e1;
	    buffer[13] |= (byte)((kPCR_PID >> 8) & 0x1f);
	    buffer[14] = (byte)(kPCR_PID & 0xff);

	    
     byte streamType = 0x1b; 
	    int pidx = kData.length;
	    for (short i = 0; i < numSources; ++i) {
	        buffer[pidx++] = streamType; 
	
	        short ES_PID = (short)(0x01e0 + i + 1);
	        buffer[pidx++] = (byte)(0xe0 | (ES_PID >> 8));
	        buffer[pidx++] = (byte)(ES_PID & 0xff);
	        buffer[pidx++] = (byte)0xf0;
	        buffer[pidx++] = 0x00;
	    }
	    
	   byte[] bcrc = bcrc(buffer, 5, 12+numSources*5);
	    for (int i=0; i<4; i++)
	    	buffer[(17+numSources*5)+i] = bcrc[i];

	    Write_to_file(buffer, buffer.length);
}
public void writeAccessUnit(byte[] accessUnit, long timeUs) throws IOException  
{
	short sourceIndex = 0; 
	
    byte[] buffer = new byte[188];
    for (int i=0; i<188; i++)
    	buffer[i] = (byte)0xff;	
    short PID = (short)(0x01e0 + sourceIndex + 1);
	
    byte continuity_counter = VideoSourceIncrementContinuityCounter();
    byte stream_id = (byte)0xe0;
    int PTS = (int)((timeUs * 9L) / 100L);
    long PCR =(int)(((timeUs-PTSaheadOfPCRus) * 9L) / 100L);
	PCR = PCR << 9L;
	int PES_packet_length = accessUnit.length + 8;
	boolean adaptation = (timeUs != 0); 
    int offset = 0;
    boolean lastAccessUnit = ((accessUnit.length - offset) < 184);
    if (PES_packet_length >= 65536) {
        // It's valid to set this to 0 for video according to the specs.
        PES_packet_length = 0;

	    int pidx = 0;
	    buffer[pidx++] = 0x47;
	    buffer[pidx++] = (byte)(0x40 | (PID >> 8));
	    buffer[pidx++] = (byte)(PID & 0xff);
	    buffer[pidx++] =  (byte)((adaptation ? 0x30 : 0x10) | continuity_counter);
    	
	    if (adaptation)
	    {
        	int paddingSize = 0;
        	if (lastAccessUnit)
        		paddingSize = 184 - (accessUnit.length - offset);
        	else
        		paddingSize = 8; 
            buffer[pidx++] = (byte)(paddingSize - 1); 
        	buffer[pidx++] = 0x10; 
        	buffer[pidx++] = (byte)(( PCR >> 34 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 26 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 18 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 10 ) & 0xff);
            buffer[pidx++] = (byte)(0x7e | ( ( PCR & (1<<9) ) >> 2 ) | ( ( PCR & (1<<8) ) >> 8 ));
            buffer[pidx++] = (byte)(PCR & 0xff);
            
        	if (lastAccessUnit)
        	{
        		pidx += paddingSize-8;
        	}
	    }

        buffer[pidx++] = 0x00;
	    buffer[pidx++] = 0x00;
	    buffer[pidx++] = 0x01;
	    buffer[pidx++] = stream_id;
	    buffer[pidx++] = (byte)(PES_packet_length >> 8);
	    buffer[pidx++] = (byte)(PES_packet_length & 0xff);
	    buffer[pidx++] = (byte)0x84;
	    buffer[pidx++] = (byte)0x80;
	    buffer[pidx++] = 0x05;
	    buffer[pidx++] = (byte)(0x20 | (((PTS >> 30) & 7) << 1) | 1);
	    buffer[pidx++] = (byte)((PTS >> 22) & 0xff);
	    buffer[pidx++] = (byte)((((PTS >> 15) & 0x7f) << 1) | 1);
	    buffer[pidx++] = (byte)((PTS >> 7) & 0xff);
	    buffer[pidx++] = (byte)(((PTS & 0x7f) << 1) | 1);
	
	    int sizeLeft = 188 - pidx;
	    int copy = accessUnit.length;
	    if (copy > sizeLeft) {
	        copy = sizeLeft;
	    }
	    System.arraycopy(accessUnit,0,buffer,pidx,copy);

	    Write_to_file(buffer, buffer.length);
	    NumTSPacketsWritten++;
	    offset = copy;
	    while (offset < accessUnit.length) {
	        lastAccessUnit = ((accessUnit.length - offset) < 184);
    }
	    for (int i=0; i<188; i++)
	    	buffer[i] = (byte)0xff;

	    continuity_counter = VideoSourceIncrementContinuityCounter();

        pidx = 0;
        buffer[pidx++] = 0x47;
        buffer[pidx++] = (byte)(0x00 | (byte)(PID >> 8));
        buffer[pidx++] = (byte)(PID & 0xff);
        buffer[pidx++] = (byte)((adaptation ? 0x30 : 0x10) | continuity_counter);

        if (adaptation) {
        	
        	int paddingSize = 0;
        	if (lastAccessUnit)
        		paddingSize = 184 - (accessUnit.length - offset);
        	else
        		paddingSize = 8; 
            buffer[pidx++] = (byte)(paddingSize - 1); 
        	buffer[pidx++] = 0x10; 
            buffer[pidx++] = (byte)(( PCR >> 34 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 26 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 18 ) & 0xff);
            buffer[pidx++] = (byte)(( PCR >> 10 ) & 0xff);
            buffer[pidx++] = (byte)(0x7e | ( ( PCR & (1<<9) ) >> 2 ) | ( ( PCR & (1<<8) ) >> 8 ));
            buffer[pidx++] = (byte)(PCR & 0xff);
            
        	if (lastAccessUnit)
        	{
        		pidx += paddingSize-8;
        	}
        }

        sizeLeft = 188 - pidx;
        copy = accessUnit.length - offset;
        if (copy > sizeLeft) {
            copy = sizeLeft;
        }

       
	    System.arraycopy(accessUnit,offset,buffer,pidx,copy);

	    Write_to_file(buffer, buffer.length);
	    NumTSPacketsWritten++;

        offset += copy;
    }
}
public void initCrcTable() {
    int poly = 0x04C11DB7;
    for (int i = 0; i < 256; i++) {
        int crc = i << 24;
        for (int j = 0; j < 8; j++) {
            crc = (crc << 1) ^ (((crc & 0x80000000) != 0) ? (poly) : 0);
        }
        CrcTable[i] = crc;
    }
}
public byte[] bcrc(byte[] data, int offset, int length) {
    int crc = 0xFFFFFFFF;
    for (int i=offset; i<offset+length; i++) {
        crc = (crc << 8) ^ CrcTable[((crc >> 24) ^ data[i]) & 0xFF];
    }
	byte[] res = new byte[4];
	res[0] = (byte)(crc >>> 24);
	res[1] = (byte)(crc >>> 16);
	res[2] = (byte)(crc >>> 8);
	res[3] = (byte)(crc & 0xff);
	return res;
}
@SuppressWarnings("unused")
public void output(byte[] dataunit)
{
	try
	{
	writerToBuffer.write(dataunit);
	if(writerToBuffer.size()>2048)
	{

		byte tsblock[]=writerToBuffer.toByteArray();
	}
		writerToBuffer.reset();
	}
	catch(IOException e)
	{
		e.getStackTrace();
	}

}
public void writeTS() throws IOException
{
	if(NumTSPacketsWritten >= NumTSPacketsBeforeMeta);
	writeProgramAssociationTable();
	writeProgramMapTable();
	NumTSPacketsBeforeMeta = NumTSPacketsWritten + 1280;
}
public void input(byte []h264nal,int offset,int length,int Rtptimestamp)
{
	try
	{
		writeTS();
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		b.write(H264AccessUnitDelimiter);
		b.write(H264SequenceParamSet);
		b.write(H264PictureParamSet);
		b.write(h264nal,offset,length);
		byte h264data[]=b.toByteArray();
		writeAccessUnit(h264data, PTSaheadOfPCRus+Rtptimestamp*10000);
		mTimeStamp += mTimeStampIncr;
	}
	catch(IOException e)
	{
		e.getStackTrace();
		
	}
}


}