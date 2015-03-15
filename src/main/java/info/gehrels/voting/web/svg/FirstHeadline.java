package info.gehrels.voting.web.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

class FirstHeadline {
    private final String officeName;

    FirstHeadline(String officeName) {
        this.officeName = officeName;
    }

    public Element build(SVGDocument document, double baseX, double baseY) {
        return new TextElement().withX(baseX).withY((baseY + 20)).withText(officeName).build(document);
    }
}
