package o_project_compiler.loggers;

public abstract class Logger<T> {

    protected enum LoggerKind {
        INFO_LOGGER, ERROR_LOGER
    };

    protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());
    private final LoggerKind kind;
    protected final String messageHead;

    public Logger(LoggerKind kind, String messageHead) {
        this.kind = kind;
        this.messageHead = messageHead;
    }

    protected abstract String messageBody(T loggedObject, Object... context);

    public final void log(T loggedObject, Integer line, Integer column, Object... context) {
        String line_string = "";
        String column_string = "";

        if (line != null) {
            line_string = "line " + String.format("%3d", line);
        }

        if (column != null) {
            column_string = ", column " + String.format("%3d", column);
        }

        String message = "Error at: " + line_string + column_string + ": " + this.messageBody(loggedObject, context) + ".";

        if (this.kind == LoggerKind.INFO_LOGGER) {
            log.info(message);
        } else {
            log.error(message);
        }
    }

}
