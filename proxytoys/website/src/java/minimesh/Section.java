package minimesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A section in a website, holding pages.
 * 
 * @author Joe Walnes
 */
public class Section {

    private String name;
    private List pages = new ArrayList();

    public Section(String name) {
        this.name = name;
    }
    
    // XSteam with JDK 1.3 or non-Sun JDK
    protected Section() {
    }

    public String getName() {
        return name;
    }

    public List getPages() {
        return Collections.unmodifiableList(pages);
    }

    public void addPage(Page page) {
        pages.add(page);
    }
}
