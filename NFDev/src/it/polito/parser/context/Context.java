package it.polito.parser.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import it.polito.parser.Variable;

public class Context {

	private String className;

	private Map<String, List<Variable>> classVariables;
	private Map<String, MethodContext> methodContexts; 
	
	private CompilationUnit compilationUnit;
	
	private boolean isDataDriven = false;
	public int tableSize = 0;
	public List<String> tableTypes = new ArrayList<>();
	
	public Context(CompilationUnit compilationUnit){
		this(null, compilationUnit);
	}
	
	public Context(String className, CompilationUnit compilationUnit) {
		this.className = className;
		this.compilationUnit = compilationUnit;
		this.classVariables = new HashMap<>();
		this.methodContexts = new HashMap<>();
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className){
		this.className = className;
	}
	
	public CompilationUnit getCompilationUnit(){
		return this.compilationUnit;
	}
	
	public MethodContext getMethodContext(String methodName) {
		return methodContexts.get(methodName);
	}

	public MethodContext addMethodContext(MethodContext methodContext) {
		return this.methodContexts.put(methodContext.getMethodName(), methodContext);
	}
	
	public Collection<MethodContext> getMethodContexts(){
		return methodContexts.values();
	}
	
	public void addVariable(Variable var){
		
		List<Variable> list = classVariables.get(var.getName());
		
		if(list==null){
			list = new ArrayList<>();
			list.add(var);
			classVariables.put(var.getName(), list);
		}
		else
			list.add(var);
		
	}
	
	public Map<String,List<Variable>> getVariablesMap(){
		return classVariables;
	}
	
	public List<Variable> getVariable(String varName){
		return classVariables.get(varName);
	}
	
	public Collection<List<Variable>> getVariables(){
		return classVariables.values();
	}

	public boolean isDataDriven() {
		return isDataDriven;
	}

	public void setDataDriven(boolean isDataDriven) {
		this.isDataDriven = isDataDriven;
	}

}
