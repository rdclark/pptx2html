package net.nextquestion.pptx2html.model;

import org.antlr.runtime.Token;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 5/18/11
 * Time: 14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Relationship  {

    final private String relID;
    final private String relType;
    final private String relTarget;

    public Relationship(String relID, String relType, String relTarget) {
        this.relID = relID;
        this.relType = relType;
        this.relTarget = relTarget;
    }

    public Relationship(Token relID, Token relType, Token relTarget) {
        this(relID.getText(), relType.getText(), relTarget.getText());
    }

    public String getRelID() {
        return relID;
    }

    public String getRelType() {
        return relType;
    }

    public String getRelTarget() {
        return relTarget;
    }
}
