package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;

import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 5/17/11
 * Time: 17:57 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractParserTest {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?> \n";

    /**
     * Helper functions for writing literate tests.
     *
     * @param source   the tree returned from the parser
     * @param nodeType the numeric node type from the parser (expressed as an uppercase constant)
     * @return the found node or nil.
     */
    public static Tree child(Tree source, int nodeType) {
        List<Tree> subtrees = new ArrayList<Tree>();
        extract(source, nodeType, false, subtrees);
        return subtrees.isEmpty() ? null : subtrees.get(0);
    }

    public static Tree descendant(Tree source, int nodeType) {
        List<Tree> subtrees = extract(source, nodeType, true, null);
        return subtrees.isEmpty() ? null : subtrees.get(0);
    }

    private static List<Tree> extract(Tree parent, int nodeType, boolean recursive, List<Tree> result) {
        if (result == null) result = new ArrayList<Tree>();
        int numChildren = parent.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            Tree child = parent.getChild(i);
            if (child.getType() == nodeType) {
                result.add(child);
            } else if (recursive && child.getChildCount() > 0) {
                extract(child, nodeType, recursive, result);
            }
        }
        return result;
    }

    /**
     * @param fileName
     * @throws java.io.IOException
     * @throws javax.xml.stream.XMLStreamException
     *
     * @throws org.antlr.runtime.RecognitionException
     *
     */
    protected abstract Tree parse(String fileName) throws IOException, XMLStreamException, RecognitionException;

    protected List<Tree> children(Tree source, int nodeType) {
        return extract(source, nodeType, false, null);
    }

    protected List<Tree> descendants(Tree source, int nodeType) {
        return extract(source, nodeType, true, null);
    }

    protected TokenStream getTokenStream(String fileName, String tokenFileName) throws IOException, XMLStreamException {
        Reader tokenReader = new FileReader(tokenFileName);
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader, new FileReader(fileName));
        return new CommonTokenStream(tokenSource);
    }
}
