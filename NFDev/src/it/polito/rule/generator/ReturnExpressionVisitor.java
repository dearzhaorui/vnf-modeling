package it.polito.rule.generator;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;

public class ReturnExpressionVisitor extends ASTVisitor {

	private String variableName;
	private String packetField;
	private RuleContext ruleContext;
	private int counterFlag = -1;
	
	public ReturnExpressionVisitor(RuleContext ruleContext, String packetField){
		
		variableName = null;
		this.ruleContext = ruleContext;
		this.packetField = packetField;
	}
	
	public boolean visit(CastExpression node){
		node.getExpression().accept(this);
		
		return false;
	}
	
	
	public boolean visit(MethodInvocation node){
		counterFlag++;
		node.getExpression().accept(this);
		//TODO evaluate if methodName is necessary for improving rule generation 
		if(counterFlag == 0){
			ruleContext.generateRuleForExitingPacket(packetField, node);
		}
		
		counterFlag--;
		return false;
	}
	
	public boolean visit(SimpleName node){
		variableName = node.getFullyQualifiedName();
		counterFlag++;
		if(counterFlag == 0){
			ruleContext.generateRuleForExitingPacket(packetField, variableName);
		}
		counterFlag--;
		return false;
	}
	
	
	public boolean visit(StringLiteral node){
		variableName = node.getLiteralValue();
		counterFlag++;
		if(counterFlag == 0){
			ruleContext.generateRuleForExitingPacket(packetField, variableName);
		}
		counterFlag--;
		return false;
	}
	
	public boolean visit(QualifiedName node){
		variableName = node.getName().getFullyQualifiedName();
		counterFlag++;
		if(counterFlag==0){
			ruleContext.generateRuleForExitingPacket(packetField, variableName);
		}
		counterFlag--;
		return false;
	}
}
