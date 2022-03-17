package o_project_compiler.methodsignature;

import o_project_compiler.exceptions.WrongObjectException;
import o_project_compiler.symboltable.Tab;
import o_project_compiler.util.Utils;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ClassMethodSignature extends MethodSignature {

    private final Struct class_struct;

    public ClassMethodSignature(Obj method, Struct clss) throws WrongObjectException {
        super(method, true);
        this.class_struct = clss;
    }

    public ClassMethodSignature(String name, Struct clss) {
        super(name);
        this.class_struct = clss;
    }

    public Struct getThisParameterType() {
        return class_struct;
    }

    @Override
    public String toString() {
        return class_struct != Tab.noType ? Utils.typeToString(class_struct) + "." + super.toString() : super.toString();
    }

}
