import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter
{
	BufferedWriter bw;
	private int EQctr, GTctr, LTctr;
	private String currFile;
	
	public CodeWriter(String file) throws IOException
	{
		File f = new File(file);
		
		if(!f.exists())
			f.createNewFile();
		
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		
		EQctr = 0;
		GTctr = 0;
		LTctr = 0;
		
		currFile = file.substring(file.lastIndexOf('\\')+1, file.indexOf('.'));
	}
	
	public void setFileName(String fileName)
	{
		currFile = fileName.substring(0, fileName.indexOf('.'));
	}
	
	public void writeArithmetic(String command) throws IOException
	{
		if(command.equals(Parser.arith[0])) //add
		{
			//Assumptions: 1) D already contains value pointing to top of stack, 2) A has address pointing to D
			//v4
			//bw.write("A=A-1\n"); //pop topstack to D
			//bw.write("D=M\n"); //Eliminate by Assumption 1)
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=D+M\n"); //Add the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
		}
		if(command.equals(Parser.arith[1])) //sub
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			//x - y, y is popped from stack first, x second
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M-D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
		}
		if(command.equals(Parser.arith[2])) //neg
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("D=-D\n");
			bw.write("M=D\n");
		}
		if(command.equals(Parser.arith[3])) //eq
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("A=A-1\n"); //Decrement "SP"
			
			bw.write("D=M-D\n"); //Subtract the two values. If D=0, equal (jump). If D!=0, not equal (no jump).
			bw.write("@EQ_"+EQctr+"\n");
			bw.write("D;JEQ\n");
			bw.write("@SP\n"); //When x != y
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=0\n");
			bw.write("@NeQ_"+EQctr+"\n");
			bw.write("0;JMP\n"); //Jump to bypass x=y code
			
			bw.write("(EQ_"+EQctr+")\n"); //When x = y
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=-1\n");
			
			bw.write("(NeQ_"+EQctr+")\n"); //Not Equal 
			bw.write("@SP\n");
			bw.write("M=M-1\n"); //bw.write("M=M-1\n"); Get rid of D=D+1, Simpler
			bw.write("A=M\n");
			bw.write("A=A-1\n"); //A=A-1 Sets A to result location
			bw.write("D=M\n"); //D=M Sets D to value of result
			
			EQctr++;
		}
		if(command.equals(Parser.arith[4])) //gt
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("A=A-1\n"); //Decrement "SP"
			
			bw.write("D=M-D\n"); //Subtract the two values. If D=0, equal (jump). If D!=0, not equal (no jump).
			bw.write("@GT_"+GTctr+"\n");
			bw.write("D;JGT\n");
			bw.write("@SP\n"); //When x <= y
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=0\n");
			bw.write("@NgT_"+GTctr+"\n");
			bw.write("0;JMP\n"); //Jump to bypass x>y code
			
			bw.write("(GT_"+GTctr+")\n"); //When x > y
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=-1\n");
			
			bw.write("(NgT_"+GTctr+")\n"); //x not greater than y
			bw.write("@SP\n");
			bw.write("M=M-1\n"); //bw.write("M=M-1\n"); Get rid of D=D+1, Simpler
			bw.write("A=M\n");
			bw.write("A=A-1\n"); //A=A-1 Sets A to result location
			bw.write("D=M\n"); //D=M Sets D to value of result
			
			GTctr++;			
		}
		if(command.equals(Parser.arith[5])) //lt
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("A=A-1\n"); //Decrement "SP"
			
			bw.write("D=M-D\n"); //Subtract the two values. If D=0, equal (jump). If D!=0, not equal (no jump).
			bw.write("@LT_"+LTctr+"\n");
			bw.write("D;JLT\n");
			bw.write("@SP\n"); //When x <= y
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=0\n");
			bw.write("@NlT_"+LTctr+"\n");
			bw.write("0;JMP\n"); //Jump to bypass x>y code
			
			bw.write("(LT_"+LTctr+")\n"); //When x > y
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("A=A-1\n");
			bw.write("M=-1\n");
			
			bw.write("(NlT_"+LTctr+")\n"); //x not greater than y
			bw.write("@SP\n");
			bw.write("M=M-1\n"); //bw.write("M=M-1\n"); Get rid of D=D+1, Simpler
			bw.write("A=M\n");
			bw.write("A=A-1\n"); //A=A-1 Sets A to result location
			bw.write("D=M\n"); //D=M Sets D to value of result
			
			LTctr++;	
		}
		if(command.equals(Parser.arith[6])) //and
		{
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M&D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
		}
		if(command.equals(Parser.arith[7])) //or
		{ 
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M|D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
		}
		if(command.equals(Parser.arith[8])) //not
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("D=!D\n");
			bw.write("M=D\n");
		}
	}
	
	public void WritePushPop(String command, String segment, int index) throws IOException
	{
		if(command.equals(Parser.PU))
		{
			if(segment.equals("constant"))
			{
				//v1
				bw.write("@"+index+"\n");
				bw.write("D=A\n"); //D=index
				bw.write("@SP\n"); //@0, M=Ram[0]=256
				bw.write("A=M\n"); //A=M=256
				bw.write("M=D\n"); //M=Ram[256]=index
				bw.write("@SP\n"); //Update SP
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
				bw.write("D=M\n");
			}
			else if(segment.equals("local"))
			{				
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@LCL\n");
				bw.write("A=M\n");
				bw.write("A=A+D\n"); //local + i
				bw.write("D=M\n"); //D = RAM[local + i]
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n"); //push to top of stack
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
			}
			else if(segment.equals("argument"))
			{
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@ARG\n");
				bw.write("A=M\n");
				bw.write("A=A+D\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
			}
			else if(segment.equals("this"))
			{
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@THIS\n");
				bw.write("A=M\n");
				bw.write("A=A+D\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
			}
			else if(segment.equals("that"))
			{
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@THAT\n");
				bw.write("A=M\n");
				bw.write("A=A+D\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
			}
			else if(segment.equals("pointer")) //Assume that index for pointer will only be 0 or 1
			{
				String tstt = "";
				
				if(index == 0)
					tstt = "THIS";
				else if(index == 1)
					tstt = "THAT";					
				
				bw.write("@" + tstt + "\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
			}
			else if(segment.equals("temp")) //Assume index is only between 0 and 8
			{
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@R5\n");
				bw.write("A=A+D\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");	
			}
			else if(segment.equals("static"))
			{
				bw.write("@"+currFile+"."+index+"\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");	
			}
		}
		if(command.equals(Parser.PO))
		{
			if(segment.equals("pointer")) //Assume that index for pointer will only be 0 or 1
			{
				String tstt = "";
				
				if(index == 0)
					tstt = "THIS";
				else if(index == 1)
					tstt = "THAT";
				
				bw.write("D=M\n");
				bw.write("@"+tstt+"\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("M=M-1\n");
				bw.write("A=M-1\n");
			}
			else if(segment.equals("static"))
			{
				//assume D=top stack value, A=address to top value
				bw.write("@"+currFile+"."+index+"\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("AM=M-1\n");
				bw.write("A=A-1\n");
				bw.write("D=M\n");
			}
			else
			{
				String preSym = "";
				String AM = "A=M\n";
				
				if(segment.equals("local"))
					preSym = "LCL";
				else if(segment.equals("argument"))
					preSym = "ARG";
				else if(segment.equals("this"))
					preSym = "THIS";
				else if(segment.equals("that"))
					preSym = "THAT";
				else if(segment.equals("temp"))
				{
					preSym = "R5";
					AM = "";
				}
				
				bw.write("D=M\n");
				bw.write("@R13\n"); //store value at of top of stack in R13
				bw.write("M=D\n");
				bw.write("@"+index+"\n");
				bw.write("D=A\n");
				bw.write("@" + preSym + "\n");
				bw.write(AM);
				bw.write("D=A+D\n");
				bw.write("@R14\n"); //store address local + i in R14
				bw.write("M=D\n");
				bw.write("@R13\n");
				bw.write("D=M\n"); //retrieve value that was at top of stack
				bw.write("@R14\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("M=M-1\n");
				bw.write("A=M-1\n"); //A points to top value of stack				
			}
		}
	}
	
	public void Close() throws IOException
	{
		bw.close();
	}
}

//add
/*//v1
bw.write("A=A-1\n"); //pop topstack to D
bw.write("D=M\n"); 
bw.write("A=A-1\n"); //Decrement "SP"
bw.write("D=D+M\n"); //Add the two values
bw.write("M=D\n"); //push result to SP
bw.write("D=A+1\n"); 
bw.write("@SP\n");
bw.write("M=D\n");
*/

/*//v2
//bw.write("A=A-1\n"); //pop topstack to D
//bw.write("D=M\n"); 
bw.write("A=A-1\n"); //Decrement "SP"
bw.write("D=D+M\n"); //Add the two values
bw.write("M=D\n"); //push result to SP
bw.write("D=A+1\n"); 
bw.write("@SP\n");
bw.write("M=D\n");
*/

/*//v3
bw.write("A=A-1\n"); //pop topstack to D
bw.write("D=M\n"); 
bw.write("A=A-1\n"); //Decrement "SP"
bw.write("M=D+M\n"); //Add the two values
bw.write("D=A+1\n"); 
bw.write("@SP\n");
bw.write("M=D\n");
*/

//WritePUSH
//v2
/*
bw.write("@"+index+"\n");
bw.write("D=A\n"); //D=index
bw.write("@SP\n"); //@0, M=Ram[0]=256
bw.write("A=M\n"); //A=M=256
bw.write("M=D\n"); //M=Ram[256]=index
bw.write("M=M+1\n");

//bw.write("@SP\n"); //Update SP
//bw.write("AM=M+1\n");
//bw.write("M=M+1\n");
 */

//WritePop
/*
if(segment.equals("local"))
{
	bw.write("D=M\n");
	bw.write("@R13\n"); //store value at of top of stack in R13
	bw.write("M=D\n");
	bw.write("@"+index+"\n");
	bw.write("D=A\n");
	bw.write("@LCL\n");
	bw.write("A=M\n");
	bw.write("D=A+D\n");
	bw.write("@R14\n"); //store address local + i in R14
	bw.write("M=D\n");
	bw.write("@R13\n");
	bw.write("D=M\n"); //retrieve value that was at top of stack
	bw.write("@R14\n");
	bw.write("A=M\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n"); //A points to top value of stack
}
else if(segment.equals("argument"))
{
	bw.write("D=M\n");
	bw.write("@R13\n"); //store value at of top of stack in R13
	bw.write("M=D\n");
	bw.write("@"+index+"\n");
	bw.write("D=A\n");
	bw.write("@ARG\n");
	bw.write("A=M\n");
	bw.write("D=A+D\n");
	bw.write("@R14\n"); //store address local + i in R14
	bw.write("M=D\n");
	bw.write("@R13\n");
	bw.write("D=M\n"); //retrieve value that was at top of stack
	bw.write("@R14\n");
	bw.write("A=M\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n"); //A points to top value of stack
}
else if(segment.equals("this"))
{
	bw.write("D=M\n");
	bw.write("@R13\n"); //store value at of top of stack in R13
	bw.write("M=D\n");
	bw.write("@"+index+"\n");
	bw.write("D=A\n");
	bw.write("@THIS\n");
	bw.write("A=M\n");
	bw.write("D=A+D\n");
	bw.write("@R14\n"); //store address local + i in R14
	bw.write("M=D\n");
	bw.write("@R13\n");
	bw.write("D=M\n"); //retrieve value that was at top of stack
	bw.write("@R14\n");
	bw.write("A=M\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n"); //A points to top value of stack
}
else if(segment.equals("that"))
{
	bw.write("D=M\n");
	bw.write("@R13\n"); //store value at of top of stack in R13
	bw.write("M=D\n");
	bw.write("@"+index+"\n");
	bw.write("D=A\n");
	bw.write("@THAT\n");
	bw.write("A=M\n");
	bw.write("D=A+D\n");
	bw.write("@R14\n"); //store address local + i in R14
	bw.write("M=D\n");
	bw.write("@R13\n");
	bw.write("D=M\n"); //retrieve value that was at top of stack
	bw.write("@R14\n");
	bw.write("A=M\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n"); //A points to top value of stack
}
else if(segment.equals("pointer")) //Assume that index for pointer will only be 0 or 1
{
	String tstt = "";
	
	if(index == 0)
		tstt = "THIS";
	else if(index == 1)
		tstt = "THAT";
	
	bw.write("D=M\n");
	bw.write("@"+tstt+"\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n");
}
else if(segment.equals("temp")) //Assume index is only between 0 and 8
{
	bw.write("D=M\n");	
	bw.write("@R13\n"); //store value at of top of stack in R13
	bw.write("M=D\n");
	bw.write("@"+index+"\n");
	bw.write("D=A\n");
	bw.write("@R5\n");
	bw.write("D=A+D\n");
	bw.write("@R14\n"); //store address local + i in R14
	bw.write("M=D\n");
	bw.write("@R13\n");
	bw.write("D=M\n"); //retrieve value that was at top of stack
	bw.write("@R14\n");
	bw.write("A=M\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("M=M-1\n");
	bw.write("A=M-1\n"); //A points to top value of stack				
}
else if(segment.equals("static"))
{
	//assume D=top stack value, A=address to top value
	bw.write("@"+currFile+"."+index+"\n");
	bw.write("M=D\n");
	bw.write("@SP\n");
	bw.write("AM=M-1\n");
	bw.write("A=A-1\n");
	bw.write("D=M\n");
}
*/
