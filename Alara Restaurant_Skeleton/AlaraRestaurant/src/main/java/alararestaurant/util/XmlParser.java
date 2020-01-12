package alararestaurant.util;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public interface XmlParser {
    public <O> O parseXml(Class<O> objectClass, String path) throws FileNotFoundException, JAXBException;
}
