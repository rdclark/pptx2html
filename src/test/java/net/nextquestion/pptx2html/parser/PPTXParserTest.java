package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.testng.annotations.Test;

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


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class PPTXParserTest {

    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?> \n";

//    @BeforeMethod
//    public void setup() {
//    }

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

    /**
     * @param fileName
     * @throws java.io.IOException
     * @throws javax.xml.stream.XMLStreamException
     *
     * @throws org.antlr.runtime.RecognitionException
     *
     */
    private Tree parse(String fileName) throws IOException, XMLStreamException, RecognitionException {
        Reader tokenReader = new FileReader("target/generated-sources/antlr3/PPTX.tokens");
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader, new FileReader(fileName));
        TokenStream tokens = new CommonTokenStream(tokenSource);
        PPTXParser parser = new PPTXParser(tokens);
        return (CommonTree) parser.slide().getTree();
    }


    /**
     * Helper functions for writing literate tests.
     *
     * @param source   the tree returned from the parser
     * @param nodeType the numeric node type from the parser (expressed as an uppercase constant)
     * @return the found node or nil.
     */
    public static Tree child(Tree source, int nodeType) {
        List<Tree> subtrees = new ArrayList<Tree>();
        extract(source, nodeType, false, subtrees);
        return subtrees.isEmpty() ? null : subtrees.get(0);
    }

    public static Tree descendant(Tree source, int nodeType) {
        List<Tree> subtrees = extract(source, nodeType, true, null);
        return subtrees.isEmpty() ? null : subtrees.get(0);
    }

    private List<Tree> children(Tree source, int nodeType) {
        return extract(source, nodeType, false, null);
    }

    private List<Tree> descendants(Tree source, int nodeType) {
        return extract(source, nodeType, true, null);
    }

    private static List<Tree> extract(Tree parent, int nodeType, boolean recursive, List<Tree> result) {
        if (result == null) result = new ArrayList<Tree>();
        int numChildren = parent.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            Tree child = parent.getChild(i);
            if (child.getType() == nodeType) {
                result.add(child);
            } else if (recursive && child.getChildCount() > 0) {
                extract(child, nodeType, recursive, result);
            }
        }
        return result;
    }

}
