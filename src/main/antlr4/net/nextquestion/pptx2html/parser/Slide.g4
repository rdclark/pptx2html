grammar Slide;

slide:	SLD_START slideContainer SLD_END
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
	

shape:	SP_START shapePlaceholder? textBody? SP_END
	;
		
shapePlaceholder 
	:	NVSPPR_START NVPR_START PH_START
		t=TYPE_ATTR?
		PH_END NVPR_END NVSPPR_END
	;

textBody:	TXBODY_START 
			(p=paragraph)+
		TXBODY_END
	;

paragraph:	P_START textRun* P_END
	;

textRun	:
            R_START
                RPR_START clean=SMTCLEAN_ATTR? RPR_END
                // clean will exist and be "0" if we should use the text as is.
                T_START textContent* T_END
            R_END
	;

textContent: t=TEXT
    ;

picture	:	PIC_START pictureProperties? blip PIC_END
	;

pictureProperties 
	:	NVPICPR_START NVPR_START PH_START
		PH_END NVPR_END NVPICPR_END
	;


blip:	BLIP_START ref=EMBED_ATTR BLIP_END
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

RPR_START	: 'RPR';
RPR_END		: '/RPR';
SMTCLEAN_ATTR	: 'SMTCLEAN';

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
