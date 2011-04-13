package net.nextquestion.pptx2html.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import net.nextquestion.pptx2html.parser.PPTXParser;
import net.nextquestion.pptx2html.adaptors.StaxTokenSource;

import static net.nextquestion.pptx2html.parser.PPTXParser.*;



/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class PPTXParserTest {

    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?> \n";

    private CommonTree parseTree;
    private List<Tree> subtrees;

    @BeforeMethod
    public void setup() {
        parseTree = null;
        subtrees = new LinkedList<Tree>();
    }

    @Test
    public void declarationSupportsBuiltinType() throws Exception {
        Tree tree = parse("src/test/resources/TitleSlide.xml");
        assertThat(tree.toStringTree(), is(equalTo("(SLIDE SLIDE_CONTAINER)")));
    }


    /**
     * Parses the named file and sticks the result in the <code>parseTree</code> global.
     * While this uses a global and has side effects (boo, hiss), it enables writing literate
     * tests, so the tradeoff is worth it.
     *
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
        parseTree = (CommonTree) parser.slide().getTree();
        return parseTree;
    }


    /**
     * Helper function for writing literate tests. Reads the result of the parser and sets the <code>rules</code>
     * global to a list of all the rule definitions found therein.
     *
     * @param nodeType
     * @return this, allowing one to chain method calls.
     */
    private PPTXParserTest nodeMatching(int nodeType) {
        if (subtrees.isEmpty()) {
            int numChildren = parseTree.getChildCount();
            for (int i = 0; i < numChildren; i++) {
                Tree child = parseTree.getChild(i);
                if (child.getType() == nodeType)
                    subtrees.add(child);
            }
        }
        return this;
    }

    private String toStringTree() {
        return subtrees.isEmpty() ? null : subtrees.get(0).toStringTree();
    }
}
