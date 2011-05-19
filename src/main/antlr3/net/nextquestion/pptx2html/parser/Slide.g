grammar Slide;

@header {
package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Slide;

}

slide returns [Slide result]
scope {
  Slide container;
}
@init {
  $slide::container = new Slide();
}
	:	SLD_START slideContainer SLD_END
		{ $result = $slide::container; }
	;

slideContainer
	:	CSLD_START shapeTree CSLD_END
	;

shapeTree
	:	SPTREE_START (NVPR_START NVPR_END)? shapeTreeContent* SPTREE_END
	;

shapeTreeContent
	:	picture | shape
	;
	

shape
scope {
  String shapeType;
  List<String> strings;
}
@init {
  $shape::strings = new ArrayList<String>();
}
	:	SP_START shapePlaceholder? textBody? SP_END
		{ $slide::container.addText($shape::shapeType, $shape::strings); }
	;
		
shapePlaceholder 
	:	NVSPPR_START NVPR_START PH_START
		t=TYPE_ATTR
		PH_END NVPR_END NVSPPR_END
		{ $shape::shapeType = $t.text; }
	;

textBody:	TXBODY_START 
			(p=paragraph { $shape::strings.add(p); })+ 
		TXBODY_END
	;

paragraph returns [String result]
@init {
  StringBuffer buf = new StringBuffer();
}
	:	P_START 
			(R_START T_START (t=TEXT {buf.append(t.getText());})* T_END R_END)*
		P_END
		{ $result = buf.toString(); }
	;

textRun	:	
	;

picture	:	PIC_START pictureProperties? blip PIC_END
	;

pictureProperties 
	:	NVPICPR_START NVPR_START PH_START
		PH_END NVPR_END NVPICPR_END
	;


blip	:	BLIP_START ref=EMBED_ATTR BLIP_END
		{ $slide::container.addImageRef($ref.getText()); }

	;
	
BLIP_START 	: 'BLIP';
BLIP_END   	: '/BLIP';
CSLD_START 	: 'CSLD';
CSLD_END   	: '/CSLD';
SLD_START 	: 'SLD';
SLD_END   	: '/SLD';
SP_START 	: 'SP';
SP_END   	: '/SP';
SPTREE_START 	: 'SPTREE';
SPTREE_END   	: '/SPTREE';

NVPICPR_START	: 'NVPICPR';
NVPICPR_END	: '/NVPICPR';
NVSPPR_START	: 'NVSPPR';
NVSPPR_END	: '/NVSPPR';
NVPR_START	: 'NVPR';
NVPR_END	: '/NVPR';
PH_START	: 'PH';
PH_END		: '/PH';
PIC_START	: 'PIC';
PIC_END		: '/PIC';

TYPE_ATTR	: 'TYPE';
EMBED_ATTR	: 'EMBED';	
TXBODY_START	: 'TXBODY';
TXBODY_END	: '/TXBODY';
P_START		: 'P';
P_END		: '/P';
R_START		: 'R';
R_END		: '/R';
T_START		: 'T';
T_END		: '/T';

TEXT		: 'TEXT';
