import java.io.*;
import java.util.*;
import java.sql.*;

import org.antlr.v4.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class runSQL 
{
	private static StatVisitor sqlStat;
	
	private static node localNode;
	private static node catalogNode;

	private static ArrayList<node> table1_nodes_list;
	private static ArrayList<node> table2_nodes_list;

	public static void main(String[] args) throws Exception
	{
		//get antlr to extract the data i need
		String inputFile = null;
		if(args.length > 0) 
		{
			inputFile = args[1];
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
		sqlStat = new StatVisitor();
		sqlStat.visit(tree);

		//find out where partitions are stored 
		catalogNode = getNode(args[0], "catalog");
		localNode = getNode(args[0], "localnode");

		table1_nodes_list = new ArrayList<node>();
		table2_nodes_list = new ArrayList<node>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		final String GETNODESQL = "SELECT nodedriver, nodeurl, nodeuser, nodepasswd, nodeid, partmtd FROM dtables WHERE tname = ?";

		if(sqlStat.requireJoin)
		{
			try
			{
				String t1PartitionMethod = "";
				String t2PartitionMethod = "";

				conn = DriverManager.getConnection(catalogNode.getHostname(),
												   catalogNode.getUsername(),
												   catalogNode.getPassword());

				ps = conn.prepareStatement(GETNODESQL);
				String table1Name = sqlStat.table_data.get(0);
				System.out.println("tabl1Name = " + table1Name);
				ps.setString(1, table1Name);
				rs = ps.executeQuery();

				while(rs.next())
				{
					node temp = new node();

					temp.setDriver(rs.getString(1));
					temp.setHostname(rs.getString(2));
					temp.setUsername(rs.getString(3));
					temp.setPassword(rs.getString(4));
					temp.setNodeNum(Integer.parseInt(rs.getString(5)));
					t1PartitionMethod = rs.getString(6); // <- little repetitive

					table1_nodes_list.add(temp);
				}

				String table2Name = sqlStat.table_data.get(2);
				System.out.println("tabl2Name = " + table2Name);
				ps.setString(1, table2Name);
				rs = ps.executeQuery();

				while(rs.next())
				{
					node temp = new node();

					temp.setDriver(rs.getString(1));
					temp.setHostname(rs.getString(2));
					temp.setUsername(rs.getString(3));
					temp.setPassword(rs.getString(4));
					temp.setNodeNum(Integer.parseInt(rs.getString(5)));
					t2PartitionMethod = rs.getString(6); // <- little repetitive

					table2_nodes_list.add(temp);
				}

				//DBO
				for(node n: table1_nodes_list)
					n.printNode();

				for(node n: table2_nodes_list)
					n.printNode();

				System.out.println("partition methods : " + t1PartitionMethod + " and " + t2PartitionMethod);

				Connection connTest = DriverManager.getConnection(table1_nodes_list.get(0).getHostname(),
												  				  table1_nodes_list.get(0).getUsername(),
												   				  table1_nodes_list.get(0).getPassword());

				Connection conn2Test = DriverManager.getConnection(table2_nodes_list.get(0).getHostname(),
												   				   table2_nodes_list.get(0).getUsername(),
												   				   table2_nodes_list.get(0).getPassword());

				PreparedStatement psTest = null;
				PreparedStatement ps2Test = null;
				ResultSet rsTest = null;
				ResultSet rs2Test = null;

				String create = "";
				final String ORDERS = "orders";
				 String SHOW = "SHOW CREATE TABLE " + ORDERS;
				psTest = connTest.prepareStatement(SHOW);
				rsTest = psTest.executeQuery();

				if(rsTest.next())
				{
					create = rsTest.getString(2);
				}

				create = create.replaceAll("CREATE", "CREATE TEMPORARY");
				create = create.replaceAll("orders", "ordersTEMP");

				System.out.println("create temp: " + create);

				ps2Test = conn2Test.prepareStatement(create);
				ps2Test.executeUpdate();

				final String LINEITEM = "lineitem";
				Statement stmt2 = conn2Test.createStatement();

				psTest = connTest.prepareStatement("SELECT * FROM orders");
				rsTest = psTest.executeQuery();

				LinkedList<String> colNames = getColumnNames(table1_nodes_list.get(0), "orders");

				int numCols = colNames.size();
				LinkedList<String> dataTypes = getDataTypes(table1_nodes_list.get(0), "orders", numCols);

				String temp = "";
				String values = "";

				for(int i = 0; i < numCols; i++)
				{
					values += colNames.get(i);
					if(i < numCols - 1)
					{
						values += ", ";
					}
				}

				while(rsTest.next())
				{
					for(int i = 1; i <= numCols; i++)
					{
						String dtype = dataTypes.get(i - 1);

						if(dtype.equalsIgnoreCase("var") || dtype.equalsIgnoreCase("varchar") || 
							dtype.equalsIgnoreCase("date") || dtype.equalsIgnoreCase("char"))
						{
							temp += "'";
						}
						temp += rsTest.getString(i);
						
						if(dtype.equalsIgnoreCase("var") || dtype.equalsIgnoreCase("varchar") || 
							dtype.equalsIgnoreCase("date") || dtype.equalsIgnoreCase("char"))
						{
							temp += "'";
						}

						if(i <= numCols - 1)
						{
							temp+= ", ";
						}					
					}
					System.out.println(temp);
					System.out.println(values);
					String insert = "INSERT INTO ordersTEMP (" + values + ") Values(" + temp + ")";
					System.out.println(insert);
					stmt2.executeUpdate(insert);
					temp = "";
				}

				ps2Test = conn2Test.prepareStatement("SELECT * FROM ordersTEMP");
				rs2Test = ps2Test.executeQuery();

				System.out.println("SELECTING FROM TEMP TABLE");
				String temp1 = "";
				while(rs2Test.next())
				{
					for(int i = 1; i <= numCols; i++)
					{
						temp1 += rs2Test.getString(i);
						if(i <= numCols - 1)
						{
							temp1+= ", ";
						}
					}

					System.out.println(temp1);
					temp1 = "";
				}

			}
			catch(SQLException sqle)
			{
				printSQLException(sqle);
			}
		}
		else
		{

		}



	}

	/**
	* Get node corresponding to the catalogdb node.
	*
	* @param filename string containing filename of clustercfg
	* @return node corresponding to catalog node
	*/
	public static node getNode(String filename, String nodename) throws IOException
	{
		String hostname, username, password, driver;
		Properties connProps = new Properties();
		FileInputStream fis = new FileInputStream(filename);
		node obtainedNode = new node();
		hostname = username = password = driver = "";

		connProps.load(fis);

		hostname = connProps.getProperty(nodename + ".hostname");
		username = connProps.getProperty(nodename + ".username");
		password = connProps.getProperty(nodename + ".passwd");
        driver   = connProps.getProperty(nodename + ".driver");

        obtainedNode.setHostname(hostname);
        obtainedNode.setUsername(username);
        obtainedNode.setPassword(password);
       	obtainedNode.setDriver(driver);

        System.out.println("DEBUG OUTPUT: " + nodename +" [" + obtainedNode.getHostname() + 
        					", " + obtainedNode.getUsername() + ", " + obtainedNode.getPassword() + "]");
		
		fis.close();

		return obtainedNode;
	}

	/**
	* Neatly prints the contents of the SQLException (this function is from the Apache Derby sample code)
	*
	* @param e the SQLException from which to print details
	*/
	public static void printSQLException(SQLException e)
	{
	    // Unwraps the entire exception chain to unveil the real cause of the
	    // Exception.
	    while (e != null)
	    {
	        System.err.println("\n----- SQLException -----");
	        System.err.println("  SQL State:  " + e.getSQLState());
	        System.err.println("  Error Code: " + e.getErrorCode());
	        System.err.println("  Message:    " + e.getMessage());
	        // for stack traces, refer to derby.log or uncomment this:
	        //e.printStackTrace(System.err);
	        e = e.getNextException();
	    }
	}

	/**
	* Gathers all column names of the table in the nodes we are targeting.
	*
	* @param targetNode the node we are targeting to retrieve column names
	* @param tableName
	* @return LinkedList<String> with column names
	*/
	public static LinkedList<String> getColumnNames(node targetNode, String tableName) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList<String> column_names = new LinkedList<String>();

		try
		{
			conn = DriverManager.getConnection(	targetNode.getHostname(),
												targetNode.getUsername(),
												targetNode.getPassword());

			ps = conn.prepareStatement("SELECT DISTINCT column_name FROM information_schema.columns WHERE table_name=?");
			ps.setString(1, tableName);
			rs = ps.executeQuery();

			while(rs.next())
				column_names.add(rs.getString(1));

			if(ps != null)
				ps.close();
			if(rs != null)
				rs.close();
			if(conn != null)
				conn.close();

			//DEBUG
			System.out.println("DEBUG OUTPUT: column_names from [" + targetNode.getHostname() + "]");

			for(String s : column_names)
				System.out.print(s + " ");

			System.out.println("");

			return column_names;
		}
		catch(SQLException sqle)
		{
			System.out.println("Error: unable to retreive column names from [" + targetNode.getHostname() + "]");
			throw sqle;
		}
	}
	
	/**
	* Gathers all data types of the table in the nodes we are targeting.
	*
	* @param targetNode the node we are targeting to retrieve data types
	* @param tableName
	* @return LinkedList<String> with data types
	*/
	public static LinkedList<String> getDataTypes(node targetNode, String tableName, int numCols) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList<String> data_types = new LinkedList<String>();

		try
		{
			conn = DriverManager.getConnection(	targetNode.getHostname(),
												targetNode.getUsername(),
												targetNode.getPassword());

			ps = conn.prepareStatement("SELECT data_type FROM information_schema.columns WHERE table_name=?");
			ps.setString(1, tableName);
			rs = ps.executeQuery();

			for(int i = 0; rs.next() && i < numCols; i ++)
				data_types.add(rs.getString(1));

			if(ps != null)
				ps.close();
			if(rs != null)
				rs.close();
			if(conn != null)
				conn.close();

			//DEBUG
			System.out.println("DEBUG OUTPUT: data_types from [" + targetNode.getHostname() + "]");

			for(String s : data_types)
				System.out.print(s + " ");

			System.out.println("");

			return data_types;
		}
		catch(SQLException sqle)
		{
			System.out.println("Error: unable to retreive data types from [" + targetNode.getHostname() + "]");
			throw sqle;
		}
	}
}

