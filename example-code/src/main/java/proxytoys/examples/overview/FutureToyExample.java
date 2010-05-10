/*
 * (c) 2009, 2010 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13-Oct-2009
 */
package proxytoys.examples.overview;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.future.Future;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


/**
 * @author Paul Hammant
 * @author Joerg Schaible
 */
public class FutureToyExample {

    public static void packageOverviewExample1() throws InterruptedException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = Future.proxy(DocumentBuilderFactory.newInstance().newDocumentBuilder())
                .build(new CglibProxyFactory());
        Document document = documentBuilder.parse(new SlowInputSource(new StringReader("<root/>")));
        System.out.println("Root document name: " + document.getDocumentElement().getNodeName());
        Thread.sleep(200); // should do something more useful here
        System.out.println("Root document name: " + document.getDocumentElement().getNodeName());
    }


    public static void main(String[] args) throws InterruptedException, ParserConfigurationException, SAXException, IOException {
        System.out.println();
        System.out.println();
        System.out.println("Running Future Toy Example");
        System.out.println();
        System.out.println("Example 1 of Package Overview:");
        packageOverviewExample1();
    }

    private static class SlowInputSource extends InputSource {
        public SlowInputSource(Reader characterStream) {
            super(characterStream);
        }

        @Override
        public Reader getCharacterStream() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            return super.getCharacterStream();
        }

    }
}