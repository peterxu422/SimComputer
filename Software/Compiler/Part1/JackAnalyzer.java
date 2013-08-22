import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Vector;

public class JackAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String arg = "Square";
		String file = "SquareGame";
		String home10path = "C:\\Users\\Peter\\Documents\\Nand2Tetris\\projects\\10\\"+arg+"\\";
		String jackfile = "";
		String path = "";
		//path = home10path + "CompEngTest\\" + file + ".xml";
		//jackfile = home10path + file + ".jack";
		
		File home10 = new File(home10path);
		File[] listOfFiles = home10.listFiles();
		Vector<File> jFiles = new Vector<File>();
		
		for(File files : listOfFiles) {
			String name = files.getName();
			String ext = "";
			
			int c;
			if((c = name.indexOf('.')) != -1)
				ext = name.substring(c, name.length());
			if(ext.equals(".jack"))
				jFiles.add(files);
		}
		
		String jackfile2 = home10path + "CompEngTest\\" + file + "Test.jack";
		String path2 = home10path + "CompEngTest\\" + file + "Test.xml";
		
		BufferedWriter bw;
		JackTokenizer jt;
		CompilationEngine ce, ce2;
		
		try {
				for(File f : jFiles) {
				System.out.println("------" + f.getName() + "------");
				jackfile = home10path + f.getName();
				path = home10path + "CompEngTest\\" + f.getName().substring(0, f.getName().indexOf('.')) + ".xml";
				ce = new CompilationEngine(jackfile, path);	
			}
			
			//ce = new CompilationEngine(jackfile, path);
			//ce2 = new CompilationEngine(jackfile2, path2);
			//ce2.compileLet();
			
			/*			
			File f = new File(path2);
			
			if(!f.exists())
				f.createNewFile();
			
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			jt = new JackTokenizer(jackfile2);
			bw.write("<tokens>\n");
			while(jt.hasMoreTokens())
			{
				jt.advance();
				String s = null;
				String type = jt.tokenType();
				
				if(type.equals(JackTokenizer.ID))
					bw.write("<identifier>"+jt.identifier()+"</identifier>\n");
				else if(type.equals(JackTokenizer.INTC))
					bw.write("<integerConstant>"+jt.intVal()+"</integerConstant>\n");
				else if(type.equals(JackTokenizer.K))
					bw.write("<keyword>"+jt.keyWord()+"</keyword>\n");
				else if(type.equals(JackTokenizer.STRC))
					bw.write("<stringConstant>"+jt.stringVal()+"</stringConstant>\n");
				else if(type.equals(JackTokenizer.SYM)) {
					String t;
					if(JackTokenizer.currToken.equals("<"))
						t = "&lt;";
					else if(JackTokenizer.currToken.equals(">"))
						t = "&gt;";
					else if(JackTokenizer.currToken.equals("&"))
						t = "&amp;";
					else
						t = jt.symbol() + "";
					bw.write("<symbol>"+t+"</symbol>\n");
				}
				
				System.out.println(JackTokenizer.currToken);
			}
*/			
//			bw.write("</tokens>\n");
//			bw.close();
				
		} 
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		String s = "\"HOW MANY NUMBERS? \"";
		
		//Regex for string constant
		String pattern = "^[\"]([(\\S| )&&[^\"]])*[\"]?";
		//Regex for Identifier
		//String pattern = "^[^\\d][\\w]*";
		//String pattern = "\\s+";
		
		//String pattern = "[\\S]*[\"][\\S]*[\"][\\S]*";
		//System.out.println(s + " " + s.matches(pattern));
		//System.out.println(s.replaceAll("\\s+", " "));
		//[\\S ]*
		// Returns true if the string contains a arbitrary number of characters except b; regex: "([\\w&&[^b]])*"
		
		//
	}

}
