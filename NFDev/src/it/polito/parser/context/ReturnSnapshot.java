package it.polito.parser.context;

import java.util.ArrayList;
import java.util.List;

import it.polito.parser.IfElseBranch;
import it.polito.parser.MyExpression;


public class ReturnSnapshot {
	
	private MethodContext methodContext;
	
	private List<IfElseBranch> conditions;
	private List<IfElseBranch> previousConditions;
	private List<MyExpression> returnPredicates;
	
	private String packetName;
	private String interfaceName;
	private int nestingLevel;
	

	public ReturnSnapshot(MethodContext methodContex,
						  List<IfElseBranch> conditions, 
						  List<IfElseBranch> previousConditions, 
						  List<MyExpression> returnPredicates,
						  String packetName,
						  String interfaceName,
						  int nestingLevel) {
		
		assert methodContex != null;
		assert conditions != null;
		assert previousConditions != null;
		assert returnPredicates != null;
		assert packetName != null;	
		assert interfaceName != null;
		
		this.methodContext = methodContex;
		this.conditions = new ArrayList<>(conditions);
		this.previousConditions = new ArrayList<>(previousConditions);	
		this.returnPredicates = new ArrayList<>(returnPredicates);
		this.packetName = packetName;
		this.interfaceName = interfaceName;
		this.nestingLevel = nestingLevel;
	}


	public MethodContext getMethodContext() {
		return methodContext;
	}


	public List<IfElseBranch> getConditions() {
		return conditions;
	}


	public List<IfElseBranch> getPreviousConditions() {
		return previousConditions;
	}

	public List<MyExpression> getReturnPredicates() {
		return returnPredicates;
	}
	
	public int getNestingLevel(){
		return nestingLevel;
	}
	
	public String getPacketName(){
		return packetName;
	}
	
	public String getInterfaceName(){
		return interfaceName;
	}
	
	public String toString(){
		
		StringBuilder result = new StringBuilder("");
		int i = 1;
		
		for(IfElseBranch previousCondition : previousConditions)
			result.append("\tPrevious IF-THEN-ELSE #" + i++ +" " +previousCondition + "\n");
		
		i = 1;
		
		for(IfElseBranch condition : conditions)
			result.append("\tCondition #" + i++  +" " +condition + "\n");
		
		result.append("\tRules for the outgoing packet:\n");
		
		for(MyExpression returnPredicate : returnPredicates)
			result.append("\t\t " + returnPredicate + "\n");
		
		result.append("\tThe remaining fields are unchanged\n");
		
		return result.toString(); 
	}

}
