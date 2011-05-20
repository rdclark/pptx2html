grammar RELS;

tokens {
	RELATIONSHIPS; RELATIONSHIP;
}

@header {
package net.nextquestion.pptx2html.parser;

import net.nextquestion.pptx2html.model.Relationship;
import java.util.Map;
import java.util.HashMap;
}


relationships returns [Map<String, Relationship> relationshipMap]
@init {
	relationshipMap = new HashMap<String, Relationship>();
}
	:	RELATIONSHIPS_START 
			(r=relationship { $relationshipMap.put(r.getRelID(), r); })+ 
		RELATIONSHIPS_END
	;

relationship returns [Relationship r]
	:	RELATIONSHIP_START id=ID_ATTR target=TARGET_ATTR rtype=TYPE_ATTR RELATIONSHIP_END
		{ $r = new Relationship(id, rtype, target); }
	;

RELATIONSHIP_START:	'RELATIONSHIP';
RELATIONSHIP_END:	'/RELATIONSHIP';
RELATIONSHIPS_START:	'RELATIONSHIPS';
RELATIONSHIPS_END:	'/RELATIONSHIPS';
ID_ATTR	:		'ID';
TARGET_ATTR:		'TARGET';
TYPE_ATTR:		'TYPE';	