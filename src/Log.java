import java.io.IOException;
import java.util.logging.*;

public class Log {


    private static Logger logger;

    private static Logger setConsoleLogger(String clsName){
        logger = Logger.getLogger(clsName);

        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel( Level.ALL );
        logger.addHandler( systemOut );
        logger.setLevel( Level.ALL );

        logger.setUseParentHandlers( false );

        return logger;
    }

    private Logger setFileLogger(String clsName, String logFileName) {
        logger = Logger.getLogger(clsName);

        try {
            FileHandler fileHandler = new FileHandler(logFileName,true);
            fileHandler.setLevel(Level.FINE);
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    public static Logger getLogger(String className) {
        return setConsoleLogger(className);
    }

}
