package net.nextquestion.pptx2html;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.parser.SlideParser;
import net.nextquestion.pptx2html.translator.PowerpointTranslator;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


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
                File tempDir = new File("tmp/");
                for (String filename : args) {
                    PowerpointTranslator translator = new PowerpointTranslator(filename, tempDir);
                    // ask the translator to do its work
                }
            } else {
                System.err.println("Usage: java -jar pptx2html-1.0d1-jar-with-dependencies.jar <directory | filename.dmo>");
            }
        } catch (Exception ex) {
            System.err.println("ANTLR demo parser threw exception:");
            ex.printStackTrace();
        }
    }



}
