package me.ccute.kvstore.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private static final Properties props = new Properties();

    // This static blocks runs once when the server boots up
    static {
        try (FileInputStream fis = new FileInputStream("conf/kvstore.conf")) {
            props.load(fis);
            logger.info("Loaded configuration from conf/kvstore.conf");
        } catch(IOException e) {
            logger.warn("Warning: conf/kvstore.conf not loaded. Using default values...");
        }
    }

    public static int getInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        return (val != null) ? Integer.parseInt(val) : defaultValue;
    }

    public static String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
