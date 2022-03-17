package o_project_compiler.loggers;

import o_project_compiler.SemanticAnalyzer;
import o_project_compiler.methodsignature.ClassMethodSignature;
import o_project_compiler.methodsignature.MethodSignature;
import o_project_compiler.util.Utils;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticErrorLogger extends Logger<Obj> {

    public enum SemanticErrorKind {
        INAPPLICABLE_METHOD, INVALID_PROG_NAME, DUPLICATE_GLOBAL_NAME, DUPLICATE_PARAMETER,
        DUPLICATE_MEMBER, DUPLICATE_LOCAL_VARIABLE, MAIN_METHOD_DECLARATION_NOT_FOUND, NON_PRIMITIVE_TYPE,
        UNRESOLVED_VARIABLE, UNRESOLVED_TYPE, INVALID_SUPERCLASS, NON_VOID_MAIN, MAIN_WITH_PARAMETERS,
        RETURNED_VALUE_FROM_VOID_METHOD, RETURN_NOT_FOUND, ASSIGINING_SYMBOLIC_CONSTANT, TYPE_MISMATCH, UNDEFINED_METHOD,
        MISPLACED_BREAK, MISPLACED_CONTINUE, UNDEFINED_OPERATION, INDEXING_NON_ARRAY, ACCESSING_MEMBER_OF_NON_OBJECT,
        UNRESOLVED_MEMBER, INCOMPATIBLE_RETURN_TYPE, UNINVOKABLE_METHOD
    }

    public SemanticErrorLogger() {
        super(LoggerKind.ERROR_LOGER, "Semantic error");
    }

    @Override
    protected String messageBody(Obj obj, Object... context) {
        SemanticErrorKind semanticErrorKind = (SemanticErrorKind) context[0];
        switch (semanticErrorKind) {
            case INAPPLICABLE_METHOD:
                return "The method \"" + ((Object[]) context[1])[0]
                        + "\" is not applicable for the arguments \"" + ((Object[]) context[1])[1] + "\"";
            case INVALID_PROG_NAME:
                return "Name \"" + obj.getName() + "\" is invalid";
            case DUPLICATE_GLOBAL_NAME:
                return "Global name \"" + obj.getName() + "\" is duplicated";
            case DUPLICATE_PARAMETER:
                return "Parameter \"" + obj.getName() + "\" is duplicated";
            case DUPLICATE_MEMBER:
                return "Inner class member \"" + ((Obj) ((Object[]) context[1])[0]).getName() + "."
                        + obj.getName() + "\" is duplicated";
            case DUPLICATE_LOCAL_VARIABLE:
                return "Local variable \"" + obj.getName() + "\" is duplicated";
            case MAIN_METHOD_DECLARATION_NOT_FOUND:
                return "Declaration of global method \"void " + SemanticAnalyzer.MAIN + "()\" not found";
            case NON_PRIMITIVE_TYPE:
                return "Type \"" + Utils.typeToString(obj.getType()) + "\" is not a primitive data type";
            case UNRESOLVED_VARIABLE:
                return "\"" + obj.getName() + "\" cannot be resolved to a variable";
            case UNRESOLVED_TYPE:
                return "\"" + obj.getName() + "\" cannot be resolved to a type";
            case INVALID_SUPERCLASS:
                return "\"" + obj.getName() + "\" is not a valid superclass type";
            case NON_VOID_MAIN:
                return "Method \"" + SemanticAnalyzer.MAIN + "\" must be declared as \"void\"";
            case MAIN_WITH_PARAMETERS:
                return "Method \"" + SemanticAnalyzer.MAIN + "\" must not have any formal parameters";
            case RETURNED_VALUE_FROM_VOID_METHOD:
                return "Void methods cannot return a value";
            case RETURN_NOT_FOUND:
                return "Method \"" + obj.getName() + "\" must return a result of type \""
                        + Utils.typeToString(obj.getType()) + "\"";
            case ASSIGINING_SYMBOLIC_CONSTANT:
                return "Symbolic constant \"" + obj.getName() + "\" cannot be assigned";
            case TYPE_MISMATCH:
                return "Cannot convert from \""
                        + Utils.typeToString((Struct) ((Object[]) (context[1]))[0]) + "\" to \""
                        + Utils.typeToString((Struct) ((Object[]) (context[1]))[1]) + "\"";
            case UNDEFINED_METHOD:
                Object[] undefined_method_context = (Object[]) context[1];
                MethodSignature overriddenMethodSignature1 = (MethodSignature) undefined_method_context[0];
                return "The method \"" + overriddenMethodSignature1.toString() + "\" is undefined";
            case MISPLACED_BREAK:
                return "break cannot be used outside of a loop";
            case MISPLACED_CONTINUE:
                return "continue cannot be used outside of a loop";
            case UNDEFINED_OPERATION:
                Object[] undefined_operation_context = (Object[]) context[1];
                String operator = (String) undefined_operation_context[undefined_operation_context.length - 1];
                StringBuilder operandTypes = new StringBuilder();
                for (int i = 0; i < undefined_operation_context.length - 1; i++) {
                    operandTypes.append("\"" + Utils.typeToString((Struct) undefined_operation_context[i]) + "\"");
                    if (i < undefined_operation_context.length - 2) {
                        operandTypes.append(", ");
                    }
                }
                return "The operator \"" + operator + "\" is undefined for the argument type(s) "
                        + operandTypes;
            case INDEXING_NON_ARRAY:
                return "The type of the designator must be an array type but it resolved to \""
                        + Utils.typeToString((Struct) ((Object[]) context[1])[0]) + "\"";
            case ACCESSING_MEMBER_OF_NON_OBJECT:
                return "The type of the designator must be a class type but it resolved to \""
                        + Utils.typeToString((Struct) ((Object[]) context[1])[0]) + "\"";
            case UNRESOLVED_MEMBER:
                return "\"" + obj.getName() + "\" cannot be resolved to a class member";
            case INCOMPATIBLE_RETURN_TYPE:
                Object[] return_type_context = (Object[]) context[1];
                ClassMethodSignature overriddenMethodSignature2 = (ClassMethodSignature) return_type_context[0];
                return "The return type is incompatible with \"" + overriddenMethodSignature2.toString() + "\"";
            case UNINVOKABLE_METHOD:
                Object[] uninvokable_context = (Object[]) context[1];
                MethodSignature overriddenMethodSignature3 = (MethodSignature) uninvokable_context[0];
                Struct type = (Struct) uninvokable_context[1];
                return "Cannot invoke \"" + overriddenMethodSignature3.getMethodName() + " "
                        + overriddenMethodSignature3.getParameterList() + "\" on the type \"" + Utils.typeToString(type)
                        + "\"";
            default:
                return "";
        }
    }
}
