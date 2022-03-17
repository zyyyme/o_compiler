// generated with ast extension for cup
// version 0.8
// 16/2/2022 22:28:35


package o_project_compiler.ast;

public interface SyntaxNode { 

    public void accept(Visitor visitor); 

    public void childrenAccept(Visitor visitor); 
    public void traverseBottomUp(Visitor visitor); 
    public void traverseTopDown(Visitor visitor); 

    public SyntaxNode getParent(); 
    public void setParent(SyntaxNode parent); 
    public int getLine(); 
    public void setLine(int line); 
}
