package net.nextquestion.pptx2html.model;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 5/19/11
 * Time: 15:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SlideTest {

    private File slideFile;
    private Slide slide;

    @Before
    public void setUp() {
        slideFile = new File("hello.xml");
        slide = new Slide(slideFile);
    }

    @Test
    public void slideRoutesTitleStrings() {
        String[] title = {"Hello, world"};

        slide.addText("ctrTitle", Arrays.asList(title));
        List<String> titles = slide.getTitles();

        assertThat(titles.size(), equalTo(1));
        assertThat(titles.get(0), equalTo("Hello, world"));
    }

    @Test
    public void titleStringIsNotInBullets() {
        String[] title = {"Hello, world"};
        slide.addText("ctrTitle", Arrays.asList(title));
        List<String> titles = slide.getTitles();

        assertThat(titles.size(), equalTo(1));
        assertThat(titles.get(0), equalTo("Hello, world"));
    }


    @Test
    public void firstTitleIsMainTitle() {
        String[] title = {"Hello, world"};

        slide.addText("ctrTitle", Arrays.asList(title));
        assertThat(slide.getTitle(), equalTo("Hello, world"));
    }


    @Test
    public void slideRoutesNonTitleStringsToBody() {
        String[] message = {"Hello, world"};

        slide.addText("", Arrays.asList(message));
        assertThat(slide.getTitles().isEmpty(), equalTo(true));
        List<String> strings = slide.getBullets();
        assertThat(strings.size(), equalTo(1));
        assertThat(strings.get(0), equalTo("Hello, world"));
    }

    @Test
    public void slideResolvesImageReferences() {
        Relationship r1 = new Relationship("rId1", "image", "test.png");
        Map<String, Relationship> relationships = new HashMap<String, Relationship>();
        relationships.put("rId1", r1);

        slide.addImageRef("rId1");
        slide.addRelationships(relationships);
        List <String> images = slide.getImageNames();

        assertThat(images.size(),  equalTo(1));
        assertThat(images.get(0), equalTo("test.png"));
    }

}
