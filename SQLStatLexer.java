// Generated from SQLStat.g4 by ANTLR 4.6
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLStatLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STAR=1, SELECT=2, FROM=3, WHERE=4, AND=5, EQUALS=6, LESS=7, GREATER=8, 
		LESSEQ=9, GREATEQ=10, ID=11, INT=12, COMMA=13, SEMI=14, WS=15;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"STAR", "SELECT", "FROM", "WHERE", "AND", "EQUALS", "LESS", "GREATER", 
		"LESSEQ", "GREATEQ", "ID", "INT", "COMMA", "SEMI", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'*'", null, null, null, null, "'='", "'<'", "'>'", "'<='", "'>='", 
		null, null, "','", "';'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "STAR", "SELECT", "FROM", "WHERE", "AND", "EQUALS", "LESS", "GREATER", 
		"LESSEQ", "GREATEQ", "ID", "INT", "COMMA", "SEMI", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SQLStatLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SQLStat.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\21^\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\f"+
		"\6\fI\n\f\r\f\16\fJ\3\r\6\rN\n\r\r\r\16\rO\3\16\3\16\3\16\3\16\3\17\3"+
		"\17\3\20\6\20Y\n\20\r\20\16\20Z\3\20\3\20\2\2\21\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21\3\2\23\4\2UUuu"+
		"\4\2GGgg\4\2NNnn\4\2EEee\4\2VVvv\4\2HHhh\4\2TTtt\4\2QQqq\4\2OOoo\4\2Y"+
		"Yyy\4\2JJjj\4\2CCcc\4\2PPpp\4\2FFff\b\2))/\60\62;C\\aac|\3\2\62;\5\2\13"+
		"\f\17\17\"\"`\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3!\3\2"+
		"\2\2\5#\3\2\2\2\7*\3\2\2\2\t/\3\2\2\2\13\65\3\2\2\2\r;\3\2\2\2\17=\3\2"+
		"\2\2\21?\3\2\2\2\23A\3\2\2\2\25D\3\2\2\2\27H\3\2\2\2\31M\3\2\2\2\33Q\3"+
		"\2\2\2\35U\3\2\2\2\37X\3\2\2\2!\"\7,\2\2\"\4\3\2\2\2#$\t\2\2\2$%\t\3\2"+
		"\2%&\t\4\2\2&\'\t\3\2\2\'(\t\5\2\2()\t\6\2\2)\6\3\2\2\2*+\t\7\2\2+,\t"+
		"\b\2\2,-\t\t\2\2-.\t\n\2\2.\b\3\2\2\2/\60\t\13\2\2\60\61\t\f\2\2\61\62"+
		"\t\3\2\2\62\63\t\b\2\2\63\64\t\3\2\2\64\n\3\2\2\2\65\66\t\r\2\2\66\67"+
		"\t\16\2\2\678\t\17\2\289\3\2\2\29:\b\6\2\2:\f\3\2\2\2;<\7?\2\2<\16\3\2"+
		"\2\2=>\7>\2\2>\20\3\2\2\2?@\7@\2\2@\22\3\2\2\2AB\7>\2\2BC\7?\2\2C\24\3"+
		"\2\2\2DE\7@\2\2EF\7?\2\2F\26\3\2\2\2GI\t\20\2\2HG\3\2\2\2IJ\3\2\2\2JH"+
		"\3\2\2\2JK\3\2\2\2K\30\3\2\2\2LN\t\21\2\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2"+
		"\2OP\3\2\2\2P\32\3\2\2\2QR\7.\2\2RS\3\2\2\2ST\b\16\2\2T\34\3\2\2\2UV\7"+
		"=\2\2V\36\3\2\2\2WY\t\22\2\2XW\3\2\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2["+
		"\\\3\2\2\2\\]\b\20\2\2] \3\2\2\2\6\2JOZ\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}