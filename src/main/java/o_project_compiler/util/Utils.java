

package o_project_compiler.util;

import o_project_compiler.exceptions.WrongObjectException;
import o_project_compiler.exceptions.WrongStructureException;
import o_project_compiler.methodsignature.ClassMethodSignature;
import o_project_compiler.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;


public final class Utils {

    private Utils() {
    }

    public static boolean haveSameSignatures(Obj method1, Obj method2) throws WrongObjectException {
        if (method1 == null || method2 == null) {
            return false;
        }
        return new ClassMethodSignature(method1, Tab.noType).equals(new ClassMethodSignature(method2, Tab.noType));
    }

    public static boolean returnTypesAssignmentCompatible(Obj overridingMethod, Obj overriddenMethod)
            throws WrongObjectException {
        if (overridingMethod.getKind() != Obj.Meth || overriddenMethod.getKind() != Obj.Meth) {
            throw new WrongObjectException();
        }
        return assignableTo(overridingMethod.getType(), overriddenMethod.getType());
    }

    public static String typeToString(Struct type) {
        switch (type.getKind()) {
            case Struct.Bool:
                return "bool";
            case Struct.Int:
                return "int";
            case Struct.Char:
                return "char";
            case Struct.Array:
                return typeToString(type.getElemType()) + "[]";
            case Struct.Class:
                if (type == Tab.nullType) {
                    return "null";
                } else {
                    return Tab.findObjForClass(type).getName();
                }
            case Struct.None:
                return "void";
            default:
                return null;
        }
    }

    public static String getCompactClassMethodSignature(Obj method) throws WrongObjectException {
        return new ClassMethodSignature(method, Tab.noType).getCompactSignature();
    }

    public static boolean assignableTo(Struct source, Struct destination) {
        if (!canSubstitute(source, destination)) {
            return source.assignableTo(destination);
        }
        return true;
    }

    private static boolean canSubstitute(Struct subclass, Struct superclass) {
        if (subclass.getKind() == Struct.Class && superclass.getKind() == Struct.Class) {
            if (subclass == superclass) {
                return true;
            }
            Struct subclass1 = subclass.getElemType();
            while (subclass1 != null) {
                if (subclass1 == superclass) {
                    return true;
                }
                subclass1 = subclass1.getElemType();
            }
        }
        if (subclass.getKind() == Struct.Array && superclass.getKind() == Struct.Array) {
            return canSubstitute(subclass.getElemType(), superclass.getElemType());
        }
        return false;
    }

    public static int sizeOfClassInstance(Struct clss) throws WrongStructureException {
        if (clss.getKind() != Struct.Class) {
            throw new WrongStructureException();
        }
        int numberOfFields = 0;
        Struct superclass = clss;
        while (superclass != null) {
            numberOfFields += superclass.getNumberOfFields();
            superclass = superclass.getElemType();
        }
        return numberOfFields * 4;
    }

    public static boolean isPrimitiveDataType(Struct type) {
        return type.equals(Tab.intType) || type.equals(Tab.charType) || type.equals(Tab.BOOL_TYPE);
    }

}
