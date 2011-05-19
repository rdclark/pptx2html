package net.nextquestion.pptx2html;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.parser.SlideParser;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;


/**
 * Test driver program
 *
 * @author Richard Clark (rdclark@nextquestion.net)
 */
class Main {

    /**
     * Just a simple test driver for the ASP parser
     * to show how to call it.
     *
     * @param args file and/or directory names
     */

    public static void main(String[] args) {
        try {

            if (args.length > 0) {
                // Recursively parse each directory, and each file on the
                // command line
                //
                for (String filename : args) {
                    parse(new File(filename));
                }
            } else {
                System.err.println("Usage: java -jar pptx2html-1.0d1-jar-with-dependencies.jar <directory | filename.dmo>");
            }
        } catch (Exception ex) {
            System.err.println("ANTLR demo parser threw exception:");
            ex.printStackTrace();
        }
    }

    public static void parse(File source) throws Exception {

        // Open the supplied file or directory
        //
        try {

            // From here, any exceptions are just thrown back up the chain
            //
            if (source.isDirectory()) {
                System.out.println("Directory: " + source.getAbsolutePath());
                String files[] = source.list();

                for (String name : files) {
                    parse(new File(source, name));
                }
            }

            // Else find out if it is an ASP.Net file and parse it if it is
            //
            else {
                // File without paths etc
                //
                String sourceFile = source.getName();

                if (sourceFile.length() > 3) {
                    String suffix = sourceFile.substring(sourceFile.length() - 4).toLowerCase();

                    // Ensure that this is a DEMO script (or seemingly)
                    //
                    if (suffix.compareTo(".dmo") == 0) {
                        parseSource(source.getAbsolutePath());
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("ANTLR demo parser caught error on file open:");
            ex.printStackTrace();
        }

    }

    public static void parseSource(String source) throws Exception {
        // Parse an ANTLR demo file
        //
        try {
            Reader tokenReader = new FileReader(source);
            StaxTokenSource tokenSource = new StaxTokenSource(tokenReader, new FileReader(source));
            TokenStream tokens = new CommonTokenStream(tokenSource);
            SlideParser parser = new SlideParser(tokens);

            System.out.println("file: " + source);

            // Provide some user feedback
            //
            System.out.println("    Parser Start");
            long pStart = System.currentTimeMillis();
            parser.slide();
            long stop = System.currentTimeMillis();
            System.out.println("      Parsed in " + (stop - pStart) + "ms.");

        } catch (Exception ex) {

        }
    }

}
