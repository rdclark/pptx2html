package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.parser.RELSParser;
import net.nextquestion.pptx2html.parser.SlideParser;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Reads a Powerpoint file and extracts it into a HTML5 format slide show.
 */
public class PowerpointTranslator {


    final private File explodedPresentation;
    private List<Slide> slides;

    final private StaxTokenSource slideTokenSource;
    final private StaxTokenSource relsTokenSource;

    /**
     * Package-level only, for testing
     * @param explodedPresentation the unzipped PPTX file's container
     * @throws java.io.IOException for trouble with any of the XML files
     */
    PowerpointTranslator(File explodedPresentation) throws IOException {
        this.explodedPresentation = explodedPresentation;
        slideTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/Slide.tokens"));
        relsTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/RELS.tokens"));
    }

//    public PowerpointTranslator(String presentationName) throws IOException {
//        this(presentationName, new File("tmp/"));
//    }

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

    /**
     * Generates the HTML for the slideshow.  Package-level to facilitate testing (i.e. without copying
     * the rest of the files that make up a complete package.)
     * @return the HTML source for a slideshow.
     * @throws XMLStreamException   if there was a problem with any of the source files
     * @throws RecognitionException  is there was an ANTLR problem
     * @throws FileNotFoundException if any of the files is missing (very unlikely!)
     */
    String renderSlideshow() throws XMLStreamException, RecognitionException, FileNotFoundException {
        STGroup group = new STGroupFile("src/main/resources/templates/slideshow.stg", '«', '»');
        ST st = group.getInstanceOf("s6");
        st.add("slides", getSlides());
        return st.render();
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
            BufferedOutputStream dest;
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


    File packageSlideshow(File explodedPresentation, File outputDirectory) throws IOException, XMLStreamException, RecognitionException {
        if (explodedPresentation == null || !explodedPresentation.isDirectory()) throw new IllegalArgumentException("need exploded presentation");
        if (!outputDirectory.exists()) outputDirectory.mkdirs();

        File slideshowDir = new File(outputDirectory, explodedPresentation.getName());
        slideshowDir.mkdir();

        // Write the main slide file
        File slideFile = new File(slideshowDir, "index.html");
        String html = renderSlideshow();
        FileUtils.writeStringToFile(slideFile, html, "UTF-8");

        // TODO copy the base slideshow files
        File imagesDir = new File(slideshowDir, "images");
        FileUtils.copyDirectory(new File(explodedPresentation, "ppt/media"), imagesDir);

        return slideshowDir;
    }
}
