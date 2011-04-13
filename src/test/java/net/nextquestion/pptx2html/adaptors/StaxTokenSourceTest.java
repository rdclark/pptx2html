package net.nextquestion.pptx2html.adaptors;

import org.antlr.runtime.Token;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 22:10
 */
@Test
public class StaxTokenSourceTest {

    private static final String TOKEN_DEFINITIONS = "TEXT=1\nA_START=2\nA_END=3\nHREF_ATTR=4\nTARGET_ATTR=5\n";

    @Test
    public void singleTag() throws IOException, XMLStreamException {
        StaxTokenSource tokenSource = new StaxTokenSource(new StringReader(TOKEN_DEFINITIONS), new StringReader("<a/>"));
        Token start = tokenSource.nextToken();
        assert start != null : "null start token";
        assert start.getType() == 2 : "Expected token type 2, found " + start.getType();
        assert "<a".equals(start.getText()) : "Expected <a, found " + start.getText();
        assert start.getLine() == 1;

        Token end = tokenSource.nextToken();
        assert end != null : "null end token";
        assert end.getType() == 3 : "Expected token type 3, found " + end.getType();
        assert "/a".equals(end.getText()) : "Expected <a, found " + end.getText();
        assert end.getLine() == 1;
    }

    @Test
    public void singleAttribute() throws IOException, XMLStreamException {
        StaxTokenSource tokenSource = new StaxTokenSource(new StringReader(TOKEN_DEFINITIONS), new StringReader("<a href=\"http://www.nextquestion.net\"/>"));
        Token start = tokenSource.nextToken();
        assert start != null : "null start token";
        assert start.getType() == 2 : "Expected token type 2, found " + start.getType();
        assert "<a".equals(start.getText()) : "Expected <a, found " + start.getText();
        assert start.getLine() == 1;

        Token attr = tokenSource.nextToken();
        assert attr != null : "null attribute token";
        assert attr.getType() == 4 : "Expected token type 4, found " + attr.getType();
        assert "http://www.nextquestion.net".equals(attr.getText()) : "Expected URL, found " + attr.getText();
        assert attr.getLine() == 1;

        Token end = tokenSource.nextToken();
        assert end != null : "null end token";
        assert end.getType() == 3 : "Expected token type 3, found " + end.getType();
        assert "/a".equals(end.getText()) : "Expected <a, found " + end.getText();
        assert end.getLine() == 1;
    }

    @Test
    public void sortedAttributes() throws IOException, XMLStreamException {
        StaxTokenSource tokenSource = new StaxTokenSource(new StringReader(TOKEN_DEFINITIONS), new StringReader("<a target=\"_blank\" href=\"http://www.nextquestion.net\"/>"));
        Token start = tokenSource.nextToken();
        assert start != null : "null start token";
        assert start.getType() == 2 : "Expected token type 2, found " + start.getType();
        assert "<a".equals(start.getText()) : "Expected <a, found " + start.getText();
        assert start.getLine() == 1;

        Token href = tokenSource.nextToken();
        assert href != null : "null attribute token";
        assert href.getType() == 4 : "Expected token type 4, found " + href.getType();
        assert "http://www.nextquestion.net".equals(href.getText()) : "Expected URL, found " + href.getText();
        assert href.getLine() == 1;

        Token target = tokenSource.nextToken();
        assert target != null : "null attribute token";
        assert target.getType() == 5 : "Expected token type 5, found " + target.getType();
        assert "_blank".equals(target.getText()) : "Expected URL, found " + target.getText();
        assert target.getLine() == 1;

        Token end = tokenSource.nextToken();
        assert end != null : "null end token";
        assert end.getType() == 3 : "Expected token type 3, found " + end.getType();
        assert "/a".equals(end.getText()) : "Expected <a, found " + end.getText();
        assert end.getLine() == 1;
    }

    @Test
    public void text() throws IOException, XMLStreamException {
        StaxTokenSource tokenSource = new StaxTokenSource(new StringReader(TOKEN_DEFINITIONS), new StringReader("<a>This is a test</a>"));
        Token start = tokenSource.nextToken();
        assert start != null : "null start token";
        assert start.getType() == 2 : "Expected token type 2, found " + start.getType();
        assert "<a".equals(start.getText()) : "Expected <a, found " + start.getText();
        assert start.getLine() == 1;

        Token text = tokenSource.nextToken();
        assert text != null : "null text token";
        assert text.getType() == 1;
        assert "This is a test".equals(text.getText());

        Token end = tokenSource.nextToken();
        assert end != null : "null end token";
        assert end.getType() == 3 : "Expected token type 3, found " + end.getType();
        assert "/a".equals(end.getText()) : "Expected <a, found " + end.getText();
        assert end.getLine() == 1;
    }

}
