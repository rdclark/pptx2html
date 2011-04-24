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
        assertThat(descendant(tree, SLIDE_CONTAINER), hasChild(SHAPE_TREE));
    }

    @Test
    public void shapeTreeContainsShapes() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(descendants(descendant(tree, SHAPE_TREE), SHAPE).size(), equalTo(2));
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
        List<Tree> subtrees = new ArrayList<Tree>();
        extract(source, nodeType, true, subtrees);
        return subtrees.isEmpty() ? null : subtrees.get(0);
    }

    private List<Tree> children(Tree source, int nodeType) {
        List<Tree> subtrees = new ArrayList<Tree>();
        extract(source, nodeType, false, subtrees);
        return subtrees;
    }

    private List<Tree> descendants(Tree source, int nodeType) {
        List<Tree> subtrees = new ArrayList<Tree>();
        extract(source, nodeType, true, subtrees);
        return subtrees;
    }

    private static List<Tree> extract(Tree parent, int nodeType, boolean recursive, List<Tree> result) {
        int numChildren = parent.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            Tree child = parent.getChild(i);
            if (child.getType() == nodeType) {
                result.add(child);
            } else if (recursive && child.getChildCount() > 0) {
                extract(child, nodeType, false, result);
            }
        }
        return result;
    }

}
