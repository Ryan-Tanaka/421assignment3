// Generated from SQLStat.g4 by ANTLR 4.0
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

public class SQLStatBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements SQLStatVisitor<T> {
	@Override public T visitLessExp(SQLStatParser.LessExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitStat(SQLStatParser.StatContext ctx) { return visitChildren(ctx); }

	@Override public T visitGrtrEqExp(SQLStatParser.GrtrEqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectFrom(SQLStatParser.SelectFromContext ctx) { return visitChildren(ctx); }

	@Override public T visitColNames(SQLStatParser.ColNamesContext ctx) { return visitChildren(ctx); }

	@Override public T visitFromTnames(SQLStatParser.FromTnamesContext ctx) { return visitChildren(ctx); }

	@Override public T visitWhereConditions(SQLStatParser.WhereConditionsContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectCols(SQLStatParser.SelectColsContext ctx) { return visitChildren(ctx); }

	@Override public T visitCond(SQLStatParser.CondContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectFromWhere(SQLStatParser.SelectFromWhereContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectAll(SQLStatParser.SelectAllContext ctx) { return visitChildren(ctx); }

	@Override public T visitEqExp(SQLStatParser.EqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitGrtrExp(SQLStatParser.GrtrExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitLessEqExp(SQLStatParser.LessEqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitTNames(SQLStatParser.TNamesContext ctx) { return visitChildren(ctx); }
}