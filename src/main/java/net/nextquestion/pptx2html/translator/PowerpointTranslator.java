package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.parser.RELSParser;
import net.nextquestion.pptx2html.parser.SlideParser;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import sun.misc.Regexp;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    /**
     * Package-level only, for testing
     */
    PowerpointTranslator(File explodedPresentation) throws IOException {
        this.explodedPresentation = explodedPresentation;
        slideTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/Slide.tokens"));
        relsTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/RELS.tokens"));
    }

    public PowerpointTranslator(String presentationName) throws IOException {
        this(presentationName, new File("tmp/"));
    }

    public PowerpointTranslator(String presentationName, File tempDirectory) throws IOException {
        this(unzip(presentationName, tempDirectory));
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
            for (File slideFile : slideFiles) {
                // Extract relationships, extract the Slide, merge
                File relsFile = new File(relsFolder, slideFile.getName() + ".rels");
                relsTokenSource.useReader(new FileReader(relsFile));
                RELSParser relsParser = new RELSParser(new CommonTokenStream(relsTokenSource));
                Map<String, Relationship> relationships = relsParser.relationships();

                slideTokenSource.useReader(new FileReader(slideFile));
                SlideParser slideParser = new SlideParser(new CommonTokenStream(slideTokenSource));
                Slide slide = slideParser.slide(slideFile);
                slide.addRelationships(relationships);
                slides.add(slide);

            }
        }
        return slides;
    }

    public String buildSlideshow() throws XMLStreamException, RecognitionException, FileNotFoundException {
        STGroup group = new STGroupFile("src/main/resources/templates/slideshow.stg", '«', '»');
        ST st = group.getInstanceOf("s6");
        st.add("slides", getSlides());
        String result = st.render(); // yields "int x = 0;"
        return result;
    }

    protected TokenStream getTokenStream(String fileName, String tokenFileName) throws IOException, XMLStreamException {
        Reader tokenReader = new FileReader(tokenFileName);
        StaxTokenSource tokenSource = new StaxTokenSource(tokenReader);
        tokenSource.useReader(new FileReader(fileName));
        return new CommonTokenStream(tokenSource);
    }

    final private static int BUFFER = 2048;

    private static File unzip(String pptxName, File tempDirectory) throws FileNotFoundException {
        // TODO Use Java's temporary file facility
        if (!pptxName.toLowerCase().endsWith(".pptx"))
            throw new IllegalArgumentException("PPTX file required: " + pptxName);
        File zippedFile = new File(pptxName);
        if (!zippedFile.exists()) throw new FileNotFoundException(zippedFile.getName());
        String folderName = pptxName.substring(0, pptxName.length() - 5);

        File unzippedPresentation = new File(tempDirectory, folderName);
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zippedFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(new File(unzippedPresentation, entry.getName()));
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER))
                        != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unzippedPresentation;
    }


}
