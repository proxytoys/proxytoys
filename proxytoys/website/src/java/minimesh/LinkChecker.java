package minimesh;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
 * Verifies all the links in a SiteMap.
 * 
 * @author Joe Walnes
 */
public class LinkChecker {

    private final Collection knownPageFileNames;
    private final SiteMap siteMap;
    private final Reporter reporter;

    /**
     * Callback for errors.
     */
    public static interface Reporter {
        void badLink(Page page, String link);
    }

    public LinkChecker(SiteMap siteMap, Reporter reporter) {
        this.siteMap = siteMap;
        this.reporter = reporter;
        knownPageFileNames = new HashSet();
        List allPages = siteMap.getAllPages();
        for (Iterator iterator = allPages.iterator(); iterator.hasNext();) {
            Page page = (Page)iterator.next();
            knownPageFileNames.add(page.getFilename());
        }
    }

    /**
     * Verifies all the links in the site. Returns true if all links are valid.
     * 
     * @return
     */
    public boolean verify() {
        boolean success = true;
        List allPages = siteMap.getAllPages();
        for (Iterator iterator = allPages.iterator(); iterator.hasNext();) {
            Page page = (Page)iterator.next();
            Collection links = page.getLinks();
            for (Iterator iterator1 = links.iterator(); iterator1.hasNext();) {
                String link = (String)iterator1.next();
                if (!verifyLink(link)) {
                    success = false;
                    reporter.badLink(page, link);
                }
            }
        }
        return success;
    }

    protected boolean verifyLink(String link) {
        if (link.startsWith("mailto:")) {
            // todo: valid email addresses should be configurable
            return true;
        } else if (link.startsWith("javascript:")) {
            return true;
        } else if (link.startsWith("http://")) {
            // todo: HTTP get this address to check it's valid (cache result)
            return true;
        } else if (link.startsWith("nntp://")) {
            // todo: News get this address to check it's valid (cache result)
            return true;
        } else if (link.startsWith(siteMap.getProperty("javadoc-location"))) {
            // todo: Check the class/package is valid
            return true;
        } else {
            int anchorIdx = link.lastIndexOf('#');
            if (anchorIdx >= 0) {
                // todo: Check anchors
                if (anchorIdx == 0) {
                    return true;
                }
                link = link.substring(0, link.lastIndexOf('#'));
            }
            if (knownPageFileNames.contains(link)) {
                return true;
            }
        }
        return false;
    }

}
