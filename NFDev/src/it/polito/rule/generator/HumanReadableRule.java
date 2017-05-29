package it.polito.rule.generator;

import java.util.Iterator;
import java.util.List;

import it.polito.nfdev.jaxb.*;

public class HumanReadableRule {
	
	private ExpressionResult expressionResult;
	private StringBuilder rule;
	private int innerLevel;
	private int ruleNumber;

	public HumanReadableRule(ExpressionResult expressionResult){
		
		assert expressionResult != null;
		this.expressionResult = expressionResult;
		this.rule = new StringBuilder();
		this.innerLevel = 0;
		this.ruleNumber = 0;
	}
	
	public String generateRule(){
		
		List<ExpressionObject> list = expressionResult.getLogicalExpressionResult();
		
		for(ExpressionObject result : list){
			rule.append("Rule_"+ruleNumber++ +":\n\n");
			getType(result);
			rule.append("\n\n");
			innerLevel = 0;
		}
		
		return rule.toString();
	}
	
	private void getType(ExpressionObject obj){
		if(obj.getAnd()!=null){
			
			generateAnd(obj.getAnd());			
		}else if(obj.getOr()!=null){
			
			generateOr(obj.getOr());
		}else if(obj.getNot()!=null){
			
			generateNot(obj.getNot());
		}else if(obj.getEqual()!=null){
			
			generateEqual(obj.getEqual());
		}else if(obj.getGreaterThan()!=null){
			
			generateGreaterThan(obj.getGreaterThan());
		}else if(obj.getGreaterThanOrEqual()!=null){
			
			generateGreaterThanOrEqual(obj.getGreaterThanOrEqual());
		}else if(obj.getLessThan()!=null){
			
			generateLessThan(obj.getLessThan());
		}else if(obj.getLessThanOrEqual()!=null){
			
			generateLessThanOrEqual(obj.getLessThanOrEqual());
		}else if(obj.getImplies()!=null){
			
			generateImplies(obj.getImplies());
		}else if(obj.getExist()!=null){
			
			generateExist(obj.getExist());
		}else if(obj.getSend()!=null){
			
			generateSend(obj.getSend());
		}else if(obj.getRecv()!=null){
			
			generateRecv(obj.getRecv());
		}else if(obj.getFieldOf()!=null){
			
			generateFieldOf(obj.getFieldOf());
		}else if(obj.getIsInternal()!=null){
			
			generateIsInternal(obj.getIsInternal());
		}else if(obj.getMatchEntry()!=null){
			
			generateMatchEntry(obj.getMatchEntry());
		}else if(obj.getParam()!=null){
			
			rule.append(obj.getParam());
		}
	}
	
	private void generateAnd(LOAnd op){
		List<ExpressionObject> list = op.getExpression();
		
		rule.append("(");
		Iterator<ExpressionObject> it = list.iterator();
		while(it.hasNext()){
			
			getType(it.next());
			if(it.hasNext()){
				rule.append(" && ");
			}
		}
		rule.append(")");
	}
	
	private void generateOr(LOOr op){
		List<ExpressionObject> list = op.getExpression();
		
		rule.append("(");
		Iterator<ExpressionObject> it = list.iterator();
		while(it.hasNext()){
			
			getType(it.next());
			if(it.hasNext()){
				rule.append(" || ");
			}
		}
		rule.append(")");
	}
	
	private void generateNot(LONot op){
		
		rule.append("!(");
		getType(op.getExpression());
		rule.append(")");
	}
	
	private void generateEqual(LOEquals op){
		
		rule.append("(");
		getType(op.getLeftExpression());
		rule.append(" == ");
		getType(op.getRightExpression());
		rule.append(")");
		
	}
	
	private void generateGreaterThan(LOGreaterThan op){
		
		rule.append("(");
		getType(op.getLeftExpression());
		rule.append(" > ");
		getType(op.getRightExpression());
		rule.append(")");
		
	}
	
	private void generateGreaterThanOrEqual(LOGreaterThanOrEqual op){
		
		rule.append("(");
		getType(op.getLeftExpression());
		rule.append(" >= ");
		getType(op.getRightExpression());
		rule.append(")");
		
	}
	
	private void generateLessThan(LOLessThan op){
		
		rule.append("(");
		getType(op.getLeftExpression());
		rule.append(" < ");
		getType(op.getRightExpression());
		rule.append(")");
		
	}
	
	private void generateLessThanOrEqual(LOLessThanOrEqual op){
		
		rule.append("(");
		getType(op.getLeftExpression());
		rule.append(" <= ");
		getType(op.getRightExpression());
		rule.append(")");
		
	}
	
	private void generateImplies(LOImplies op){
		
		rule.append("(");
		getType(op.getAntecedentExpression());
		rule.append(" ==> ");
		generateTabbing();
		getType(op.getConsequentExpression());
		rule.append(")");
		
	}
	
	private void generateExist(LOExist op){
			
		rule.append(" E(");
		Iterator<String> it = op.getUnit().iterator();
		while(it.hasNext()){
			rule.append(it.next());
			if(it.hasNext()){
				rule.append(", ");
			}
		}
		rule.append(" | ");
		generateTabbing();
		getType(op.getExpression());
		rule.append(")");
	}
	
	private void generateSend(LFSend func){
		
		rule.append(" send(");
		rule.append(func.getSource()+",");
		rule.append(func.getDestination()+",");
		rule.append(func.getPacketOut()+",");
		rule.append(func.getTimeOut());
		rule.append(")");
		
	}
	
	private void generateRecv(LFRecv func){
		
		rule.append(" recv(");
		rule.append(func.getSource()+",");
		rule.append(func.getDestination()+",");
		rule.append(func.getPacketIn()+",");
		rule.append(func.getTimeIn());
		rule.append(")");
		
	}
	
	private void generateFieldOf(LFFieldOf func){
		
		rule.append(func.getUnit()+"."+func.getField());
		
	}
	
	private void generateIsInternal(LFIsInternal func){
		
		rule.append("isInternal(");
		generateFieldOf(func.getFieldOf());
		rule.append(")");
	}
	
	private void generateMatchEntry(LFMatchEntry func){
		
		rule.append("matchEnty(");
		
		Iterator<LFFieldOf> it = func.getValue().iterator();
		while(it.hasNext()){			
			generateFieldOf(it.next());
			
			if(it.hasNext())
				rule.append(", ");
		}
		
		rule.append(")");
	}
	
	private void generateTabbing(){
		innerLevel++;
		rule.append("\n");
		for(int i = 0; i< innerLevel; i++){
			rule.append("\t");
		}
	}
}
