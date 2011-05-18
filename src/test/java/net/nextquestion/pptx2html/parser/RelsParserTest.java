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
import java.util.List;

import static net.nextquestion.pptx2html.parser.RELSParser.*;
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
public class RelsParserTest extends AbstractParserTest {

    private static final String TEST_FILE = "src/test/resources/slide3.xml.rels";

    @Test
    public void containsRelationshipElements() throws Exception {
        Tree tree = parse(TEST_FILE);
        assertThat(tree, hasChild(RELATIONSHIP));
    }

    @Test
    public void relationshipHasTarget() throws Exception {
        Tree tree = parse(TEST_FILE);
        assertThat(child(tree, RELATIONSHIP), hasChild(TARGET_ATTR));
    }

    @Test
    public void relationshipHasType() throws Exception {
        Tree tree = parse(TEST_FILE);
        assertThat(child(tree, RELATIONSHIP), hasChild(TYPE_ATTR));
    }

    @Test
    public void relationshipHasId() throws Exception {
        Tree tree = parse(TEST_FILE);
        assertThat(child(tree, RELATIONSHIP), hasChild(ID_ATTR));
    }

    @Override
    protected Tree parse(String fileName) throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream(fileName, "target/generated-sources/antlr3/RELS.tokens");
        RELSParser parser = new RELSParser(tokens);
        return (CommonTree) parser.relationships().getTree();
    }

}
