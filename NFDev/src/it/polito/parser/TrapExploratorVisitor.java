package it.polito.parser;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;

public class TrapExploratorVisitor extends ASTVisitor {
	
	private boolean containsIf = false;
	private boolean containsReturn = false;
	private boolean isATrap = false;
	
	public boolean isATrap()
	{
		return isATrap;
	}
	
	public boolean isContainsIf() {
		return containsIf;
	}

	public void setContainsIf(boolean containsIf) {
		this.containsIf = containsIf;
	}

	public boolean isContainsReturn() {
		return containsReturn;
	}

	public void setContainsReturn(boolean containsReturn) {
		this.containsReturn = containsReturn;
	}

	@Override
	public boolean visit(IfStatement node) {
		this.containsIf = true;
		if(node.getElseStatement() == null)	// No Else
		{
			this.isATrap = false;
			return false;
		}
		TrapExploratorVisitor t = new TrapExploratorVisitor();
		ReturnStatementExplorator r = new ReturnStatementExplorator();
		node.getThenStatement().accept(t);
		node.getThenStatement().accept(r);
		TrapExploratorVisitor t2 = new TrapExploratorVisitor();
		ReturnStatementExplorator r2 = new ReturnStatementExplorator();
		node.getElseStatement().accept(t2);
		node.getElseStatement().accept(r2);
		boolean isThenATrap = false, isElseATrap = false;
		if(r.hasReturnStatement() || t.isATrap())
			isThenATrap = true;
		if(r2.hasReturnStatement() || t2.isATrap())
			isElseATrap = true;
		this.isATrap = isThenATrap && isElseATrap;
		return false;
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		this.containsReturn = true;
		return true;
	}

}
