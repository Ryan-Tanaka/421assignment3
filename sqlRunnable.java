import java.sql.*;
import java.util.*;
import java.lang.Thread;

/**
* Private inner class that uses threads to connect to each node in the cluster.
* Member variable "hostname" is used to locate where "from clause" information 
* is contained. 
*/
public class sqlRunnable implements Runnable
{
	node targetNode;
	String query;
	String tablename;

	public sqlRunnable() {} //empty constructor

	public sqlRunnable(node targetNode, String query, String tablename)
	{
		this.targetNode = targetNode;
		this.query = query;
		this.tablename = tablename;
	}

	public void run()
	{
		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;

		try
		{
			conn = DriverManager.getConnection( targetNode.getHostname() + "?useSSL=false",
												targetNode.getUsername(),
												targetNode.getPassword());

			Class.forName(targetNode.getDriver());

			s = conn.createStatement();
			rs = s.executeQuery(query);

			LinkedList<String> colNames = getColumnNames(targetNode, tablename);

			int numCols = colNames.size();
			//LinkedList<String> dataTypes = getDataTypes(node1, table1Name, numCols);

			String temp = "";

			while(rs.next())
			{
				for(int i = 1; i <= numCols; i++)
				{
					temp += rs.getString(i) + " | ";
				}

				System.out.println(temp);
				temp = "";
			}

			if(s != null)
				s.close();
			if(rs != null)
				rs.close();
			if(conn != null)
				conn.close();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e)
		{
			printSQLException(e);
			targetNode.setStatus(0);
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