/**
 * @author Peter Xu peterxu422@gmail.com
 */
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Provides a symbol table abstraction. The symbol table associates the identifier names found in the program with identifier
 * properties needed for compilation: type, kind, and running index. The symbol table for Jack programs has two nested scopes (class, subroutines)
 * 
 * Assumption: Any identifier not found in the symbol table may be assumed to be a subroutine name or a class name
 */
public class SymbolTable {
	public static final String ST = "STATIC";
	public static final String F = "FIELD";
	public static final String A = "ARG";
	public static final String V = "VAR";
	
	private Hashtable<String, STValues> classScope;
	private Hashtable<String, STValues> subScope;
	private Hashtable<String, STValues> currScope;
	private int subArgIdx, subVarIdx;
	private int classStaticIdx, classFieldIdx;
	
	/**
	 * Creates a new empty symbol table.
	 */
	public SymbolTable() {
		classScope = new Hashtable<String, STValues>();
		subScope = new Hashtable<String, STValues>();
		currScope = classScope;
		subArgIdx = 0;
		subVarIdx = 0;
		classStaticIdx = 0;
		classFieldIdx = 0;
	}
	
	/**
	 * Starts a new subroutine scope (i.e. resets the subroutine's symbol table)
	 */
	public void startSubroutine(String subtype) {
		subScope.clear();
		currScope = subScope;
		subArgIdx = subtype.equals("method") ? 1 : 0;
		subVarIdx = 0;
	}
	
	/**
	 * Defines a new identifier of a given 'name', 'type', and 'kind' and assigns it a running index. STATIC and FIELD identifiers have
	 * a class scope, while ARG and VAR identifiers have a subroutine scope
	 */
	public void Define(String name, String type, String kind) {
		int i = -1;
		STValues tmp = null;
		
		if(kind.equals(ST) || kind.equals(F)) {
			switch(kind) {
			case ST:				i = classStaticIdx++;
									break;
			case F:					i = classFieldIdx++;
									break;
			}
			tmp = classScope.put(name, new STValues(type, kind, i));
			
			if(tmp != null) {
				System.out.println("Multiple declarations of class identifier: " + name);
				System.exit(1);
			}
		}
		else if(kind.equals(A) || kind.equals(V)) {
			switch(kind) {
			case A:					i = subArgIdx++;
									break;
			case V:					i = subVarIdx++;
									break;
			}
			tmp = subScope.put(name, new STValues(type, kind, i));

			if(tmp != null) {
				System.out.println("Multiple declarations of subroutine identifier: " + name);
				System.exit(1);
			}
		}
		else
			throw new IllegalArgumentException("Identifier '" + name + "' has an invalid 'kind': " + kind);
		
		System.out.println("ClassScope: " + classScope.toString());
		System.out.println("SubScope: " + subScope.toString());
	}
	
	/**
	 * Returns the number of variables of the given 'kind' already defined in the current scope.
	 * 
	 * @kind STATIC, FIELD, ARG, or VAR
	 */
	public int VarCount(String kind) {
		int count = 0;
		Hashtable<String, STValues> tmpScope = null;
		Enumeration<String> e;
		
		if(kind.equals(SymbolTable.V) || kind.equals(SymbolTable.A))
			tmpScope = subScope;
		else if(kind.equals(SymbolTable.F) || kind.equals(SymbolTable.ST))
			tmpScope = classScope;
		else {
			System.out.println("Expected static, field, argument, or variable kind.");
			System.exit(1);
		}
		
		e = tmpScope.keys();
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			if(tmpScope.get(key) != null && tmpScope.get(key).getKind().equals(kind))
				count++;
		}
		
		/*
		System.out.println(subScope.toString());
		System.out.println(classScope.toString());
		System.out.println("HEYYYYYYY: " + count + " kind:" + kind);
		*/
		return count;
	}
	
	/**
	 * Returns the 'kind' of the named identifier in the current scope. If the identifier is unknown in the current scope, 
	 * returns NONE.
	 */
	public String KindOf(String name) {
		STValues tmp = currScope.get(name);
		String kind = null;
		
		if(tmp != null)
			return tmp.getKind();
		
		if(currScope != classScope) {
			tmp = classScope.get(name);
			if(tmp != null)
				return tmp.getKind();
		}
		
		return "NONE";
	}
	
	/**
	 * Returns the 'type' of the named identifier in the current scope.
	 */
	public String TypeOf(String name) {
		STValues tmp = currScope.get(name);
		
		if(tmp != null)
			return tmp.getType();
		
		if(currScope != classScope) {
			tmp = classScope.get(name);
			if(tmp != null)
				return tmp.getType();
		}
		
		return null;
	}
	
	/**
	 * Returns the 'index' assigned to the named identifier
	 */
	public int IndexOf(String name) {
		STValues tmp = currScope.get(name);
		if(tmp != null)
			return tmp.getIndex();
		
		if(currScope != classScope) {
			tmp = classScope.get(name);
			if(tmp != null)
				return tmp.getIndex();
		}
		
		return -1;
	}
}
