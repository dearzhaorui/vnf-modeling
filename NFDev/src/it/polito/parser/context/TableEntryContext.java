package it.polito.parser.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;

import it.polito.parser.Constants;
import it.polito.parser.IfElseBranch;
import it.polito.parser.IfElseBranch.Branch;
import it.polito.parser.MyExpression;


public class TableEntryContext implements Comparable<TableEntryContext> {
	
	private List<IfElseBranch> conditionList;
	private String value;
	private String type = Constants.NONE;   // ”None“
	private int position;
	
	private MyExpression expression;
	
	public TableEntryContext(List<IfElseBranch> conditionList, String value, int position) {
	
		this.conditionList = new ArrayList<>(conditionList);
		this.value = value;
		this.position = position;
		
		for(IfElseBranch branch : conditionList){
			Branch type = branch.getBranch();
			boolean negated = false;
			
			if(type.compareTo(Branch.ELSE)==0){
				negated = true;
			}
			
			ConditionOnInterfaceVisitor visitor = new ConditionOnInterfaceVisitor(negated);
			branch.getStatement().accept(visitor);
			
			//String varName = visitor.getName();
			//String methodName = visitor.getMethodName();
			negated = visitor.negated;
			
			if(visitor.conditionFound()){
				if(negated)
					this.type = Constants.EXTERNAL;
				else
					this.type = Constants.INTERNAL;
				
				break;
			}
			
		}
	}
	
	public List<IfElseBranch> getConditionList(){
		return conditionList;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getType(){
		return type;
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setExpression(MyExpression expression){
		this.expression = expression;
	}
	
	public MyExpression getExpression(){
		return expression;
	}

	@Override
	public int compareTo(TableEntryContext o) {
		return position - o.position;
	}
	
	private class ConditionOnInterfaceVisitor extends ASTVisitor{
		
		boolean negated;
		String methodName = null;
		String varName = null;
		
		private boolean toStop;
		
		public ConditionOnInterfaceVisitor(boolean negated){
			this.negated = negated;
			this.toStop = false;
		}
		
		public boolean visit(IfStatement node){
			node.getExpression().accept(this);
			
			return false;
		}
		
		public boolean visit(InfixExpression node){
			
			if(toStop)
				return false;
			
			node.getLeftOperand().accept(this);
			
			if(toStop)
				return false;
			
			if(node.getRightOperand()!=null)
				node.getRightOperand().accept(this);
			
			return false;
		}
		
		public boolean visit(ParenthesizedExpression node){
			
			node.getExpression().accept(this);
			
			return false;
		}
		
		public boolean visit(PrefixExpression node){
			
			if(toStop)
				return false;
			
			if(node.getOperator().equals(PrefixExpression.Operator.NOT)){
				if(negated)
					negated = false;
				else
					negated = true;
				
			}
			node.getOperand().accept(this);
			
			return false;
		}
		
		public boolean visit(MethodInvocation node){
			
			node.getExpression().accept(this);
			methodName = node.getName().getFullyQualifiedName();
			
			if(varName!=null && methodName!=null){
				if(varName.compareTo(Constants.INTERFACE_PARAMETER)==0 && methodName.compareTo(Constants.IS_INTERNAL_METHOD)==0)
					toStop = true;
			}
			
			return false;
		}
		
		public boolean visit(SimpleName node){
			varName = node.getFullyQualifiedName();
			return false;
		}
		
		public boolean conditionFound(){
			return toStop;
		}
		
	}
	
}
