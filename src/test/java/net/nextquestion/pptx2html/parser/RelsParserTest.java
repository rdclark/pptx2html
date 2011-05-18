package net.nextquestion.pptx2html.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static net.nextquestion.pptx2html.parser.RELSParser.*;
import static org.antlr.hamcrest.HasTypedChild.hasChild;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class RelsParserTest extends AbstractParserTest {

    private Tree tree;

    @Before
    public void parseTestFile() throws IOException, XMLStreamException, RecognitionException {
        tree = parse("src/test/resources/slide3.xml.rels");
    }

    @Test
    public void containsRelationshipElements() throws Exception {
        assertThat(tree, hasChild(RELATIONSHIP));
    }

    @Test
    public void relationshipHasTarget() throws Exception {
        assertThat(child(tree, RELATIONSHIP), hasChild(TARGET_ATTR));
    }

    @Test
    public void relationshipHasType() throws Exception {
        assertThat(child(tree, RELATIONSHIP), hasChild(TYPE_ATTR));
    }

    @Test
    public void relationshipHasId() throws Exception {
        assertThat(child(tree, RELATIONSHIP), hasChild(ID_ATTR));
    }

    @Override
    protected Tree parse(String fileName) throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream(fileName, "target/generated-sources/antlr3/RELS.tokens");
        RELSParser parser = new RELSParser(tokens);
        return (CommonTree) parser.relationships().getTree();
    }

}
