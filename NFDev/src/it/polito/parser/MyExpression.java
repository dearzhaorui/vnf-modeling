package it.polito.parser;

import org.eclipse.jdt.core.dom.Expression;

public class MyExpression {
	
	private String packetName;
	private Expression field;
	private Expression value;
	private int nestingLevel;
	
	public MyExpression(Expression field, Expression value, int nestingLevel) {
		this.field = field;
		this.value = value;
		this.nestingLevel = nestingLevel;
	}
	
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}	
	public Expression getField() {
		return field;
	}
	public void setField(Expression field) {
		this.field = field;
	}
	public Expression getValue() {
		return value;
	}
	public void setValue(Expression value) {
		this.value = value;
	}
	
	public String getPacketName(){
		return this.packetName;
	}
	
	public void setPacketName(String packetName){
		this.packetName = packetName;
	}
	
	public String toString(){
		return new String("Field -> " + field + " \t=  Value -> " + value );
	}

}
