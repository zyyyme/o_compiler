package o_project_compiler.inheritancetree.visitor;

import o_project_compiler.inheritancetree.InheritanceTreeNode;

public interface InheritanceTreeVisitor {

    public void visit(InheritanceTreeNode node);

}