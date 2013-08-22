import java.io.*;

public class Parser 
{
	public static final String A = "C_ARITHMETIC";
	public static final String PU = "C_PUSH";
	public static final String PO = "C_POP";
	public static final String L = "C_LABEL";
	public static final String G = "C_GOTO";
	public static final String I = "C_IF";
	public static final String F = "C_FUNCTION";
	public static final String R = "C_RETURN";
	public static final String C = "C_CALL";
	
	public static String arith[];
	
	private BufferedReader br;
	private String currCmd;
	
	public Parser(String filename) throws FileNotFoundException
	{
		br = new BufferedReader(new FileReader(filename));
		currCmd = "";
		
		arith = new String[]{"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};
	}
	
	public String getCurrCmd()				{return currCmd;}
	
	public boolean hasMoreCommands() throws IOException
	{
		return br.ready();
	}
	
	public void advance() throws IOException
	{
		if(hasMoreCommands())
		{
			currCmd = br.readLine();	
			currCmd = currCmd.replaceAll("\\s+", " ");
			
			int idx;
			if((idx = currCmd.indexOf("//")) != -1)
				currCmd = currCmd.substring(0, idx);
		}		
	}
	
	public String commandType()
	{
		if(currCmd.contains("push"))
			return PU;
		
		if(currCmd.contains("pop"))
			return PO;
		
		for(int i=0; i < arith.length; i++)
			if(currCmd.equals(arith[i]))
				return A;
		
		return null;
	}
	
	public String arg1()
	{
		String cmdType = commandType();
		
		if(cmdType.equals(R))
		{
			System.out.println("arg1() should not be called on " + cmdType);
			System.exit(1);
		}
		
		if(cmdType.equals(A))
			return currCmd;
		
		if(cmdType.equals(PU) || cmdType.equals(PO))
		{
			String[] tokens = currCmd.split(" ");
			return tokens[1];
		}
		
		return null;
	}
	
	public int arg2()
	{	
		String cmdType = commandType();
		
		if(cmdType.equals(PU) || cmdType.equals(PO))
		{
			String[] tokens = currCmd.split(" ");
			int arg2;
			try
			{
				arg2 = Integer.parseInt(tokens[2]);
				return arg2;
			}
			catch (NumberFormatException nfe)
			{
				System.out.println("Not a valid integer value: " + tokens[2]);
				nfe.printStackTrace();
			}
		}
		
		//Check for valid commands, otherwise return minimum value;
		return Integer.MIN_VALUE;
	}
}
