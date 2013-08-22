import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter
{
	BufferedWriter bw;
	private int EQctr, GTctr, LTctr, retAddrCtr;
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
		retAddrCtr = 0;
		
		currFile = file.substring(file.lastIndexOf('\\')+1, file.indexOf('.'));
	}
	
	public void setFileName(String fileName)
	{
		currFile = fileName.substring(0, fileName.indexOf('.'));
	}
	
	public void writeInit() throws IOException
	{
		bw.write("//!-Init\n");
		bw.write("@256\n");
		bw.write("D=A\n");
		bw.write("@SP\n");
		bw.write("M=D\n");
		writeCall("Sys.init", 0);
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeArithmetic(String command) throws IOException
	{	
		if(command.equals(Parser.arith[0])) //add
		{
			//Assumptions: 1) D already contains value pointing to top of stack, 2) A has address pointing to D
			bw.write("//!-add\n");
			bw.write("@SP\n");			//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=D+M\n"); //Add the two values
			bw.write("D=A+1\n"); 
			
			//bw.write("@22222\n"); //Debug tool
			
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[1])) //sub
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			//x - y, y is popped from stack first, x second
			bw.write("//!-sub\n");
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");			
			
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M-D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			
			//bw.write("@22222\n"); //Debug tool
			
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[2])) //neg
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("//!-neg\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
			bw.write("D=-D\n");
			bw.write("M=D\n");
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[3])) //eq
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("//!-eq\n");			
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
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
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[4])) //gt
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("//!-gt\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
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
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[5])) //lt
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("//!-lt\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
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
			
			//bw.write("@22222\n"); //Debug tool
			
			bw.write("@SP\n");
			bw.write("M=M-1\n"); //bw.write("M=M-1\n"); Get rid of D=D+1, Simpler
			bw.write("A=M\n");
			bw.write("A=A-1\n"); //A=A-1 Sets A to result location
			bw.write("D=M\n"); //D=M Sets D to value of result
			
			LTctr++;
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[6])) //and
		{
			bw.write("//!-and\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M&D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[7])) //or
		{ 
			bw.write("//!-or\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
			bw.write("A=A-1\n"); //Decrement "SP"
			bw.write("M=M|D\n"); //Subtract the two values
			bw.write("D=A+1\n"); 
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
			
			bw.write("\n"); //Debug tool
		}
		if(command.equals(Parser.arith[8])) //not
		{
			//Assumptions: 1) D already contains topmost value of stack, 2) A has address pointing to D (SP addr is one above)
			bw.write("//!-not\n");
			
			bw.write("@SP\n");		//Without making assumptions
			bw.write("A=M\n");
			bw.write("A=A-1\n");
			bw.write("D=M\n");
			
			bw.write("D=!D\n");
			bw.write("M=D\n");
			
			bw.write("\n"); //Debug tool
		}
	}
	
	public void WritePushPop(String command, String segment, int index) throws IOException
	{
		if(command.equals(Parser.PU))
		{
			bw.write("//!-push " + segment + " " + index + "\n");
			
			if(segment.equals("constant"))
			{
				//v1
				bw.write("@"+index+"\n");
				bw.write("D=A\n"); //D=index
				bw.write("@SP\n"); //@0, M=Ram[0]=256
				bw.write("A=M\n"); //A=M=256
				bw.write("M=D\n"); //M=Ram[256]=index
				
				//bw.write("@22222\n"); //Debug tool
				
				bw.write("@SP\n"); //Update SP 
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
				bw.write("D=M\n");
				
				bw.write("\n"); //Debug tool
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
				
				//bw.write("@22222\n"); //Debug tool
				
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
				
				bw.write("\n"); //Debug tool
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
				
				//bw.write("@22222\n"); //Debug tool
				
				bw.write("@SP\n");
				bw.write("AM=M+1\n");
				bw.write("A=A-1\n");
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
			}
		}
		if(command.equals(Parser.PO))
		{
			bw.write("//!-pop " + segment + " " + index + "\n");
			
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
				
				bw.write("\n"); //Debug tool
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
				
				bw.write("\n"); //Debug tool
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
				
				//bw.write("@22222\n"); //Debug tool
				
				bw.write("@SP\n");
				bw.write("M=M-1\n");
				bw.write("A=M-1\n"); //A points to top value of stack
				
				bw.write("\n"); //Debug tool
			}
		}
	}
	
	public void writeLabel(String label) throws IOException
	{
		bw.write("//!-label " + label + "\n");
		bw.write("("+label+")\n");
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeGoto(String label) throws IOException
	{
		bw.write("//!-goto " + label + "\n");
		
		bw.write("@"+label+"\n");
		bw.write("0;JMP\n");
		//Be careful about previous assumptions, e.g. A points to address of top value of stack. This might cause
		//errors for push, pop, add, etc.
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeIf(String label) throws IOException
	{
		//Assume A = address of top element, D = value of top element
		bw.write("//!-if-goto " + label + "\n");
		
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("M=M-1\n");
		bw.write("@"+label+"\n");
		bw.write("D;JNE\n");
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeCall(String functionName, int numArgs) throws IOException
	{
		//call f n; call function f AFTER n arguments have been pushed onto stack. Assumes the arguments are already pushed
		
		//push return-address, Using label declared below
		bw.write("//!-call " + functionName + " " + numArgs + "\n");
		
		bw.write("@retAddr"+retAddrCtr+"\n");
		bw.write("D=A\n");
		bw.write("@SP\n");
		bw.write("AM=M+1\n");
		bw.write("A=A-1\n");
		bw.write("M=D\n");
		
		//push LCL, Save LCL of the calling function
		bw.write("@LCL\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("AM=M+1\n");
		bw.write("A=A-1\n");
		bw.write("M=D\n");
		
		//push ARG, Save ARG of the calling function
		bw.write("@ARG\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("AM=M+1\n");
		bw.write("A=A-1\n");
		bw.write("M=D\n");
		
		//push THIS, save THIS of the calling function
		bw.write("@THIS\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("AM=M+1\n");
		bw.write("A=A-1\n");
		bw.write("M=D\n");
		
		//push THAT, save THAT of the calling function
		bw.write("@THAT\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("AM=M+1\n");
		bw.write("A=A-1\n");
		//bw.write("A=A-1\n");
		bw.write("M=D\n");
		
		//ARG = SP-n-5, Reposition ARG (n=numArgs)
		bw.write("@"+numArgs+"\n");
		bw.write("D=A\n");
		bw.write("@5\n");
		bw.write("D=D+A\n");
		bw.write("@SP\n");
		bw.write("D=M-D\n");
		bw.write("@ARG\n");
		bw.write("M=D\n");
		
		//LCL = SP, Reposition LCL
		bw.write("@SP\n");
		bw.write("D=M\n");
		bw.write("@LCL\n");
		bw.write("M=D\n");
		
		//bw.write("@22222\n"); //Debug tool
		
		//goto f, Transfer control
		bw.write("@"+functionName+"\n");
		bw.write("0;JMP\n");
		
		//(return-address), Declare a label for the return-address
		bw.write("(retAddr"+retAddrCtr+")\n");
		retAddrCtr++;
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeReturn() throws IOException
	{
		bw.write("//!-return\n");
		
		//Store FRAME = LCL, FRAME is tmp variable R13; holds the address of LCL
		bw.write("@LCL\n");
		bw.write("D=M\n");
		bw.write("@R13\n");
		bw.write("M=D\n");
		
		//RET=*(FRAME-5). Put the return-addr in tmp var R14
		bw.write("@5\n");
		bw.write("D=A\n");
		bw.write("@LCL\n");
		bw.write("A=M-D\n");
		bw.write("D=M\n");
		bw.write("@R14\n");
		bw.write("M=D\n");
		
		//*ARG=pop(), Reposition the return value for the caller
		bw.write("@SP\n");
		bw.write("A=M-1\n");
		bw.write("D=M\n");
		bw.write("@ARG\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		//SP=ARG+1 Restore SP of the caller
		bw.write("D=A\n");
		bw.write("@SP\n");
		bw.write("M=D+1\n");
		
		//THAT=*(FRAME-1), Restore THAT of the caller
		bw.write("@R13\n"); //R13 has the address of LCL
		bw.write("AM=M-1\n");
		bw.write("D=M\n");
		bw.write("@THAT\n");
		bw.write("M=D\n");
		
		//THIS=*(FRAME-2), Restore THIS of the caller
		bw.write("@R13\n");
		bw.write("AM=M-1\n");
		bw.write("D=M\n");
		bw.write("@THIS\n");
		bw.write("M=D\n");
		
		//ARG=*(FRAME-3), Restore ARG of the caller
		bw.write("@R13\n");
		bw.write("AM=M-1\n");
		bw.write("D=M\n");
		bw.write("@ARG\n");
		bw.write("M=D\n");
		
		//LCL=*(FRAME-4), Restore LCL of the caller
		bw.write("@R13\n");
		bw.write("AM=M-1\n");
		bw.write("D=M\n");
		bw.write("@LCL\n");
		bw.write("M=D\n");
		
		//bw.write("@22222\n"); //Debug tool
		
		//goto RET, Goto return-address (in the caller's code)
		bw.write("@R14\n");
		bw.write("A=M\n");
		bw.write("0;JMP\n");
		
		bw.write("\n"); //Debug tool
	}
	
	public void writeFunction(String functionName, int numLocals) throws IOException
	{		
		bw.write("//!-function " + functionName + " " + numLocals + "\n");
		
		//declare a function f and give it k variables. Assume ARG and LCL are set
		bw.write("("+functionName+")\n");
		bw.write("@LCL\n");
		bw.write("A=M\n");
		
		for(int i=0; i < numLocals; i++)
		{
			bw.write("M=0\n");
			bw.write("A=A+1\n");
		}
		
		bw.write("D=A\n");
		
		//bw.write("@22222\n"); //Debug tool
		
		bw.write("@SP\n");
		bw.write("AM=D\n");
		
		bw.write("\n"); //Debug tool
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
