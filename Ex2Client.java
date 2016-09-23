/*
	Fanny Avila
	CS380 -01
	Exercise 2
	
*/
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.net.UnknownHostException;
import java.io.IOException;
//import java.util.*;
import java.util.zip.CRC32;
public final class Ex2Client
{
		private static Socket soc;	
		private static String str;
		private static OutputStream out ;
		private static InputStream in;
		private static DataInputStream dis;
		private static byte [] data;
		private static byte [] returnData;
		private static int crCode;
		
		public static void main(String args[] )throws IOException
		{
			try
			{
				soc = new Socket("codebank.xyz", 38102);
				out = soc.getOutputStream();
				in = soc.getInputStream();
				dis = new DataInputStream(in);
			}
			catch(UnknownHostException e)
			{
				System.out.println("Unknown Host");
				System.exit(0);
			}
			try
			{
				readHalfBytes();
				crc();
				writeReturnMessage();
				out.write(returnData);
				System.out.print("\n\nAccept: ");
				// gets last byte, if one then accept
				if(dis.read() == 1)
					System.out.print("Yes");
				else
					System.out.println("No, Try Again"); 
			}
			catch(IOException e){}
			dis.close();
			out.close(); 
			in.close();
			soc.close();
			System.out.print("\n\nDisconnected from Server");
		}
	public static void readHalfBytes() throws IOException
	{
		data = new byte[100]; 
		// will concatinate bytes befor storing in array...
		for(int i = 0; i < 100; i++ )
		{
			int fullByte = dis.readByte();
			//System.out.println(fullByte);
			// shift 4
			fullByte = fullByte << 4;
			int otherByte = dis.readByte();
			//System.out.println(otherByte);
			fullByte = (fullByte ^ otherByte);
			//System.out.println(fullByte);
			data[i] = (byte)fullByte;
			//System.out.println(data[i]);
		}
		System.out.println("Data recieved: ");
		printData();
	}
	private static void writeReturnMessage()
	{
		returnData = new byte[4];
		int temp = crCode;
		//System.out.println(temp);
		//System.out.println(temp);
		int j = 3;
		for (int i = 0; i < 4; i++)
		{	
			temp = crCode >> (8*j);
			temp = temp & (0x00FF);
			returnData[i] = (byte)temp;
			//System.out.println(temp);
			j--;
		}
		System.out.print("\n\nReturn message: ");
		System.out.print(javax.xml.bind.DatatypeConverter.printHexBinary(returnData) );
	}
	private static void crc()
	{
		CRC32 cr = new CRC32();
		cr.update(data);
		crCode= (int)cr.getValue();
		//System.out.println("\n"+ crCode);
	}
	public static void printData()
	{	int j = 0 ;
		String str = javax.xml.bind.DatatypeConverter.printHexBinary(data);
		for (int i  =  0; i < str.length(); i++)
		{
			if (i >= (20 * (j+1)))
			{
				System.out.println("");
				j++;
			}
				System.out.print(str.charAt(i) + "");
			
		}
	}
}
