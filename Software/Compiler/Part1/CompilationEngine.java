import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class CompilationEngine {
	private JackTokenizer jt;
	private BufferedWriter bw;
	private String indent;
	private int numIndent;
	
	/*
	 * Creates a new compilation engine with the given input and output. The next routine called must be compileClass()
	 */
	public CompilationEngine(String in, String out) throws FileNotFoundException, IOException {
		indent = "";
		numIndent = 0;
		
		File f = new File(out);
		
		if(!f.exists())
			f.createNewFile();
		
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		
		jt = new JackTokenizer(in);
		bw = new BufferedWriter(fw);
		
		//jt.advance();
		//System.out.println(jt.currToken);
		
		CompileClass();
		bw.close();
	}
	
	private void makeIndents() {
		indent = "";
		for(int i=0; i < numIndent; i++)
			indent += "  ";
	}
	
	/*
	 * Compiles a complete class
	 * 'class' className '{' classVarDec* subroutineDec* '}'
	 */
	public void CompileClass() throws IOException {
		jt.advance();
		bw.write("<class>\n");
		System.out.println("<class>\n");
		
		numIndent++;
		makeIndents();
		if(jt.keyWord() != null && jt.keyWord().equals("class")) {
			bw.write(indent + "<keyword> class </keyword>\n");
			System.out.println(indent + "<keyword> class </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'class'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '{') {
			bw.write(indent + "<symbol> { </symbol>\n");
			System.out.println(indent + "<symbol> { </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '{'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//classVarDec*
		jt.advance();
		while(jt.keyWord() != null && (jt.keyWord().equals("static") || jt.keyWord().equals("field"))) {
			CompileClassVarDec();
		}
		
		//subroutineDec*
		//no advance() needed before, by assumption of CompileClassVarDec()
		String t = jt.keyWord();
		while(t != null && (t.equals("constructor") | t.equals("function") | t.equals("method"))) {
			CompileSubroutine();
			jt.advance();
			t = jt.keyWord();
		}

		//jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '}') {
			bw.write(indent + "<symbol> } </symbol>\n");
			System.out.println(indent + "<symbol> } </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '}'.  But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		numIndent--;
		makeIndents();
		bw.write("</class>\n");
		System.out.println("</class>\n");
	}
	
	/*
	 * Compiles static declaration or a field declaration
	 * ('static' | 'field') type varName (',' varName)* ';'
	 * type: 'int' | 'char' | 'boolean' | className
	 * 
	 * Assumes CompileClassVarDec() will always advance to the next token in all cases. No jt.advance() necessary for compilations
	 * after it
	 */
	public void CompileClassVarDec() throws IOException {
		bw.write(indent + "<classVarDec>\n");
		System.out.println(indent + "<classVarDec>\n");
		numIndent++;
		makeIndents();

		if(jt.keyWord() != null && (jt.keyWord().equals("static") || jt.keyWord().equals("field"))) {
			bw.write(indent + "<keyword> " + jt.keyWord() + " </keyword>\n");	
			System.out.println(indent + "<keyword> " + jt.keyWord() + " </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'static' or 'field'.  But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		String t = jt.keyWord();
		if(t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
			bw.write(indent + "<keyword> " + t + " </keyword>\n");
			System.out.println(indent + "<keyword> " + t + " </keyword>\n");
		}
		else if(jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected variable type. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		while(jt.symbol() != '#' && jt.symbol() == ',') {
			bw.write(indent + "<symbol> , </symbol>\n");
			System.out.println(indent + "<symbol> , </symbol>\n");
			
			jt.advance();
			if(jt.tokenType().equals(JackTokenizer.ID)) {
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			jt.advance();
		}
		
		if(jt.symbol() != '#' && jt.symbol() == ';') {
			bw.write(indent + "<symbol> ; </symbol>\n");
			System.out.println(indent + "<symbol> ; </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ';'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</classVarDec>\n");
		System.out.println(indent + "</classVarDec>\n");
	}
	
	/*
	 * Compiles a complete method, function, or constructor
	 * subroutine: ('constructor' | 'function' | 'method') ('void' | type) subroutineName '(' parameterList ')' subroutineBody
	 * type: 'int' | 'char' | 'boolean' | className
	 * subroutineName: identifier
	 * subroutineBody: '{' varDec* statements '}'
	 */
	public void CompileSubroutine() throws IOException {
		bw.write(indent + "<subroutineDec>\n");
		System.out.println(indent + "<subroutineDec>\n");
		numIndent++;
		makeIndents();
		
		//constructor | function | method
		String t = jt.keyWord();
		if(t != null && (t.equals("constructor") | t.equals("function") | t.equals("method"))) {
			bw.write(indent + "<keyword> " + t + " </keyword>\n");
			System.out.println(indent + "<keyword> " + t + " </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'constructor', 'function', or 'method'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//void | type
		jt.advance();
		t = jt.keyWord();
		if(t != null && (t.equals("void") | t.equals("int") | t.equals("char") | t.equals("boolean"))) {
			bw.write(indent + "<keyword> " + t + " </keyword>\n");
			System.out.println(indent + "<keyword> " + t + " </keyword>\n");
		}
		else if(jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected variable type. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//subroutineName
		jt.advance();
		if(jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//(
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '(') {
			bw.write(indent + "<symbol> ( </symbol>\n");
			System.out.println(indent + "<symbol> ( </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '('. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//parameterList
		jt.advance();
		compileParameterList();
		
		//). jt.advance() not needed b/c in compileParamList() in all cases it will go to next token, including the jt.adv() from before compileParamList()
		if(jt.symbol() != '#' && jt.symbol() == ')') {
			bw.write(indent + "<symbol> ) </symbol>\n");
			System.out.println(indent + "<symbol> ) </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ')'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//subroutineBody: '{' varDec* statements '}'
		bw.write(indent + "<subroutineBody>\n");
		System.out.println(indent + "<subroutineBody>\n");
		numIndent++;
		makeIndents();
		
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '{') {
			bw.write(indent + "<symbol> { </symbol>\n");
			System.out.println(indent + "<symbol> { </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '{'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		while(jt.keyWord() != null && jt.keyWord().equals("var")) {
			compileVarDec();
			jt.advance();
		}
		
		compileStatements();
		
		//no advance() b/c of compileStatements() assumption
		if(jt.symbol() != '#' && jt.symbol() == '}') {
			bw.write(indent + "<symbol> } </symbol>\n");
			System.out.println(indent + "<symbol> } </symbol>\n");
		}	
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '}'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</subroutineBody>\n");
		System.out.println(indent + "</subroutineBody>\n");
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</subroutineDec>\n");
		System.out.println(indent + "</subroutineDec>\n");
	}
	
	/*
	 * Compiles a (possibly empty) parameter list, not including the enclosing "()"
	 * parameterList: ((type varName)(',' type varName)*)?
	 * 
	 * Assumption: Will advance to the next token automatically for any case;
	 */
	public void compileParameterList() throws IOException {
		bw.write(indent + "<parameterList>\n");
		System.out.println(indent + "<parameterList>\n");
		numIndent++;
		makeIndents();
		
		String t = jt.keyWord();
		String type = jt.tokenType();
		if(t != null && type != null && (type.equals(JackTokenizer.K) || type.equals(JackTokenizer.ID))) //Need to do it this way to handle ? operator in grammar
		{
			//type
			if(type.equals(JackTokenizer.K) && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
				bw.write(indent + "<keyword> " + t + "</keyword>\n");
				System.out.println(indent + "<keyword> " + t + "</keyword>\n");
			}
			else if(type.equals(JackTokenizer.ID)) {
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected variable type. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			//varName
			jt.advance();
			if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			//,
			jt.advance();
			while(jt.symbol() != '#' && jt.symbol() == ',')
			{
				bw.write(indent + "<symbol> , </symbol>\n");
				System.out.println(indent + "<symbol> , </symbol>\n");
				
				//type
				jt.advance();
				t = jt.keyWord();
				if(t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
					bw.write(indent + "<keyword> " + t + "</keyword>\n");
					System.out.println(indent + "<keyword> " + t + "</keyword>\n");
				}
				else if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
					bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
					System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				}
				else {
					System.out.println("Line " + jt.getNumLine() + ": Expected variable type. But encountered: " + JackTokenizer.currToken);
					System.exit(1);
				}
				
				//varName
				jt.advance();
				if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
					bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
					System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				}
				else {
					System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
					System.exit(1);
				}
				
				jt.advance();
			}

		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</parameterList>\n");
		System.out.println(indent + "</parameterList>\n");
	}
	
	/*
	 * Compiles a var declaration
	 * varDec: 'var' type varName (',' varName)* ';'
	 * type: int | char | boolean | className
	 */
	public void compileVarDec() throws IOException {
		bw.write(indent + "<varDec>\n");
		System.out.println(indent + "<varDec>\n");
		numIndent++;
		makeIndents();
		
		//var
		if(jt.keyWord() != null && jt.keyWord().equals("var")) {
			bw.write(indent + "<keyword> var </keyword>\n");
			System.out.println(indent + "<keyword> var </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'var'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//type
		jt.advance();
		String t = jt.keyWord();
		if(t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
			bw.write(indent + "<keyword> " + t + " </keyword>\n");
			System.out.println(indent + "<keyword> " + t + " </keyword>\n");
		}
		else if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected variable type. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//varName
		jt.advance();
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//(',' varName)*
		jt.advance();
		while(jt.symbol() != '#' && jt.symbol() == ',') {
			bw.write(indent + "<symbol> , </symbol>\n");
			System.out.println(indent + "<symbol> , </symbol>\n");
			
			jt.advance();
			if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			jt.advance();
		}
		
		//;
		if(jt.symbol() != '#' && jt.symbol() == ';') {
			bw.write(indent + "<symbol> ; </symbol>\n");
			System.out.println(indent + "<symbol> ; </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ';'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</varDec>\n");
		System.out.println(indent + "</varDec>\n");
	}
	
	/*
	 * Compiles a sequence of statements, not including the enclosing "{}"
	 * statements: statement*
	 * statement: letStatement | ifStatement | whileStatement | doStatement | returnStatement
	 * 
	 * Assume any time compileStatements() is used, that the compilation after it does not require an advance(). This method 
	 * automatically advances to the next token at the end
	 */
	public void compileStatements() throws IOException {
		bw.write(indent + "<statements>\n");
		System.out.println(indent + "<statements>\n");
		numIndent++;
		makeIndents();
		
		String t = jt.keyWord();
		while(t != null && (t.equals("let") || t.equals("if") || t.equals("while") || t.equals("do") || t.equals("return"))) {
			if(t.equals("let")) {
				compileLet();
				jt.advance();		//advance() at the end except for 'if' are necessary to match cases in 'if' when there's an 'else'. 'else' forces an advance to the next token
			}
			else if(t.equals("if")) {
				compileIf();
			}
			else if(t.equals("while")) {
				compileWhile();
				jt.advance();
			}
			else if(t.equals("do")) {
				compileDo();
				jt.advance();
			}
			else if(t.equals("return")) {
				compileReturn();
				jt.advance();
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'let', 'if', 'while', 'do', or 'return'. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			t = jt.keyWord();
		}
	
		numIndent--;
		makeIndents();
		bw.write(indent + "</statements>\n");
		System.out.println(indent + "</statements>\n");
	}
	
	/*
	 * Compiles a do statement
	 * 	doStatement: 'do' subroutineCall ';'
	 *	subroutineCall: subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
	 */
	public void compileDo() throws IOException {
		bw.write(indent + "<doStatement>\n");
		System.out.println(indent + "<doStatement>\n");
		numIndent++;
		makeIndents();
		
		//do
		if(jt.keyWord() != null && jt.keyWord().equals("do")) {
			bw.write(indent + "<keyword> do </keyword>\n");
			System.out.println(indent + "<keyword> do </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'do'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//subroutineCall
		//subroutineName
		jt.advance();
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {	//subroutineName | className | varName
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//(
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '(') {
			bw.write(indent + "<symbol> ( </symbol>\n");
			System.out.println(indent + "<symbol> ( </symbol>\n");
		}
		//.
		else if(jt.symbol() != '#' && jt.symbol() == '.') {
			bw.write(indent + "<symbol> . </symbol>\n");
			System.out.println(indent + "<symbol> . </symbol>\n");
			
			//subroutineName
			jt.advance();
			if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			//(
			jt.advance();
			if(jt.symbol() != '#' && jt.symbol() == '(') {
				bw.write(indent + "<symbol> ( </symbol>\n");
				System.out.println(indent + "<symbol> ( </symbol>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected symbol '('. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '(' or '.' . But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		CompileExpressionList();	//Make this method advance to next token for all cases. Just like ParameterList
		 
		//)
		if(jt.symbol() != '#' && jt.symbol() == ')') {
			bw.write(indent + "<symbol> ) </symbol>\n");
			System.out.println(indent + "<symbol> ) </symbol>\n");
		}
		else {
			System.out.println("--Line " + jt.getNumLine() + ": Expected symbol ')'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//;
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == ';') {
			bw.write(indent + "<symbol> ; </symbol>\n");
			System.out.println(indent + "<symbol> ; </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ';'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</doStatement>\n");
		System.out.println(indent + "</doStatement>\n");
	}
	
	/*
	 * Compiles a let statement
	 * letStatement: 'let' varName ('[' expression ']')? '=' expression ';'
	 */
	public void compileLet() throws IOException {
		bw.write(indent + "<letStatement>\n");
		System.out.println(indent + "<letStatement>\n");
		numIndent++;
		makeIndents();
		
		//let
		if(jt.keyWord() != null && jt.keyWord().equals("let")) {
			bw.write(indent + "<keyword> let </keyword>\n");
			System.out.println(indent + "<keyword> let </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'let'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//varName
		jt.advance();
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//('[' expression ']')?
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '[') {
			//[
			bw.write(indent + "<symbol> [ </symbol>\n");
			System.out.println(indent + "<symbol> [ </symbol>\n");
			
			//expression
			jt.advance();
			CompileExpression();
			
			//]
			if(jt.symbol() != '#' && jt.symbol() == ']') {
				bw.write(indent + "<symbol> ] </symbol>\n");
				System.out.println(indent + "<symbol> ] </symbol>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected symbol ']'. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			jt.advance();
		}
		
		//no advance
		//=
		if(jt.symbol() != '#' && jt.symbol() == '=') {
			bw.write(indent + "<symbol> = </symbol>\n");
			System.out.println(indent + "<symbol> = </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '='. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//expression
		jt.advance();
		CompileExpression();
		
		//;
		if(jt.symbol() != '#' && jt.symbol() == ';') {
			bw.write(indent + "<symbol> ; </symbol>\n");
			System.out.println(indent + "<symbol> ; </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ';'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</letStatement>\n");
		System.out.println(indent + "</letStatement>\n");
	}
	
	/*
	 * Compiles a while statement
	 * whileStatement: 'while' '(' expression ')' '{' statements '}'
	 */
	public void compileWhile() throws IOException {
		bw.write(indent + "<whileStatement>\n");
		System.out.println(indent + "<whileStatement>\n");
		numIndent++;
		makeIndents();
		
		//while
		if(jt.keyWord() != null && jt.keyWord().equals("while")) {
			bw.write(indent + "<keyword> while </keyword>\n");
			System.out.println(indent + "<keyword> while </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'while'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//(
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '(') {
			bw.write(indent + "<symbol> ( </symbol>\n");
			System.out.println(indent + "<symbol> ( </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '('. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//expression
		jt.advance();
		CompileExpression();
		
		//)
		if(jt.symbol() != '#' && jt.symbol() == ')') {
			bw.write(indent + "<symbol> ) </symbol>\n");
			System.out.println(indent + "<symbol> ) </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ')'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//{
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '{') {
			bw.write(indent + "<symbol> { </symbol>\n");
			System.out.println(indent + "<symbol> { </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '{'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//statements
		jt.advance();
		compileStatements();
		
		//no advance b/c of compileStatements() assumption
		//}
		if(jt.symbol() != '#' && jt.symbol() == '}') {
			bw.write(indent + "<symbol> } </symbol>\n");
			System.out.println(indent + "<symbol> } </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '}'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}		
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</whileStatement>\n");
		System.out.println(indent + "</whileStatement>\n");
	}
	
	/*
	 * Compiles a return statement
	 * returnStatement: 'return' expression? ';'
	 */
	public void compileReturn() throws IOException {
		bw.write(indent + "<returnStatement>\n");
		System.out.println(indent + "<returnStatement>\n");
		numIndent++;
		makeIndents();
		
		//return
		if(jt.keyWord() != null && jt.keyWord().equals("return")) {
			bw.write(indent + "<keyword> return </keyword>\n");
			System.out.println(indent + "<keyword> return </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'return'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		

		jt.advance();
		String t = jt.tokenType();
		if(t != null && (t.equals(JackTokenizer.INTC) || t.equals(JackTokenizer.STRC) || t.equals(JackTokenizer.K) || 
				t.equals(JackTokenizer.ID)) || (t.equals(JackTokenizer.SYM)) && (jt.symbol() == '(' || jt.symbol() == '-' || jt.symbol() == '~' ))
			CompileExpression();
			
		//jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == ';') {
			bw.write(indent + "<symbol> ; </symbol>\n");
			System.out.println(indent + "<symbol> ; </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ';'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</returnStatement>\n");
		System.out.println(indent + "</returnStatement>\n");
	}
	
	/*
	 * Compiles an if statement. possibly with a trailing else clause.
	 * ifStatement: 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
	 * 
	 * Assume that compileIf() automatically advances to the next token
	 */
	public void compileIf() throws IOException {
		bw.write(indent + "<ifStatement>\n");
		System.out.println(indent + "<ifStatement>\n");
		numIndent++;
		makeIndents();
		
		if(jt.keyWord() != null && jt.keyWord().equals("if")) {
			bw.write(indent + "<keyword> if </keyword>\n");
			System.out.println(indent + "<keyword> if </keyword>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected keyword 'if'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '(') {
			bw.write(indent + "<symbol> ( </symbol>\n");
			System.out.println(indent + "<symbol> ( </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '('. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		//***CHECK ALL CASES OF EXPRESSION. Expression should advance for all cases
		jt.advance();
		CompileExpression();
		
		
		if(jt.symbol() != '#' && jt.symbol() == ')') {
			bw.write(indent + "<symbol> ) </symbol>\n");
			System.out.println(indent + "<symbol> ) </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol ')'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		if(jt.symbol() != '#' && jt.symbol() == '{') {
			bw.write(indent + "<symbol> { </symbol>\n");
			System.out.println(indent + "<symbol> { </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '{'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance();
		compileStatements();
		
		//no advance b/c of compileStatements() assumption
		if(jt.symbol() != '#' && jt.symbol() == '}') {
			bw.write(indent + "<symbol> } </symbol>\n");
			System.out.println(indent + "<symbol> } </symbol>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected symbol '}'. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
		
		jt.advance(); //Consider case when there's no else. token will advance regardless. Check for the rest of compileStatements()
		if(jt.keyWord() != null && jt.keyWord().equals("else")) {
			bw.write(indent + "<keyword> else </keyword>\n");
			System.out.println(indent + "<keyword> else </keyword>\n");
			
			jt.advance();
			if(jt.symbol() != '#' && jt.symbol() == '{') {
				bw.write(indent + "<symbol> { </symbol>\n");
				System.out.println(indent + "<symbol> { </symbol>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected symbol '{'. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			jt.advance();
			compileStatements();
			
			//no advance b/c of compileStatements() assumption
			if(jt.symbol() != '#' && jt.symbol() == '}') {
				bw.write(indent + "<symbol> } </symbol>\n");
				System.out.println(indent + "<symbol> } </symbol>\n");
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Expected symbol '}'. But encountered: " + JackTokenizer.currToken);
				System.exit(1);
			}
			
			jt.advance(); //To match case where there is no else;
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</ifStatement>\n");
		System.out.println(indent + "</ifStatement>\n");
	}
	
	/*
	 * Compiles an expression
	 * expression: term (op term)*
	 * op: '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
	 * 
	 * Assume that CompileExpression() advances to next token for all cases. Not like ExpressionList and ParameterList
	 * which prints its tags always even if the list is empty. Expression should not print if it's empty
	 */
	public void CompileExpression() throws IOException {
		bw.write(indent + "<expression>\n");
		System.out.println(indent + "<expression>\n");
		numIndent++;
		makeIndents();
		
		CompileTerm();
		
		char op = jt.symbol();
		while(op != '#' && (op == '+' || op == '-' || op == '*' || op == '/' || op == '&' || op == '|' || op == '<' ||
				op == '>' || op == '=')) {
			String s = "";
			
			if(op == '<')
				s = "&lt;";
			else if(op == '>')
				s = "&gt;";
			else if(op == '&')
				s = "&amp;";
			else
				s = op + "";
			
			bw.write(indent + "<symbol> " + s + " </symbol>\n");
			System.out.println(indent + "<symbol> " + s + " </symbol>\n");
			
			jt.advance();
			CompileTerm();
			op = jt.symbol();
		}
		
		numIndent--;
		makeIndents();
		bw.write(indent + "</expression>\n");
		System.out.println(indent + "</expression>\n");
	}
	
	/*
	 * Compiles a term. This routine is faced with a slight difficulty when trying to decide between some of the alternative
	 * parsing rules. Specifically, if the current token is an identifier, the routine must distinguish between a variable, an
	 * array entry, and a subroutine call. A single look-ahead, which may be one of "[", "{", or "." suffices to distinguish
	 * between the three possibilities. Any other token is not part of this term and should not be advanced over.
	 * 
	 * term: integerConstant|stringConstant|keywordConstant|varName|varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
	 * 
	 * Assume CompileTerm() advances to the next token
	 */
	public void CompileTerm() throws IOException {
		bw.write(indent + "<term>\n");
		System.out.println(indent + "<term>\n");
		numIndent++;
		makeIndents();
		
		String type = jt.tokenType();
		if(type != null) {
			//integerConstant
			if(type.equals(JackTokenizer.INTC)) {
				bw.write(indent + "<integerConstant> " + jt.intVal() + " </integerConstant>\n");
				System.out.println(indent + "<integerConstant> " + jt.intVal() + " </integerConstant>\n");
				jt.advance();
			}
			//stringConstant
			else if(type.equals(JackTokenizer.STRC)) {
				bw.write(indent + "<stringConstant> " + jt.stringVal() + " </stringConstant>\n");
				System.out.println(indent + "<stringConstant> " + jt.stringVal() + " </stringConstant>\n");
				jt.advance();
			}
			//keywordConstant
			else if(type.equals(JackTokenizer.K)) {
				bw.write(indent + "<keyword> " + jt.keyWord() + " </keyword>\n");
				System.out.println(indent + "<keyword> " + jt.keyWord() + " </keyword>\n");
				jt.advance();
			}
			//varName
			else if(type.equals(JackTokenizer.ID)) {
				//varName only
				bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
				
				//varName '[' expression ']'
				jt.advance(); //potentially skips these checks and advances to the next token. Make the whole method skip to the next token
				if(jt.symbol() != '#' && jt.symbol() == '[') {
					bw.write(indent + "<symbol> [ </symbol>\n");
					System.out.println(indent + "<symbol> [ </symbol>\n");
					
					jt.advance();
					CompileExpression();
					
					if(jt.symbol() != '#' && jt.symbol() == ']') {
						bw.write(indent + "<symbol> ] </symbol>\n");
						System.out.println(indent + "<symbol> ] </symbol>\n");
					}
					else {
						System.out.println("Line " + jt.getNumLine() + ": Expected symbol ']'. But encountered: " + JackTokenizer.currToken);
						System.exit(1);
					}
					
					jt.advance();
				}
				//subroutineCall: subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
				else if(jt.symbol() != '#' && (jt.symbol() == '(' || jt.symbol() == '.')) {
					if(jt.symbol() == '(') {	
						bw.write(indent + "<symbol> ( </symbol>\n");
						System.out.println(indent + "<symbol> ( </symbol>\n");
					}
					else if(jt.symbol() == '.') {
						bw.write(indent + "<symbol> . </symbol>\n");
						System.out.println(indent + "<symbol> . </symbol>\n");
						
						//subroutineName
						jt.advance();
						if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
							bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
							System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
						}
						else {
							System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
							System.exit(1);
						}
						
						//(
						jt.advance();
						if(jt.symbol() != '#' && jt.symbol() == '(') {
							bw.write(indent + "<symbol> ( </symbol>\n");
							System.out.println(indent + "<symbol> ( </symbol>\n");
						}
						else {
							System.out.println("Line " + jt.getNumLine() + ": Expected symbol '('. But encountered: " + JackTokenizer.currToken);
							System.exit(1);
						}
					}	
					
					jt.advance();
					CompileExpressionList();	//Make this method advance to next token for all cases. Just like ParameterList

					//)
					if(jt.symbol() != '#' && jt.symbol() == ')') {
						bw.write(indent + "<symbol> ) </symbol>\n");
						System.out.println(indent + "<symbol> ) </symbol>\n");
					}
					else {
						System.out.println("Line " + jt.getNumLine() + ": Expected symbol ')'. But encountered: " + JackTokenizer.currToken);
						System.exit(1);
					}
					
					jt.advance();
				}
				else {
					
				}
			}
			//'(' expression ')'
			else if(type.equals(JackTokenizer.SYM) && jt.symbol() == '(') {
				bw.write(indent + "<symbol> ( </symbol>\n");
				System.out.println(indent + "<symbol> ( </symbol>\n");
				
				jt.advance();
				CompileExpression();
				
				if(jt.symbol() != '#' && jt.symbol() == ')') {
					bw.write(indent + "<symbol> ) </symbol>\n");
					System.out.println(indent + "<symbol> ) </symbol>\n");
				}
				
				jt.advance();
			}
			//unaryOp term
			//unaryOp: '-' | '~'
			else if(type.equals(JackTokenizer.SYM) && (jt.symbol() == '-' || jt.symbol() == '~')) {
				bw.write(indent + "<symbol> " + jt.symbol() + " </symbol>\n");
				System.out.println(indent + "<symbol> " + jt.symbol() + " </symbol>\n");
				
				jt.advance();
				CompileTerm();
			}
			else {
				System.out.println("Line " + jt.getNumLine() + ": Invalid term. Encountered " + JackTokenizer.currToken);
				System.exit(1);
			}
		}

		//Expressionless
/*		
		if(jt.tokenType() != null && jt.tokenType().equals(JackTokenizer.ID)) {
			bw.write(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
			System.out.println(indent + "<identifier> " + jt.identifier() + " </identifier>\n");
		}
		else {
			System.out.println("Line " + jt.getNumLine() + ": Expected identifier. But encountered: " + JackTokenizer.currToken);
			System.exit(1);
		}
*/		
		numIndent--;
		makeIndents();
		bw.write(indent + "</term>\n");
		System.out.println(indent + "</term>\n");
	}
	
	/*
	 * Compiles a (possibly empty) comma-separated list of expressions
	 * expressionList: (expression (',' expression)* )?
	 * 
	 * Assume method advances to next token in all cases.
	 */
	public void CompileExpressionList() throws IOException {
		bw.write(indent + "<expressionList>\n");
		System.out.println(indent + "<expressionList>\n");
		numIndent++;
		makeIndents();
		
		String t = jt.tokenType();
		if(t != null && (t.equals(JackTokenizer.INTC) || t.equals(JackTokenizer.STRC) || t.equals(JackTokenizer.K) || 
				t.equals(JackTokenizer.ID)) || (t.equals(JackTokenizer.SYM)) && (jt.symbol() == '(' || jt.symbol() == '-' || jt.symbol() == '~' )) {
		
			CompileExpression();
			
			while(jt.symbol() != '#' && jt.symbol() == ',') {
				bw.write(indent + "<symbol> , </symbol>\n");
				System.out.println(indent + "<symbol> , </symbol>\n");
				
				jt.advance();
				CompileExpression();
			}
		}
		//Expressionless
/*		
		if(jt.tokenType().equals(JackTokenizer.ID)) {
			CompileExpression();
			
			jt.advance();
			while(jt.symbol() != '#' && jt.symbol() == ',') {
				bw.write(indent + "<symbol> , </symbol>\n");
				System.out.println(indent + "<symbol> , </symbol>\n");
				
				jt.advance();
				CompileExpression();		
				jt.advance();
			}
		}
*/		
		numIndent--;
		makeIndents();
		bw.write(indent + "</expressionList>\n");
		System.out.println(indent + "</expressionList>\n");
	}
}
