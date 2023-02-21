package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jdk.jfr.internal.LogLevel;

import static jdk.jfr.internal.LogLevel.DEBUG;
import static jdk.jfr.internal.LogLevel.INFO;
import static jdk.jfr.internal.LogLevel.WARN;

public class BotLogger {
    private static LogLevel logLevel = INFO;
    private static final int MAX_LOG_SIZE = 100;
    private static final List<String> logs = new ArrayList<>();

    public static void info(String message) {
        log(message, INFO);
    }

    public static void debug(String message) {
        if (logLevel != DEBUG) {
            return;
        }

        log(message, DEBUG);
    }


    public static void warn(String message) {
        log(message, WARN);
    }

    private static void log(String message, LogLevel level) {
        addLog(message);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[stackTrace.length > 3 ? 4 : stackTrace.length].getClassName();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(level.name()
                + " - " + dateFormat.format(date) + " - [" + className + "] - " + message);
    }

    private static void addLog(String message) {
        logs.add(message);
        if (logs.size() > MAX_LOG_SIZE) {
            logs.remove(0);
        }
    }

    public static List<String> getLogs() {
        return logs;
    }

    public static void setLogLevel(LogLevel logLevel) {
        BotLogger.logLevel = logLevel;
    }

}