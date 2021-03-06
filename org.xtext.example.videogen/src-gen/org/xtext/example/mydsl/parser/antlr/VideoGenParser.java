/*
 * generated by Xtext 2.15.0
 */
package org.xtext.example.mydsl.parser.antlr;

import com.google.inject.Inject;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.xtext.example.mydsl.parser.antlr.internal.InternalVideoGenParser;
import org.xtext.example.mydsl.services.VideoGenGrammarAccess;

public class VideoGenParser extends AbstractAntlrParser {

	@Inject
	private VideoGenGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalVideoGenParser createParser(XtextTokenStream stream) {
		return new InternalVideoGenParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "VideoGeneratorModel";
	}

	public VideoGenGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(VideoGenGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
