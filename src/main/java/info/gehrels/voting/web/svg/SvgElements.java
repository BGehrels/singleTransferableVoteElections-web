package info.gehrels.voting.web.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public final class SvgElements {
    public static Element createTextElement(SVGDocument document, int baseX, int baseY, String textContent) {
        Element text = document.createElement("text");
        text.setAttribute("x", String.valueOf(baseX));
        text.setAttribute("y", String.valueOf(baseY + 20));
        text.setTextContent(textContent);
        return text;
    }
}
