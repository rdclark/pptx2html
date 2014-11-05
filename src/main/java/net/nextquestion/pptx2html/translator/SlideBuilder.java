package net.nextquestion.pptx2html.translator;

import net.nextquestion.pptx2html.model.Slide;
import net.nextquestion.pptx2html.parser.SlideBaseListener;
import net.nextquestion.pptx2html.parser.SlideParser;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rdclark on 11/2/14.
 */
public class SlideBuilder extends SlideBaseListener {

    private Slide container;
    private String shapeType;
    private List<String> strings;
    StringBuffer paragraphBuffer;
    Boolean paragraphStart;

    public SlideBuilder(Slide container) {
        this.container = container;
    }

    @Override
    public void enterShape(@NotNull SlideParser.ShapeContext ctx) {
        strings = new ArrayList<String>();
    }

    @Override
    public void exitShape(@NotNull SlideParser.ShapeContext ctx) {
        container.addText(shapeType, strings);
        // Clean up (to catch any cases of re-using these without re-initialization)
        shapeType = null;
        strings = null;
    }

    @Override
    public void enterParagraph(@NotNull SlideParser.ParagraphContext ctx) {
        paragraphBuffer = new StringBuffer();
    }

    @Override
    public void exitParagraph(@NotNull SlideParser.ParagraphContext ctx) {
        strings.add(paragraphBuffer.toString().trim());
        paragraphBuffer = null;
    }

    @Override
    public void exitShapePlaceholder(@NotNull SlideParser.ShapePlaceholderContext ctx) {
        shapeType = (ctx.t==null)?"":ctx.t.getText();
    }

    @Override
    public void enterTextRun(@NotNull SlideParser.TextRunContext ctx) {
        paragraphStart = ctx.clean != null;
    }

    @Override
    public void exitTextContent(@NotNull SlideParser.TextContentContext ctx) {
        if (!paragraphStart) paragraphBuffer.append(' ');
        paragraphBuffer.append(ctx.t.getText());
        paragraphStart = false;
    }

    @Override
    public void exitBlip(@NotNull SlideParser.BlipContext ctx) {
        container.addImageRef(ctx.ref.getText());
    }
}
