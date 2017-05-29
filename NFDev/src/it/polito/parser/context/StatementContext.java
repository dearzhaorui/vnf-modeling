package it.polito.parser.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import it.polito.parser.ForLoop;
import it.polito.parser.IfElseBranch;
import it.polito.parser.MyExpression;

public class StatementContext {
	
	private MethodContext methodContext;
	
	private List<IfElseBranch> conditions;
	private List<IfElseBranch> previousConditions;
	private List<ForLoop> forLoops;
	private List<MyExpression> returnPredicates;
	
	private int nestingLevel;
	private int skippedActions;
	
	private boolean isDeadCode;
	
	private CompilationUnit compilationUnit;

	public StatementContext(MethodContext methodContext) {
		
		assert methodContext != null;
		
		this.conditions = new ArrayList<>();
		this.previousConditions = new ArrayList<>();
		this.forLoops = new ArrayList<>();
		this.returnPredicates = new ArrayList<>();
		
		this.nestingLevel = 0;
		this.skippedActions = 0;
		
		this.isDeadCode = false;
		
		this.methodContext = methodContext;
		
		this.compilationUnit = methodContext.getContext().getCompilationUnit();
		
	}

	public MethodContext getMethodContext() {
		return methodContext;
	}

	public void setMethodContext(MethodContext methodContext) {
		this.methodContext = methodContext;
	}

	public List<IfElseBranch> getConditions() {
		return conditions;
	}

	public boolean addConditions(IfElseBranch condition) {
		return conditions.add(condition);
	}

	public List<IfElseBranch> getPreviousConditions() {
		return previousConditions;
	}

	public void setPreviousConditions(List<IfElseBranch> previousConditions) {
		this.previousConditions = previousConditions;
	}

	public List<ForLoop> getForLoops() {
		return forLoops;
	}

	public void setForLoops(List<ForLoop> forLoops) {
		this.forLoops = forLoops;
	}

	public List<MyExpression> getReturnPredicates() {
		return returnPredicates;
	}

	public void setReturnPredicates(List<MyExpression> returnPredicates) {
		this.returnPredicates = returnPredicates;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public void setNestingLevel(Integer nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	
	public int increaseNestingLevel(){
		return nestingLevel++;
	}
	
	public int decreaseNestingLevel(){
		return nestingLevel--;
	}

	public int getSkippedActions() {
		return skippedActions;
	}

	public void setSkippedActions(int skippedActions) {
		this.skippedActions = skippedActions;
	}

	public ReturnSnapshot createSnapshot(String packetName, String interfaceName){
		return new ReturnSnapshot(methodContext, conditions, previousConditions, returnPredicates, packetName, interfaceName, nestingLevel);
	}

	public boolean isDeadCode() {
		return isDeadCode;
	}

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

}
