package it.polito.parser;

import org.eclipse.jdt.core.dom.IfStatement;

public class IfElseBranch {
	
	public enum Branch { IF, ELSE };
	
	private IfStatement statement;
	private Branch branch;
	private int nestingLevel;
	
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	public IfStatement getStatement() {
		return statement;
	}
	public void setStatement(IfStatement statement) {
		this.statement = statement;
	}
	public Branch getBranch() {
		return branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	public boolean ifBranchContainsReturn() {
		ReturnStatementExplorator r = new ReturnStatementExplorator();
		statement.getThenStatement().accept(r);
		return r.hasReturnStatement();
	}
	public boolean elseBranchContainsReturn() {
		if(statement.getElseStatement() == null)
			return false;
		ReturnStatementExplorator r = new ReturnStatementExplorator();
		statement.getElseStatement().accept(r);
		return r.hasReturnStatement();  // by default, false, if has ReturnStatement after searching by Explorator,= true; 
	}
	
	public String toString(){
		
		return new String("Branch (" + branch + "): " + statement.getExpression().toString());
		
	}

}
