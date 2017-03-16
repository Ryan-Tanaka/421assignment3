// Generated from SQLStat.g4 by ANTLR 4.0
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class StatVisitor extends SQLStatBaseVisitor<String> 
{
	// 0    : columns
	// 1    : table names
	// 2    : conditions
	public ArrayList<String> col_data = new ArrayList<String>();
	public ArrayList<String> table_data = new ArrayList<String>();
	public ArrayList<String> cond_data = new ArrayList<String>();

	@Override 
	public String visitLessExp(SQLStatParser.LessExpContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitStat(SQLStatParser.StatContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitGrtrEqExp(SQLStatParser.GrtrEqExpContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitSelectFrom(SQLStatParser.SelectFromContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitSelectColNames(SQLStatParser.SelectColNamesContext ctx) 
	{ 
		int size = ctx.getChildCount();
	
		for(int i = 0; i < size; i++)
		{
			col_data.add(ctx.ID(i).getText());
		}

		return visitChildren(ctx); 
	}

	@Override 
	public String visitFromTnames(SQLStatParser.FromTnamesContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitWhereConditions(SQLStatParser.WhereConditionsContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitSelectCols(SQLStatParser.SelectColsContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitCond(SQLStatParser.CondContext ctx) 
	{ 
		int size = ctx.getChildCount();

		for(int i = 0; i < size; i++)
		{
			cond_data.add(ctx.expr(i).getText());
		}

		return visitChildren(ctx); 
	}

	@Override 
	public String visitSelectFromWhere(SQLStatParser.SelectFromWhereContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitSelectAll(SQLStatParser.SelectAllContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitEqExp(SQLStatParser.EqExpContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitGrtrExp(SQLStatParser.GrtrExpContext ctx) 
	{ 
		return visitChildren(ctx); 
	}

	@Override 
	public String visitLessEqExp(SQLStatParser.LessEqExpContext ctx) 
	{ 
		return visitChildren(ctx); 
	}


	@Override 
	public String visitTableNames(SQLStatParser.TableNamesContext ctx) 
	{ 
		int size = ctx.getChildCount();

		for(int i = 0; i < size; i++)
		{
			table_data.add(ctx.ID(i).getText());
		}
		
		return visitChildren(ctx); 
	}
}