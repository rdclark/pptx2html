package net.nextquestion.pptx2html.adaptors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Pair;

import javax.xml.stream.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bridge between StAX and ANTLR 4. Original design by Kunle Odata for ANTLR 3, modified by Richard Clark.
 * Original inspiration at http://www.antlr.org/wiki/display/ANTLR3/Interfacing+StAX+to+ANTLR
 *
 * @author rdclark
 */
public class StaxTokenSource implements TokenSource {

    /* Use a XMLStreamReader for speed */
    protected XMLStreamReader reader;

    /* Creates the stream reader */
    final private XMLInputFactory factory;

    /* Used to look up the token type numbers for each tag and attribute */
    private Map<String, Integer> string2type = new HashMap<String, Integer>();

    /* Buffers multiple tokens (start, attributes..., stop) for a tag */
    private Queue<Token> tokens = new LinkedList<Token>();
    private CommonTokenFactory tokenFactory;


    public StaxTokenSource(Reader tokenDefinitionsReader) throws IOException {
        factory = XMLInputFactory.newInstance();
        initMapping(tokenDefinitionsReader);
        tokenFactory = new CommonTokenFactory();
    }

    /**
     * @deprecated  create the token source and then set the input file
     * @param tokenDefinitionsReader
     * @param xmlReader
     * @throws IOException
     * @throws XMLStreamException
     */
    public StaxTokenSource(Reader tokenDefinitionsReader, Reader xmlReader) throws IOException, XMLStreamException {
        this(tokenDefinitionsReader);
        useReader(xmlReader);
    }


    public void useReader(Reader xmlReader) throws XMLStreamException {
        this.reader = factory.createXMLStreamReader(xmlReader);
        tokens.clear();
    }

    public Token nextToken() {
        try {
            while (tokens.isEmpty()) {
                boolean ok = collectToken();
                if (!ok) break;
            }
        } catch (XMLStreamException e) {
            // do nothing, for now
        }
        if (tokens.isEmpty())
            return tokenFactory.create(Token.EOF, "");
        Token result = tokens.remove();
        return result;
    }

    public int getLine() {
        return 0;   // we don't track lines
    }

    public int getCharPositionInLine() {
        return -1;  // we don't track character offsets
    }

    public CharStream getInputStream() {
        return null; // no matching character stream
    }

    public String getSourceName() {
        return "STaX Token Source";
    }

    public void setTokenFactory(@NotNull TokenFactory<?> tokenFactory) {
        this.tokenFactory = (CommonTokenFactory) tokenFactory;
    }

    public TokenFactory<?> getTokenFactory() {
        return tokenFactory;
    }

    /**
     * Gets the next tag or text item (if any) and append one or more tokens to the buffer.
     *
     * @return false if at the end of the stream
     * @throws XMLStreamException for pqrsing failures
     */
    public boolean collectToken() throws XMLStreamException {
        if (!reader.hasNext()) return false;
        int eventType = reader.next();
        String tag;
        switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
                tag = reader.getLocalName();
                tokens.add(makeElementToken(reader, tag, "_START", "<" + tag));
                // Collect attributes, sort, add to queue
                int numAttrs = reader.getAttributeCount();
                // Attribute tokens don't carry their names around, so I need to link them for sorting
                List<NamedToken> taggedAttributes = new ArrayList<NamedToken>(numAttrs);
                for (int i = 0; i < numAttrs; i++) {
                    tag = reader.getAttributeLocalName(i);
                    String attrName = tag.toUpperCase() + "_ATTR";
                    taggedAttributes.add(new NamedToken(attrName, makeToken(reader, attrName, reader.getAttributeValue(i))));
                }
                Collections.sort(taggedAttributes);

                for (NamedToken entry : taggedAttributes) {
                    tokens.add(entry.getToken());
                }
                break;

            case XMLStreamConstants.END_ELEMENT:
                tag = reader.getLocalName();
                tokens.add(makeElementToken(reader, tag, "_END", "/" + tag));
                break;

            case XMLStreamConstants.END_DOCUMENT:
                reader.close();
                return false;

            case XMLStreamConstants.CDATA:
                tokens.add(makeToken(reader, "TEXT", reader.getText()));
                break;

            case XMLStreamConstants.CHARACTERS:
                String text = reader.getText();
                // TODO gate special processing on whether whitespace recognition is enabled
                // Locate leading runs of spaces before non-spaces
                Matcher m = Pattern.compile("(\\s*)(\\S*(?:[ \\t]+\\S+)*)").matcher(text);
                while (m.find()) {
                    String spaces = m.group(1);
                    if (spaces.length() > 0)
                        tokens.add(makeToken(reader, "TEXT", spaces, Token.HIDDEN_CHANNEL));
                    String body = m.group(2);
                    if (body.length() > 0)
                        tokens.add(makeToken(reader, "TEXT", body));
                }
                break;
//
//    XMLStreamConstants.COMMENT:
//        break;
//
//    XMLStreamConstants.DTD:
//        break;
//
//    XMLStreamConstants.ENTITY_DECLARATION
//    XMLStreamConstants.ENTITY_REFERENCE
//    XMLStreamConstants.NAMESPACE
//    XMLStreamConstants.NOTATION_DECLARATION
//    XMLStreamConstants.PROCESSING_INSTRUCTION
//    XMLStreamConstants.SPACE
//    XMLStreamConstants.START_DOCUMENT
        }
        return true;
    }

    private Token makeToken(XMLStreamReader reader, String tokenName, String text) {
        return makeToken(reader, tokenName, text, Token.DEFAULT_CHANNEL);
    }


    private Token makeToken(XMLStreamReader reader, String tokenName, String text, int channel) {
        CommonToken token;
        final Pair<TokenSource, CharStream> source = new Pair<TokenSource, CharStream>(this, null);
        final Location loc = reader.getLocation();
        final int line = loc.getLineNumber();
        final int col = loc.getColumnNumber();
        if (string2type.containsKey(tokenName)) {
            return tokenFactory.create(source, string2type.get(tokenName), text, channel, -1, -1, line, col);
        } else {
            // it's unknown, so create an "unknown" token and hide it from the parser
            return tokenFactory.create(source, Token.INVALID_TYPE, text, Token.HIDDEN_CHANNEL, -1, -1, line, col);
        }
    }

    private Token makeElementToken(XMLStreamReader reader, String tag, String suffix, String text) {
        String tokenName = tag.toUpperCase() + suffix;
        return makeToken(reader, tokenName, text);
    }

    /**
     * Parses an ANTLR tokens file to map the token names with their numbers.
     *
     * @param tokenDefinition a stream connected to a token file.
     * @throws IOException if there's a problem reading the definitions.
     */
    public void initMapping(Reader tokenDefinition) throws IOException {
        BufferedReader reader = new BufferedReader(tokenDefinition);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            String tokenName = parts[0];
            String tokenType = parts[1];

            Integer iType = new Integer(tokenType);
            string2type.put(tokenName, iType);
        }
    }


    private class NamedToken implements Comparable<NamedToken> {
        private String name;
        private Token token;

        public NamedToken(String name, Token token) {
            this.name = name;
            this.token = token;
        }

        public Token getToken() {
            return token;
        }

        public int compareTo(NamedToken other) {
            return name.compareTo(other.name);
        }
    }

}
