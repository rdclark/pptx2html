package net.nextquestion.pptx2html.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds content common to all slides.
 * @author rdclark
 * Date: 5/22/11
 * Time: 10:33 AM
 */
public class Slideshow {

    final private List<Slide> slides = new ArrayList<Slide>();
    final private List<String> footer  = new ArrayList<String>();

    public List<Slide> getSlides() {
        return slides;
    }

    public void add(Slide slide) {
        slides.add(slide);
        slide.setParent(this);
    }

    public List<String> getFooter() {
        return footer;
    }

    /**
     * Grabs each unique footer found, adding the first one as the main footer and the rest as sub-footers.
     * @param s the footer text
     */
    void addFooter(String s) {
        if (s == null || s.length() == 0) return;
        if (!footer.contains(s)) footer.add(s);
    }
}
