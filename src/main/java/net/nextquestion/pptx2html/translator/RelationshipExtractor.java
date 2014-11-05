package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.parser.RELSBaseListener;
import net.nextquestion.pptx2html.parser.RELSParser;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;

/**
 * Extracts the action code from the ANTLR 3 parser into a listener for the tree walker.
 */
public class RelationshipExtractor extends RELSBaseListener {

    private HashMap<String, Relationship> relationshipMap;

    public HashMap<String, Relationship> getRelationshipMap() {
        return relationshipMap;
    }

    @Override
    public void enterRelationships(@NotNull RELSParser.RelationshipsContext ctx) {
        relationshipMap = new HashMap<String, Relationship>();
    }

    @Override
    public void exitRelationship(@NotNull RELSParser.RelationshipContext ctx) {
        final String relID = ctx.id.getText();
        Relationship relationship = new Relationship(relID, ctx.rtype.getText(), ctx.target.getText());
        relationshipMap.put(relID, relationship);
    }

}
