package it.polito.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Stage2Visitor extends ASTVisitor {
	
	private boolean isFunctionProcessed;
	private CompilationUnit result;
	
	public Stage2Visitor(CompilationUnit result) {
		this.result = result;
		this.isFunctionProcessed = false;
	}

	public boolean isFunctionProcessed() {
		return isFunctionProcessed;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		System.out.println("Found variable " + node.toString());
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("Class name -> " + node.getName());
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		String methodName = node.getName().toString();
		if(!methodName.equalsIgnoreCase(Constants.MAIN_NF_METHOD))
			return true;
		isFunctionProcessed = true;
		System.out.println("Found method -> " + node.getName());
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> l = node.parameters();
		if(l.size() != 2)
		{
			System.err.println("[ERROR] The "+ Constants.MAIN_NF_METHOD + "() method MUST have exactly two parameters!");
			return false;
		}
		System.out.println("\tFound parameter -> " + l.get(0).getName());
		System.out.println("\tBody -> " + node.getBody().getLength() + " bytes");
		System.out.println("\tBody start position = " + node.getBody().getStartPosition());
		node.accept(new MethodVisitor());
		System.out.println("\tParsing model...");
		int counter = 0;
		StatementVisitor myStmtVisitor = null;
		@SuppressWarnings("unchecked")
		List<Statement> stmts = (List<Statement>)node.getBody().statements();
		for(Statement s : stmts)
		{
			//if(counter == 0)	/* First statement */
				//myStmtVisitor = new Statement2Visitor(result, new ArrayList<IfElseBranch>(), false, new ArrayList<IfElseBranch>(), new ArrayList<ForLoop>(), 0, new ArrayList<MyExpression>(), 0);
			//else
				//myStmtVisitor = new Statement2Visitor(result, new ArrayList<IfElseBranch>(), false, myStmtVisitor.getPreviousIfElseBranch(), myStmtVisitor.getLoops(), 0, myStmtVisitor.getPredicatesOnSentPacket(), myStmtVisitor.getSkippedActions());
			s.accept(myStmtVisitor);
			counter++;
		}
		if(myStmtVisitor != null)
			System.out.println("\tSkipped "+myStmtVisitor.getSkippedActions()+" DROP action(s).\n");
		
		System.out.println("\tStage2 done!\n");
		return true;
	}
}
