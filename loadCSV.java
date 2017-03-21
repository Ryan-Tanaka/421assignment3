import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;
import java.sql.*;
import org.apache.commons.csv.*;

public class loadCSV
{
	private static node catalogNode;
	private static LinkedList<node> nodes_list;
	private static char delimiter;

	public static void executeLoadCSV(String[] args)
	{
		final int NOTPARTITION = 0;
		final int RANGE = 1;
		final int HASH = 2;

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		Properties connProps = new Properties();

		int partitionMethod = 0;
		int numnodes = 0;
		int partitionParam1;

		String tablename;
		String tempString;
		String partitionColumnName = "";
		String getNodesSQL = "SELECT nodedriver, nodeurl, nodeuser, nodepasswd, nodeid FROM dtables WHERE tname = ?";

		nodes_list = new LinkedList<node>();

		ArrayList<String> partParam1_list = new ArrayList<String>();
		ArrayList<String> partParam2_list = new ArrayList<String>();
		
		File csvFile;
		FileInputStream fis;

		delimiter = '|';

		try
		{
			catalogNode = getCatalogDB(args[0]); //get catalog node from clustercfg file
			fis = new FileInputStream(args[0]);
			connProps.load(fis); 
			csvFile = new File(args[1]);
			/*
			* Get table name that we will insert data into. 
			* Get partition method.
			* Get all nodes that contain the specified table.
			*/
			tablename = connProps.getProperty("tablename");
			tempString = connProps.getProperty("partition.method");

			switch(tempString.toLowerCase())
			{
				case "notpartition" : partitionMethod = NOTPARTITION; break;
				case "range"        : partitionMethod = RANGE; break;
				case "hash"			: partitionMethod = HASH; break;
			}

			conn = DriverManager.getConnection( catalogNode.getHostname(),
												catalogNode.getUsername(),
												catalogNode.getPassword());

			ps = conn.prepareStatement(getNodesSQL);
			ps.setString(1, tablename);

			rs = ps.executeQuery();

			while(rs.next())
			{
				node temp = new node();

				temp.setDriver(rs.getString(1));
				temp.setHostname(rs.getString(2));
				temp.setUsername(rs.getString(3));
				temp.setPassword(rs.getString(4));
				temp.setNodeNum(Integer.parseInt(rs.getString(5)));

				nodes_list.add(temp);
			}

			if(rs != null)
				rs.close();
			
			switch(partitionMethod)
			{
				case NOTPARTITION :
					//load csv
					loadNotPartition(csvFile, tablename, nodes_list); 

					//update dtables in catalog node 
					ps = conn.prepareStatement("UPDATE dtables SET partmtd=? WHERE tname=?");
					ps.setInt(1, NOTPARTITION);
					ps.setString(2, tablename);
					
					try
					{
						ps.executeUpdate();
						System.out.println("[" + catalogNode.getHostname() + "]: catalog updated");

						if(ps != null)
								ps.close();
						if(conn != null)
							conn.close();						
					}
					catch(SQLException e)
					{
						System.out.println("[" + catalogNode.getHostname() + "]: failed to update");
					}
					break;

				case RANGE :
					//get partition info
					numnodes = Integer.parseInt(connProps.getProperty("numnodes"));
					partitionColumnName = connProps.getProperty("partition.column");

					System.out.println("DEBUG OUTPUT: numNodes = " + numnodes);
					System.out.println("DEBUG OUTPUT: partitionColumnName = " + partitionColumnName);

					for(int i = 1; i <= numnodes; i++)
					{
						partParam1_list.add(connProps.getProperty("partition.node" + i + ".param" + 1));
						partParam2_list.add(connProps.getProperty("partition.node" + i + ".param" + 2));
					}

					loadRangePartition(csvFile, tablename, new ArrayList<node>(nodes_list), partitionColumnName,
										partParam1_list, partParam2_list);

					break;

				case HASH : 
					//get partition info
					partitionParam1 = Integer.parseInt(connProps.getProperty("partition.param1"));
                                        partitionColumnName = connProps.getProperty("partition.column");

					loadHashPartition(csvFile, tablename, new ArrayList<node>(nodes_list), partitionColumnName, partitionParam1);
					break;


			}
			
			fis.close();

		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e)
		{
			printSQLException(e);
		}
		catch(NoSuchElementException e)
		{
			e.printStackTrace();
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadNotPartition(File file, String tablename, LinkedList<node> nodes)
		throws FileNotFoundException, SQLException, IOException, ParseException
	{
		Connection conn = null;
		Statement s = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int numCols = 0;
		int numRows = 0;
		int batchSize = 0;
		String insertSQL;
		String value, dataType, temp;
		LinkedList<Connection> connection_list = new LinkedList<Connection>();
		LinkedList<PreparedStatement> ps_list = new LinkedList<PreparedStatement>();
		LinkedList<String> data_types = new LinkedList<String>();

		value = dataType = temp = "";

		//get number of rows in csv file
		numRows = getFileNumberOfLines(file);

		//get number of columns
		numCols = getColumnNames(nodes.getFirst(), tablename).size();		

		//get data types
		data_types = getDataTypes(nodes.getFirst(), tablename, numCols);

		System.out.println("DEBUG OUTPUT: numCols = " + numCols);

		insertSQL = buildPreparedStatementString(numCols, tablename);

		try
		{
			// get a connection to each node and a corresponding prepared statement
			for(node n: nodes)
			{
				try
				{
					connection_list.add(DriverManager.getConnection( n.getHostname(),
																	 n.getUsername(),
																	 n.getPassword()));

					connection_list.getLast().setAutoCommit(false);

					ps_list.add(connection_list.getLast().prepareStatement(insertSQL));
				}
				catch(SQLException sqle)
				{
					System.out.println("Error @ [" + n.getHostname() + "]");
					throw sqle;					
				}

			}
			
			/**
			* This is where things get a little messy. For each row, I set appropriate values
			* for each column in each preparedStatement based on their data type. This is so that my loader
			* can at least be a little generic. The segment of code will batch them into groups
			* of 50 before executing the batch. 
			*/
			batchSize = 1; 

			Reader in = new FileReader(file);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter(delimiter).parse(in);

			for(CSVRecord record: records)
			{
				for(PreparedStatement pstmt : ps_list)
				{				
					for(int i = 1; i <= numCols; i++)
					{
						value = record.get(i - 1); //at the (i - 1)th column, get that value as a string
						dataType = data_types.get(i - 1); //get the exact data type of the value we just obtained

						switch(dataType.toLowerCase()) //based on the data type of value, we set the prepareStatement appropriately (for not few types)
						{
							case "char"    : pstmt.setString(i, value); break;
							case "varchar" : pstmt.setString(i, value); break;
							case "int"     : pstmt.setInt(i, Integer.parseInt(value)); break;
							case "decimal" : pstmt.setBigDecimal(i, new BigDecimal(value)); break; //apparently decimal is BigDecimal in java
							
							case "date"    : SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //convert to appropriate date type
											 java.util.Date date = sdf.parse(value);
											 java.sql.Date sqlDate = new java.sql.Date(date.getTime());
											 pstmt.setDate(i, sqlDate);
											 break;
						}
					}

					pstmt.addBatch();
				}
				
				batchSize++; //every 50 rows we will execute the batch

				for(int z = 0; z < nodes.size(); z++)
				{
					try
					{
						if(batchSize % 50 == 0 || batchSize >= numRows)
							ps_list.get(z).executeBatch();
					}
					catch(SQLException sqle)
					{
						System.out.println("Error @ [" + nodes_list.get(z).getHostname() + "]");
						throw sqle;	
					}				

				}
			}

			for(Connection connection : connection_list) //call commit on all connections 
			{
				if(connection != null)
				{
					connection.commit();
					connection.close();
				}
			}

			for(node n : nodes)
				System.out.println("[" + n.getHostname() + "]: " + numRows + "rows inserted");			
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Error: csv file not found..");
			throw fnfe;
		}
		catch(SQLException sqle)
		{
			for(Connection connection: connection_list)
				connection.rollback();

			throw sqle;
		}
		finally
		{
			try
			{
				for(Connection connection : connection_list)
				{
					if(connection != null)
						connection.close();
				}				
			}
			catch(SQLException sqle)
			{
				throw sqle;
			}
		}
	}

	public static void loadRangePartition(File file, String tablename, ArrayList<node> nodes, 
										  String partitionColumnName, ArrayList<String> partParam1_list,
										  ArrayList<String> partParam2_list)
	throws FileNotFoundException, SQLException, IOException, ParseException
	{
		Connection conn = null;
		Statement s = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int numCols = 0;
		int numRows = 0;
		int batchSize1 = 0;
		int batchSize2 = 0;
		int indexOfPartCol = 0;
		int sendToNode = 0;

		String value, dataType, temp, insertSQL;
		final String UPDATESQL = ("UPDATE dtables SET partmtd=1, partcol='" + partitionColumnName + "', partparam1=?, partparam2=? WHERE tname='" + tablename + "' AND nodeid=?");

		ArrayList<Connection> connection_list = new ArrayList<Connection>();
		ArrayList<PreparedStatement> ps_list = new ArrayList<PreparedStatement>();
		ArrayList<String> row = new ArrayList<String>();
		int[] batchSize_list = new int[nodes.size()]; 

		ArrayList<String> data_types;
		ArrayList<String> column_names;

		value = dataType = temp = "";

		//get number of rows in csv file
		numRows = getFileNumberOfLines(file);

		//get number of columns
		column_names = new ArrayList<String>(getColumnNames(nodes.get(0), tablename));

		numCols = column_names.size();

		//get data types
		data_types = new ArrayList<String>(getDataTypes(nodes.get(0), tablename, numCols));

		System.out.println("DEBUG OUTPUT: numCols = " + numCols);

		insertSQL = buildPreparedStatementString(numCols, tablename);

		for(int i = 0; i < numCols; i++)
		{
			if(column_names.get(i).equalsIgnoreCase(partitionColumnName))
			{
				indexOfPartCol = i;
				break;
			}
		}

		try
		{
			for(node n: nodes)
			{
				try
				{
					connection_list.add(DriverManager.getConnection( n.getHostname(),
																	 n.getUsername(),
																	 n.getPassword()));

					connection_list.get(connection_list.size() - 1).setAutoCommit(false);

					ps_list.add(connection_list.get(connection_list.size() - 1).prepareStatement(insertSQL));					
				}
				catch(SQLException sqle)
				{
					System.out.println("Error @ [" + n.getHostname() + "]");
					throw sqle;
				}		

			}

			for(int i = 0; i < ps_list.size(); i++)
			{
				batchSize_list[i] = 0;
			}

			for(int i = 0; i < column_names.size(); i++)
			{
				if(partitionColumnName.equalsIgnoreCase(column_names.get(i)))
				{
					indexOfPartCol = i;
					break;
				}
			}

			System.out.println("DEBUG OUTPUT: indexOfPartCol = " + indexOfPartCol);

			System.out.print("DEBUG OUTPUT: partParam1_list = ");
			for(String z : partParam1_list)
			{
				System.out.print(z + " ");
			}
			System.out.println("");

			System.out.print("DEBUG OUTPUT: partParam2_list = ");
			for(String y : partParam2_list)
			{
				System.out.print(y + " ");
			}
			System.out.println("");

			Reader in = new FileReader(file);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter(delimiter).parse(in);

			int count = 0;

			for(CSVRecord record: records)
			{
				row.clear();
				count++;

				for(int i = 1; i <= numCols; i++)
				{
					row.add(record.get(i - 1));
				}

				for(int i = 0; i < nodes.size(); i++)
				{
					/*
					System.out.println("DEBUG OUTPUT: row = ");
					for(String r : row)
						System.out.print(r + " | ");
					System.out.println("");

					System.out.println("checkPartition(row.get(" + indexOfPartCol + "), partParam1_list.get(" + i + "), partParam2_list.get(" + i + ") = " + checkPartition(row.get(indexOfPartCol), partParam1_list.get(i), partParam2_list.get(i)));
					*/

					if(checkPartition(row.get(indexOfPartCol), partParam1_list.get(i), partParam2_list.get(i)))
					{
						for(int k = 1; k <= numCols; k++)
						{
							value = row.get(k - 1);
							dataType = data_types.get(k - 1);

							switch(dataType.toLowerCase())
							{
								case "char"    : ps_list.get(i).setString(k, value); break;
								case "varchar" : ps_list.get(i).setString(k, value); break;
								case "int"     : ps_list.get(i).setInt(k, Integer.parseInt(value)); break;
								case "decimal" : ps_list.get(i).setBigDecimal(k, new BigDecimal(value)); break; //apparently decimal is BigDecimal in java
								
								case "date"    : SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //convert to appropriate date type
												 java.util.Date date = sdf.parse(value);
												 java.sql.Date sqlDate = new java.sql.Date(date.getTime());
												 ps_list.get(i).setDate(k, sqlDate);
												 break;
							}
						}

						ps_list.get(i).addBatch();
						batchSize_list[i]++;
						//System.out.println("batchSize_list[" + i +"] = " + batchSize_list[i]);
					}
				}

				for(int j = 0; j < ps_list.size(); j++)
				{
					if(batchSize_list[j] % 5 == 0 || count >= numRows)
					{
						try
						{
							ps_list.get(j).executeBatch();
						}
						catch(SQLException sqle)
						{
							System.out.println("Error @ [" + nodes_list.get(j).getHostname() + "]");
							throw sqle;
						}
					}
				}
			}

			for(Connection connection : connection_list)
			{
				if(connection != null)
				{
					connection.commit();
				}
			}

			for(int i = 0; i < nodes.size(); i++)
				System.out.println("[" + nodes.get(i).getHostname() + "]: " + batchSize_list[i] + " rows inserted");

			try
			{
				conn = DriverManager.getConnection( catalogNode.getHostname(),
													catalogNode.getUsername(),
													catalogNode.getPassword());

				ps = conn.prepareStatement(UPDATESQL);

				for(int b = 0; b < nodes.size(); b++)
				{
					ps.setString(1, partParam1_list.get(b));
					ps.setString(2, partParam2_list.get(b));
					ps.setInt(3, nodes.get(b).getNodeNum());
					ps.executeUpdate();
				}

				System.out.println("[" + catalogNode.getHostname() + "]: catalog updated");
			}
			catch(SQLException sqle)
			{
				System.out.println("[" + catalogNode.getHostname() + "]: failed to update");
				throw sqle;
			}
			finally
			{
				try
				{
					if(ps != null)
						ps.close();
					if(conn != null)
						conn.close();					
				}
				catch(SQLException sqle)
				{
					throw sqle;
				}
			}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Error: csv file not found..");
			throw fnfe;
		}
		catch(SQLException sqle)
		{
			for(Connection connection: connection_list)
				connection.rollback();

			throw sqle;
		}
		finally
		{
			try
			{
				for(Connection connection : connection_list)
				{
					if(connection != null)
						connection.close();
				}
			}
			catch(SQLException sqle)
			{
				throw sqle;
			}
		}
	}	

	public static void loadHashPartition(File file, String tablename, ArrayList<node> nodes, String partitionColumnName,
										 int partitionParam1)
		throws FileNotFoundException, SQLException, IOException, ParseException
	{
		Connection conn = null;
		Statement s = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int numCols = 0;
		int numRows = 0;
		int batchSize1 = 0;
		int batchSize2 = 0;
		int indexOfPartCol = 0;
		int sendToNode = 0;

		String value, dataType, temp, insertSQL;
		final String UPDATESQL = ("UPDATE dtables SET partmtd=2, partcol='" + partitionColumnName + "', partparam1=? WHERE tname='" + tablename + "' AND nodeid=?");
                System.out.println("updatesql: " + UPDATESQL);
		ArrayList<Connection> connection_list = new ArrayList<Connection>();
		ArrayList<PreparedStatement> ps_list = new ArrayList<PreparedStatement>();
		ArrayList<String> row = new ArrayList<String>();
		int[] batchSize_list = new int[nodes.size()]; 

		ArrayList<String> data_types;
		ArrayList<String> column_names;

		value = dataType = temp = "";

		//get number of rows in csv file
		numRows = getFileNumberOfLines(file);

		//get number of columns
		column_names = new ArrayList<String>(getColumnNames(nodes.get(0), tablename));

		numCols = column_names.size();

		//get data types
		data_types = new ArrayList<String>(getDataTypes(nodes.get(0), tablename, numCols));

		System.out.println("DEBUG OUTPUT: numCols = " + numCols);

		insertSQL = buildPreparedStatementString(numCols, tablename);

		for(int i = 0; i < numCols; i++)
		{
			if(column_names.get(i).equalsIgnoreCase(partitionColumnName))
			{
				indexOfPartCol = i;
				break;
			}
		}

		try
		{
			for(node n: nodes)
			{
				try
				{
					connection_list.add(DriverManager.getConnection( n.getHostname(),
																	 n.getUsername(),
																	 n.getPassword()));

					connection_list.get(connection_list.size() - 1).setAutoCommit(false);

					ps_list.add(connection_list.get(connection_list.size() - 1).prepareStatement(insertSQL));					
				}
				catch(SQLException sqle)
				{
					System.out.println("Error @ [" + n.getHostname() + "]");
					throw sqle;
				}		

			}

			for(int i = 0; i < ps_list.size(); i++)
			{
				batchSize_list[i] = 0;
			}

			for(int i = 0; i < column_names.size(); i++)
			{
				if(partitionColumnName.equalsIgnoreCase(column_names.get(i)))
				{
					indexOfPartCol = i;
					break;
				}
			}

			Reader in = new FileReader(file);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter(delimiter).parse(in);

			int count = 0;
			int bucket = 0;
			int tempInt = 0;

			for(CSVRecord record: records)
			{
				row.clear();
				count++;

				for(int i = 1; i <= numCols; i++)
				{
					row.add(record.get(i - 1));
				}

				bucket = (Integer.parseInt(row.get(indexOfPartCol)) % partitionParam1) + 1;

				for(int i = 0; i < nodes.size(); i++)
				{
					if(nodes.get(i).getNodeNum() == bucket)
					{
						for(int k = 1; k <= numCols; k++)
						{
							value = row.get(k - 1);
							dataType = data_types.get(k - 1);

							switch(dataType.toLowerCase())
							{
								case "char"    : ps_list.get(i).setString(k, value); break;
								case "varchar" : ps_list.get(i).setString(k, value); break;
								case "int"     : ps_list.get(i).setInt(k, Integer.parseInt(value)); break;
								case "decimal" : ps_list.get(i).setBigDecimal(k, new BigDecimal(value)); break; //apparently decimal is BigDecimal in java
								
								case "date"    : SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //convert to appropriate date type
												 java.util.Date date = sdf.parse(value);
												 java.sql.Date sqlDate = new java.sql.Date(date.getTime());
												 ps_list.get(i).setDate(k, sqlDate);
												 break;
							}
						}

						ps_list.get(i).addBatch();
						batchSize_list[i]++;
					}
				}

				for(int j = 0; j < ps_list.size(); j++)
				{
					if(batchSize_list[j] % 5 == 0 || count >= numRows)
					{
						try
						{
							ps_list.get(j).executeBatch();
						}
						catch(SQLException sqle)
						{
							System.out.println("Error @ [" + nodes_list.get(j).getHostname() + "]");
							throw sqle;
						}
					}
				}
			}

			for(Connection connection : connection_list)
			{
				if(connection != null)
				{
					connection.commit();
				}
			}

			for(int i = 0; i < nodes.size(); i++)
				System.out.println("[" + nodes.get(i).getHostname() + "]: " + batchSize_list[i] + " rows inserted");

			try
			{
				conn = DriverManager.getConnection( catalogNode.getHostname(),
													catalogNode.getUsername(),
													catalogNode.getPassword());

				ps = conn.prepareStatement(UPDATESQL);

				for(int b = 0; b < nodes.size(); b++)
				{
					ps.setString(1, partitionParam1 + "");
					ps.setInt(2, nodes.get(b).getNodeNum());
					ps.executeUpdate();
				}

				System.out.println("[" + catalogNode.getHostname() + "]: catalog updated");
			}
			catch(SQLException sqle)
			{
				System.out.println("[" + catalogNode.getHostname() + "]: failed to update");
				throw sqle;
			}
			finally
			{
				try
				{
					if(ps != null)
						ps.close();
					if(conn != null)
						conn.close();					
				}
				catch(SQLException sqle)
				{
					throw sqle;
				}
			}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Error: csv file not found..");
			throw fnfe;
		}
		catch(SQLException sqle)
		{
			for(Connection connection: connection_list)
				connection.rollback();

			throw sqle;
		}
		finally
		{
			try
			{
				for(Connection connection : connection_list)
				{
					if(connection != null)
						connection.close();
				}
			}
			catch(SQLException sqle)
			{
				throw sqle;
			}
		}
	}

	/**
	* Checks whether or not the given value fits within the given partition.
	* 
	* @param value is the String representation of the value given
	* @param partParam1 is the String representation of the lower bound of the partition
	* @param partParam2 is the String representation of the upper bound of the partition 
	*
	* @return true or false if the value fits within the partition or not 
	*/
	public static boolean checkPartition(String value, String partParam1, String partParam2)
	{
		int temp1, temp2, tempValue;
		boolean fitsInPartition = false;

		final boolean PARTPARAM1BOUNDARY = partParam1.equalsIgnoreCase("-inf");
		final boolean PARTPARAM2BOUNDARY = partParam2.equalsIgnoreCase("+inf");

		//case 1: partparam1 & partparam2 = -+inf
		if(PARTPARAM1BOUNDARY && PARTPARAM2BOUNDARY)
		{
			fitsInPartition = true;
		}
		//case 2: partparam1 = -inf
		else if(PARTPARAM1BOUNDARY)
		{
			temp2 = Integer.parseInt(partParam2);

			if(value.equalsIgnoreCase("-inf"))
				fitsInPartition = true;

			else if(Integer.parseInt(value) < temp2)
				fitsInPartition = true;
		}
		//case 3: partparam2 = +inf
		else if(PARTPARAM2BOUNDARY)
		{
			temp1 = Integer.parseInt(partParam1);

			if(value.equalsIgnoreCase("+inf"))
				fitsInPartition = true;

			else if(Integer.parseInt(value) > temp1)
				fitsInPartition = true;
		}
		//case 4: bounded on both sides by integer values
		else
		{
			temp1 = Integer.parseInt(partParam1);
			temp2 = Integer.parseInt(partParam2);

			if(!(value.equalsIgnoreCase("-inf")) && !(value.equalsIgnoreCase("+inf")))
			{
				tempValue = Integer.parseInt(value);

				if(tempValue > temp1 && tempValue <= temp2)
					fitsInPartition = true;
			}
		}

		return fitsInPartition;
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
	* Get node corresponding to the catalogdb node.
	*
	* @param filename string containing filename of clustercfg
	* @return node corresponding to catalog node
	*/
	public static node getCatalogDB(String filename) throws IOException
	{
		String hostname, username, password, driver;
		Properties connProps = new Properties();
		FileInputStream fis = new FileInputStream(filename);
		node catalogNode = new node();
		hostname = username = password = driver = "";

		connProps.load(fis);

		hostname = connProps.getProperty("catalog.hostname");
		username = connProps.getProperty("catalog.username");
		password = connProps.getProperty("catalog.passwd");
        driver   = connProps.getProperty("catalog.driver");

        catalogNode.setHostname(hostname);
        catalogNode.setUsername(username);
        catalogNode.setPassword(password);
        catalogNode.setDriver(driver);

        System.out.println("DEBUG OUTPUT: CatalogDB [" + catalogNode.getHostname() + ", " + catalogNode.getUsername() + ", " + catalogNode.getPassword() + "]");
		
		fis.close();

		return catalogNode;
	}

	/**
	* Gets the number of lines from the csv file provided. The file must not contain
	* and empty lines in the begining or end to provide correct number.
	*
	* @param csvFile the File object that corresponds to the csv file
	* @return number of lines in csv file
	*/
	public static int getFileNumberOfLines(File csvFile) throws IOException
	{
		int numberOfLines = 0;
		String temp = "";

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(csvFile));

			while((temp = br.readLine()) != null)
				numberOfLines++;

			br.close();

			System.out.println("DEBUG OUTPUT: numberOfLines=" + numberOfLines); 

			return numberOfLines;
		}
		catch(IOException io)
		{
			System.out.println("Error: csv file not found..");
			throw io;
		}
	}

	/**
	* Creates the string to be used in the prepared statement based
	* on the number of columns that the table contains.
	* Works by adding ? marks into "INSERT INTO tablename VALUES(?,?,?,.....?)"
	*
	* @param numberOfColumns the number of columns in the table
	* @return string containing insert statement with appropriate number of ? marks
	*/
	public static String buildPreparedStatementString(int numberOfColumns, String tablename)
	{
		String insertSQL = "insert into " + tablename + " values("; //we will build this string up 

		for(int i = 0; i < numberOfColumns; i++)
		{
			if(i < numberOfColumns - 1)
				insertSQL += "?, ";
			else
				insertSQL += "?)"; 
		}

		System.out.println("DEBUG OUTPUT: insertSQL=" + insertSQL);

		return insertSQL;
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

/**
* things to consolidate
*	1. use private static variables for lists all lists (use hash.map instead)
*/


