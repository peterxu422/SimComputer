import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try
		{	
			//String prog = "Max";
			String prog = args[0];
			String filename, filepath, fileloc;
			String folder = prog;
			folder = folder.toLowerCase();
			filename = prog + ".asm";
			fileloc = "C:\\Users\\Peter\\Documents\\Nand2Tetris\\projects\\06\\";
			filepath = fileloc + folder + "\\" + filename;
			
			Parser p = new Parser(filepath);
			Code c = new Code();
			SymbolTable st = new SymbolTable();

			String instr = "";
			
			String hack = filename.substring(0, filename.indexOf('.'));
			File file = new File(fileloc + folder + "\\" + hack + "1.hack");
			
			if(!file.exists())
				file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			//1st Pass
			int romAddr = 0;
			
			while(p.hasMoreCommands())
			{
				p.advance();
				
				if(p.getCurrCmd().equals(""))
					continue;
				
				if(p.commandType().equals("A_COMMAND"))
					romAddr++;
				else if(p.commandType().equals("C_COMMAND"))
					romAddr++;
				else if(p.commandType().equals("L_COMMAND"))
				{
					//Assume all L commands are already upper case and code has no duplicate (symbol)'s
					String lbl = p.symbol();
					st.addEntry(lbl, romAddr);				
				}
			}
			
			p = new Parser(filepath); //so that p.hasMoreCommands() is true
			
			//2nd Pass
			int ramAddr = 16;
			
			while(p.hasMoreCommands())
			{
				p.advance();
				
				if(p.getCurrCmd().equals(""))
					continue;
				
				if(p.commandType().equals("A_COMMAND"))
				{
					String atValue = p.symbol();
					int addr = -1;
					try //checks if value is an integer
					{
						addr = Integer.parseInt(atValue);
					}
					catch (NumberFormatException nfe) //checks if value is a symbol
					{	
						if(st.contains(atValue))
							addr = st.GetAddress(atValue); 
						else
						{
							st.addEntry(atValue, ramAddr);
							ramAddr++;
							addr = st.GetAddress(atValue);
						}
					}
					
					if(addr < 0)
					{
						System.out.println("Compile error: Negative address value");
						System.exit(1);
					}
					
					instr = String.format("%16s", Integer.toBinaryString(addr)); //String.format "%[argument number] [flags] [width] [.precision] type"
					instr = instr.replace(' ', '0');
				}
				else if(p.commandType().equals("L_COMMAND")) //L cmds were already handled in 1st pass
					continue;
				else if(p.commandType().equals("C_COMMAND")) //C-command
				{
					String comp = c.comp(p.comp());
					String dest = c.dest(p.dest());
					String jump = c.jump(p.jump());
					
					instr = "111" + comp + dest + jump;
				}
				
				System.out.println(instr + " " + p.getCurrCmd());
				bw.write(instr + "\n");
			}
			
			bw.close();
			fw.close();
		}
		catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

}

/*
System.out.println("\n");
//Test Code			
System.out.println(p.commandType("@i") + " " + p.getCurrCmd());
System.out.println(p.commandType("M = 1")  + " " + p.getCurrCmd());
System.out.println(p.commandType("D      =     D -A")  + " " + p.getCurrCmd());
System.out.println(p.commandType("D;    JGT")  + " " + p.getCurrCmd());
System.out.println(p.commandType("0 ;   JMP")  + " " + p.getCurrCmd());

System.out.println("\n\n");

System.out.println("dest: " + p.dest("@i") + " code: " + c.dest(p.dest("@i")) + " " + p.getCurrCmd());
System.out.println("dest: " + p.dest("M = 1") + " code: " + c.dest(p.dest("M = 1")) + " " + p.getCurrCmd());
System.out.println("comp: " + p.comp("M = 1") + " code: " + c.comp(p.comp("M = 1")) + " " + p.getCurrCmd());
System.out.println("jump: " + p.jump("M = 1") + " code: " + c.jump(p.jump("M = 1")) + " " + p.getCurrCmd());
System.out.println("dest: " + p.dest("D      =     D -A") + " code: " + c.dest(p.dest("D      =     D -A")) + " " + p.getCurrCmd());
System.out.println("comp: " + p.comp("D      =     D -A") + " code: " + c.comp(p.comp("D      =     D -A")) + " " + p.getCurrCmd());
System.out.println("jump: " + p.jump("D      =     D -A") + " code: " + c.jump(p.jump("D      =     D -A")) + " " + p.getCurrCmd());
System.out.println("dest: " + p.dest("D;    JGT") + " code: " + c.dest(p.dest("D;    JGT"))  + " " + p.getCurrCmd());
System.out.println("comp: " + p.comp("D;    JGT") + " code: " + c.comp(p.comp("D;    JGT"))  + " " + p.getCurrCmd());
System.out.println("jump: " + p.jump("D;    JGT") + " code: " + c.jump(p.jump("D;    JGT"))  + " " + p.getCurrCmd());
System.out.println("dest: " + p.dest("0 ;   JMP") + " code: " + c.dest(p.dest("0 ;   JMP"))  + " " + p.getCurrCmd());
System.out.println("comp: " + p.comp("0 ;   JMP") + " code: " + c.comp(p.comp("0 ;   JMP"))  + " " + p.getCurrCmd());
System.out.println("jump: " + p.jump("0 ;   JMP") + " code: " + c.jump(p.jump("0 ;   JMP"))  + " " + p.getCurrCmd());
*/

/*//No Symbols Assembler
while(p.hasMoreCommands())
{
	p.advance();	
	String instr = "";
	
	if(p.currCmd.equals(""))
		continue;
	
	if(p.commandType().equals("A_COMMAND"))
	{
		int addr = Integer.parseInt(p.currCmd.substring(1));
		instr = String.format("%16s", Integer.toBinaryString(addr)); //String.format "%[argument number] [flags] [width] [.precision] type"
		instr = instr.replace(' ', '0');
	}
	else
	{
		String comp = c.comp(p.comp());
		String dest = c.dest(p.dest());
		String jump = c.jump(p.jump());
		
		instr = "111" + comp + dest + jump;
	}
	
	System.out.println(instr + " " + p.currCmd);
	bw.write(instr + "\n");
	//System.out.println(p.currCmd);
}	
*/