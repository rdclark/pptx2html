package net.nextquestion.pptx2html.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rdclark
 * Date: 5/18/11
 * Time: 14:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Slide {

    final private File sourceFile;

    private List<String> bullets = new ArrayList<String>();
    private List<String> titles = new ArrayList<String>();
    private String mainTitleType;
    private List<String> imageRefs = new ArrayList<String>();
    private Map<String, Relationship> relationshipMap = new HashMap<String, Relationship>();

    public Slide(File sourceFile) {
        this.sourceFile = sourceFile;
    }


    public void addText(String shapeType, List<String> paragraphs) {
        if (shapeType == null) shapeType = "";
        if (shapeType.toLowerCase().endsWith("title")) {
            for (String p : paragraphs) {
                if (p.length() > 0) {
                    titles.add(p);
                    if (mainTitleType == null) mainTitleType = shapeType;
                }
            }
        } else {
            for (String p : paragraphs) {
                if (p.length() > 0) {
                    bullets.add(p);
                }
            }
        }
    }

    public void addImageRef(String refText) {
        imageRefs.add(refText);
    }

    public List<String> getBullets() {
        return bullets;
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<String> getImageRefs() {
        return imageRefs;
    }

    /**
     * Returns "ctrTitle" for a title-only slide, "title" for the title of a regular slide.
     *
     * @return
     */
    public String getMainTitleType() {
        return mainTitleType;
    }

    public void addRelationships(Map<String, Relationship> relationships) {
        relationshipMap.putAll(relationships);
    }

    public boolean hasImages() {
        return !imageRefs.isEmpty();
    }

    public List<File> getImageFiles() {
        List<File> result = new ArrayList<File>(imageRefs.size());
        for (String refid : imageRefs) {
            if (relationshipMap.containsKey(refid)) {
                String target = relationshipMap.get(refid).getRelTarget();
                result.add(new File(sourceFile.getParent(), target));
            }
        }
        return result;
    }

    public List<String> getImageNames() {
        List<File> imageFiles = getImageFiles();
        List<String> result = new ArrayList<String>(imageFiles.size());
        for (File file: imageFiles) {
            result.add(file.getName());
        }
        return result;
    }

    public String getTitle() {
        return titles.isEmpty() ? "" : titles.get(0);
    }

}
