grammar PPTX;

options {
	output=AST;
}

tokens {
	SLIDE; SLIDE_CONTAINER; SHAPE_TREE;
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
	:	SPTREE_START SPTREE_END
	->	^(SHAPE_TREE)
	;


CSLD_START : 'CSLD';
CSLD_END   : '/CSLD';
SLD_START : 'SLD';
SLD_END   : '/SLD';
SPTREE_START : 'SPTREE';
SPTREE_END   : '/SPTREE';

