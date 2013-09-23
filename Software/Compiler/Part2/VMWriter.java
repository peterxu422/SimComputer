import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * Emits VM commands into a file, using the VM command syntax.
 * @author Peter
 *
 */
public class VMWriter {
	
	private BufferedWriter bw;
	
	/**
	 * Creates a new file and prepares it for writing.
	 * @param out
	 */
	public VMWriter(String out) throws IOException {
		File f = new File(out);
		
		if(!f.exists())
			f.createNewFile();
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		
		bw = new BufferedWriter(fw);
	}
	
	/**
	 * Writes a VM push command.
	 * @param segment CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
	 * @param index
	 */
	public void writePush(String segment, int index) throws IOException {
		String seg = "";
		switch(segment) {
		case "CONST":			seg = "constant";
								break;
		case SymbolTable.A:		seg = "argument";
								break;
		case "LOCAL":			seg = "local";
								break;
		case SymbolTable.V:		seg = "local";
								break;
		case SymbolTable.ST:	seg = "static";
								break;
		case SymbolTable.F:	 	seg = "this";
								break;
		case "THIS":			seg = "this";
								break;
		case "THAT":			seg = "that";
								break;
		case "POINTER":			seg = "pointer";
								break;
		case "TEMP":			seg = "temp";
								break;
		}
		
		if(!seg.isEmpty())
			bw.write("push " + seg + " " + index + "\n");
	}
	
	/**
	 * Writes a VM pop command.
	 * @param segment CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
	 * @param index
	 */
	public void writePop(String segment, int index) throws IOException {
		String seg = "";
		switch(segment) {
		case "CONST":			seg = "constant";
								break;
		case SymbolTable.A:		seg = "argument";
								break;
		case "LOCAL":			seg = "local";
								break;
		case SymbolTable.V:		seg = "local";
								break;
		case SymbolTable.ST:	seg = "static";
								break;
		case SymbolTable.F:	 	seg = "this";
								break;
		case "THIS":			seg = "this";
								break;
		case "THAT":			seg = "that";
								break;
		case "POINTER":			seg = "pointer";
								break;
		case "TEMP":			seg = "temp";
								break;
		}
		
		if(!seg.isEmpty())
			bw.write("pop " + seg + " " + index + "\n");
	}
	
	/**
	 * Writes a VM arithmetic command.
	 * @param command ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
	 */
	public void WriteArithmetic(String command) throws IOException {
		if(command.equals("ADD") || command.equals("SUB") || command.equals("NEG") || command.equals("EQ") || command.equals("GT")
				|| command.equals("LT") || command.equals("AND") || command.equals("OR") || command.equals("NOT")) {
			command = command.toLowerCase(); //may not be necessary
			bw.write(command + "\n");
		}
		else {
			System.out.println("Invalid VM command: " + command);
			System.exit(1);
		}
	}
	
	/**
	 * Writes a VM label command.
	 * @param label
	 */
	public void WriteLabel(String label) throws IOException {
		bw.write("label " + label + "\n");
	}
	
	/**
	 * Writes a VM goto command.
	 * @param label
	 */
	public void WriteGoto(String label) throws IOException {
		bw.write("goto " + label + "\n");
	}
	
	/**
	 * Writes a VM If-goto command.
	 * @param label
	 */
	public void WriteIf(String label) throws IOException {
		bw.write("if-goto " + label + "\n");
	}
	
	/**
	 * Writes a VM call command.
	 * @param name
	 * @param nArgs
	 */
	public void writeCall(String name, int nArgs) throws IOException {
		bw.write("call " + name + " " + nArgs + "\n");
	}
	
	/**
	 * Writes a VM function command.
	 * @param name name of the function. Should be preceded by the class name and a '.' as well.
	 * @param nLocals number of local arguments that will occur in the function
	 */
	public void writeFunction(String name, int nLocals) throws IOException {
		bw.write("function " + name + " " + nLocals + "\n");
	}
	
	/**
	 * Writes a VM return command.
	 */
	public void writeReturn() throws IOException {
		bw.write("return\n");
	}
	
	public void writeComment(String msg) throws IOException {
		bw.write("//" + msg + "\n");
	}
	
	/**
	 * Closes the output file.
	 */
	public void close() throws IOException {
		bw.close();
	}
}
