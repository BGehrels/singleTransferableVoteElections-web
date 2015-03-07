package info.gehrels.voting.web.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public final class TextElement {
    private final Element element;

    public TextElement(SVGDocument svgDocument) {
        this.element = svgDocument.createElement("text");
        element.setAttribute("font-family", "sans-serif");
    }

    public TextElement withX(double x) {
        element.setAttribute("x", String.valueOf(x));
        return this;
    }

    public TextElement withY(double x) {
        element.setAttribute("y", String.valueOf(x));
        return this;
    }

    public TextElement withText(String text) {
        element.setTextContent(text);
        return this;
    }

    public TextElement withMiddleAnchor() {
        element.setAttribute("text-anchor", "middle");
        return this;
    }

    public TextElement withFontSize(double fontSize) {
        element.setAttribute("font-size", String.valueOf(fontSize));
        return this;
    }

    public Element build() {
        return element;
    }
}
