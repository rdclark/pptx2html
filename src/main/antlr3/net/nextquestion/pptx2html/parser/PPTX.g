grammar PPTX;

options {
	output=AST;
}

tokens {
	SLIDE; SLIDE_CONTAINER; 
	SHAPE_TREE; SHAPE;
	PLACEHOLDER_TYPE;
	TEXT_BODY; PARAGRAPH; TEXT_RUN;
}

@header {
package net.nextquestion.pptx2html.parser;
}

slide
	:	SLD_START slideContainer SLD_END // TODO the container may be the wrong thing to look for -- may want the text bodies instead
	->	^(SLIDE slideContainer)
	;

slideContainer
	:	CSLD_START shapeTree CSLD_END
	->	^(SLIDE_CONTAINER shapeTree)
	;

shapeTree
	:	SPTREE_START (NVPR_START NVPR_END)? shape+ SPTREE_END
	->	^(SHAPE_TREE shape+)
	;

shape	:	SP_START placeholderType? textBody? SP_END
	->	^(SHAPE placeholderType? textBody?)
	;
	
placeholderType 
	:	NVSPPR_START NVPR_START PH_START
		TYPE_ATTR
		PH_END NVPR_END NVSPPR_END
	->	^(PLACEHOLDER_TYPE TYPE_ATTR)
	;

textBody:	TXBODY_START paragraph+ TXBODY_END
	->	^(TEXT_BODY paragraph+)
	;

paragraph	
	:	P_START textRun* P_END
	->	^(PARAGRAPH textRun*)
	;

textRun	:	R_START T_START TEXT* T_END R_END
	->	^(TEXT_RUN TEXT*)
	;


CSLD_START 	: 'CSLD';
CSLD_END   	: '/CSLD';
SLD_START 	: 'SLD';
SLD_END   	: '/SLD';
SP_START 	: 'SP';
SP_END   	: '/SP';
SPTREE_START 	: 'SPTREE';
SPTREE_END   	: '/SPTREE';

NVSPPR_START	: 'NVSPPR';
NVSPPR_END	: '/NVSPPR';
NVPR_START	: 'NVPR';
NVPR_END	: '/NVPR';
PH_START	: 'PH';
PH_END		: '/PH';

TYPE_ATTR	: 'TYPE';

TXBODY_START	: 'TXBODY';
TXBODY_END	: '/TXBODY';
P_START		: 'P';
P_END		: '/P';
R_START		: 'R';
R_END		: '/R';
T_START		: 'T';
T_END		: '/T';

TEXT		: 'TEXT';
