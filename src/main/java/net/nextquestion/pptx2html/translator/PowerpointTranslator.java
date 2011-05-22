package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.adaptors.StaxTokenSource;
import net.nextquestion.pptx2html.model.Relationship;
import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.model.Slideshow;
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
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Reads a Powerpoint file and extracts it into a HTML5 format slide show.
 */
public class PowerpointTranslator {


    private File explodedPresentation;
    private Slideshow slideshow;

    final private StaxTokenSource slideTokenSource;
    final private StaxTokenSource relsTokenSource;

    static public File convertPresentation(File presentation, File outputDirectory) throws IOException, XMLStreamException, RecognitionException {
        if (!presentation.isFile()) throw new IllegalArgumentException("Not a file: " + presentation);
        if (!outputDirectory.isDirectory()) FileUtils.forceMkdir(outputDirectory);
        File tempDir = FileUtils.getTempDirectory();
        PowerpointTranslator translator = new PowerpointTranslator(presentation, tempDir);
        File slideshow = translator.packageSlideshow(outputDirectory);
        translator.cleanupTempFiles();
        return slideshow;
    }

    void cleanupTempFiles() throws IOException {
        FileUtils.deleteDirectory(explodedPresentation);
    }

    /**
     * Package-level only, for testing
     *
     * @param explodedPresentation the unzipped PPTX file's container
     * @throws java.io.IOException for trouble with any of the XML files
     */
    PowerpointTranslator(File explodedPresentation) throws IOException {
        this.explodedPresentation = explodedPresentation;
        slideTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/Slide.tokens"));
        relsTokenSource = new StaxTokenSource(new FileReader("target/generated-sources/antlr3/RELS.tokens"));
    }

    public PowerpointTranslator(File presentation, File tempDirectory) throws IOException {
        this(unzip(presentation, tempDirectory));
//        if (explodedPresentation != null && explodedPresentation.isDirectory())
//            FileUtils.deleteDirectory(explodedPresentation);
    }


    public Slideshow getSlideshow() throws FileNotFoundException, XMLStreamException, RecognitionException {
        if (slideshow == null) {
            slideshow = new Slideshow();
            File slideFolder = new File(explodedPresentation, "ppt/slides");
            File relsFolder = new File(slideFolder, "_rels");

            int slideNum = 1;
            for (; ; ) {
                File slideFile = new File(slideFolder, "slide" + slideNum + ".xml");
                if (!slideFile.exists()) break;
                System.err.println("Processing " + slideFile.getName());
                // Extract relationships, extract the Slide, merge
                File relsFile = new File(relsFolder, slideFile.getName() + ".rels");
                relsTokenSource.useReader(new FileReader(relsFile));
                RELSParser relsParser = new RELSParser(new CommonTokenStream(relsTokenSource));
                Map<String, Relationship> relationships = relsParser.relationships();

                slideTokenSource.useReader(new FileReader(slideFile));
                SlideParser slideParser = new SlideParser(new CommonTokenStream(slideTokenSource));
                Slide slide = new Slide(slideFile);
                slideshow.add(slide);
                slideParser.slide(slide);
                slide.addRelationships(relationships);
                slideNum++;
            }

        }
        return slideshow;
    }

    public List<Slide> getSlides() throws FileNotFoundException, XMLStreamException, RecognitionException {
        return getSlideshow().getSlides();
    }

    /**
     * Generates the HTML for the slideshow.  Package-level to facilitate testing (i.e. without copying
     * the rest of the files that make up a complete package.)
     *
     * @return the HTML source for a slideshow.
     * @throws XMLStreamException    if there was a problem with any of the source files
     * @throws RecognitionException  is there was an ANTLR problem
     * @throws FileNotFoundException if any of the files is missing (very unlikely!)
     */
    String renderSlideshow() throws XMLStreamException, RecognitionException, FileNotFoundException {
        STGroup group = new STGroupFile("src/main/resources/templates/slideshow.stg", '«', '»');
        ST st = group.getInstanceOf("s6");
        st.add("slideshow", getSlideshow());
        return st.render();
    }

    final private static int BUFFER = 2048;

    private static File unzip(File zippedFile, File tempDirectory) throws IOException {
        String pptxName = zippedFile.getName();
        if (!zippedFile.exists()) throw new FileNotFoundException(pptxName);
        if (!pptxName.toLowerCase().endsWith(".pptx"))
            throw new IllegalArgumentException("PPTX file required: " + pptxName);
        File unzippedPresentation = new File(tempDirectory, pptxName.substring(0, pptxName.length() - 5));
        FileUtils.forceMkdir(unzippedPresentation);
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
                String entryName = entry.getName();
                File file = new File(unzippedPresentation, entryName);
                if (!file.getParentFile().isDirectory()) FileUtils.forceMkdir(file.getParentFile());
                FileOutputStream fos = new FileOutputStream(file);
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


    File packageSlideshow(File outputDirectory) throws IOException, XMLStreamException, RecognitionException {
        if (explodedPresentation == null || !explodedPresentation.isDirectory())
            throw new IllegalArgumentException("need exploded presentation");
        if (!outputDirectory.exists()) FileUtils.forceMkdir(outputDirectory);

        File slideshowDir = new File(outputDirectory, explodedPresentation.getName());
        FileUtils.forceMkdir(slideshowDir);

        // Write the main slide file
        File slideFile = new File(slideshowDir, "index.html");
        String html = renderSlideshow();
        FileUtils.writeStringToFile(slideFile, html, "UTF-8");

        // copy the base slideshow files
        File s6Dir = new File("s6");
        if (s6Dir.isDirectory()) {
            FileUtils.copyDirectoryToDirectory(new File(s6Dir, "shared"), slideshowDir);
            FileUtils.copyFileToDirectory(new File(s6Dir, "blank.css"), slideshowDir);
        }
        File imagesDir = new File(slideshowDir, "images");
        FileUtils.copyDirectory(new File(explodedPresentation, "ppt/media"), imagesDir);

        return slideshowDir;
    }
}
