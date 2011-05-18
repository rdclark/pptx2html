package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Relationship;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Map;

import static net.nextquestion.pptx2html.parser.RELSParser.*;
import static org.antlr.hamcrest.HasTypedChild.hasChild;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class RelsParserTest extends AbstractParserTest {

    private Map map;
    private Relationship relationship;

    @Before
    public void parseTestFile() throws IOException, XMLStreamException, RecognitionException {
        map = parseRelationships("src/test/resources/slide3.xml.rels");
        if (map != null && map.containsKey("rId1"))
            relationship = (Relationship) map.get("rId1");
    }

    @Test
    public void containsRelationshipElements() throws Exception {
        assertThat(map.isEmpty(), equalTo(false));
    }

    @Test
    public void relationshipHasContents() throws Exception {
        assertThat(relationship.getRelTarget(), equalTo("foo"));
        assertThat(relationship.getRelType(), equalTo("foo"));
        assertThat(relationship.getRelID(), equalTo("foo"));
    }

    protected Map parseRelationships(String fileName) throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream(fileName, "target/generated-sources/antlr3/RELS.tokens");
        RELSParser parser = new RELSParser(tokens);
        return parser.relationships();
    }

}
