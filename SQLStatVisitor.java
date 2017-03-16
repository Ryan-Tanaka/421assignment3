// Generated from SQLStat.g4 by ANTLR 4.6
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SQLStatParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SQLStatVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SQLStatParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(SQLStatParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectFrom}
	 * labeled alternative in {@link SQLStatParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectFrom(SQLStatParser.SelectFromContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectFromWhere}
	 * labeled alternative in {@link SQLStatParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectFromWhere(SQLStatParser.SelectFromWhereContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectCols}
	 * labeled alternative in {@link SQLStatParser#select_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectCols(SQLStatParser.SelectColsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectAll}
	 * labeled alternative in {@link SQLStatParser#select_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectAll(SQLStatParser.SelectAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fromTnames}
	 * labeled alternative in {@link SQLStatParser#from_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromTnames(SQLStatParser.FromTnamesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whereConditions}
	 * labeled alternative in {@link SQLStatParser#where_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereConditions(SQLStatParser.WhereConditionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectColNames}
	 * labeled alternative in {@link SQLStatParser#colnames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectColNames(SQLStatParser.SelectColNamesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tableNames}
	 * labeled alternative in {@link SQLStatParser#tnames}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableNames(SQLStatParser.TableNamesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cond}
	 * labeled alternative in {@link SQLStatParser#conditions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(SQLStatParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eqExp}
	 * labeled alternative in {@link SQLStatParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExp(SQLStatParser.EqExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lessExp}
	 * labeled alternative in {@link SQLStatParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessExp(SQLStatParser.LessExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code grtrExp}
	 * labeled alternative in {@link SQLStatParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrtrExp(SQLStatParser.GrtrExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lessEqExp}
	 * labeled alternative in {@link SQLStatParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessEqExp(SQLStatParser.LessEqExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code grtrEqExp}
	 * labeled alternative in {@link SQLStatParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrtrEqExp(SQLStatParser.GrtrEqExpContext ctx);
}