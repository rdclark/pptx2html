package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.translator.SlideBuilder;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


/**
 * Check the slide parser against a set of known slide files.
 */
public class SlideParserTest extends ParserTestUtilities {

    private Slide titleSlide;

    @Before
    public void parseTitleSlide() throws IOException, XMLStreamException, RecognitionException {
        titleSlide = parseSlide("src/test/resources/TitleSlide.xml");
    }

    @Test
    public void slideHasTitle() throws Exception {
        List<String> titles = titleSlide.getTitles();
        assertThat(titles.size(), equalTo(1));
        assertThat(titles.get(0), equalTo("Test Slide Title"));
    }
    @Test
    public void titleSlideHasNoStrings() throws Exception {
        List<String> strings = titleSlide.getBullets();
        assertThat(strings, notNullValue());
        assertThat(strings.isEmpty(), equalTo(true));
    }

    @Test
    public void imageSlideContainsPictureElement() throws Exception {
        Slide imageSlide = parseSlide("src/test/resources/ImageBodySlide.xml");
        assertThat(imageSlide.getImageRefs().size(), equalTo(1));
        assertThat(imageSlide.getImageRefs().get(0), equalTo("rId2"));
    }

    protected Slide parseSlide(String fileName) throws IOException, XMLStreamException, RecognitionException {
        TokenStream tokens = getTokenStream(fileName, "target/generated-sources/antlr4/Slide.tokens");
        SlideParser parser = new SlideParser(tokens);
        Slide slide = new Slide(new File(fileName));
        ParseTree tree = parser.slide();
        SlideBuilder builder = new SlideBuilder(slide);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(builder, tree);
        return slide;
    }


}
