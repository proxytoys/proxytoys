package minimesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * Holds the structure of a website.
 * 
 * @author Joe Walnes
 */
public class SiteMap {

    private List sections = new ArrayList();
    private List pages = new ArrayList();
    private Properties properties = new Properties();

    public void addSection(Section section) {
        sections.add(section);
    }

    public void addPage(Page page) {
        pages.add(page);
    }
    
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public List getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List getAllPages() {
        List result = new ArrayList();
        for (Iterator i = sections.iterator(); i.hasNext();) {
            Section section = (Section)i.next();
            for (Iterator iterator = section.getPages().iterator(); iterator.hasNext();) {
                Object item = iterator.next();
                if (item instanceof Page) {
                    result.add(item);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    public String getProperty(String key) {
        return (String)properties.get(key);
    }
}
