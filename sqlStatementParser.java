// Generated from sqlStatement.g4 by ANTLR 4.0
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class sqlStatementParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STAR=1, SELECT=2, FROM=3, WHERE=4, AND=5, EQUALS=6, LESS=7, GREATER=8, 
		LESSEQ=9, GREATEQ=10, ID=11, INT=12, COMMA=13, SEMI=14, WS=15;
	public static final String[] tokenNames = {
		"<INVALID>", "'*'", "SELECT", "FROM", "WHERE", "AND", "'='", "'<'", "'>'", 
		"'<='", "'>='", "ID", "INT", "','", "';'", "WS"
	};
	public static final int
		RULE_stat = 0, RULE_query = 1, RULE_select_clause = 2, RULE_from_clause = 3, 
		RULE_where_clause = 4, RULE_colnames = 5, RULE_tnames = 6, RULE_conditions = 7, 
		RULE_expr = 8;
	public static final String[] ruleNames = {
		"stat", "query", "select_clause", "from_clause", "where_clause", "colnames", 
		"tnames", "conditions", "expr"
	};

	@Override
	public String getGrammarFileName() { return "sqlStatement.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public sqlStatementParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StatContext extends ParserRuleContext {
		public List<QueryContext> query() {
			return getRuleContexts(QueryContext.class);
		}
		public QueryContext query(int i) {
			return getRuleContext(QueryContext.class,i);
		}
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_stat);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(18); query();
				}
				}
				setState(21); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SELECT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryContext extends ParserRuleContext {
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	 
		public QueryContext() { }
		public void copyFrom(QueryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SelectFromWhereContext extends QueryContext {
		public Where_clauseContext where_clause() {
			return getRuleContext(Where_clauseContext.class,0);
		}
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(sqlStatementParser.SEMI, 0); }
		public From_clauseContext from_clause() {
			return getRuleContext(From_clauseContext.class,0);
		}
		public SelectFromWhereContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitSelectFromWhere(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectFromContext extends QueryContext {
		public Select_clauseContext select_clause() {
			return getRuleContext(Select_clauseContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(sqlStatementParser.SEMI, 0); }
		public From_clauseContext from_clause() {
			return getRuleContext(From_clauseContext.class,0);
		}
		public SelectFromContext(QueryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitSelectFrom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_query);
		try {
			setState(32);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new SelectFromContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(23); select_clause();
				setState(24); from_clause();
				setState(25); match(SEMI);
				}
				break;

			case 2:
				_localctx = new SelectFromWhereContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(27); select_clause();
				setState(28); from_clause();
				setState(29); where_clause();
				setState(30); match(SEMI);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_clauseContext extends ParserRuleContext {
		public Select_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_clause; }
	 
		public Select_clauseContext() { }
		public void copyFrom(Select_clauseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SelectAllContext extends Select_clauseContext {
		public TerminalNode STAR() { return getToken(sqlStatementParser.STAR, 0); }
		public TerminalNode SELECT() { return getToken(sqlStatementParser.SELECT, 0); }
		public SelectAllContext(Select_clauseContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitSelectAll(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectColsContext extends Select_clauseContext {
		public ColnamesContext colnames() {
			return getRuleContext(ColnamesContext.class,0);
		}
		public TerminalNode SELECT() { return getToken(sqlStatementParser.SELECT, 0); }
		public SelectColsContext(Select_clauseContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitSelectCols(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_clauseContext select_clause() throws RecognitionException {
		Select_clauseContext _localctx = new Select_clauseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_select_clause);
		try {
			setState(38);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new SelectColsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(34); match(SELECT);
				setState(35); colnames();
				}
				break;

			case 2:
				_localctx = new SelectAllContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(36); match(SELECT);
				setState(37); match(STAR);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class From_clauseContext extends ParserRuleContext {
		public From_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_from_clause; }
	 
		public From_clauseContext() { }
		public void copyFrom(From_clauseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FromTnamesContext extends From_clauseContext {
		public TnamesContext tnames() {
			return getRuleContext(TnamesContext.class,0);
		}
		public TerminalNode FROM() { return getToken(sqlStatementParser.FROM, 0); }
		public FromTnamesContext(From_clauseContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitFromTnames(this);
			else return visitor.visitChildren(this);
		}
	}

	public final From_clauseContext from_clause() throws RecognitionException {
		From_clauseContext _localctx = new From_clauseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_from_clause);
		try {
			_localctx = new FromTnamesContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(40); match(FROM);
			setState(41); tnames();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Where_clauseContext extends ParserRuleContext {
		public Where_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_where_clause; }
	 
		public Where_clauseContext() { }
		public void copyFrom(Where_clauseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WhereConditionsContext extends Where_clauseContext {
		public TerminalNode WHERE() { return getToken(sqlStatementParser.WHERE, 0); }
		public ConditionsContext conditions() {
			return getRuleContext(ConditionsContext.class,0);
		}
		public WhereConditionsContext(Where_clauseContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitWhereConditions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Where_clauseContext where_clause() throws RecognitionException {
		Where_clauseContext _localctx = new Where_clauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_where_clause);
		try {
			_localctx = new WhereConditionsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(43); match(WHERE);
			setState(44); conditions();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ColnamesContext extends ParserRuleContext {
		public ColnamesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colnames; }
	 
		public ColnamesContext() { }
		public void copyFrom(ColnamesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ColNamesContext extends ColnamesContext {
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public ColNamesContext(ColnamesContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitColNames(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColnamesContext colnames() throws RecognitionException {
		ColnamesContext _localctx = new ColnamesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_colnames);
		int _la;
		try {
			_localctx = new ColNamesContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(46); match(ID);
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TnamesContext extends ParserRuleContext {
		public TnamesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tnames; }
	 
		public TnamesContext() { }
		public void copyFrom(TnamesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TNamesContext extends TnamesContext {
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public TNamesContext(TnamesContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitTNames(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TnamesContext tnames() throws RecognitionException {
		TnamesContext _localctx = new TnamesContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_tnames);
		int _la;
		try {
			_localctx = new TNamesContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(52); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(51); match(ID);
				}
				}
				setState(54); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionsContext extends ParserRuleContext {
		public ConditionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions; }
	 
		public ConditionsContext() { }
		public void copyFrom(ConditionsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CondContext extends ConditionsContext {
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public CondContext(ConditionsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitCond(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionsContext conditions() throws RecognitionException {
		ConditionsContext _localctx = new ConditionsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_conditions);
		int _la;
		try {
			_localctx = new CondContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(57); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(56); expr();
				}
				}
				setState(59); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LessExpContext extends ExprContext {
		public TerminalNode LESS() { return getToken(sqlStatementParser.LESS, 0); }
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public LessExpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitLessExp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EqExpContext extends ExprContext {
		public TerminalNode EQUALS() { return getToken(sqlStatementParser.EQUALS, 0); }
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public EqExpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitEqExp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GrtrEqExpContext extends ExprContext {
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public TerminalNode GREATEQ() { return getToken(sqlStatementParser.GREATEQ, 0); }
		public GrtrEqExpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitGrtrEqExp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GrtrExpContext extends ExprContext {
		public TerminalNode GREATER() { return getToken(sqlStatementParser.GREATER, 0); }
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public GrtrExpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitGrtrExp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LessEqExpContext extends ExprContext {
		public TerminalNode LESSEQ() { return getToken(sqlStatementParser.LESSEQ, 0); }
		public List<TerminalNode> ID() { return getTokens(sqlStatementParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(sqlStatementParser.ID, i);
		}
		public LessEqExpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlStatementVisitor ) return ((sqlStatementVisitor<? extends T>)visitor).visitLessEqExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_expr);
		try {
			setState(76);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new EqExpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(61); match(ID);
				setState(62); match(EQUALS);
				setState(63); match(ID);
				}
				break;

			case 2:
				_localctx = new LessExpContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(64); match(ID);
				setState(65); match(LESS);
				setState(66); match(ID);
				}
				break;

			case 3:
				_localctx = new GrtrExpContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(67); match(ID);
				setState(68); match(GREATER);
				setState(69); match(ID);
				}
				break;

			case 4:
				_localctx = new LessEqExpContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(70); match(ID);
				setState(71); match(LESSEQ);
				setState(72); match(ID);
				}
				break;

			case 5:
				_localctx = new GrtrEqExpContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(73); match(ID);
				setState(74); match(GREATEQ);
				setState(75); match(ID);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\21Q\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t"+
		"\t\4\n\t\n\3\2\6\2\26\n\2\r\2\16\2\27\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3#\n\3\3\4\3\4\3\4\3\4\5\4)\n\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\6\7"+
		"\62\n\7\r\7\16\7\63\3\b\6\b\67\n\b\r\b\16\b8\3\t\6\t<\n\t\r\t\16\t=\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\nO\n\n\3"+
		"\n\2\13\2\4\6\b\n\f\16\20\22\2\2Q\2\25\3\2\2\2\4\"\3\2\2\2\6(\3\2\2\2"+
		"\b*\3\2\2\2\n-\3\2\2\2\f\61\3\2\2\2\16\66\3\2\2\2\20;\3\2\2\2\22N\3\2"+
		"\2\2\24\26\5\4\3\2\25\24\3\2\2\2\26\27\3\2\2\2\27\25\3\2\2\2\27\30\3\2"+
		"\2\2\30\3\3\2\2\2\31\32\5\6\4\2\32\33\5\b\5\2\33\34\7\20\2\2\34#\3\2\2"+
		"\2\35\36\5\6\4\2\36\37\5\b\5\2\37 \5\n\6\2 !\7\20\2\2!#\3\2\2\2\"\31\3"+
		"\2\2\2\"\35\3\2\2\2#\5\3\2\2\2$%\7\4\2\2%)\5\f\7\2&\'\7\4\2\2\')\7\3\2"+
		"\2($\3\2\2\2(&\3\2\2\2)\7\3\2\2\2*+\7\5\2\2+,\5\16\b\2,\t\3\2\2\2-.\7"+
		"\6\2\2./\5\20\t\2/\13\3\2\2\2\60\62\7\r\2\2\61\60\3\2\2\2\62\63\3\2\2"+
		"\2\63\61\3\2\2\2\63\64\3\2\2\2\64\r\3\2\2\2\65\67\7\r\2\2\66\65\3\2\2"+
		"\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29\17\3\2\2\2:<\5\22\n\2;:\3\2\2\2"+
		"<=\3\2\2\2=;\3\2\2\2=>\3\2\2\2>\21\3\2\2\2?@\7\r\2\2@A\7\b\2\2AO\7\r\2"+
		"\2BC\7\r\2\2CD\7\t\2\2DO\7\r\2\2EF\7\r\2\2FG\7\n\2\2GO\7\r\2\2HI\7\r\2"+
		"\2IJ\7\13\2\2JO\7\r\2\2KL\7\r\2\2LM\7\f\2\2MO\7\r\2\2N?\3\2\2\2NB\3\2"+
		"\2\2NE\3\2\2\2NH\3\2\2\2NK\3\2\2\2O\23\3\2\2\2\t\27\"(\638=N";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}