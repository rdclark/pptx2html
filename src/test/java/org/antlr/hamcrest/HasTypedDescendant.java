package org.antlr.hamcrest;

import org.antlr.runtime.tree.Tree;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;


/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 4/23/11
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class HasTypedDescendant<T extends Tree> extends BaseMatcher<Tree> {
    private final int nodeType;

    public HasTypedDescendant(int nodeType) {
        this.nodeType = nodeType;
    }

    public boolean matches(Object item) {
        return hasDescendant((Tree) item, nodeType);
    }

    private boolean hasDescendant(Tree parent, int nodeType) {
        if (parent == null) return false;
        int numChildren = parent.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            Tree child = parent.getChild(i);
            if (child.getType() == nodeType) {
                return true;
            } else if (child.getChildCount() > 0) {
                if (hasDescendant(child, nodeType))
                    return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendValue(nodeType);
    }

    /**
     * Does the tree contain a node of the specified type?
     */
    @Factory
    public static <T extends Tree> Matcher<Tree> hasDescendant(int nodeType) {
        return new HasTypedDescendant<Tree>(nodeType);
    }

}
