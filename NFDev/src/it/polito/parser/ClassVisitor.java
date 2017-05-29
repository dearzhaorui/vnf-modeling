package it.polito.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import it.polito.parser.Variable.Type;
import it.polito.parser.context.Context;
import it.polito.parser.context.MethodContext;
import it.polito.parser.context.StatementContext;

/**
 * Main class performing the Stage 1 parsing phase.
 * Main tasks of this phase:
 * 	- scan the variables in the source code;
 * 	- find matchEntry() method calls and parse the involved fields (which fields are being matched)
 * 	- find storeEntry() method calls and parse the involved fields (which fields are inserted in the table)
 */
public class ClassVisitor extends ASTVisitor {
	
	/* Indicates whether the VNF main method has been processed or not */
	private boolean isFunctionProcessed;

	private Context classContext;
	/* A map containing each encountered identifier and the corresponding information (Variable class) */
	private Map<String, Variable> variables;
	
	/* The name of the variable used for the VNF's table (FIXME: can be statically choosen?) */
	private String variableTypeName;
	
	public ClassVisitor(Context classContext) {
		assert classContext != null;
		
		this.classContext = classContext;
		classContext.getCompilationUnit();
		
		this.isFunctionProcessed = false;
		this.variables = new HashMap<String, Variable>();
		
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("Class name -> " + node.getName().toString());
		this.classContext.setClassName(node.getName().toString());
		return true;
	}
	
	/**
	 * This is called every time a member variable declaration is found inside the class source code
	 * (i.e. all the variables declared inside the class but outside any method).
	 *
	 * @param  node  the FieldDeclaration statement
	 * @return      whether to continue the parsing after this FieldDeclaration statement
	 */
	@Override
	public boolean visit(FieldDeclaration node) {
		variableTypeName = null;
		System.out.print("Found variable -> " + node);
		AnnotationVisitor aVisitor = new AnnotationVisitor();
		/* We want to find annotations on the variables (done by AnnotationVisitor class). */
		node.accept(aVisitor);
		node.accept(new ASTVisitor() {
			public boolean visit(SimpleType node){
				variableTypeName = node.getName().getFullyQualifiedName();
				return false;
			}
		});
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> fragments = node.fragments();
		/* We have a list because a declaration statement can contain multiple decls separated by commas */
		for(VariableDeclarationFragment fragment : (List<VariableDeclarationFragment>)fragments)
		{
			//System.out.println("\tVariable decl. found => " + fragment.getName() + " = " + fragment.getInitializer());
			// put the variable in the map
			Variable v = new Variable(fragment.getName().toString(), fragment.getInitializer(), Type.GENERIC, variableTypeName);
			if(aVisitor.foundAnnotation())
			{
				/* We have two types of class member we are interested in: CONF member (like CONSTANTS) and the TABLE */
				switch(aVisitor.getAnnotation())
				{
					case "Configuration":
						v.setType(Type.CONFIGURATION);
						break;
					case "Table":
						assert aVisitor.getFieldsType() != null;
						assert aVisitor.getFieldsType().length > 0;
						v.setType(Type.TABLE);
						v.setFieldsType(aVisitor.getFieldsType());
						//this.tableVariableName = v.getName();
						break;
					default:
						break;
				}
			}
			//System.out.println("Saving var => " + fragment.getName().toString());
			variables.put(fragment.getName().toString(), v);
			
			this.classContext.addVariable(v);
		}
		return false;
	}
	
	/**
	 * This is called every time a method is found inside the class source code
	 *
	 * @param  node  the MethodDeclaration statement
	 * @return      whether to continue the parsing after this MethodDeclaration statement
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		//System.out.println("Variables -> " + variables.size());
		String methodName = node.getName().toString();
		if(!methodName.equals(Constants.MAIN_NF_METHOD) && !methodName.equals(classContext.getClassName())) // Check the method name
			return true;
		/* We have found the main method (with a standard name) */
		
		MethodContext methodContext = new MethodContext(methodName, classContext);
		classContext.addMethodContext(methodContext);
		
		
		isFunctionProcessed = true;
		System.out.println("Found method -> " + node.getName());
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> l = node.parameters();
		//if(l.size() != 2)
		//{
			/* PARANOIC MODE ON: Check the number of parameters is correct! */
			//System.err.println("[ERROR] The "+ Constants.MAIN_NF_METHOD + "() method MUST have exactly two parameters!");
			//return false;
		//}
		if(l.size() != 0)
			System.out.println("\tFound parameter -> " + l.get(0).getName());
		
		for(SingleVariableDeclaration singleVariableDeclaration : l){
			variableTypeName = null;
			singleVariableDeclaration.accept(new ASTVisitor() {
				
				public boolean visit(SimpleType node){
					variableTypeName = node.getName().getFullyQualifiedName();
					return false;
				}
				
				public boolean visit(ParameterizedType node){
					variableTypeName = node.getType().toString();
					return false;
				}
				
			});
			Variable v = new Variable(singleVariableDeclaration.getName().getFullyQualifiedName(), singleVariableDeclaration.getInitializer(), Type.GENERIC,variableTypeName);
			methodContext.addMethodVariable(v);
		}
		
		System.out.println("\tBody -> " + node.getBody().getLength() + " bytes");
		System.out.println("\tBody start position = " + node.getBody().getStartPosition());
		/* This will extract annotation on the method (if present) */
		node.accept(new MethodVisitor());
		System.out.println("\tParsing model...");
		StatementVisitor myStmtVisitor = null;
		
		StatementContext statementContext = new StatementContext(methodContext);
		
		@SuppressWarnings("unchecked")
		/* We parse all the statements, one after the other */
		List<Statement> stmts = (List<Statement>)node.getBody().statements();
		for(Statement s : stmts)
		{		
			myStmtVisitor = new StatementVisitor(statementContext);
			s.accept(myStmtVisitor);
		}
		
		System.out.println("\tStage1 done!");
		return false;
	}
	
	public boolean isFunctionProcessed() {
		return isFunctionProcessed;
	}

}
