import java.io.*;
import java.util.*;
import java.sql.*;

import org.antlr.v4.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class runSQL 
{
	public static void main(String[] args) throws Exception
	{
		String inputFile = null;
		if(args.length > 0) 
		{
			inputFile = args[0];
		}

		InputStream is = System.in;
		if(inputFile != null)
		{
			is = new FileInputStream(inputFile);
		}

		ANTLRInputStream input = new ANTLRInputStream(is);
		SQLStatLexer lexer = new SQLStatLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLStatParser parser = new SQLStatParser(tokens);
		ParseTree tree = parser.stat();
		StatVisitor sqlStat = new StatVisitor();
		sqlStat.visit(tree);

		System.out.println(tree.toStringTree(parser));

	}
}

