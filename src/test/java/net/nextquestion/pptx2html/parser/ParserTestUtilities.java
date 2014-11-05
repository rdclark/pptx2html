package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Base for the various parser tests.
 */
public abstract class ParserTestUtilities {

    protected TokenStream getTokenStream(String fileName, String tokenFileName) throws IOException, XMLStreamException {
        Reader tokenReader = new FileReader(tokenFileName);
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader);
        tokenSource.useReader(new FileReader(fileName));
        return new CommonTokenStream(tokenSource, Token.DEFAULT_CHANNEL);
    }
}
