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
public abstract class ParserTestUtilities {

    protected TokenStream getTokenStream(String fileName, String tokenFileName) throws IOException, XMLStreamException {
        Reader tokenReader = new FileReader(tokenFileName);
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader);
        tokenSource.useReader(new FileReader(fileName));
        return new CommonTokenStream(tokenSource);
    }
}
