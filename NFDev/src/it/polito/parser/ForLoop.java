package it.polito.parser;

import org.eclipse.jdt.core.dom.EnhancedForStatement;

public class ForLoop {
	
	private int nestingLevel;
	private EnhancedForStatement statement;
	
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	public EnhancedForStatement getStatement() {
		return statement;
	}
	public void setStatement(EnhancedForStatement statement) {
		this.statement = statement;
	}

}
