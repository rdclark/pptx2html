grammar PPTX;

options {
	output=AST;
}

tokens {
	SLIDE; SLIDE_CONTAINER;
}

@header {
package net.nextquestion.pptx2html.parser;
}

slide
	:	SLD_START slideContainer SLD_END // TODO the container may be the wrong thing to look for -- may want the text bodies instead
	->	^(SLIDE slideContainer)
	;

slideContainer
:	CSLD_START CSLD_END
->	^(SLIDE_CONTAINER)
;

CSLD_START : 'CSLD';
CSLD_END   : '/CSLD';
SLD_START : 'SLD';
SLD_END   : '/SLD';

