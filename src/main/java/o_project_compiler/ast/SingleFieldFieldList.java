// generated with ast extension for cup
// version 0.8
// 16/2/2022 22:28:35


package o_project_compiler.ast;

public class SingleFieldFieldList extends FieldList {

    private Field Field;

    public SingleFieldFieldList (Field Field) {
        this.Field=Field;
        if(Field!=null) Field.setParent(this);
    }

    public Field getField() {
        return Field;
    }

    public void setField(Field Field) {
        this.Field=Field;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Field!=null) Field.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Field!=null) Field.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Field!=null) Field.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleFieldFieldList(\n");

        if(Field!=null)
            buffer.append(Field.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleFieldFieldList]");
        return buffer.toString();
    }
}
