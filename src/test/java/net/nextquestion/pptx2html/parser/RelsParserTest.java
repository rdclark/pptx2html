package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Relationship;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/12/11
 * Time: 23:19
 */
public class RelsParserTest extends ParserTestUtilities {

    private Map map;
    private Relationship relationship;

    @Before
    public void parseTestFile() throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream("src/test/resources/slide3.xml.rels", "target/generated-sources/antlr3/RELS.tokens");
        RELSParser parser = new RELSParser(tokens);
        map = parser.relationships();
        if (map != null && map.containsKey("rId1"))
            relationship = (Relationship) map.get("rId1");
    }

    @Test
    public void containsRelationshipElements() throws Exception {
        assertThat(map.isEmpty(), equalTo(false));
    }

    @Test
    public void relationshipHasContents() throws Exception {
        assertThat(relationship.getRelTarget(), equalTo("http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout"));
        assertThat(relationship.getRelType(), equalTo("../slideLayouts/slideLayout2.xml"));
        assertThat(relationship.getRelID(), equalTo("rId1"));
    }

}
