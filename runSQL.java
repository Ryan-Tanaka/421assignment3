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
					t1PartitionMethod = rs.getString(6); // <- little repetetive

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
					t1PartitionMethod = rs.getString(6); // <- little repetetive

					table2_nodes_list.add(temp);
				}

				for(node n: table1_nodes_list)
					n.printNode();

				for(node n: table2_nodes_list)
					n.printNode();

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
}

