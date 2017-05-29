package it.polito.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;

import it.polito.nfdev.lib.RoutingResult.Action;

public class ReturnStatementExplorator extends ASTVisitor {
	
	private List<ReturnStatement> returnList;
	private List<Action> actionList;
	private boolean foundReturn;
	
	public ReturnStatementExplorator(){
		this.foundReturn = false;
		this.returnList = new ArrayList<>();
		this.actionList = new ArrayList<>();
	}
	
	public boolean hasReturnStatement() {
		return foundReturn;
	}
	
	public List<ReturnStatement> getReturnList(){
		return returnList;
	}
	
	public List<Action> getActionList(){
		return actionList;
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		
		ReturnStatementVisitor visitor = new ReturnStatementVisitor();
		
		node.getExpression().accept(visitor);
		actionList.add(visitor.getAction());
		
		this.foundReturn = true;
		returnList.add(node);
		return true;
	}

}
