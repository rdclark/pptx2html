package net.nextquestion.pptx2html;

import net.nextquestion.pptx2html.translator.PowerpointTranslator;

import java.io.File;


/**
 * Test driver program
 *
 * @author Richard Clark (rdclark@nextquestion.net)
 */
class Main {

    /**
     * Just a simple test driver
     *
     * @param args file and/or directory names
     */

    public static void main(String[] args) {
        try {

            if (args.length > 0) {
                File outputDir = new File("slideshows");
                for (String filename : args) {
                    PowerpointTranslator.convertPresentation(new File(filename), outputDir);
                }
            } else {
                System.err.println("Usage: pass file names of presentations to convert");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
