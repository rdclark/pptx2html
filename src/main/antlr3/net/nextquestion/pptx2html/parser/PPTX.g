grammar PPTX;

options {
	output=AST;
}

tokens {
	SLIDE; SLIDE_CONTAINER; 
	SHAPE_TREE; SHAPE;
	PLACEHOLDER_TYPE;
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

shape	:	SP_START placeholderType? SP_END
	->	^(SHAPE placeholderType?)
	;
	
placeholderType 
	:	NVSPPR_START NVPR_START PH_START
		TYPE_ATTR
		PH_END NVPR_END NVSPPR_END
	->	^(PLACEHOLDER_TYPE TYPE_ATTR)
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

