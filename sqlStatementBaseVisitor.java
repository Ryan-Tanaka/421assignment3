// Generated from sqlStatement.g4 by ANTLR 4.0
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

public class sqlStatementBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements sqlStatementVisitor<T> {
	@Override public T visitLessExp(sqlStatementParser.LessExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitStat(sqlStatementParser.StatContext ctx) { return visitChildren(ctx); }

	@Override public T visitGrtrEqExp(sqlStatementParser.GrtrEqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectFrom(sqlStatementParser.SelectFromContext ctx) { return visitChildren(ctx); }

	@Override public T visitColNames(sqlStatementParser.ColNamesContext ctx) { return visitChildren(ctx); }

	@Override public T visitFromTnames(sqlStatementParser.FromTnamesContext ctx) { return visitChildren(ctx); }

	@Override public T visitWhereConditions(sqlStatementParser.WhereConditionsContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectCols(sqlStatementParser.SelectColsContext ctx) { return visitChildren(ctx); }

	@Override public T visitCond(sqlStatementParser.CondContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectFromWhere(sqlStatementParser.SelectFromWhereContext ctx) { return visitChildren(ctx); }

	@Override public T visitSelectAll(sqlStatementParser.SelectAllContext ctx) { return visitChildren(ctx); }

	@Override public T visitEqExp(sqlStatementParser.EqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitGrtrExp(sqlStatementParser.GrtrExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitLessEqExp(sqlStatementParser.LessEqExpContext ctx) { return visitChildren(ctx); }

	@Override public T visitTNames(sqlStatementParser.TNamesContext ctx) { return visitChildren(ctx); }
}