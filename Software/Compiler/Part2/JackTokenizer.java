/**
 * @author Peter Xu peterxu422@gmail.com
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class JackTokenizer {
	
	//TOKENTYPE
	public static final String K = "KEYWORD";
	public static final String SYM = "SYMBOL";
	public static final String ID = "IDENTIFIER";
	public static final String INTC = "INT_CONST";
	public static final String STRC = "STRING_CONST";
	
	//KEYWORD
	private final String[] keywords = {"class", "method", "function", "constructor", "int", "boolean", "char", "void", "var",
			"static", "field", "let", "do", "if", "else", "while", "return", "true", "false", "null", "this"};
	private final char[] sym = {'{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'};
	
	public static String currToken;
	private int numLine;
	private String[] currLineTokens;
	private int cltIdx;
	private BufferedReader br;
	
	/*
	 * Opens the input file/stream and gets ready to tokenize it
	 */
	public JackTokenizer(String filename)  throws FileNotFoundException {	
		br = new BufferedReader(new FileReader(filename));
		cltIdx = 0;
		numLine = 1;
	}
	
	/*
	 * Do we have more tokens in the input?
	 */
	public boolean hasMoreTokens() throws IOException {
		return br.ready();
	}
	
	/*
	 * Gets the next token from the input and makes it the current token. This method should only be called if hasMoreTokens()
	 * is true. Initially there is no current token
	 */
	public void advance() throws IOException {
		if(!hasMoreTokens())
			return;
		
		if(cltIdx == 0)
			getLine();
		
		do {
			currToken = currLineTokens[cltIdx];
			cltIdx++;
		}while(currToken.isEmpty());

//		currToken = currLineTokens[cltIdx];
//		cltIdx++;
		if(cltIdx == currLineTokens.length)
			cltIdx = 0;
/*	
		//Order of checking, by most common: Keyword, Identifier, Symbol, IntegerC, StringC
		String type = tokenType();
		//Keyword
		if(type.equals(K))
			currToken = keyWord();
		//Identifier
		else if(type.equals(ID))
			currToken = identifier();
		//Symbol
		else if(type.equals(SYM))
			currToken = symbol() + "";
		//IntegerC
		else if(type.equals(INTC))
			currToken = intVal() + "";
		//StringC
		else if(type.equals(STRC))
			currToken = stringVal();
		else
			currToken = null;
	*/
	}
	
	public int getNumLine() {
		return numLine;
	}
	
	private void getLine() throws IOException {
		String currLine;
		do {
			currLine = br.readLine();
			numLine++;
			//System.out.println("currLine:" + currLine+"|"+currLine.isEmpty());
			//Assume 1) /** comments start at the beginning of their own line, and 2) */ is at the end of its line 
			int d;
			if((d=currLine.indexOf("/**")) != -1) {
				int e;
				do {
					e=currLine.indexOf("*/");
					currLine = br.readLine();
				}while(e == -1);
			}
			
			if((d=currLine.indexOf("//")) != -1) {
				currLine = currLine.substring(0, d);
				//System.out.println("d:"+d);
			}
			
			currLine = handleQuotes(currLine);
			currLine = currLine.replaceAll("\\s+", " ");
			currLineTokens = currLine.split(" ");
		
			//System.out.println("currLine2:"+currLine+"|");
			//printCLT();
		
		}while(currLine.isEmpty() | currLine.matches("\\s+"));

		//System.out.println("currLine1:"+currLine+"|");
		//printCLT();
		
		Vector<String> v = new Vector<String>();
		for(int i=0; i < currLineTokens.length; i++)
		{
			if(currLineTokens[i].isEmpty())
				continue;
			
			/*
			boolean strconst = false;
			if(currLineTokens[i].matches("[\\S]*[\"][\\S]*[\"][\\S]*")) {
				//currLineTokens[i] = currLineTokens[i].replaceAll("#s@", " ");
				strconst = true;
			}
			*/
			
			String tok = currLineTokens[i];
			
			if(tok.isEmpty())
				continue;
			
			//Checks if there are symbols in the current token. Should skip this if clt is a string const
			boolean hasSym = false;
			for(int j=0; j < sym.length; j++)
			{
				if(tok.equals(sym[j]+""))
					break;
				else if(tok.contains(sym[j]+""))
				{
					String tmp = "";
					hasSym = true;
					int lastIdx = 0;
					for(int k=0; k < tok.length(); k++)
					{
						if(tok.charAt(k) == '\"') {
							k++;
							while(tok.charAt(k) != '\"')
								k++;
							String str = tok.substring(lastIdx, ++k);
							str = str.replaceAll("#s@", " ");
							v.add(str);
							lastIdx = k;
						}
						else {
							for(int l=0; l < sym.length; l++)
							{
								if(tok.charAt(k) == sym[l])
								{
									if(lastIdx != k)
										v.add(tok.substring(lastIdx, k));
									v.add(sym[l] + "");
									lastIdx = k+1;
								}
							}
						}
						if(lastIdx < tok.length() && k == tok.length()-1)
							v.add(tok.substring(lastIdx, tok.length()));
					}
					break;
				}
			}
			
			if(!hasSym)
				v.add(currLineTokens[i]);
			
			
		}
	
		String[] tmp = new String[v.size()];
		
		//System.out.println(v + " size:"+v.size());
		for(int i=0; i < v.size(); i++)
			tmp[i] = v.get(i);

		currLineTokens = tmp;
		//printCLT();
		//System.out.println("finalcurrLine:" + currLine + "|"+currLine.isEmpty());
	}
	
	public void printCLT() {
		System.out.print("[");
		for(int i=0; i < currLineTokens.length; i++)
		{
			String tmp = currLineTokens[i];
			
			if(i != currLineTokens.length-1)
				tmp += ", ";
			
			System.out.print(tmp);
		}
		System.out.print("]\n");
	}
	
	//Assume Quotes are on one line
	public String handleQuotes(String s) {
		String tmp = s;
		int c;
		if((c=s.indexOf("\"")) != -1) {
			tmp = "";
			c++;
			tmp += s.substring(0, c);
			char d;
			while((d=s.charAt(c)) != '\"') {
				if(d != ' ') 
					tmp += d;
				else
					tmp += "#s@";
				c++;
			}
			if(d == '\"') {
				tmp += "\"";
				c++;
				String tmp2 = s.substring(c, s.length());
				tmp += handleQuotes(tmp2);
			}
			else
				tmp += s;
		}
		return tmp;
	}
	
	/*
	 * Returns the type of the current token
	 */
	public String tokenType() {
		if(currToken.isEmpty())
			return null;
		
		//keyword
		for(int i=0; i < keywords.length; i++)
			if(currToken.equals(keywords[i]))
				return K;
			
		//symbol
		for(int i=0; i < sym.length; i++)
			if(currToken.charAt(0) == sym[i])
				return SYM;
		
		//identifier
		if(currToken.matches("^[^\\d][\\w]*"))
			return ID;
		
		//intc
		try {
			int i = Integer.parseInt(currToken);
			if(i >= 0 && i <= 32767)
				return INTC;
		}
		catch (NumberFormatException nfe) {
			//strc
			// Returns true if the string contains a arbitrary number of characters except b; regex: "([\\w&&[^b]])*"
			if(currToken.matches("^[\"]([(\\S| )&&[^\"]])*[\"]"))
				return STRC;
		}
		
		return null;
	}
	
	/*
	 * Returns the keyword which is the current token. Should be called only when tokenType() is KEYWORD
	 */
	public String keyWord() {
		if(!tokenType().equals(K)) 
			return null;

		return currToken;
	}
	
	/*
	 * Returns the character which is the current token. Should be called only when tokenType() is SYMBOL
	 */
	public char symbol() {
		if(!tokenType().equals(SYM))
			return '#';
		
		return currToken.charAt(0);
	}
	
	/*
	 * Returns the identifier which is the current token. Should be called only when tokenType() is IDENTIFIER
	 */
	public String identifier() {
		if(!tokenType().equals(ID))
			return null;
		
		return currToken;
	}
	
	/*
	 * Returns the integer value of the current token. Should be called only when tokenType() is INT_CONST
	 */
	public int intVal() {
		if(!tokenType().equals(INTC))
			return Integer.MIN_VALUE;
		
		return Integer.parseInt(currToken);
	}
	
	/*
	 * Returns the string value of the current token, without the double quotes. Should be called only when tokenType()
	 * is STRING_CONST
	 */
	public String stringVal() {
		if(!tokenType().equals(STRC))
			return null;
		//currToken.replaceAll("#s@", " ");
		return currToken.substring(1, currToken.length()-1);
	}
	
}
/*
public static final String CL = "CLASS";
public static final String M = "METHOD";
public static final String FN = "FUNCTION";
public static final String CSTR = "CONSTRUCTOR";
public static final String INT = "INT";
public static final String B = "BOOLEAN";
public static final String CH = "CHAR";
public static final String VO = "VOID";
public static final String VA = "VAR";
public static final String STC = "STATIC";
public static final String FLD = "FIELD";
public static final String L = "LET";
public static final String D = "DO";
public static final String IF = "IF";
public static final String E = "ELSE";
public static final String W = "WHILE";
public static final String R = "RETURN";
public static final String T = "TRUE";
public static final String F ="FALSE";
public static final String N = "NULL";
public static final String TH = "THIS";
private static final Exception  = null;
*/