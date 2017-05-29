package it.polito.parser;

import org.eclipse.jdt.core.dom.ASTVisitor;

public class MethodVisitor extends ASTVisitor {
	
	public boolean visit(org.eclipse.jdt.core.dom.MarkerAnnotation node) {
		System.out.println("\tFound marker annotation -> " + node.getTypeName().getFullyQualifiedName());
		return true;
	}
	
	public boolean visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation node) {
		System.out.println("\tFound single member annotation -> " + node.getTypeName().getFullyQualifiedName());
		return true;
	}
	
	public boolean visit(org.eclipse.jdt.core.dom.NormalAnnotation node) {
		System.out.println("\tFound normal annotation -> " + node.getTypeName().getFullyQualifiedName());
		return true;
	}
	
}
