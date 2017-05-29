package it.polito.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.parser.IfElseBranch.Branch;
import it.polito.parser.Variable.Type;

public class Statement1Visitor extends ASTVisitor {
	
	private List<IfElseBranch> conditions;
	private List<IfElseBranch> previousIfElseBranch;
	private List<ForLoop> loops;
	private boolean foundReturn;
	private boolean isDeadCode;
	private int nestingLevel;
	private CompilationUnit compilationUnit;
	
	private Map<String, Variable> variables;

	/**
	 * This class will parse a statement and (if it is a IF) will also parse
	 * recursively the IF-ELSE blocks. 
	 *
	 * @param  compilationUnit  The compilation unit. Can be used to print useful information (source line)
	 * @param  variables the already encounterd variables of the code
	 * @param  conditions the previously IF-ELSE branches we entered before to get here
	 * @param  isDeadCode a boolean variable stating if this code is dead (not reachable) TODO: to be checked if the implementation is ok
	 * @param  previousIfElseBranch  the previously seen IF-ELSE statements (encountered but NOT entered in)
	 * @param  loops	the loops we entered in to get here
	 * @param  nestingLevel the current nesting level (if this is an IF inside an IF, then the nestingLevel is 2). 
	 */
	public Statement1Visitor(CompilationUnit compilationUnit, Map<String, Variable> variables, List<IfElseBranch> conditions, boolean isDeadCode, List<IfElseBranch> previousIfElseBranch, List<ForLoop> loops, int nestingLevel) {
		this.conditions = conditions;
		this.variables = variables;
		this.foundReturn = false;
		this.isDeadCode = isDeadCode;
		this.loops = loops;
		this.previousIfElseBranch = previousIfElseBranch;
		this.nestingLevel = nestingLevel;
		this.compilationUnit = compilationUnit;
	}
	
	public boolean isDeadCode() {
		return isDeadCode;
	}
	
	public boolean isFoundReturn() {
		return foundReturn;
	}

	public List<IfElseBranch> getConditions() {
		return conditions;
	}
	
	public List<IfElseBranch> getPreviousIfElseBranch() {
		return previousIfElseBranch;
	}
	
	public List<ForLoop> getLoops() {
		return loops; 
	}
	
	/**
	 * This method will process an IF-ELSE statement and other inner IF-ELSE stmt (recursive).
	 *
	 * @param  node  The IfStatement encountered
	 * @return a boolean saying if the parsing should continue to the next statement or not 
	 */
	@Override
	public boolean visit(IfStatement node) {
		//System.out.println("\tNode " + node.getExpression() + " received " + previousIfElseBranch.size() + " prev. if");
		//System.out.println("\tNode " + node.getExpression() + node.getNodeType() + " has parent " + node.getParent() + node.getNodeType());
		/* TO BE TESTED: This class purpose is to discover if this node is a trap (code which I cannot exit from because e.g. it contains return) */
		TrapExploratorVisitor trapFinder = new TrapExploratorVisitor();
		node.accept(trapFinder);
		
		/* We check whether the IF block does contain a return stmt */
		ReturnStatementExplorator visitor = new ReturnStatementExplorator();
		node.getThenStatement().accept(visitor);
		/* Same check for the ELSE block as well */
		ReturnStatementExplorator visitor2 = new ReturnStatementExplorator();
		if(node.getElseStatement() != null)
			node.getElseStatement().accept(visitor2);				//by default: false
		if(!visitor.hasReturnStatement() && !visitor2.hasReturnStatement())	// No return stmt inside this IF-THEN-ELSE
			return true;	// in case of no return stmt, must return 'true' to allow parser to continue to parse next node..... 
		
		List<IfElseBranch> c = new ArrayList<IfElseBranch>();
		Statement1Visitor s;
		IfElseBranch b = new IfElseBranch();
		b.setStatement(node);
		b.setBranch(Branch.IF);
		b.setNestingLevel(nestingLevel);
		if(visitor.hasReturnStatement())	/* Visit IF branch */
		{
			conditions.add(b);
			c.addAll(conditions);
			/* We recursively visit the THEN branch, adding the current IF to the list of encountered IF-THEN-ELSE stmts */
			s = new Statement1Visitor(compilationUnit, variables, c, false, previousIfElseBranch, loops, nestingLevel+1);
			node.getThenStatement().accept(s);	// go to next level to visit new IfStatement in "ThenStatement" 
			conditions.remove(b);
		}

		if(node.getElseStatement() == null || !visitor2.hasReturnStatement())	/* Skip ELSE branch */
		{
			/* No else branch, we are done! */
			previousIfElseBranch.add(b);
			removePreviousIf(nestingLevel+1);
			return false;
		}
		
		removePreviousIf(nestingLevel+1);	// has ELSE Branch and has Return Statement;;---> so at the end of this Method, it will return false,stop visit
		
		b = new IfElseBranch();
		b.setStatement(node);
		b.setBranch(Branch.ELSE);
		b.setNestingLevel(nestingLevel);
		conditions.add(b);
		c = new ArrayList<IfElseBranch>();
		c.addAll(conditions);
		/* We visit the ELSE branch */
		s = new Statement1Visitor(compilationUnit, variables, c, false, previousIfElseBranch, loops, nestingLevel+1);
		node.getElseStatement().accept(s);	/* Visit ELSE branch */  // if run here, it means there must be ELSE Branch.
		
		conditions.remove(b);
		
		removePreviousIf(nestingLevel+1);
		
		previousIfElseBranch.add(b);
		
		if(trapFinder.isATrap())	//in this ifStatement(nesting), must exist a ReturnStatement // Not much useful for the moment...
			this.isDeadCode = true;   // must have some code is skipped
		
		return false;	// no continue to visit next node
	}
	
	/**
	 * This method will process return statements
	 *
	 * @param  node  The ReturnStatement encountered
	 * @return a boolean saying if the parsing should continue to the next statement or not 
	 */
	@Override
	public boolean visit(ReturnStatement node) {
		this.foundReturn = true;
		if(this.isDeadCode)
		{
			System.err.println("\t[ERROR] Found RETURN but it is dead code! " + node);
			return false;
		}

		/*
		System.out.println("\t*****Found action " + r.getAction() + " (with "+conditions.size()+" conditions leading to it) at line " + compilationUnit.getLineNumber(node.getStartPosition()) + ". Reached nesting level " + nestingLevel);
		
		int j = 1;
		for(IfElseBranch ieb : previousIfElseBranch)
			System.out.println("\t\tPrevious IF-THEN-ELSE #" + j++ + " " + ieb.getStatement().getExpression() + " (IF has RETURN="+ieb.ifBranchContainsReturn()+") (ELSE has RETURN="+ieb.elseBranchContainsReturn()+")");
		
		j = 1;
		for(IfElseBranch ieb : conditions)
			System.out.println("\t\tCondition #" + j++ + " ("+ieb.getBranch()+"): " + ieb.getStatement().getExpression());
		
		for(ForLoop fLoop : loops)
			System.out.println("\tEncountered " + fLoop.getStatement().getExpression() + " with nesting level " + fLoop.getNestingLevel());
		
		System.out.println();
		*/
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		System.out.println("\tFound while -> " + node.getExpression());
		return true;
	}
	
	/* The idea was to support only this kind of loop */
	/**
	 * This method will process for loops, specifically loops like for(String s : list) { ... }
	 *
	 * @param  node  The EnhancedForStatement encountered
	 * @return a boolean saying if the parsing should continue to the next statement or not 
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		System.out.println("\tFound FOR-EACH. Variable declaration -> " + node.getParameter() + ". Iterate over -> " + node.getExpression());
		ForLoop l = new ForLoop();
		l.setNestingLevel(nestingLevel);
		l.setStatement(node);
		loops.add(l);
		List<IfElseBranch> c = new ArrayList<IfElseBranch>();
		c.addAll(conditions);
		Statement1Visitor s = new Statement1Visitor(compilationUnit, variables, c, false, previousIfElseBranch, loops, nestingLevel+1);
		node.getBody().accept(s);
		return false;
	}
	
	@Override
	public boolean visit(LineComment node) {
		System.out.println("\tFound comment -> " + node);
		return true;
	}
	
	@Override
	public boolean visit(BlockComment node) {
		System.out.println("\tFound comment block -> " + node);
		return true;
	}
	
	/**
	 * This method will process a variable declaration inside the VNF's main method
	 *
	 * @param  node  The VariableDeclarationStatement encountered
	 * @return a boolean saying if the parsing should continue to the next statement or not 
	 */
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		//System.out.println("\tVariable ("+node.fragments()+") declared");
		//@SuppressWarnings("unchecked")
		/*List<VariableDeclarationFragment> fragments = node.fragments();
		for(VariableDeclarationFragment fragment : fragments)
		{
			System.out.println("\tVariable decl. found => " + fragment.getName() + " = " + fragment.getInitializer());
			// put the variable in the map
			Variable v = new Variable(fragment.getName().toString(), fragment.getInitializer(), Type.GENERIC);
			//System.out.println("Saving var => " + fragment.getName().toString());
			variables.put(fragment.getName().toString(), v);
			fragment.getInitializer().accept(new ASTVisitor() {
				@SuppressWarnings("unchecked")
				public boolean visit(MethodInvocation node) {
					if(node.getName().toString().equalsIgnoreCase(Constants.MATCH_ENTRY_METHOD_NAME))
					{
						System.out.println("\tFound matchEntry() method in variable initialization!");
						for(Expression exp : (List<Expression>) node.arguments())
							v.addMatchedField(exp.toString());
					}
					return false;
				}
			});
		}*/
		return false;
	}
	
	/**
	 * This method will process an expression inside the VNF's main method,
	 * such as e.g. packet_in.setField(PacketField.PORT_DST,(String)entry.getValue(1))
	 *
	 * @param  node  The ExpressionStatement encountered
	 * @return a boolean saying if the parsing should continue to the next statement or not 
	 */
	@Override
	public boolean visit(ExpressionStatement node) {
		//System.out.println("\tExpressionStatement " + node.getExpression());
		node.accept( new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node) {
				/* We identify the expression, e.g. the method called */
				switch(node.getName().toString())
				{
					case Constants.STORE_ENTRY_METHOD_NAME:
						//System.out.println(node.getName() + " invoked on " + node.getExpression() + " with parameter " + node.arguments().get(0));
						//System.out.println("Retrieved variable => " + variables.get(node.getExpression().toString()).getName());
						System.out.println("\t" + Constants.STORE_ENTRY_METHOD_NAME + " method found!");
						int j=1;
						for(IfElseBranch ieb : previousIfElseBranch)
							System.out.println("\t\tPrevious IF-THEN-ELSE #" + j++ + " " + ieb.getStatement().getExpression() + " (IF has RETURN="+ieb.ifBranchContainsReturn()+") (ELSE has RETURN="+ieb.elseBranchContainsReturn()+")");
						for(IfElseBranch ieb : conditions)
							System.out.println("\t\tCondition #" + j++ + " ("+ieb.getBranch()+"): " + ieb.getStatement().getExpression());
						break;
					case Constants.SET_FIELD_METHOD:
						if(node.arguments().size() != 2)
						{
							System.err.println("ERROR in the " + Constants.SET_FIELD_METHOD + " method");
							return false;
						}
						Variable v = variables.get(node.getExpression().toString());
						if(v == null)
						{
							System.err.println("ERROR: " + Constants.SET_FIELD_METHOD + " method called on undefined variable!");
							return false;
						}
						v.addFieldContraint(new FieldValuePair(fromStringToPktField(node.arguments().get(0).toString()), node.arguments().get(1)));
						System.out.println("\tFound setField() method on " + node.getExpression() + " => " + node.arguments().get(0) + " = " + node.arguments().get(1));
						break;
					case Constants.MATCH_ENTRY_METHOD_NAME:
						System.out.println("\tFound matchEntry() method on " + node.getExpression());
						break;
					default:
						break;
				}
				return false;
			}
		});
		return true;
	}

	protected PacketField fromStringToPktField(String arg) {
		switch(arg)
		{
			case "PacketField.IP_SRC":
				return PacketField.IP_SRC;
			case "PacketField.IP_DST":
				return PacketField.IP_DST;
			case "PacketField.PORT_SRC":
				return PacketField.PORT_SRC;
			case "PacketField.PORT_DST":
				return PacketField.PORT_DST;
		}
		return null;
	}
	
	private void removePreviousIf(int nestingLevel)
	{
		List<IfElseBranch> toRemove = new ArrayList<IfElseBranch>();
		for(IfElseBranch ifElse : previousIfElseBranch)
			if(ifElse.getNestingLevel() == nestingLevel)
				toRemove.add(ifElse);
		previousIfElseBranch.removeAll(toRemove);
		return;
	}

}
