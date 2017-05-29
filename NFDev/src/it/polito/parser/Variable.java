package it.polito.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;

public class Variable extends ASTVisitor{
	
	public enum Type { TABLE, CONFIGURATION, GENERIC };
	public enum Scope { CLASS, METHOD };
	
	private String name;
	private String typeName;
	private Expression exp;
	private Type type;
	private List<FieldValuePair> fields;
	private List<Expression> matchedFieldOnTableEntry;
	private List<String> matchedFieldNameOnTableEntry;
	private String[] fieldsType;
	
	public Variable(String name, Expression exp, Type type, String typeName) {
		this.name = name;
		this.exp = exp;
		this.type = type;
		this.typeName = typeName;
		this.fields = new ArrayList<>();
		this.matchedFieldOnTableEntry = new ArrayList<>();
		this.matchedFieldNameOnTableEntry = new ArrayList<>();
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getTypeName(){
		return typeName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void addFieldContraint(FieldValuePair pair) {
		this.fields.add(pair);
	}
	
	public List<FieldValuePair> getFieldContraints() {
		return new ArrayList<FieldValuePair>(fields);
	}
	
	public void addMatchedField(Expression field) {
		this.matchedFieldOnTableEntry.add(field);
	}
	
	public List<Expression> getMatchedFields() {
		return new ArrayList<Expression>(matchedFieldOnTableEntry);
	}

	public List<String> getMatchedFieldName() {
		return matchedFieldNameOnTableEntry;
	}

	public void addMatchedFieldName(String fieldName) {
		this.matchedFieldNameOnTableEntry.add(fieldName);
	}

	public String[] getFieldsType() {
		return fieldsType;
	}

	public void setFieldsType(String[] fieldsType) {
		this.fieldsType = fieldsType;
	}

	public String toString(){
		return new String(name + " \t =  " + exp +"\n");
	}
	
}
