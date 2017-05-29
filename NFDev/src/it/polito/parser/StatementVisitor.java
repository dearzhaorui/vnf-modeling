package it.polito.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import it.polito.parser.IfElseBranch.Branch;
import it.polito.parser.Variable.Type;
import it.polito.parser.context.StatementContext;
import it.polito.nfdev.lib.RoutingResult.Action;

public class StatementVisitor extends ASTVisitor {
	
	private List<IfElseBranch> conditions;
	private List<IfElseBranch> previousIfElseBranch;
	private List<ForLoop> loops;
	private List<MyExpression> predicatesOnSentPacket;
	private boolean foundReturn;
	private boolean isDeadCode;
	private boolean foundForward;
	private int nestingLevel;
	private int skippedActions;
	private CompilationUnit compilationUnit;
	
	private Map<String, List<Variable>> variables;
	
	private StatementContext statementContext;
	
	private String simpleName;
	private String variableTypeName;
	
	private boolean isGlobal;

	public StatementVisitor(StatementContext statementContext){
		assert statementContext != null;
		
		this.statementContext = statementContext;
		this.conditions = statementContext.getConditions();
		this.foundReturn = false;
		this.isDeadCode = statementContext.isDeadCode();
		this.loops = statementContext.getForLoops();
		this.previousIfElseBranch = statementContext.getPreviousConditions();
		this.nestingLevel = statementContext.getNestingLevel();
		this.predicatesOnSentPacket = statementContext.getReturnPredicates();
		this.compilationUnit = statementContext.getCompilationUnit();
		this.skippedActions = statementContext.getSkippedActions();
		this.variables = statementContext.getMethodContext().getMethodVariablesMap();
		this.simpleName = null;
		
	}
	
	
	public boolean getFoundForward(){
		return this.foundForward;
	}
	
	public boolean isDeadCode() {
		return isDeadCode;
	}
	
	public boolean isFoundReturn() {
		return foundReturn;
	}

	public List<IfElseBranch> getConditions() {
		return conditions;
	}
	
	public List<IfElseBranch> getPreviousIfElseBranch() {
		return previousIfElseBranch;
	}
	
	public List<ForLoop> getLoops() { 
		return loops; 
	}
	
	public List<MyExpression> getPredicatesOnSentPacket() {
		return predicatesOnSentPacket;
	}
	
	public int getSkippedActions() {
		return skippedActions;
	}
	
	
	@Override
	public boolean visit(IfStatement node) {
		
		ReturnStatementExplorator visitorIf = new ReturnStatementExplorator();
		node.getThenStatement().accept(visitorIf);
		ReturnStatementExplorator visitorElse = new ReturnStatementExplorator();
		if(node.getElseStatement() != null)
			node.getElseStatement().accept(visitorElse);
		if(!visitorIf.hasReturnStatement() && !visitorElse.hasReturnStatement())	// No return stmt inside this IF-THEN-ELSE
			return false;
		
		
		if(visitorIf.hasReturnStatement()){
			
			IfElseBranch b = new IfElseBranch();
			b.setStatement(node);
			b.setBranch(Branch.IF);
			b.setNestingLevel(nestingLevel);
			conditions.add(b);
				
			statementContext.increaseNestingLevel();
			node.getThenStatement().accept(new StatementVisitor(statementContext));	/* Visit IF branch */
			statementContext.decreaseNestingLevel();
			
			conditions.remove(b);
			
			
			if(addPreviousCondition(visitorIf))
				previousIfElseBranch.add(b);
				
		}

		removePreviousIf(nestingLevel+1);
		removePreviousConditionsOnPacket(nestingLevel+1);
		
		if(node.getElseStatement() != null && visitorElse.hasReturnStatement()){
			
			if(addPreviousCondition(visitorIf) && !addPreviousCondition(visitorElse)){
				removePreviousIf(nestingLevel);
				removePreviousConditionsOnPacket(nestingLevel);
			}
			
			if(visitorIf.hasReturnStatement()){
				IfElseBranch b = new IfElseBranch();
				b.setStatement(node);
				b.setBranch(Branch.ELSE);
				b.setNestingLevel(nestingLevel);
				conditions.add(b);				
			
				statementContext.increaseNestingLevel();
				node.getElseStatement().accept(new StatementVisitor(statementContext));	/* Visit ELSE branch */
				statementContext.decreaseNestingLevel();
				
				conditions.remove(b);
			}else{
				statementContext.increaseNestingLevel();
				node.getElseStatement().accept(new StatementVisitor(statementContext));	/* Visit ELSE branch */
				statementContext.decreaseNestingLevel();
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(Assignment node){
		isGlobal = false;
		System.out.println("\tFound assignment -> "+ node.toString());
		// System.out.println("\t\tType -> "+node.getRightHandSide().toString());
		node.getLeftHandSide().accept(new ASTVisitor(){
		
			public boolean visit(ThisExpression node){
				
				isGlobal = true;
				return false;
			}
			
			public boolean visit(SimpleName node){
				
				simpleName = node.getFullyQualifiedName();
				return false;
			}
			
		});
		
		System.out.println("\t\tType -> "+ ASTNode.nodeClassForType(node.getRightHandSide().getNodeType()).getSimpleName());
		List<Variable> var = variables.get(simpleName);
		
		if(var!=null && !isGlobal){			
			Variable v = new Variable(simpleName,node.getRightHandSide(),var.get(0).getType(),var.get(0).getTypeName());
			var.add(v);
			//var.setExp(node.getRightHandSide());
			System.out.println("\t\tVariable initialized!");
		}else{
			var = statementContext.getMethodContext().getContext().getVariable(simpleName);
			if(var != null){
				Variable v = new Variable(simpleName,node.getRightHandSide(),var.get(0).getType(),var.get(0).getTypeName());
				var.add(v);
				//var.setExp(node.getRightHandSide());
				if(var.get(0).getTypeName().compareTo(Constants.TABLE_TYPE)==0){
					Expression temp = node.getRightHandSide();
					if(!(temp instanceof NullLiteral) && temp instanceof ClassInstanceCreation){
						
						int tableSize = 0;
						ClassInstanceCreation ci = (ClassInstanceCreation)temp;
						
						Object num1 = ci.arguments().get(0);
						Object num2 = ci.arguments().get(1);
						
						if(num1 instanceof NumberLiteral){
							int intTemp = Integer.parseInt(((NumberLiteral)num1).getToken());
							tableSize += intTemp; 
						}
						
						if(num2 instanceof NullLiteral){
							int intTemp = Integer.parseInt(((NumberLiteral)num1).getToken());
							tableSize += intTemp; 
						}
						
						statementContext.getMethodContext().getContext().tableSize = tableSize;
						System.out.println("\t\tTable initialized! Size: " + tableSize);
					}
				}
				System.out.println("\t\tGlobal Variable initialized!");
			}
			else
				System.out.println("\t\tVariable not in memory!");
		}
			
		simpleName = null;
		return false;
	}
	
	
	@Override
	public boolean visit(ReturnStatement node) {
		this.foundReturn = true;
		if(this.isDeadCode)
		{
			System.err.println("\t[ERROR] Found RETURN but it is dead code! " + node);
			return false;
		}
		ReturnStatementVisitor r = new ReturnStatementVisitor();
		node.accept(r);
		if(r.getAction() == Action.DROP)
		{
			skippedActions++;
			return false;
		}
		System.out.println("\t*****Found action " + r.getAction() + " (with "+conditions.size()+" conditions leading to it) at line " + compilationUnit.getLineNumber(node.getStartPosition()) + ". Reached nesting level " + nestingLevel);
		
		int j = 1;
		for(IfElseBranch ieb : previousIfElseBranch)
			System.out.println("\t\tPrevious IF-THEN-ELSE #" + j++ + " " + ieb.getStatement().getExpression() + " (IF has RETURN="+ieb.ifBranchContainsReturn()+ ") (ELSE has RETURN="+ieb.elseBranchContainsReturn()+")");
		
		j = 1;
		for(IfElseBranch ieb : conditions)
			System.out.println("\t\tCondition #" + j++ + " ("+ieb.getBranch()+"): " + ieb.getStatement().getExpression());
		
		for(ForLoop fLoop : loops)
			System.out.println("\tEncountered " + fLoop.getStatement().getExpression() + " with nesting level " + fLoop.getNestingLevel());
		
		if(r.getAction() == Action.FORWARD)
		{
			System.out.println("\n\t\tRules for the outgoing packet:");
			for(MyExpression expr : predicatesOnSentPacket)
				System.out.println("\t\t\tpacket." + expr.getField() + "\t= " + expr.getValue());
			System.out.println("\t\tThe remaining fields are unchanged");
			
			statementContext.getMethodContext().addReturnSnapshots(statementContext.createSnapshot(r.getPacketName(),r.getInterfaceName()));
		}
		
		System.out.println();
		return false;
	}
	
	private void removePreviousIf(int nestingLevel)
	{
		List<IfElseBranch> toRemove = new ArrayList<IfElseBranch>();
		for(IfElseBranch ifElse : previousIfElseBranch)
			if(ifElse.getNestingLevel() >= nestingLevel)
				toRemove.add(ifElse);
		previousIfElseBranch.removeAll(toRemove);
		return;
	}
	
	private void removePreviousConditionsOnPacket(int nestingLevel)
	{
		List<MyExpression> toRemove = new ArrayList<MyExpression>();
		for(MyExpression condition : predicatesOnSentPacket)
			if(condition.getNestingLevel() >= nestingLevel)
				toRemove.add(condition);
		predicatesOnSentPacket.removeAll(toRemove);
		return;
	}
	
	private boolean addPreviousCondition(ReturnStatementExplorator visitor){
		boolean forwardFlag = false;
		boolean dropFlag = false;
		
		for(Action temp : visitor.getActionList()){
			if(temp.compareTo(Action.DROP)==0)
				dropFlag = true;
			else if(temp.compareTo(Action.FORWARD)==0)
				forwardFlag = true;
		}
		
		if(dropFlag && !forwardFlag)
			return true;
		else
			return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		System.out.println("\tFound while -> " + node.getExpression());
		return true;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		System.out.println("\tFound FOR-EACH. Variable declaration -> " + node.getParameter() + ". Iterate over -> " + node.getExpression());
		ForLoop l = new ForLoop();
		l.setNestingLevel(nestingLevel);
		l.setStatement(node);
		loops.add(l);
		List<IfElseBranch> c = new ArrayList<IfElseBranch>();
		c.addAll(conditions);
		
		statementContext.increaseNestingLevel();
		StatementVisitor s = new StatementVisitor(statementContext);
		//new Statement2Visitor(compilationUnit, variables, c, false, previousIfElseBranch, loops, nestingLevel+1, predicatesOnSentPacket, skippedActions);
		node.getBody().accept(s);
		statementContext.decreaseNestingLevel();
		return false;
	}
	
	@Override
	public boolean visit(ExpressionStatement node) {
		//System.out.println("\tExpressionStatement " + node.getExpression());
		ExpressionVisitor v = new ExpressionVisitor(nestingLevel,statementContext);
		node.accept(v);
		predicatesOnSentPacket.addAll(v.getPredicates());
	
		return true;
	}
	
	@Override
	public boolean visit(LineComment node) {
		System.out.println("\tFound comment -> " + node);
		return true;
	}
	
	@Override
	public boolean visit(BlockComment node) {
		System.out.println("\tFound comment block -> " + node);
		return true;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		variableTypeName = null;
		node.accept(new ASTVisitor() {
			public boolean visit(SimpleType node){
				variableTypeName = node.getName().getFullyQualifiedName();
				return false;
			}
		});
		//System.out.println("\tVariable ("+node.fragments()+") declared");
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> fragments = node.fragments();
		for(VariableDeclarationFragment fragment : fragments)
		{
			System.out.println("\tVariable decl. found => " + fragment.getName() + " = " + fragment.getInitializer());
			// put the variable in the map
			Variable v = new Variable(fragment.getName().toString(), fragment.getInitializer(), Type.GENERIC,variableTypeName);
			//System.out.println("Saving var => " + fragment.getName().toString());
			//variables.put(fragment.getName().toString(), v);
			statementContext.getMethodContext().addMethodVariable(v);
			
			if(fragment.getInitializer()!=null){
				fragment.getInitializer().accept(new ASTVisitor() {
					@SuppressWarnings("unchecked")
					public boolean visit(MethodInvocation node) {
						if(node.getName().toString().equalsIgnoreCase(Constants.MATCH_ENTRY_METHOD_NAME))
						{
							System.out.println("\tFound matchEntry() method in variable initialization!");
							for(Expression exp : (List<Expression>) node.arguments()){
								
								exp.accept(new ASTVisitor() {
									public boolean visit(QualifiedName node){
										v.addMatchedFieldName(node.getName().getFullyQualifiedName());
																			
										return false;
									}
								});
								
								v.addMatchedField(exp);
							}
								
						}
						return false;
					}
				});
			}
			
			
		}
		return false;
	}
	
}
