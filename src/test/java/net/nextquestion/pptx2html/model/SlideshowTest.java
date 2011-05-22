package net.nextquestion.pptx2html.model;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test the Slide object's embedded logic.
 * @author rdclark
 * Date: 5/19/11
 * Time: 15:00 PM
 */
public class SlideshowTest {

    private Slide slide;
    private Slideshow slideshow;

    @Before
    public void setUp() {
        File slideFile = new File("hello.xml");
        slide = new Slide(slideFile);
        slideshow = new Slideshow();
        slideshow.add(slide);
    }

    @Test
    public void slideshowContainsSlide() {
        assertThat(slideshow.getSlides(), contains(slide));
    }

    @Test
    public void slideRoutesFooterStringsToSlideshow() {
        String[] footerArray = {"Copyright today"};

        slide.addText("ftr", Arrays.asList(footerArray));
        List<String> footerStrings = slideshow.getFooter();

        assertThat(footerStrings.size(), equalTo(1));
        assertThat(footerStrings.get(0), equalTo("Copyright today"));
    }

}
