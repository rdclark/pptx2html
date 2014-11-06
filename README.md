This project takes PowerPoint .pptx files and extracts their contents. It's based on [ANTLR 4](http://www.antlr.org/), ANother Tool for Language Recognition. There's an ANTLR 3 branch available as well.

## Limitations

1. This version does not preserve text formatting or slide layouts.
2. This version ignores shapes drawn with PowerPoint (that's a complex little drawing language) and might not catch all pictures.
3. The output is HTML formatted for a s6 slideshow.

## Building

Intall [Maven](http://maven.apache.org) and JDK 6 or later, build using the standard Maven lifecycle targets (clean, compile, test, package).

## Wishlist / roadmap

1. Other output templates (e.g. Markdown, Textile)
2. Capture inline formatting
3. Capture more of the layout options (titles, header/footer, text block positioning, picture positioning.)
