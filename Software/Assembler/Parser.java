import java.io.*;
import java.util.*;

public class Parser 
{
	private static final String A = "A_COMMAND";
	private static final String L = "L_COMMAND";
	private static final String C = "C_COMMAND";
	private static final int MAX_COMP_ENTRIES = 28;
	
	private BufferedReader br;
	private String compMap[];
	private String currCmd;
	
	public String getCurrCmd()					{return currCmd;}
	
	public Parser(String filename) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(filename));
		currCmd = "";
		compMap = new String[MAX_COMP_ENTRIES];
		
		compMap[0] = "0";
		compMap[1] = "1";
		compMap[2] = "-1";
		compMap[3] = "D";
		compMap[4] = "A";
		compMap[5] = "M";
		compMap[6] = "!D";
		compMap[7] = "!A";
		compMap[8] = "!M";
		compMap[9] = "-D";
		compMap[10] = "-A";
		compMap[11] = "-M";
		compMap[12] = "D+1";
		compMap[13] = "A+1";
		compMap[14] = "M+1";
		compMap[15] = "D-1";
		compMap[16] = "A-1";
		compMap[17] = "M-1";
		compMap[18] = "D+A";
		compMap[19] = "D+M";
		compMap[20] = "D-A";
		compMap[21] = "D-M";
		compMap[22] = "A-D";
		compMap[23] = "M-D";
		compMap[24] = "D&A";
		compMap[25] = "D&M";
		compMap[26] = "D|A";
		compMap[27] = "D|M";
	}
	
	public boolean hasMoreCommands() throws IOException
	{
		return br.ready();
	}
	
	public void advance() throws IOException
	{
		//Assumes atm that each line is its own command.
		if(hasMoreCommands())
		{
			currCmd = br.readLine();	
			currCmd = currCmd.replaceAll("\\s", "");
			
			int idx;
			if((idx = currCmd.indexOf("//")) != -1)
				currCmd = currCmd.substring(0, idx);
		}
	}
	
	//commandType
	public String commandType()
	{
		//currCmd = cmd;
		//System.out.println("      " + currCmd);
		//currCmd = currCmd.replaceAll("\\s", "");
		//System.out.println("      " + currCmd);
		
		if(currCmd.charAt(0) == '@')
			return A;
		else if(currCmd.charAt(0)=='(' && currCmd.charAt(currCmd.length()-1) == ')')
			return L;
		else
			return C;
	}
	
	public String symbol()
	{		
		if(commandType().equals(L))
		{
			String cmd = currCmd;
			return cmd.substring(cmd.indexOf('(')+1, cmd.indexOf(')'));
		}
		else if(commandType().equals(A))
			return currCmd.substring(1);
		
		return null;
	}
	
	public String dest()
	{
		if(commandType() != C)
			return null;
		
		StringTokenizer strtok = new StringTokenizer(currCmd, "=");
		String dest = (String) strtok.nextElement();
		boolean canRet = false;
		
		if(dest.equals("M") || dest.equals("D") || dest.equals("MD") || dest.equals("A")
				|| dest.equals("AM") || dest.equals("AD") || dest.equals("AMD"))
			canRet = true;
		
		if(canRet==false)
			return null;
		
		return dest;
	}
	
	public String comp()
	{
		if(commandType() != C)
			return null;
		
		int i=0;
		if(currCmd.contains("="))
			i=1;
		else if(currCmd.contains(";"))
			i=0;
		
		String delims = "[=;]";
		String[] tokens = currCmd.split(delims);
		String comp = tokens[i];
		
		//System.out.println(tokens.length);
		
		for(int j=0; j < MAX_COMP_ENTRIES; j++)
		{
			if(comp.equals(compMap[j]))
				return comp;
		}
		
		return null;
	}
	
	public String jump()
	{
		if(commandType() != C)
			return null;
		
		StringTokenizer strtok = new StringTokenizer(currCmd, ";");
		strtok.nextElement();
		String jump;
		if(strtok.hasMoreElements())
			jump = (String) strtok.nextElement();
		else 
			return null;
		
		boolean canRet = false;
		
		if(jump.equals("JGT") || jump.equals("JEQ") || jump.equals("JGE") || jump.equals("JLT")
				|| jump.equals("JNE") || jump.equals("JLE") || jump.equals("JMP"))
			canRet = true;
		
		if(canRet==false)
			return null;
		
		return jump;			
	}
}
