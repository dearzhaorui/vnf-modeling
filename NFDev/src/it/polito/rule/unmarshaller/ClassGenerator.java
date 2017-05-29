package it.polito.rule.unmarshaller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.xml.bind.MarshalException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import it.polito.parser.Constants;

public class ClassGenerator {

	private String originalName;
	private String className;
	private String nfName;
	private String fileNameXml;
	private String fileNameJava;
	private boolean isDataDriven;

	private List<String[]> imports;

	private AST ast;
	private CompilationUnit cu;
	private RuleUnmarshaller u;

	private int tableSize = 0;
	private List<String> tableTypes = new ArrayList<>();

	public ClassGenerator(String className) {
		this.originalName = className;
		this.className = "Rule_" + className;
		this.nfName = "n_" + className;
		this.imports = new ArrayList<String[]>();

		fileNameXml = "./xsd/Rule_" + className + ".xml";
		fileNameJava = "./xsd/java/Rule_" + className + ".java";

		imports.add(new String[] { "java", "util", "List" });
		imports.add(new String[] { "java", "util", "ArrayList" });
		imports.add(new String[] { "com", "microsoft", "z3", "BoolExpr" });
		imports.add(new String[] { "com", "microsoft", "z3", "Context" });
		imports.add(new String[] { "com", "microsoft", "z3", "DatatypeExpr" });
		imports.add(new String[] { "com", "microsoft", "z3", "Expr" });
		imports.add(new String[] { "com", "microsoft", "z3", "FuncDecl" });
		imports.add(new String[] { "com", "microsoft", "z3", "IntExpr" });
		imports.add(new String[] { "com", "microsoft", "z3", "Solver" });
		imports.add(new String[] { "com", "microsoft", "z3", "Sort" });
		imports.add(new String[] { "mcnet", "components", "NetContext" });
		imports.add(new String[] { "mcnet", "components", "Network" });
		imports.add(new String[] { "mcnet", "components", "NetworkObject" });
		imports.add(new String[] { "mcnet", "components", "Tuple" });

		this.ast = AST.newAST(AST.JLS3);
		this.cu = ast.newCompilationUnit();

	}

	public void startGeneration() {

		try {

			u = new RuleUnmarshaller(fileNameXml, originalName, ast);

			tableSize = u.getReuslt().getTableSize();
			isDataDriven = u.getReuslt().isDataDriven();
			tableTypes = u.getReuslt().getTableFields();
			
			if(tableSize != tableTypes.size())
				throw new RuntimeException("Error: tableSize and tableTypes.size() must be of the same size");
			
			List<String> tempList = new ArrayList<>();
			for(String type : tableTypes){
				if(type.compareTo(Constants.ENUM_GENERIC)==0)
					tempList.add(type);
			}
			tableTypes.removeAll(tempList);
			tableSize = tableTypes.size();
			
			
			PackageDeclaration pd = ast.newPackageDeclaration();
			pd.setName(ast.newName(new String[] { "mcnet", "netobjs", "generated" }));
			cu.setPackage(pd);

			for (String[] temp : imports) {
				ImportDeclaration id = ast.newImportDeclaration();
				id.setName(ast.newName(temp));
				cu.imports().add(id);
			}

			TypeDeclaration td = ast.newTypeDeclaration();
			td.setName(ast.newSimpleName(className));
			td.setSuperclassType(ast.newSimpleType(ast.newSimpleName("NetworkObject")));
			td.setInterface(false);
			td.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			cu.types().add(td);

			fieldDeclaration(td);

			constructorDeclaration(td);

			initDeclaration(td);

			getZ3NodeDeclaration(td);

			addConstraintsDeclaration(td);

			setInternalAddressDeclaration(td);

			if (!isDataDriven && tableSize > 0) {
				addEntryInit(td);
			}

			td.bodyDeclarations().add(u.generateRule());

			FileWriter writer = new FileWriter(new File(fileNameJava));
			writer.write(cu.toString());
			writer.flush();
			writer.close();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void fieldDeclaration(TypeDeclaration td) {

		// List<BoolExpr> constraints
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("constraints"));
		FieldDeclaration field = ast.newFieldDeclaration(fragment);
		ParameterizedType parameterType = ast.newParameterizedType(ast.newSimpleType(ast.newName("List")));
		parameterType.typeArguments().add(ast.newSimpleType(ast.newName("BoolExpr")));
		field.setType(parameterType);

		td.bodyDeclarations().add(field);

		// Context ctx
		fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("ctx"));
		field = ast.newFieldDeclaration(fragment);
		SimpleType simpleType = ast.newSimpleType(ast.newName("Context"));
		field.setType(simpleType);

		td.bodyDeclarations().add(field);

		// DatatypeExpr className;

		fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(nfName));
		field = ast.newFieldDeclaration(fragment);
		simpleType = ast.newSimpleType(ast.newName("DatatypeExpr"));
		field.setType(simpleType);

		td.bodyDeclarations().add(field);

		// Network net

		fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("net"));
		field = ast.newFieldDeclaration(fragment);
		simpleType = ast.newSimpleType(ast.newName("Network"));
		field.setType(simpleType);

		td.bodyDeclarations().add(field);

		// NetContext nctx

		fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("nctx"));
		field = ast.newFieldDeclaration(fragment);
		simpleType = ast.newSimpleType(ast.newName("NetContext"));
		field.setType(simpleType);

		td.bodyDeclarations().add(field);
		
//		//Integer src_ip
//		
//		fragment = ast.newVariableDeclarationFragment();
//		fragment.setName(ast.newSimpleName("src_ip"));
//		field = ast.newFieldDeclaration(fragment);
//		simpleType = ast.newSimpleType(ast.newName("Integer"));
//		field.setType(simpleType);
//
//		td.bodyDeclarations().add(field);


		// FuncDecl isInternal

		fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("isInternal"));
		field = ast.newFieldDeclaration(fragment);
		simpleType = ast.newSimpleType(ast.newName("FuncDecl"));
		field.setType(simpleType);

		td.bodyDeclarations().add(field);

		if (!isDataDriven && tableSize > 0) {
			// FuncDecl matchEntry

			fragment = ast.newVariableDeclarationFragment();
			fragment.setName(ast.newSimpleName("matchEntry"));
			field = ast.newFieldDeclaration(fragment);
			simpleType = ast.newSimpleType(ast.newName("FuncDecl"));
			field.setType(simpleType);

			td.bodyDeclarations().add(field);

			fragment = ast.newVariableDeclarationFragment();
			fragment.setName(ast.newSimpleName("entries"));
			field = ast.newFieldDeclaration(fragment);
			parameterType = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
			ParameterizedType parameterTuple = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
			parameterTuple.typeArguments().add(ast.newSimpleType(ast.newName("Expr")));
			parameterType.typeArguments().add(parameterTuple);
			field.setType(parameterType);

			td.bodyDeclarations().add(field);

		}

	}

	private void constructorDeclaration(TypeDeclaration td) {

		MethodDeclaration constructor = ast.newMethodDeclaration();
		constructor.setName(ast.newSimpleName(className));
		constructor.setConstructor(true);
		constructor.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

		SingleVariableDeclaration param1 = ast.newSingleVariableDeclaration();
		param1.setName(ast.newSimpleName("ctx"));
		param1.setType(ast.newSimpleType(ast.newName("Context")));

		SingleVariableDeclaration param2 = ast.newSingleVariableDeclaration();
		param2.setName(ast.newSimpleName("args"));
		param2.setType(ast.newArrayType(ast.newSimpleType(ast.newName("Object"))));
		param2.setVarargs(true);

		constructor.parameters().add(param1);
		constructor.parameters().add(param2);

		SuperConstructorInvocation sup = ast.newSuperConstructorInvocation();
		sup.arguments().add(ast.newName("ctx"));
		sup.arguments().add(ast.newName("args"));

		constructor.setBody(ast.newBlock());
		constructor.getBody().statements().add(sup);

		td.bodyDeclarations().add(constructor);

	}

	private void initDeclaration(TypeDeclaration td) {

		List<Statement> statements = new ArrayList<>();

		MarkerAnnotation note = ast.newMarkerAnnotation();
		note.setTypeName(ast.newName("Override"));

		MethodDeclaration init = ast.newMethodDeclaration();
		init.setName(ast.newSimpleName("init"));
		init.setConstructor(false);
		init.modifiers().add(note);
		init.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD));

		// Parameters
		SingleVariableDeclaration param1 = ast.newSingleVariableDeclaration();
		param1.setName(ast.newSimpleName("ctx"));
		param1.setType(ast.newSimpleType(ast.newName("Context")));

		SingleVariableDeclaration param2 = ast.newSingleVariableDeclaration();
		param2.setName(ast.newSimpleName("args"));
		param2.setType(ast.newArrayType(ast.newSimpleType(ast.newName("Object"))));
		param2.setVarargs(true);

		init.parameters().add(param1);
		init.parameters().add(param2);

		// Method code
		init.setBody(ast.newBlock());

		// this.ctx=ctx;

		FieldAccess leftExp = ast.newFieldAccess();
		leftExp.setExpression(ast.newThisExpression());
		leftExp.setName(ast.newSimpleName("ctx"));

		Assignment assignment = ast.newAssignment();
		assignment.setLeftHandSide(leftExp);
		assignment.setRightHandSide(ast.newSimpleName("ctx"));
		assignment.setOperator(Operator.ASSIGN);

		statements.add(ast.newExpressionStatement(assignment));

		// isEndHost=false;

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("isEndHost"));
		assignment.setRightHandSide(ast.newBooleanLiteral(false));

		statements.add(ast.newExpressionStatement(assignment));

		// constraints=new ArrayList<BoolExpr>();

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("constraints"));
		ClassInstanceCreation instance = ast.newClassInstanceCreation();
		ParameterizedType param = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
		param.typeArguments().add(ast.newSimpleType(ast.newName("BoolExpr")));
		instance.setType(param);
		assignment.setRightHandSide(instance);

		statements.add(ast.newExpressionStatement(assignment));

		// z3Node=((NetworkObject)args[0][0]).getZ3Node();

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("z3Node"));

		MethodInvocation mi = ast.newMethodInvocation();
		ParenthesizedExpression pe = ast.newParenthesizedExpression();
		CastExpression cast = ast.newCastExpression();

		mi.setExpression(pe);
		mi.setName(ast.newSimpleName("getZ3Node"));
		cast.setType(ast.newSimpleType(ast.newName("NetworkObject")));

		ArrayAccess array1 = ast.newArrayAccess();
		array1.setIndex(ast.newNumberLiteral("0"));
		ArrayAccess array2 = ast.newArrayAccess();
		array2.setIndex(ast.newNumberLiteral("0"));
		array2.setArray(ast.newSimpleName("args"));
		array1.setArray(array2);

		cast.setExpression(array1);
		pe.setExpression(cast);
		assignment.setRightHandSide(mi);

		statements.add(ast.newExpressionStatement(assignment));

		// nat=z3Node;

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName(nfName));
		assignment.setRightHandSide(ast.newSimpleName("z3Node"));

		statements.add(ast.newExpressionStatement(assignment));

		// net=(Network)args[0][1];

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("net"));
		assignment.setOperator(Operator.ASSIGN);

		cast = ast.newCastExpression();
		cast.setType(ast.newSimpleType(ast.newSimpleName("Network")));
		array1 = ast.newArrayAccess();
		array1.setIndex(ast.newNumberLiteral("1"));
		array2 = ast.newArrayAccess();
		array2.setIndex(ast.newNumberLiteral("0"));
		array2.setArray(ast.newSimpleName("args"));
		array1.setArray(array2);

		cast.setExpression(array1);
		assignment.setRightHandSide(cast);

		statements.add(ast.newExpressionStatement(assignment));

		// nctx=(NetContext)args[0][2];

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("nctx"));
		assignment.setOperator(Operator.ASSIGN);

		cast = ast.newCastExpression();
		cast.setType(ast.newSimpleType(ast.newSimpleName("NetContext")));
		array1 = ast.newArrayAccess();
		array1.setIndex(ast.newNumberLiteral("2"));
		array2 = ast.newArrayAccess();
		array2.setIndex(ast.newNumberLiteral("0"));
		array2.setArray(ast.newSimpleName("args"));
		array1.setArray(array2);

		cast.setExpression(array1);
		assignment.setRightHandSide(cast);

		statements.add(ast.newExpressionStatement(assignment));
		
//		//if(args[0].length >= 4)
//		// src_ip = (Integer)args[0][4];
//		
//		IfStatement is = ast.newIfStatement();
//		InfixExpression ife = ast.newInfixExpression();
//		is.setExpression(ife);
//		
//		ife.setOperator(InfixExpression.Operator.GREATER_EQUALS);
//		ife.setRightOperand(ast.newNumberLiteral("4"));
//
//		FieldAccess fa = ast.newFieldAccess();
//		fa.setName(ast.newSimpleName("length"));
//		
//		array1 = ast.newArrayAccess();
//		array1.setIndex(ast.newNumberLiteral("0"));
//		array1.setArray(ast.newName("args"));
//		
//		fa.setExpression(array1);
//		ife.setLeftOperand(fa);
//		
//		assignment = ast.newAssignment();
//		is.setThenStatement(ast.newExpressionStatement(assignment));
//		assignment.setLeftHandSide(ast.newName("src_ip"));
//		
//		cast = ast.newCastExpression();
//		cast.setType(ast.newSimpleType(ast.newSimpleName("Integer")));
//		array1 = ast.newArrayAccess();
//		array1.setIndex(ast.newNumberLiteral("3"));
//		array2 = ast.newArrayAccess();
//		array2.setIndex(ast.newNumberLiteral("0"));
//		array2.setArray(ast.newSimpleName("args"));
//		array1.setArray(array2);
//
//		cast.setExpression(array1);
//		assignment.setRightHandSide(cast);
//
//		statements.add(is);

		// net.saneSend(this);

		mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName("net"));
		mi.setName(ast.newSimpleName("saneSend"));
		mi.arguments().add(ast.newThisExpression());

		statements.add(ast.newExpressionStatement(mi));

		// isInternal = ctx.mkFuncDecl("isInternal", nctx.address,
		// ctx.mkBoolSort());

		assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("isInternal"));
		assignment.setOperator(Operator.ASSIGN);

		mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("mkFuncDecl"));
		mi.setExpression(ast.newName("ctx"));

		InfixExpression ie = ast.newInfixExpression();
		ie.setOperator(InfixExpression.Operator.PLUS);
		ie.setLeftOperand(ast.newName(nfName));

		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue("_isInternal");
		ie.setRightOperand(sl);
		
		FieldAccess fa = ast.newFieldAccess();
		fa.setName(ast.newSimpleName("address"));
		fa.setExpression(ast.newName("nctx"));
		
		MethodInvocation innerMi = ast.newMethodInvocation();
		innerMi.setName(ast.newSimpleName("mkBoolSort"));
		innerMi.setExpression(ast.newName("ctx"));

		mi.arguments().add(ie);
		mi.arguments().add(fa);
		mi.arguments().add(innerMi);

		assignment.setRightHandSide(mi);

		statements.add(ast.newExpressionStatement(assignment));

		if (!isDataDriven && tableSize > 0 ) {
			// matchEntry = ctx.mkFuncDecl("matchEntry", nctx.address,
			// ctx.mkBoolSort());

			assignment = ast.newAssignment();
			assignment.setLeftHandSide(ast.newSimpleName("matchEntry"));
			assignment.setOperator(Operator.ASSIGN);

			mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName("mkFuncDecl"));
			mi.setExpression(ast.newName("ctx"));

			ie = ast.newInfixExpression();
			ie.setOperator(InfixExpression.Operator.PLUS);
			ie.setLeftOperand(ast.newName(nfName));

			sl = ast.newStringLiteral();
			sl.setLiteralValue("_matchEntry");

			ie.setRightOperand(sl);

			innerMi = ast.newMethodInvocation();
			innerMi.setName(ast.newSimpleName("mkBoolSort"));
			innerMi.setExpression(ast.newName("ctx"));

			ArrayCreation ac = ast.newArrayCreation();
			ac.setType(ast.newArrayType(ast.newSimpleType(ast.newName("Sort"))));
			ArrayInitializer ai = ast.newArrayInitializer();

			for (int i = 0; i < tableSize; i++) {
				
				if(tableTypes.get(0).compareTo(Constants.ENUM_IP)==0){
					FieldAccess tempFa = ast.newFieldAccess();
					tempFa.setName(ast.newSimpleName("address"));
					tempFa.setExpression(ast.newName("nctx"));
					ai.expressions().add(tempFa);
				}
				else{
					MethodInvocation tempMi = ast.newMethodInvocation();
					tempMi.setName(ast.newSimpleName("mkIntSort"));
					tempMi.setExpression(ast.newName("ctx"));
					ai.expressions().add(tempMi);
				}	
			}
			ac.setInitializer(ai);

			mi.arguments().add(ie);
			mi.arguments().add(ac);
			mi.arguments().add(innerMi);

			assignment.setRightHandSide(mi);

			statements.add(ast.newExpressionStatement(assignment));

			// entries=new ArrayList<ArrayList<Expr>>();
			assignment = ast.newAssignment();
			assignment.setLeftHandSide(ast.newSimpleName("entries"));
			instance = ast.newClassInstanceCreation();
			param = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));

			ParameterizedType paramTuple = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
			paramTuple.typeArguments().add(ast.newSimpleType(ast.newName("Expr")));
			param.typeArguments().add(paramTuple);
			instance.setType(param);
			assignment.setRightHandSide(instance);

			statements.add(ast.newExpressionStatement(assignment));

		}

		init.getBody().statements().addAll(statements);

		td.bodyDeclarations().add(init);

	}

	private void getZ3NodeDeclaration(TypeDeclaration td) {

		MarkerAnnotation note = ast.newMarkerAnnotation();
		note.setTypeName(ast.newName("Override"));

		MethodDeclaration method = ast.newMethodDeclaration();
		method.setName(ast.newSimpleName("getZ3Node"));
		method.setConstructor(false);
		method.modifiers().add(note);
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		method.setReturnType2(ast.newSimpleType(ast.newSimpleName("DatatypeExpr")));

		ReturnStatement ret = ast.newReturnStatement();
		ret.setExpression(ast.newSimpleName(nfName));

		method.setBody(ast.newBlock());
		method.getBody().statements().add(ret);

		td.bodyDeclarations().add(method);
	}

	private void addConstraintsDeclaration(TypeDeclaration td) {

		MarkerAnnotation note = ast.newMarkerAnnotation();
		note.setTypeName(ast.newName("Override"));
		
		MethodDeclaration method = ast.newMethodDeclaration();
		method.setName(ast.newSimpleName("addConstraints"));
		method.setConstructor(false);
		method.modifiers().add(note);
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD));
		
		SingleVariableDeclaration param = ast.newSingleVariableDeclaration();
		param.setName(ast.newSimpleName("solver"));
		param.setType(ast.newSimpleType(ast.newName("Solver")));
		
		method.parameters().add(param);
		method.setBody(ast.newBlock());
		
		//BoolExpr[] constr = new BoolExpr[constraints.size()];
		
		Assignment assignment = ast.newAssignment();
		VariableDeclarationFragment varFrag = ast.newVariableDeclarationFragment();
		varFrag.setName(ast.newSimpleName("constr"));
		VariableDeclarationExpression varExp = ast.newVariableDeclarationExpression(varFrag);
		varExp.setType(ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr"))));
		
		assignment.setLeftHandSide(varExp);
		
		ArrayCreation instance = ast.newArrayCreation();
		ArrayType array = ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr")));
		instance.setType(array);
		
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("size"));
		mi.setExpression(ast.newName("constraints"));
		
		instance.dimensions().add(mi);
		
		assignment.setRightHandSide(instance);
		
		method.getBody().statements().add(ast.newExpressionStatement(assignment));
		
		
		mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("add"));
		mi.setExpression(ast.newName("solver"));
		
		MethodInvocation mi2 = ast.newMethodInvocation();
		mi2.setName(ast.newSimpleName("toArray"));
		mi2.setExpression(ast.newName("constraints"));
		mi2.arguments().add(ast.newName("constr"));
		
		mi.arguments().add(mi2);
		
		method.getBody().statements().add(ast.newExpressionStatement(mi));
		
		if(!isDataDriven && tableSize>0){
		
			IfStatement is = ast.newIfStatement();
			InfixExpression ine = ast.newInfixExpression();
			ine.setOperator(InfixExpression.Operator.EQUALS);
			NumberLiteral zero = ast.newNumberLiteral();
			zero.setToken("0");
			ine.setRightOperand(zero);
			mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName("size"));
			mi.setExpression(ast.newName("entries"));
			ine.setLeftOperand(mi);
			
			is.setExpression(ine);
			is.setThenStatement(ast.newReturnStatement());
			
			method.getBody().statements().add(is);
			
			
			for(int i = 0; i<tableSize;i++){
				assignment = ast.newAssignment();
				
				varFrag = ast.newVariableDeclarationFragment();
				varFrag.setName(ast.newSimpleName("e_"+i));
				varExp = ast.newVariableDeclarationExpression(varFrag);
				varExp.setType(ast.newSimpleType(ast.newName("Expr")));
				
				assignment.setLeftHandSide(varExp);
				
				mi = ast.newMethodInvocation();	
				if(tableTypes.get(i).compareTo(Constants.ENUM_IP)==0)
					mi.setName(ast.newSimpleName("mkConst"));
				else
					mi.setName(ast.newSimpleName("mkIntConst"));
				mi.setExpression(ast.newName("ctx"));
				
				assignment.setRightHandSide(mi);
				InfixExpression tempIfe = ast.newInfixExpression();
				tempIfe.setOperator(InfixExpression.Operator.PLUS);
				tempIfe.setLeftOperand(ast.newName(nfName));
				StringLiteral sl = ast.newStringLiteral();
				sl.setLiteralValue("_entry_e_" + i);
				tempIfe.setRightOperand(sl);
				mi.arguments().add(tempIfe);
				
				if(tableTypes.get(i).compareTo(Constants.ENUM_IP)==0){
					FieldAccess fa = ast.newFieldAccess();
					fa.setName(ast.newSimpleName("address"));
					fa.setExpression(ast.newName("nctx"));
					mi.arguments().add(fa);
				}
				
				method.getBody().statements().add(ast.newExpressionStatement(assignment));
			}
			
			assignment = ast.newAssignment();
			varFrag = ast.newVariableDeclarationFragment();
			varFrag.setName(ast.newSimpleName("entry_map"));
			varExp = ast.newVariableDeclarationExpression(varFrag);
			varExp.setType(ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr"))));
			
			assignment.setLeftHandSide(varExp);
			
			instance = ast.newArrayCreation();
			array = ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr")));
			instance.setType(array);
			
			mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName("size"));
			mi.setExpression(ast.newName("entries"));
			
			instance.dimensions().add(mi);
			
			assignment.setRightHandSide(instance);
			
			method.getBody().statements().add(ast.newExpressionStatement(assignment));
			
			
			ForStatement fs = ast.newForStatement();
			varFrag = ast.newVariableDeclarationFragment();
			varFrag.setName(ast.newSimpleName("i"));
			NumberLiteral nl = ast.newNumberLiteral();
			nl.setToken("0");
			varFrag.setInitializer(nl);
			VariableDeclarationExpression vde = ast.newVariableDeclarationExpression(varFrag);
			vde.setType(ast.newPrimitiveType(PrimitiveType.INT));

			PostfixExpression pe = ast.newPostfixExpression();
			pe.setOperator(PostfixExpression.Operator.INCREMENT);
			pe.setOperand(ast.newName("i"));
			
			InfixExpression ife = ast.newInfixExpression();
			ife.setOperator(InfixExpression.Operator.LESS);
			ife.setLeftOperand(ast.newName("i"));
			mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName("size"));
			mi.setExpression(ast.newName("entries"));
			ife.setRightOperand(mi);
			
			fs.initializers().add(vde);
			fs.updaters().add(pe);
			fs.setExpression(ife);
			
			fs.setBody(ast.newBlock());
			
			method.getBody().statements().add(fs);
			
			
			assignment = ast.newAssignment();
			ArrayAccess aa = ast.newArrayAccess();
			aa.setIndex(ast.newName("i"));
			aa.setArray(ast.newName("entry_map"));
			assignment.setLeftHandSide(aa);
			Block temp = (Block)fs.getBody();
			temp.statements().add(ast.newExpressionStatement(assignment));
			
			MethodInvocation mkAnd = ast.newMethodInvocation();
			mkAnd.setName(ast.newSimpleName("mkAnd"));
			mkAnd.setExpression(ast.newName("ctx"));
			for(int i = 0; i < tableSize; i++){
				MethodInvocation mkEq = ast.newMethodInvocation();
				mkEq.setName(ast.newSimpleName("mkEq"));
				mkEq.setExpression(ast.newName("ctx"));
				mkEq.arguments().add(ast.newName("e_"+i));
				
				MethodInvocation fieldAccess = ast.newMethodInvocation();
				fieldAccess.setName(ast.newSimpleName("get"));
				fieldAccess.arguments().add(ast.newNumberLiteral(Integer.toString(i)));
				MethodInvocation entryAccess = ast.newMethodInvocation();
				entryAccess.setName(ast.newSimpleName("get"));
				entryAccess.setExpression(ast.newName("entries"));
				entryAccess.arguments().add(ast.newName("i"));
				fieldAccess.setExpression(entryAccess);
				
				mkEq.arguments().add(fieldAccess);
				mkAnd.arguments().add(mkEq);
			}
			
			assignment.setRightHandSide(mkAnd);
			
			MethodInvocation add = ast.newMethodInvocation();
			add.setName(ast.newSimpleName("add"));
			add.setExpression(ast.newName("solver"));
			
			MethodInvocation mkForAll = ast.newMethodInvocation();
			mkForAll.setName(ast.newSimpleName("mkForall"));
			mkForAll.setExpression(ast.newName("ctx"));
			
			ArrayCreation expr = ast.newArrayCreation();
			expr.setType(ast.newArrayType(ast.newSimpleType(ast.newName("Expr"))));
			ArrayInitializer ai = ast.newArrayInitializer();
			for(int i = 0; i < tableSize; i++){	
				ai.expressions().add(ast.newName("e_"+i));	
			}
			expr.setInitializer(ai);
			
			mkForAll.arguments().add(expr);
			
			MethodInvocation mkEq = ast.newMethodInvocation();
			mkEq.setName(ast.newSimpleName("mkEq"));
			mkEq.setExpression(ast.newName("ctx"));
			
			MethodInvocation apply = ast.newMethodInvocation();
			apply.setName(ast.newSimpleName("apply"));
			apply.setExpression(ast.newName("matchEntry"));	
			for(int i = 0; i < tableSize; i++){	
				apply.arguments().add(ast.newName("e_"+i));
			}
			mkEq.arguments().add(apply);
			
			MethodInvocation mkOr = ast.newMethodInvocation();
			mkOr.setName(ast.newSimpleName("mkOr"));
			mkOr.setExpression(ast.newName("ctx"));
			mkOr.arguments().add(ast.newName("entry_map"));
			
			mkEq.arguments().add(mkOr);
			
			mkForAll.arguments().add(mkEq);
			mkForAll.arguments().add(ast.newNumberLiteral("1"));
			mkForAll.arguments().add(ast.newNullLiteral());
			mkForAll.arguments().add(ast.newNullLiteral());
			mkForAll.arguments().add(ast.newNullLiteral());
			mkForAll.arguments().add(ast.newNullLiteral());
		
			add.arguments().add(mkForAll);
			
			
			method.getBody().statements().add(ast.newExpressionStatement(add));
		}
		
		td.bodyDeclarations().add(method);
	}

	private void setInternalAddressDeclaration(TypeDeclaration td) {

		MethodDeclaration method = ast.newMethodDeclaration();
		method.setName(ast.newSimpleName("setInternalAddress"));
		method.setConstructor(false);
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

		SingleVariableDeclaration param = ast.newSingleVariableDeclaration();
		param.setName(ast.newSimpleName("internalAddress"));

		ParameterizedType pt = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
		pt.typeArguments().add(ast.newSimpleType(ast.newName("DatatypeExpr")));
		param.setType(pt);

		method.parameters().add(param);

		method.setBody(ast.newBlock());

		// List<BoolExpr> constr=new ArrayList<BoolExpr>();
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("constr"));
		ParameterizedType parameterType = ast.newParameterizedType(ast.newSimpleType(ast.newName("List")));
		parameterType.typeArguments().add(ast.newSimpleType(ast.newName("BoolExpr")));

		VariableDeclarationExpression vde = ast.newVariableDeclarationExpression(fragment);
		vde.setType(parameterType);

		ClassInstanceCreation instance = ast.newClassInstanceCreation();
		pt = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
		pt.typeArguments().add(ast.newSimpleType(ast.newName("BoolExpr")));
		instance.setType(pt);
		fragment.setInitializer(instance);

		method.getBody().statements().add(ast.newExpressionStatement(vde));

		// Expr n_0=ctx.mkConst(Nat_node,nctx.address);
		Assignment assignment = ast.newAssignment();

		VariableDeclarationFragment varFrag = ast.newVariableDeclarationFragment();
		varFrag.setName(ast.newSimpleName("in_0"));
		VariableDeclarationExpression varExp = ast.newVariableDeclarationExpression(varFrag);
		varExp.setType(ast.newSimpleType(ast.newName("Expr")));

		assignment.setLeftHandSide(varExp);

		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("mkConst"));
		mi.setExpression(ast.newName("ctx"));

		assignment.setRightHandSide(mi);
		InfixExpression ie = ast.newInfixExpression();
		ie.setOperator(InfixExpression.Operator.PLUS);
		ie.setLeftOperand(ast.newName(nfName));

		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue("_internal_node");
		ie.setRightOperand(sl);
		mi.arguments().add(ie);

		FieldAccess fa = ast.newFieldAccess();
		fa.setExpression(ast.newName("nctx"));
		fa.setName(ast.newSimpleName("address"));

		mi.arguments().add(fa);

		method.getBody().statements().add(ast.newExpressionStatement(assignment));

		EnhancedForStatement efs = ast.newEnhancedForStatement();
		efs.setExpression(ast.newName("internalAddress"));
		param = ast.newSingleVariableDeclaration();
		param.setName(ast.newSimpleName("n"));
		param.setType(ast.newSimpleType(ast.newName("DatatypeExpr")));
		efs.setParameter(param);

		mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("add"));
		mi.setExpression(ast.newName("constr"));

		MethodInvocation innerMi = ast.newMethodInvocation();
		innerMi.setName(ast.newSimpleName("mkEq"));
		innerMi.setExpression(ast.newName("ctx"));
		innerMi.arguments().add(ast.newName("in_0"));
		innerMi.arguments().add(ast.newName("n"));

		mi.arguments().add(innerMi);
		efs.setBody(ast.newExpressionStatement(mi));

		method.getBody().statements().add(efs);

		// BoolExpr[] constrs = new BoolExpr[constr.size()];
		assignment = ast.newAssignment();
		varFrag = ast.newVariableDeclarationFragment();
		varFrag.setName(ast.newSimpleName("constrs"));
		varExp = ast.newVariableDeclarationExpression(varFrag);
		varExp.setType(ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr"))));

		assignment.setLeftHandSide(varExp);

		ArrayCreation ac = ast.newArrayCreation();
		ArrayType array = ast.newArrayType(ast.newSimpleType(ast.newName("BoolExpr")));
		ac.setType(array);

		mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("size"));
		mi.setExpression(ast.newName("constr"));

		ac.dimensions().add(mi);

		assignment.setRightHandSide(ac);

		method.getBody().statements().add(ast.newExpressionStatement(assignment));

		// constraints.add(ctx.mkForAll(new
		// Expr[]{n_0},ctx.mkEq(isInternal.apply(n_0),ctx.mkOr(constr.toArray(constrs))),1,null,null,null,null));
		mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("add"));
		mi.setExpression(ast.newName("constraints"));

		MethodInvocation mkForAll = ast.newMethodInvocation();
		mkForAll.setName(ast.newSimpleName("mkForall"));
		mkForAll.setExpression(ast.newName("ctx"));
		mi.arguments().add(mkForAll);

		ac = ast.newArrayCreation();
		ac.setType(ast.newArrayType(ast.newSimpleType(ast.newName("Expr"))));
		ArrayInitializer ai = ast.newArrayInitializer();
		ai.expressions().add(ast.newName("in_0"));
		ac.setInitializer(ai);

		mkForAll.arguments().add(ac);

		MethodInvocation mkEq = ast.newMethodInvocation();
		mkEq.setName(ast.newSimpleName("mkEq"));
		mkEq.setExpression(ast.newName("ctx"));

		mkForAll.arguments().add(mkEq);

		MethodInvocation apply = ast.newMethodInvocation();
		apply.setName(ast.newSimpleName("apply"));
		apply.setExpression(ast.newName("isInternal"));
		apply.arguments().add(ast.newName("in_0"));

		mkEq.arguments().add(apply);

		MethodInvocation mkOr = ast.newMethodInvocation();
		mkOr.setName(ast.newSimpleName("mkOr"));
		mkOr.setExpression(ast.newName("ctx"));

		innerMi = ast.newMethodInvocation();
		innerMi.setName(ast.newSimpleName("toArray"));
		innerMi.setExpression(ast.newName("constr"));
		innerMi.arguments().add(ast.newName("constrs"));

		mkOr.arguments().add(innerMi);

		mkEq.arguments().add(mkOr);

		mkForAll.arguments().add(ast.newNumberLiteral("1"));
		mkForAll.arguments().add(ast.newNullLiteral());
		mkForAll.arguments().add(ast.newNullLiteral());
		mkForAll.arguments().add(ast.newNullLiteral());
		mkForAll.arguments().add(ast.newNullLiteral());

		method.getBody().statements().add(ast.newExpressionStatement(mi));

		td.bodyDeclarations().add(method);
	}

	private void addEntryInit(TypeDeclaration td) {

		if(tableSize < 1)
			return;
		
		MethodDeclaration method = ast.newMethodDeclaration();
		method.setName(ast.newSimpleName("addEntry"));
		method.setConstructor(false);
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

		List<SingleVariableDeclaration> parameters = new ArrayList<>();
		
		for(int i = 0; i<tableSize; i++){
			SingleVariableDeclaration param = ast.newSingleVariableDeclaration();
			param.setName(ast.newSimpleName("expr_"+i));
			
			param.setType(ast.newSimpleType(ast.newName("Expr")));
			method.parameters().add(param);
			parameters.add(param);
		}

		
		method.setBody(ast.newBlock());
		
		IfStatement is = ast.newIfStatement();
		method.getBody().statements().add(is);
		InfixExpression ife = ast.newInfixExpression();
		if(tableSize > 1){	
			is.setExpression(ife);
			ife.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		}
			
		for(int i = 0; i<tableSize; i++){
			InfixExpression tempIfe = ast.newInfixExpression();
			tempIfe.setOperator(InfixExpression.Operator.EQUALS);
			tempIfe.setLeftOperand(ast.newName(parameters.get(i).getName().getFullyQualifiedName()));
			tempIfe.setRightOperand(ast.newNullLiteral());
			
			if(tableSize > 1){
				if(i == 0)
					ife.setLeftOperand(tempIfe);
				else if(i == 1)
					ife.setRightOperand(tempIfe);
				else if(i > 1)
					ife.extendedOperands().add(tempIfe);		
			}else
				is.setExpression(tempIfe);
		}
		is.setThenStatement(ast.newReturnStatement());
		
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("entry"));
		ParameterizedType parameterType = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
		parameterType.typeArguments().add(ast.newSimpleType(ast.newName("Expr")));

		VariableDeclarationExpression vde = ast.newVariableDeclarationExpression(fragment);
		vde.setType(parameterType);

		ClassInstanceCreation instance = ast.newClassInstanceCreation();
		ParameterizedType pt = ast.newParameterizedType(ast.newSimpleType(ast.newName("ArrayList")));
		pt.typeArguments().add(ast.newSimpleType(ast.newName("Expr")));
		instance.setType(pt);
		fragment.setInitializer(instance);
		
		method.getBody().statements().add(ast.newExpressionStatement(vde));
		
		if(tableSize > 1){
			for(int i = 0; i < tableSize; i++){
				is = ast.newIfStatement();
				ife = ast.newInfixExpression();
				is.setExpression(ife);
				
				ife.setOperator(InfixExpression.Operator.EQUALS);
				ife.setLeftOperand(ast.newName(parameters.get(i).getName().getFullyQualifiedName()));
				ife.setRightOperand(ast.newNullLiteral());
				
				MethodInvocation mi = ast.newMethodInvocation();
				is.setThenStatement(ast.newExpressionStatement(mi));
				mi.setName(ast.newSimpleName("add"));
				mi.setExpression(ast.newName("entry"));
				
				MethodInvocation innerMi = ast.newMethodInvocation();
				mi.arguments().add(innerMi);
				innerMi.setName(ast.newSimpleName("mkBool"));
				innerMi.setExpression(ast.newName("ctx"));
				innerMi.arguments().add(ast.newBooleanLiteral(true));
				
				mi = ast.newMethodInvocation();
				is.setElseStatement(ast.newExpressionStatement(mi));
				mi.setName(ast.newSimpleName("add"));
				mi.setExpression(ast.newName("entry"));
				mi.arguments().add(ast.newName(parameters.get(i).getName().getFullyQualifiedName()));
				
				method.getBody().statements().add(is);
			}
		}else {
			MethodInvocation mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName("add"));
			mi.setExpression(ast.newName("entry"));
			mi.arguments().add(ast.newName(parameters.get(0).getName().getFullyQualifiedName()));
			
			method.getBody().statements().add(ast.newExpressionStatement(mi));
		}
		
	

//		is = ast.newIfStatement();
//		is.setThenStatement(ast.newBlock());
//		ife = ast.newInfixExpression();
//		is.setExpression(ife);
//		ife.setOperator(InfixExpression.Operator.EQUALS);
//
//		FieldAccess fa = ast.newFieldAccess();
//		fa.setName(ast.newSimpleName("length"));
//		fa.setExpression(ast.newName("entry"));
//
//		ife.setLeftOperand(fa);
//		ife.setRightOperand(ast.newNumberLiteral("9"));
//
//		method.getBody().statements().add(is);

		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("add"));
		mi.setExpression(ast.newName("entries"));
		mi.arguments().add(ast.newName("entry"));
		method.getBody().statements().add(ast.newExpressionStatement(mi));

		td.bodyDeclarations().add(method);
	}

}
