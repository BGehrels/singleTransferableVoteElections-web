package info.gehrels.voting.web.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public final class ElectionCalculationSvgDocumentBuilder {

    private boolean femaleExclusive;
    private final Document document;

    public ElectionCalculationSvgDocumentBuilder(boolean femaleExclusive) {
        this.femaleExclusive = femaleExclusive;

        DOMImplementation impl = new SVGDOMImplementation();
        document = impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
    }

    public String build() {
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
        return writer.toString();
    }
}
