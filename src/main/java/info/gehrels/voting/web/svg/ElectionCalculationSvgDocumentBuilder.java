package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedElection;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public final class ElectionCalculationSvgDocumentBuilder {

    private final SVGDocument document;

    public ElectionCalculationSvgDocumentBuilder(GenderedElection election, boolean femaleExclusive) {
        DOMImplementation impl = new SVGDOMImplementation();
        document = (SVGDocument) impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGSVGElement root = document.getRootElement();
        Element text = document.createElement("text");
        text.setAttribute("x", "0");
        text.setAttribute("y", "20");
        text.setTextContent("Wahlergebnisermittlung " + election.getOfficeName() + (femaleExclusive ? " (Frauenplätze)" : "(offene Plätze)"));
        root.appendChild(text);
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
