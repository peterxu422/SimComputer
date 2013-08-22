import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main 
{
	public static void main(String args[])
	{
		try
		{
			String home07path = "C:\\Users\\Peter\\Documents\\Nand2Tetris\\projects\\07\\";
			File home07 = new File(home07path);
			File[] listOfFolds = home07.listFiles();
			
			String prog = args[0];
			String vmFile, asmFile, filepath;
			
			vmFile = "";
			asmFile = "";
			filepath = "";

			for(File fold : listOfFolds)
			{
				File subfold = new File((home07path + fold.getName() + "\\"));
				File[] listOfFiles = subfold.listFiles();
				
				for(File files : listOfFiles)
				{
					if(files.getName().equals(prog))
					{
						vmFile = prog + ".vm";
						asmFile = prog + ".asm";
						filepath = files.getAbsolutePath() + "\\";
						break;
					}
				}
				
			}
			
			Parser p = new Parser(filepath + vmFile);
			CodeWriter cw = new CodeWriter(filepath + asmFile);
			
			while(p.hasMoreCommands())
			{
				p.advance();
				
				if(p.getCurrCmd().equals(""))
					continue;
				
				String cmdType = p.commandType();
				
				System.out.println(p.getCurrCmd() + " //" + cmdType + " arg1:" + p.arg1() + " arg2:" + p.arg2());
				
				if(cmdType.equals(Parser.PU) || cmdType.equals(Parser.PO))
				{
					cw.WritePushPop(cmdType, p.arg1(), p.arg2());
				}
				else if(cmdType.equals(Parser.A))
					cw.writeArithmetic(p.arg1());
			}
			
			cw.Close(); //Must either close or flush the bufferedwriter each time, otherwise nothing prints
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
