package com.javalite.index;

import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
import org.javalite.test.XPathHelper;

/**
 * @author igor on 11/26/17.
 */
public class HtmlConvertService {
    public HtmlConvertService() {
    }

    public String convertSectionsXML(String value) throws IOException {
        XPathHelper helper = new XPathHelper(value);
        int sectionCount = helper.count("/sections/section");
        StringBuilder sb = new StringBuilder();

        for(int i = 1; i <= sectionCount; ++i) {
            String section = helper.selectText("/sections/section[" + i + "]");
            sb.append(completelyStripHTML(section));
        }

        return sb.toString();
    }

    public static String completelyStripHTML(String html) throws IOException {
        final StringBuilder sb = new StringBuilder();
        ParserDelegator parserDelegator = new ParserDelegator();
        ParserCallback parserCallback = new ParserCallback() {
            public void handleText(char[] data, int pos) {
                sb.append(new String(data));
            }

            public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) {
            }

            public void handleEndTag(Tag t, int pos) {
            }

            public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            }

            public void handleComment(char[] data, int pos) {
            }

            public void handleError(String errMsg, int pos) {
            }
        };
        parserDelegator.parse(new StringReader(html), parserCallback, true);
        return sb.toString();
    }

    public String completelyStripHTMLandPRE(String html) throws IOException {
        final StringBuilder sb = new StringBuilder();
        ParserDelegator parserDelegator = new ParserDelegator();
        ParserCallback parserCallback = new ParserCallback() {
            private boolean code = false;

            public void handleText(char[] data, int pos) {
                if(!this.code) {
                    sb.append(new String(data));
                }

            }

            public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) {
                if(tag.isPreformatted()) {
                    this.code = true;
                }

            }

            public void handleEndTag(Tag t, int pos) {
                if(t.isPreformatted()) {
                    this.code = false;
                }

            }

            public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            }

            public void handleComment(char[] data, int pos) {
            }

            public void handleError(String errMsg, int pos) {
            }
        };
        parserDelegator.parse(new StringReader(html), parserCallback, true);
        return sb.toString();
    }
}
