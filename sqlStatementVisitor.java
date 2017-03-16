// Generated from sqlStatement.g4 by ANTLR 4.0
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface sqlStatementVisitor<T> extends ParseTreeVisitor<T> {
	T visitLessExp(sqlStatementParser.LessExpContext ctx);

	T visitStat(sqlStatementParser.StatContext ctx);

	T visitGrtrEqExp(sqlStatementParser.GrtrEqExpContext ctx);

	T visitSelectFrom(sqlStatementParser.SelectFromContext ctx);

	T visitColNames(sqlStatementParser.ColNamesContext ctx);

	T visitFromTnames(sqlStatementParser.FromTnamesContext ctx);

	T visitWhereConditions(sqlStatementParser.WhereConditionsContext ctx);

	T visitSelectCols(sqlStatementParser.SelectColsContext ctx);

	T visitCond(sqlStatementParser.CondContext ctx);

	T visitSelectFromWhere(sqlStatementParser.SelectFromWhereContext ctx);

	T visitSelectAll(sqlStatementParser.SelectAllContext ctx);

	T visitEqExp(sqlStatementParser.EqExpContext ctx);

	T visitGrtrExp(sqlStatementParser.GrtrExpContext ctx);

	T visitLessEqExp(sqlStatementParser.LessEqExpContext ctx);

	T visitTNames(sqlStatementParser.TNamesContext ctx);
}