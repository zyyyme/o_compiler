package o_project_compiler.loggers;

public class LexicalErrorLogger extends Logger<String> {

    public LexicalErrorLogger() {
        super(LoggerKind.ERROR_LOGER, "lexical error");
    }

    @Override
    protected String messageBody(String symbol, Object... context) {
        return "token \"" + symbol + "\" not recognized";
    }

}
