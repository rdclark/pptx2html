package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.translator.RelationshipExtractor;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Validates the relationships file parser against a standard set of slide files.
 */
public class RelsParserTest extends ParserTestUtilities {

    private Map map;
    private Relationship relationship;

    @Before
    public void parseTestFile() throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream("src/test/resources/slide3.xml.rels", "target/generated-sources/antlr4/RELS.tokens");
        RELSParser parser = new RELSParser(tokens);
        ParseTree tree = parser.relationships();
        RelationshipExtractor relationshipExtractor = new RelationshipExtractor();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(relationshipExtractor, tree);
        map = relationshipExtractor.getRelationshipMap();
        if (map != null && map.containsKey("rId1"))
            relationship = (Relationship) map.get("rId1");
    }

    @Test
    public void containsRelationshipElements() throws Exception {
        assertThat(map.isEmpty(), equalTo(false));
    }

    @Test
    public void relationshipHasContents() throws Exception {
        assertThat(relationship.getRelType(), equalTo("http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout"));
        assertThat(relationship.getRelTarget(), equalTo("../slideLayouts/slideLayout2.xml"));
        assertThat(relationship.getRelID(), equalTo("rId1"));
    }

}
