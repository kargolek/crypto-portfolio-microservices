package pl.kargolek.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * @author Karol Kuta-Orlowicz
 */
@Slf4j
public class PropertiesWriter {

    private final String path;
    private final String fileName;

    private File propertyFile;
    private Properties properties;

    public PropertiesWriter(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        propertyFile = new File(path, fileName);
        loadProperties();
    }

    public void writeProperty(String key, String value){
        this.properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(propertyFile.getPath())) {
            this.properties.store(output, "write property");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void loadProperties() {
        try {
            pathCreateIfNotExist();
            fileCreateIfNotExist();
            this.properties = new Properties();
            this.properties.load(new FileReader(propertyFile.getPath()));
        } catch (IOException e) {
            log.error("Unable to load properties file");
        }
    }

    private void pathCreateIfNotExist() {
        var pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }
    }

    private void fileCreateIfNotExist() throws IOException {
        var pathDir = new File(path, fileName);
        if (!pathDir.exists()) {
            pathDir.createNewFile();
        }
    }

}
