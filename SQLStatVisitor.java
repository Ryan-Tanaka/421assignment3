// Generated from SQLStat.g4 by ANTLR 4.0
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface SQLStatVisitor<T> extends ParseTreeVisitor<T> {
	T visitLessExp(SQLStatParser.LessExpContext ctx);

	T visitStat(SQLStatParser.StatContext ctx);

	T visitGrtrEqExp(SQLStatParser.GrtrEqExpContext ctx);

	T visitSelectFrom(SQLStatParser.SelectFromContext ctx);

	T visitColNames(SQLStatParser.ColNamesContext ctx);

	T visitFromTnames(SQLStatParser.FromTnamesContext ctx);

	T visitWhereConditions(SQLStatParser.WhereConditionsContext ctx);

	T visitSelectCols(SQLStatParser.SelectColsContext ctx);

	T visitCond(SQLStatParser.CondContext ctx);

	T visitSelectFromWhere(SQLStatParser.SelectFromWhereContext ctx);

	T visitSelectAll(SQLStatParser.SelectAllContext ctx);

	T visitEqExp(SQLStatParser.EqExpContext ctx);

	T visitGrtrExp(SQLStatParser.GrtrExpContext ctx);

	T visitLessEqExp(SQLStatParser.LessEqExpContext ctx);

	T visitTNames(SQLStatParser.TNamesContext ctx);
}