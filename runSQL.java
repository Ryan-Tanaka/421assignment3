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

	private static String sqlFileQuery;
	private static String table1Name;

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

		//System.out.println("col_data: " + sqlStat.col_data.toString());
		//System.out.println("table_data: " + sqlStat.table_data.toString());
		//System.out.println("cond_data: " + sqlStat.cond_data.toString());

		sqlFileQuery = "";
		is.close();

		is = new FileInputStream(inputFile);

		while(is.available() > 0)
		{
			sqlFileQuery += (char)is.read();
		}

		//System.out.println(sqlFileQuery);

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

				conn = DriverManager.getConnection(catalogNode.getHostname() + "?useSSL=false",
												   catalogNode.getUsername(),
												   catalogNode.getPassword());

				ps = conn.prepareStatement(GETNODESQL);
				table1Name = sqlStat.table_data.get(0);
				//System.out.println("tabl1Name = " + table1Name);
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
					t1PartitionMethod = rs.getString(6); // <- yes this a little repetitive

					table1_nodes_list.add(temp);
				}

				String table2Name = sqlStat.table_data.get(2);
				//System.out.println("tabl2Name = " + table2Name);
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

				/*
				for(node n: table1_nodes_list)
					n.printNode();

				for(node n: table2_nodes_list)
					n.printNode();
				*/

				Thread[] joiner_threads;
				int nodeList1Size = table1_nodes_list.size();
				int nodeList2Size = table2_nodes_list.size();
				int numSelectCols = 0;

				//System.out.println("table1name : " + table1Name);
				//System.out.println("table2name : " + table2Name);

				LinkedList<String> tableCols1 = getColumnNames(table1_nodes_list.get(0), table1Name);
				LinkedList<String> tableCols2 = getColumnNames(table2_nodes_list.get(0), table2Name);
				
				//System.out.println(sqlStat.col_data.size() + " is the size");
				//System.out.println("select * gives me : " + sqlStat.col_data.get(0));

				if(sqlStat.col_data.get(0).equals("*"))
				{
					numSelectCols = tableCols1.size() + tableCols2.size();
				}
				else
				{
					numSelectCols = sqlStat.col_data.size();
				}
				//System.out.println("partition methods : " + t1PartitionMethod + " " + t2PartitionMethod);

				//send out task
				if(t1PartitionMethod.equals("0") && t2PartitionMethod.equals("0"))
				{
					//just use 1 pair of nodes
					Thread joinerThread = new Thread(new Joiner(table1_nodes_list.get(0), table2_nodes_list.get(0), 
																sqlFileQuery, numSelectCols, table1Name));

					joinerThread.start();
					joinerThread.join();
				}
				else
				{
					if(t1PartitionMethod.equals("0"))  //at least 1 has partition method
					{
						joiner_threads = new Thread[nodeList2Size];

						for(int i = 0; i < nodeList2Size; i++)
						{
							joiner_threads[i] = new Thread(new Joiner(table2_nodes_list.get(i), table1_nodes_list.get(0), 
																		sqlFileQuery, numSelectCols, table1Name));
						}

						for(Thread t : joiner_threads)
						{
							t.start();
						}

						for(Thread t : joiner_threads)
						{
							t.join();
						}
					}
					else if(t2PartitionMethod.equals("0"))
					{
						joiner_threads = new Thread[nodeList1Size];

						for(int i = 0; i < nodeList1Size; i++)
						{
							joiner_threads[i] = new Thread(new Joiner(table1_nodes_list.get(i), table2_nodes_list.get(0), 
																		sqlFileQuery, numSelectCols, table1Name));
						}

						for(Thread t : joiner_threads)
						{
							t.start();
						}

						for(Thread t : joiner_threads)
						{
							t.join();
						}
					}
					else  //both have range or hash partition in this case
					{
						joiner_threads = new Thread[nodeList1Size * nodeList2Size];

						for(int i = 0; i < nodeList1Size; i++)
						{
							for(int j = 0; j < nodeList2Size; j++)
							{
								int offset = (i * nodeList2Size) + j;
								joiner_threads[offset] = new Thread(new Joiner(table1_nodes_list.get(i), table2_nodes_list.get(j), 
																				sqlFileQuery, numSelectCols, table1Name));
								//System.out.println("offset : " + offset);
							}
						}
						//System.out.println("number of threads : " + joiner_threads.length);

						for(Thread jd: joiner_threads)
						{
							jd.start();
						}

						for(Thread jdj: joiner_threads)
						{
							jdj.join();
						}
					}
				}
			}
			catch(SQLException sqle)
			{
				System.out.println(inputFile + " failed");
			}
		}
		else
		{
			try
			{
				conn = DriverManager.getConnection(catalogNode.getHostname() + "?useSSL=false",
												   catalogNode.getUsername(),
												   catalogNode.getPassword());

				ps = conn.prepareStatement(GETNODESQL);

				String table1Name = sqlStat.table_data.get(0);
				//System.out.println("tabl1Name = " + table1Name);
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

					table1_nodes_list.add(temp);
				}

				Thread[] threads = new Thread[table1_nodes_list.size()];

				for(int i = 0; i < table1_nodes_list.size(); i++)
				{
					threads[i] = new Thread(new sqlRunnable(table1_nodes_list.get(i), sqlFileQuery, table1Name));
				}

				for(Thread t : threads)
				{
					t.start();
				}

				for(Thread th : threads)
				{
					th.join();
				}

				for(node n : table1_nodes_list)
				{
					if(n.getStatus() == 0)
					{
						System.out.println("[" + n.getHostname() + "] " + inputFile + " failed");
					}
					else
					{
						System.out.println("[" + n.getHostname() + "] " + inputFile + " success");
					}
				}
			}
			catch(SQLException sqle)
			{
				printSQLException(sqle);
			}
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

       	/*
        System.out.println("DEBUG OUTPUT: " + nodename +" [" + obtainedNode.getHostname() + 
        					", " + obtainedNode.getUsername() + ", " + obtainedNode.getPassword() + "]");
		*/

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
			conn = DriverManager.getConnection(	targetNode.getHostname() + "?useSSL=false",
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
			/*
			System.out.println("DEBUG OUTPUT: column_names from [" + targetNode.getHostname() + "]");

			for(String s : column_names)
				System.out.print(s + " ");

			System.out.println("");
			*/

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
			conn = DriverManager.getConnection(	targetNode.getHostname() + "?useSSL=false",
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
			/*
			System.out.println("DEBUG OUTPUT: data_types from [" + targetNode.getHostname() + "]");

			for(String s : data_types)
				System.out.print(s + " ");

			System.out.println("");
			*/

			return data_types;
		}
		catch(SQLException sqle)
		{
			System.out.println("Error: unable to retreive data types from [" + targetNode.getHostname() + "]");
			throw sqle;
		}
	}
}

