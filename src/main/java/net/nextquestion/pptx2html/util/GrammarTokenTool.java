package net.nextquestion.pptx2html.util;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads a .g file, finds the token names (foo_START, foo_END, bar_ATTR) and updates the list of lexer tokens at the end.
 * (These tokens are only used for testing and to get ANTLRWOrks to stop complaining.)
 *
 * @author rdclark
 *         Created Mar 2, 2008 2:26:55 PM
 */

public class GrammarTokenTool {

    public static final Pattern LEXER_TOKEN = Pattern.compile("[A-Z0-9]+(_[A-Z0-9]+)+\\b");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: GrammarTokenTool <filename.g>");
            return;
        }
        String filename = args[0];
        try {
            processFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String filename) throws IOException {
        BufferedReader reader = null;
        Writer writer = null;
        File inFile = null;
        File outFile = null;
        Set<String> foundTokens = new HashSet<String>();
        try {
            inFile = new File(filename);
            reader = new BufferedReader(new FileReader(inFile));
            outFile = new File(filename + ".tmp");
            writer = new BufferedWriter(new FileWriter(outFile));

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("// Lexer definitions")) break;
                int i = line.indexOf("//");
                String subline = (i >= 0) ? line.substring(0, i) : line;
                Matcher m = LEXER_TOKEN.matcher(subline);
                while (m.find()) {
                    String token = line.substring(m.start(), m.end());
                    foundTokens.add(token);
                }
                writer.write(line);
                writer.write("\n");
            }
            // Done copying, write the definitions
            writer.write("// Lexer definitions for debugging (otherwise ignored)\n");
            List<String> tokens = new ArrayList<String>();
            tokens.addAll(foundTokens);
            tokens.add("TEXT");
            Collections.sort(tokens);
            for (String token : tokens) {
                writer.write(token);
                writer.write(" : '");
                if (token.endsWith("_END"))
                    writer.write('/');
                else if (token.endsWith("_ATTR"))
                    writer.write('_');
                int underscore = token.indexOf('_');
                if (underscore >= 0)
                    writer.write(token.substring(0, underscore));
                else
                    writer.write(token);
                writer.write("';\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            // Replace the input file
            inFile.delete();
            outFile.renameTo(inFile);
        }
    }
}
