package it.polito.rule.generator;


import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;

import it.polito.nfdev.jaxb.ExpressionObject;
import it.polito.nfdev.jaxb.LOAnd;
import it.polito.nfdev.jaxb.LOEquals;
import it.polito.nfdev.jaxb.LONot;
import it.polito.nfdev.jaxb.LOOr;
import it.polito.nfdev.jaxb.LogicalOperator;
import it.polito.nfdev.jaxb.ObjectFactory;


public class ConditionVisitor extends ASTVisitor {

	private ObjectFactory factory;
	private RuleContext ruleContext;
	private boolean isNull;
	private Operator operator;
	private ExpressionObject exp = null;
	
	
	public ConditionVisitor(RuleContext ruleContext){
		this.ruleContext = ruleContext;
		this.factory = ruleContext.getObjectFactory();
	}
	
	private ConditionVisitor(RuleContext ruleContext, Operator operator){
		this.ruleContext = ruleContext;
		this.operator = operator;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean visit(InfixExpression node){
	
		this.operator = node.getOperator();
		
		LogicalOperator op;
		
		if(operator!=null){
			
			if(operator.equals(Operator.CONDITIONAL_AND) || operator.equals(Operator.CONDITIONAL_OR) || operator.equals(Operator.EQUALS) || operator.equals(Operator.NOT_EQUALS)){
				
				if(operator.equals(Operator.CONDITIONAL_AND) || operator.equals(Operator.CONDITIONAL_OR)){
					
					List<ExpressionObject> expList;
					if(operator.equals(Operator.CONDITIONAL_AND)){
						LOAnd and = factory.createLOAnd();		
						expList = and.getExpression();
						op = and;
					}		
					else{
						LOOr or = factory.createLOOr();	
						expList = or.getExpression();
						op = or;
					}		
					
					ConditionVisitor visitor = new ConditionVisitor(ruleContext);
					node.getLeftOperand().accept(visitor);
					if(visitor.exp!=null)
							expList.add(visitor.exp);
					
					
					visitor.clean();
					
					node.getRightOperand().accept(visitor);
					if(visitor.exp!=null)
							expList.add(visitor.exp);
					
					
					visitor.clean();
					for(ASTNode ast : (List<ASTNode>)node.extendedOperands()){
						ast.accept(visitor);
						if(visitor.exp != null){
							expList.add(visitor.exp);
						}
						visitor.clean();
					}
					
					if(expList.isEmpty())
						exp = null;
					else if(expList.size() == 1)
						exp = expList.get(0);											
					else{
						exp = factory.createExpressionObject();
						if(operator.equals(Operator.CONDITIONAL_AND))
							exp.setAnd((LOAnd)op);
						else
							exp.setOr((LOOr)op);
					}
					
					return false;
				}else{
					
					boolean leftVisitorNull = false;
					boolean rightVisitorNull = false;				
					LOEquals eq = factory.createLOEquals();
					
					ConditionVisitor visitor = new ConditionVisitor(ruleContext,operator);
					
					node.getLeftOperand().accept(visitor);
					
					if(visitor.exp != null)
						eq.setLeftExpression(visitor.exp);
					else{
						if(visitor.isNull)
							leftVisitorNull = true;
						else{
							exp = null;
							return false;
						}
					}
					
					visitor.exp = null;
					node.getRightOperand().accept(visitor);
					
					if(visitor.exp != null)
						eq.setRightExpression(visitor.exp);
					else{
						if(visitor.isNull)
							rightVisitorNull = true;
						else{
							exp = null;
							return false;
						}
					}
					
					if((leftVisitorNull && rightVisitorNull) || (!leftVisitorNull && !rightVisitorNull)){
						exp = null;
						return false;
					}else if(leftVisitorNull || rightVisitorNull){
						
						if(leftVisitorNull)
							exp = eq.getRightExpression();
						else
							exp = eq.getLeftExpression();
					
					}
					
					return false;
				}				
			}		
		}
		
		exp = null;

		return false;
	}
	
	public boolean visit(ParenthesizedExpression node){
		
		node.getExpression().accept(this);
		
		return false;
	}
	
	public boolean visit(IfStatement node){
		
		node.getExpression().accept(this);
		
		return false;
	}
	
	public boolean visit(PrefixExpression node){
		if(node.getOperator().equals(PrefixExpression.Operator.NOT)){

			ConditionVisitor visitor = new ConditionVisitor(ruleContext);
					
			node.getOperand().accept(visitor);
			if(visitor.exp != null){
				if(visitor.exp.getNot()==null){
					LONot not = factory.createLONot();
					not.setExpression(visitor.exp);
					
					exp = factory.createExpressionObject();
					exp.setNot(not);
				}else{
					exp = visitor.exp.getNot().getExpression();
				}

				return false;
			}			
		}
		
		exp = null;
		return false;
	}
	
	public boolean visit(MethodInvocation node){
		
		StringBuilder varName = new StringBuilder();
		
		node.getExpression().accept(new ASTVisitor() {
			public boolean visit(SimpleName node){
				
				varName.append(node.getFullyQualifiedName());
				return false;
			}	
		});
		
		exp = ruleContext.generateRuleForMethod(varName.toString(), node);
		return false;
	}
	
	public boolean visit(SimpleName node){
	
		exp = ruleContext.generateRuleForVariable(node.getFullyQualifiedName(), operator, node.getStartPosition());
		
		return false;
	}
	
	public boolean visit(NullLiteral node){
		
		isNull = true;
		return false;
	}
	
	public ExpressionObject getExpression(){
		return exp;
	}
	
	public void clean(){
		exp = null;
	}
	
}
