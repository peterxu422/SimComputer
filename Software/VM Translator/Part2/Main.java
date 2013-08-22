import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class Main 
{
	public static void main(String args[])
	{
		String home08path = "C:\\Users\\Peter\\Documents\\Nand2Tetris\\projects\\08\\";
		File home08 = new File(home08path);
		File[] listOfFolds = home08.listFiles();

		String prog = args[0];
		//String prog = "FibonacciSeries";
		String vmFile, asmFile, filepath;

		File subfold;
		File[] listOfFiles;
		Vector<File> vmFiles = null;

		vmFile = "";
		asmFile = "";
		filepath = "";

		boolean sysExists = false;
		boolean folderExists = false;
		boolean fileExists = false;

		for(File fold : listOfFolds) //listOfFolds = [FunctionCalls, ProgramFlow]
		{
			subfold = new File((home08path + fold.getName() + "\\"));
			listOfFiles = subfold.listFiles();

			for(File files : listOfFiles)
			{
				sysExists = false;

				String tmpProgFolderName = files.getName(); //Will give folder whose name matches args0

				//If the foldername matches args[0], check inside the folder. Otherwise don't bother
				if(tmpProgFolderName.equals(prog))
				{
					folderExists = true;
					vmFiles = new Vector<File>(); //vector to store the .vm files

					File subfold2 = new File(subfold.getAbsolutePath() + "\\" + tmpProgFolderName + "\\");
					File[] listOfFiles2 = subfold2.listFiles();

					for(File files2 : listOfFiles2)
					{
						String files2Name = files2.getName();
						String ext = files2Name.substring(files2Name.indexOf('.')+1, files2Name.length());

						if(ext.equals("vm"))
						{
							vmFiles.add(files2);

							if(sysExists == false && files2Name.equals("Sys.vm"))
								sysExists = true;
						}
					}

					if(sysExists)
					{
						//give the filepath some dummy garbage so that it will enter the FileNotFoundException
						vmFile = null;
						asmFile = null;
						filepath = files.getAbsolutePath() + "\\";
						fileExists = true; //? <-not sure if will work everytime
					}
					else if(!sysExists)
					{
						vmFile = prog + ".vm";
						asmFile = prog + ".asm";
						filepath = files.getAbsolutePath() + "\\";
						fileExists = true;
					}

					if(fileExists)
						break;
				}

				if(folderExists)
					break;
			}

			if(folderExists)
				break;
		}

		//System.out.println("sysExists: " + sysExists); //Debugging tool

		Parser p = null;
		CodeWriter cw = null;

		try
		{
			p = new Parser(filepath + vmFile);
			cw = new CodeWriter(filepath + asmFile);
		}
		catch (FileNotFoundException fnfe) //Comes here if there are multiple .vm files (aka Main.vm and Sys.vm exist)
		{
			if(sysExists)
			{
				try
				{
					asmFile = prog + ".asm";
					cw = new CodeWriter(filepath + asmFile);
					cw.writeInit();
					
					for(File files : vmFiles)
					{
						String fileName = files.getName();
						vmFile = fileName;
						cw.setFileName(vmFile);
						
						System.out.println("vmFile: " + vmFile);
			
						p = new Parser(filepath + vmFile);
						parseFile(p, cw);
					}
				}
				catch (FileNotFoundException fnfe2)
				{
					fnfe2.printStackTrace();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
				
			}
			else
			{
				System.out.println("Compile Error");
				System.exit(1);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		try
		{
			parseFile(p, cw);
			
			cw.Close(); //Must either close or flush the bufferedwriter each time, otherwise nothing prints
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void parseFile(Parser p, CodeWriter cw) throws IOException
	{
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
			else if(cmdType.equals(Parser.L))
				cw.writeLabel(Parser.currFunc + "$" + p.arg1());
			else if(cmdType.equals(Parser.G))
				cw.writeGoto(Parser.currFunc + "$" + p.arg1());
			else if(cmdType.equals(Parser.I))
				cw.writeIf(Parser.currFunc + "$" + p.arg1());
			else if(cmdType.equals(Parser.F))
				cw.writeFunction(p.arg1(), p.arg2());
			else if(cmdType.equals(Parser.C))
				cw.writeCall(p.arg1(), p.arg2());
			else if(cmdType.equals(Parser.R))
				cw.writeReturn();
		}		
	}
}
