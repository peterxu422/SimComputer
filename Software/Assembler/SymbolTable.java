import java.util.Hashtable;

public class SymbolTable 
{
	private Hashtable<String, Integer> symTbl;
	private char legalSymbols[] = {'_', '.', '$', ':'};
	
	public SymbolTable()
	{	
		symTbl = new Hashtable<String, Integer>();
		
		addEntry("SP", 0);
		addEntry("LCL", 1);
		addEntry("ARG", 2);
		addEntry("THIS", 3);
		addEntry("THAT", 4);
		
		String reg = "R";
		for(int i=0; i<16; i++)
			addEntry(reg+i, i);
		
		addEntry("SCREEN", 16384);
		addEntry("KBD", 24576);
	}
	
	public void addEntry(String sym, int addr)
	{
		boolean isDigit = false;
		boolean isLetter = false;
		boolean legalSym = false;
		boolean validSym = true;
		
		char c0 = sym.charAt(0);
		if(c0 >= '0' && c0 <= '9') //checks if symbol starts with a digit
			validSym = false;
		else
		{
			for(int i=0; i<sym.length(); i++)
			{
				char c = sym.charAt(i);
				if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
					isLetter = true;
				else if(c >= '0' && c <= '9')
					isDigit = true;
				else
				{
					for(int j=0; j<legalSymbols.length; j++)
						if(c == legalSymbols[j])
						{
							legalSym = true;
							break;
						}
				}
				
				if(!(isDigit || isLetter || legalSym))
				{
					validSym = false;
					break;
				}
				
				isDigit = false;
				isLetter = false;
				legalSym = false;
			}
		}
		
		if(validSym == false)
		{
			System.out.println("Compile error: Invalid Symbol " + "\"" + sym + "\"" );
			System.exit(1);
		}
		
		symTbl.put(sym, new Integer(addr));
	}
	
	public boolean contains(String sym)
	{
		return symTbl.containsKey(sym);
	}
	
	public int GetAddress(String sym)
	{
		return symTbl.get(sym);
	}
}
