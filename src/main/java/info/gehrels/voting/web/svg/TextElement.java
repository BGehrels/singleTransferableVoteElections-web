package info.gehrels.voting.web.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class TextElement {
    private double x;
    private double y;
    private String text;
    private Double fontSize;
    private boolean middleAnchor;

    public TextElement withX(double x) {
        this.x = x;
        return this;
    }

    public TextElement withY(double y) {
        this.y = y;
        return this;
    }

    public TextElement withText(String text) {
        this.text = text;
        return this;
    }

    public TextElement withMiddleAnchor() {
        this.middleAnchor=true;
        return this;
    }

    public TextElement withFontSize(double fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Element build(Document svgDocument) {
        Element element = svgDocument.createElement("text");
        element.setAttribute("x", String.valueOf(x));
        element.setAttribute("y", String.valueOf(y));
        element.setAttribute("font-family", "sans-serif");
        element.setTextContent(text);

        if (fontSize != null) {
            element.setAttribute("font-size", String.valueOf(fontSize));
        }

        if (middleAnchor) {
            element.setAttribute("text-anchor", "middle");
        }

        return element;
    }
}
