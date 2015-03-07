package info.gehrels.voting.web.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

class FirstHeadline {
    private final String officeName;

    public FirstHeadline(String officeName) {
        this.officeName = officeName;
    }

    public Element build(SVGDocument document, int baseX, int baseY) {
        return new TextElement(document).withX((double) baseX).withY((double) (baseY + 20)).withText(officeName).build();
    }
}
