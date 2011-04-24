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
public class HasTypedChild<T extends org.antlr.runtime.tree.Tree> extends BaseMatcher<Tree> {
    private final int nodeType;

    public HasTypedChild(int nodeType) {
        this.nodeType = nodeType;
    }

    public boolean matches(Object item) {
        if (item == null) return false;
        int numChildren = ((Tree) item).getChildCount();
        for (int i = 0; i < numChildren; i++) {
            Tree child = ((Tree) item).getChild(i);
            if (child.getType() == nodeType) {
                return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendValue(nodeType);
    }

    /**
     * Does the tree contain a top-level node of the specified type?
     */
    @Factory
    public static <T extends org.antlr.runtime.tree.Tree> Matcher<Tree> hasChild(int nodeType) {
        return new HasTypedChild<Tree>(nodeType);
    }

}
