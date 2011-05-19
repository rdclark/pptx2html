package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.parser.RELSParser;
import net.nextquestion.pptx2html.parser.SlideParser;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import sun.misc.Regexp;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 5/19/11
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class PowerpointTranslator {

    final private File explodedPresentation;
    private List<Slide> slides;

    final private StaxTokenSource slideTokenSource;
    final private StaxTokenSource relsTokenSource;

    public PowerpointTranslator(File explodedPresentation) throws IOException {
        this.explodedPresentation = explodedPresentation;
        slideTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/Slide.tokens"));
        relsTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/RELS.tokens"));
    }

    public List<Slide> getSlides() throws FileNotFoundException, XMLStreamException, RecognitionException {
        if (slides == null) {
            slides = new ArrayList<Slide>();
            // Get a list of the slide files (in order)
            final Pattern namePattern = Pattern.compile("slide\\d+\\.xml");
            File slideFolder = new File(explodedPresentation, "ppt/slides");
            File[] slideFiles = slideFolder.listFiles(new FilenameFilter() {
                public boolean accept(File file, String name) {
                    return namePattern.matcher(name).matches();
                }
            });
            // parse relationships and slides
            File relsFolder = new File(slideFolder, "_rels");
            for (File slideFile: slideFiles) {
                // Extract relationships, extract the Slide, merge
                File relsFile = new File(relsFolder, slideFile.getName() + ".rels");
                relsTokenSource.useReader(new FileReader(relsFile));
                RELSParser relsParser = new RELSParser(new CommonTokenStream(relsTokenSource));
                Map<String, Relationship> relationships = relsParser.relationships();

                slideTokenSource.useReader(new FileReader(slideFile));
                SlideParser slideParser = new SlideParser(new CommonTokenStream(slideTokenSource));
                Slide slide = slideParser.slide();
                slide.addRelationships(relationships);
                slides.add(slide);

            }
        }
        return slides;
    }


    protected TokenStream getTokenStream(String fileName, String tokenFileName) throws IOException, XMLStreamException {
        Reader tokenReader = new FileReader(tokenFileName);
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader);
        tokenSource.useReader(new FileReader(fileName));
        return new CommonTokenStream(tokenSource);
    }

}
