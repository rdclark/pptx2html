package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static net.nextquestion.pptx2html.parser.PPTXParser.*;
import static org.antlr.hamcrest.HasTypedChild.hasChild;
import static org.antlr.hamcrest.HasTypedDescendant.hasDescendant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class PPTXParserTest extends AbstractParserTest {

    @Test
    public void slideHasSlideContainer() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(tree, hasChild(SLIDE_CONTAINER));
    }

    @Test
    public void slideContainerHasShapeTree() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(child(tree, SLIDE_CONTAINER), hasChild(SHAPE_TREE));
    }

    @Test
    public void shapeTreeContainsShapes() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(children(descendant(tree, SHAPE_TREE), SHAPE).size(), equalTo(2));
    }

    @Test
    public void shapesCanHaveAType() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(descendant(tree, SHAPE), hasChild(PLACEHOLDER_TYPE));
        assertThat(child(descendant(tree, PLACEHOLDER_TYPE), TYPE_ATTR).getText(), equalTo("ctrTitle"));
    }

    @Test
    public void shapesCanContainText() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(descendant(tree, SHAPE), hasDescendant(TEXT_BODY));
        assertThat(descendant(tree, TEXT_BODY), hasDescendant(PARAGRAPH));
    }

    @Test
    public void paragraphsCanContainTextRuns() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        List<Tree> shapes = descendants(tree, SHAPE);
        Tree paragraph = descendant(shapes.get(0), PARAGRAPH);
        assertThat(paragraph, hasDescendant(TEXT_RUN));
    }

    @Test
    public void textRunBodyContainsText() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        List<Tree> shapes = descendants(tree, SHAPE);
        Tree paragraph = descendant(shapes.get(0), PARAGRAPH);
        List<Tree> textRuns = descendants(paragraph, TEXT_RUN);
        assertThat(textRuns.size(), greaterThan((Integer) 0));
        assertThat(textRuns.get(0).getChild(0).getText(), equalTo("Test Slide Title"));
    }

    @Test
    public void imageSlideContainsPictureElement() throws Exception {
        Tree tree = parse("src/test/resources/ImageBodySlide.xml");
        assertThat(tree, hasDescendant(PICTURE));
    }

    @Override
    protected Tree parse(String fileName) throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream(fileName, "target/generated-sources/antlr3/PPTX.tokens");
        PPTXParser parser = new PPTXParser(tokens);
        return (CommonTree) parser.slide().getTree();
    }


}
