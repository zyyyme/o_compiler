package o_project_compiler.loggers;

import java_cup.runtime.Symbol;

public class SyntaxErrorLogger extends Logger<Symbol> {

    public enum SyntaxErrorKind {
        INVALID_GLOBAL_VARIABLE_DECLARATION, INVALID_CLASS_INHERITANCE, INVALID_CLASS_FIELD_DECLARATION,
        INVALID_FORMAL_PARAMETER, INVALID_ASSIGNMENT, INVALID_IF_STATEMENT_CONDITION, FATAL_ERROR, INVALID_DECLARATION
    }

    public SyntaxErrorLogger() {
        super(LoggerKind.ERROR_LOGER, "Syntax error");
    }

    @Override
    protected String messageBody(Symbol loggedObject, Object... context) {
        SyntaxErrorKind syntaxErrorKind = (SyntaxErrorKind) context[0];
        switch (syntaxErrorKind) {
            case INVALID_GLOBAL_VARIABLE_DECLARATION:
                return "Invalid global variable declaration. Continuing parsing...";
            case INVALID_CLASS_INHERITANCE:
                return "Invalid class inheritance declaration. Continuing parsing...";
            case INVALID_CLASS_FIELD_DECLARATION:
                return "Invalid class field declaration. Continuing parsing...";
            case INVALID_FORMAL_PARAMETER:
                return "Invalid formal parameter declaration. Continuing parsing...";
            case INVALID_ASSIGNMENT:
                return "Invalid assignment statement. Continuing parsing...";
            case INVALID_IF_STATEMENT_CONDITION:
                return "Invalid if-statement condition. Continuing parsing...";
            case FATAL_ERROR:
                return "Fatal syntax error. Continuing parsing...";
            case INVALID_DECLARATION:
                return "Invalid declaration. Continuing parsing...";
            default:
                return "";
        }
    }

}
