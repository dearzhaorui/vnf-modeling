package it.polito.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.bind.MarshalException;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import it.polito.parser.context.Context;
import it.polito.parser.context.MethodContext;
import it.polito.parser.context.ReturnSnapshot;
import it.polito.rule.generator.RuleGenerator;
import it.polito.rule.unmarshaller.ClassGenerator;

public class Parser {
	
	private String fileName;
	
	/**
	 * The constructor receives the full path of file containing the VNF source
	 * code to be parsed.
	 *
	 * @param  fileName  an absolute local path of the VNF source code
	 */
	public Parser(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * This method parses the fileName source file provided in the constructor and generates 
	 * First Order Logic (FOL) formulas as output.  
	 */
	private void parse() throws ParserException{
		
		BufferedReader reader = null;
		char[] source = null;
		try{
			/* Load the source file */
			reader = new BufferedReader(new InputStreamReader(Parser.class.getResourceAsStream(fileName)));
			String line = null;
			String classCode = "";
			while(true)
			{
				line = reader.readLine();
				if(line == null)
					break;
				classCode = classCode.concat(line + "\n");
			}
			source = classCode.toCharArray();
		} catch(Exception ex) {
			System.err.println("[ERROR] Unable to load file!");
			ex.printStackTrace();
			System.exit(-2);
		}
		/* Build an AST parser using the JDT library (Eclipse parsing library) */
		ASTParser parser = ASTParser.newParser(AST.JLS3);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
		// In order to parse 1.7 code, some compiler options need to be set to 1.7
		@SuppressWarnings("unchecked")
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setUnitName("Nat.src");
		
		parser.setSource(source);
		
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		if (compilationUnit.getAST().hasBindingsRecovery()) {
			System.out.println("Binding activated.");
		}else
			System.out.println("Binding not activated!");
		
		Context classContext = new Context(compilationUnit);
		/* Perform STAGE1 parsing phase */
		ClassVisitor v1 = new ClassVisitor(classContext);
		compilationUnit.accept(v1);
			
		RuleGenerator ruler = new RuleGenerator(classContext.getClassName(),true);
		MethodContext methodContext = classContext.getMethodContext(Constants.MAIN_NF_METHOD);
		if(methodContext!=null){
			
			for(ReturnSnapshot returnSnapshot : methodContext.getReturnSnapshots()){
				
				ruler.setSnapshot(returnSnapshot);
				ruler.generateRule();
				
			}
			ruler.saveRule();
		}
	
		ClassGenerator generator = new ClassGenerator(classContext.getClassName());
		generator.startGeneration();
		System.out.println("!All Done!");
		
	}
	
	/**
	 * Main (for testing purposes)
	 *
	 * @param  args  a string containing the file name of the VNF to be parsed
	 * @throws MarshalException 
	 */
	public static void main(String[] args) throws IOException, MarshalException {
		if(args.length != 1)
		{
			System.err.println("Usage: java Parser <NF_path>");
			System.exit(-1);
		}
		System.out.println("Reading VNF model...");
		
		/* Instantiate a parser */
		Parser parser = new Parser(args[0]);
		try {
			/* Parse the VNF code */
			parser.parse();
		} catch (ParserException e) {
			System.err.println("Parsing failed!");
			e.printStackTrace();
		}
	}

}
