/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

import java.io.InputStream;
import java.util.Properties;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nicklas
 */
public class Settings {

    protected Properties properties;

    public Settings() {
        this.properties = new Properties();
    }

    public void put(Object key, Object value) {
        this.properties.put(key, value);
    }

    public String get(String key) {
        return this.properties.getProperty(key, "");
    }

    public void readFromFile(InputStream is) {
        try {
            SettingsXMLHandler handler = new SettingsXMLHandler(this);
            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            xmlR.setContentHandler(handler);
            xmlR.parse(new InputSource(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Properties getProperties() {
        return this.properties;
    }
}

class SettingsXMLHandler extends DefaultHandler {

    private Settings settings;
    private boolean isElement;
    private String propertyKey;
    private String propertyValue;
    private String elementValue;

    public SettingsXMLHandler(Settings settings) {
        super();
        this.settings = settings;
        this.isElement = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (localName.equalsIgnoreCase("key")) {
            isElement = true;
        } else if (localName.equalsIgnoreCase("value")) {
            isElement = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equalsIgnoreCase("key")) {
            this.propertyKey = elementValue;
        } else if (localName.equalsIgnoreCase("value")) {
            this.propertyValue = elementValue;
        } else if (localName.equalsIgnoreCase("property")) {
            this.settings.put(propertyKey, propertyValue);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (isElement) {
            elementValue = new String(ch, start, length);
            isElement = false;
        }
    }
}
