import java.util.*;
import java.sql.*;

public class Joiner implements Runnable
{
	private node node1;
	private node node2;
	private String query;
	private String table1Name;

	public Joiner(node node1, node node2)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.query = "";
		this.table1Name = "";
	}

	@Override
	public void run()
	{
		try
		{
			Connection connTest = DriverManager.getConnection(node1.getHostname(),
											  				  node1.getUsername(),
											   				  node1.getPassword());

			Connection conn2Test = DriverManager.getConnection(node2.getHostname(),
											   				   node2.getUsername(),
											   				   node2.getPassword());

			PreparedStatement psTest = null;
			PreparedStatement ps2Test = null;
			ResultSet rsTest = null;
			ResultSet rs2Test = null;

			String create = "";
			String SHOW = "SHOW CREATE TABLE " + table1Name;
			psTest = connTest.prepareStatement(SHOW);
			rsTest = psTest.executeQuery();

			if(rsTest.next())
			{
				create = rsTest.getString(2);
			}

			create = create.replaceAll("CREATE", "CREATE TEMPORARY");
			create = create.replaceAll(table1Name, table1Name+"TEMP");

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