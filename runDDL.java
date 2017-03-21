
import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;

public class runDDL
{
	private static node[] nodes;
	private static String ddl;
    private static String ddlFileName;
	private static int numNodes;
    private static String catalogddl = "CREATE TABLE IF NOT EXISTS dtables(tname char(32), nodedriver char(64), nodeurl char(128), nodeuser char(16), nodepasswd char(16), partmtd int, nodeid int, partcol char(32), partparam1 char(32), partparam2 char(32))"; 
    
	public static void executeRunDDL(String[] args) throws InterruptedException
	{
    	String hostname, username, password, driver;
    	Properties connProps = new Properties();
    	Scanner input;
    	Thread[] threads;
        Connection conn = null;
        Statement s = null;
    	hostname = username = password = ddl = driver = "";

    	if(args.length == 2)
    	{
    		try
    		{
                /**
                * Grab DDL from file and store into string. Also remove
                * ending semicolon. 
                */
                ddlFileName = args[1];
    			input = new Scanner(new File(args[1]));
    			while(input.hasNext())
    			{
    				ddl += input.nextLine() + " ";
    			}

                ddl = ddl.replace(';', ' ');

    			input.close();

                /**
                * Load the properties file, obtain all properties and store them in nodes[].
                */
    			connProps.load(new FileInputStream(args[0]));
    			numNodes = Integer.parseInt(connProps.getProperty("numnodes"));

    			//now that we know the size of our cluster, we can define the nodes array 
    			nodes = new node[numNodes + 1];

    			//change later but this is for the threads array
    			threads = new Thread[numNodes + 1];

    			//add catalog node to the 0th index of the array
    			hostname = connProps.getProperty("catalog.hostname");
    			username = connProps.getProperty("catalog.username");
    			password = connProps.getProperty("catalog.passwd");
                driver   = connProps.getProperty("catalog.driver");

    			nodes[0] = new node(hostname, username, password, 0, driver);

                /**
                * This try catch block will check whether or not the table already exists, since DERBY does not support
                * IF NOT EXISTS in DDL. If Derby supported the NOT EXISTS statement, then I would not use nested try catch blocks. 
                */
                /*
                try
                {
                    conn = DriverManager.getConnection(hostname + ";create=true", username, password);
                    s = conn.createStatement();
                    s.execute(catalogddl);
                    conn.commit(); 
                }
                catch(SQLException sqle)
                {
                    if(sqle.getSQLState().equals("X0Y32"))
                    {
                        //if the table exists, we do nothing and resume at the end of THIS catch block
                    }
                    else
                    {
                        throw sqle;  
                    }
                }
                */
                
    			for(int i = 1; i < nodes.length; i++)
    			{
    				hostname = username = password = "";
    				hostname = connProps.getProperty("node" + i + ".hostname");
    				username = connProps.getProperty("node" + i + ".username");
    				password = connProps.getProperty("node" + i + ".passwd");
                    driver   = connProps.getProperty("node" + i + ".driver");

    				nodes[i] = new node(hostname, username, password, i, driver);
    			}

                /**
                * Starting a thread for each node that we need to connect to, then
                * calling join on all. If any error occurs at one, the others will still
                * execute their DDL statements, and the DB with metadate will be 
                * updated accordingly.
                */
    			for(int j = 1; j < threads.length; j++)
    			{
    				threads[j] = new Thread(new ddlRunnable(j));
    				threads[j].start();
    			}
                        
    			for(int i = 1; i < threads.length; i++)
    			{
    				threads[i].join();
    			}
    		}
    		catch(IOException io)
    		{
    			System.out.println(io.getMessage());
    		}
    	}
    	else //if user does not enter propper input, then inform propper usage and exit
    	{
    		System.out.println("--ERROR-------------------------------------");
    		System.out.println("Usage: java runDDL clustercfg [DDL filename]");
    		System.out.println("--------------------------------------------");
    	}
	}

    /**
    * Private inner class that uses threads to connect to each node in the cluster.
    * Member variable "targetNode" is used to locate node information contained
    * in the nodes array. 
    */
	private static class ddlRunnable implements Runnable
	{
		private int targetNode; //holds the index of node that thread will connect to
        private String hostname; //holds the hostname to which the class will connect to 

		public ddlRunnable()
		{
			//need this constructor here even though it isn't used 
		}

		public ddlRunnable(int targetNode)
		{
			this.targetNode = targetNode;
		}

		public void run()
		{
			try
			{
                long threadId = Thread.currentThread().getId();
				Connection conn = null;
				Statement s = null;
				String username, password, driver;
                String[] firstThreeWords;
                PreparedStatement updateStmt, deleteStmt;

                //System.out.println("----- threadid: " + threadId + " -----");  //to see threadid, uncomment

				hostname = nodes[targetNode].getHostname();
				username = nodes[targetNode].getUsername();
				password = nodes[targetNode].getPassword();
                
				conn = DriverManager.getConnection(hostname + "?useSSL=false", username, password);
				s = conn.createStatement();
				s.execute(ddl);
                conn.setAutoCommit(false);
				conn.commit();

                System.out.println("[" + hostname + "]: " + ddlFileName + " success");

                Thread.sleep(1000);

				if(conn != null)
                {
					conn.close();
                }

                //this section will update the catalogdb
                hostname = nodes[0].getHostname();
                username = nodes[0].getUsername();
                password = nodes[0].getPassword();
                driver   = nodes[0].getDriver();

                firstThreeWords = simpleParse(ddl);
                
                /**
                * This else if statement runs if the DDL is a drop table statement. If so, I only have 1 thread run 
                * the "delete from dtables" because it isn't necessary to run it from every single thread. 
                */

                if(firstThreeWords[0].equalsIgnoreCase("create"))
                {
                    conn = DriverManager.getConnection(hostname + "?useSSL=false", username, password);
                    conn.setAutoCommit(false);
                    updateStmt = conn.prepareStatement("insert into dtables values(?,?,?,?,?,?,?,?,?,?)");

                    updateStmt.setString(1, firstThreeWords[2]);              //tname
                    updateStmt.setString(2, nodes[targetNode].getDriver());   //nodedriver
                    updateStmt.setString(3, nodes[targetNode].getHostname()); //nodeurl
                    updateStmt.setString(4, nodes[targetNode].getUsername()); //nodeuser
                    updateStmt.setString(5, nodes[targetNode].getPassword()); //nodepasswd
                    updateStmt.setInt(6, -1);                                 //partmtd (int cant be null so -1)
                    updateStmt.setInt(7, nodes[targetNode].getNodeNum());     //nodeid 
                    updateStmt.setString(8, null);                            //partcol
                    updateStmt.setString(9, null);                            //partparam1
                    updateStmt.setString(10, null);                           //partparam2

                    updateStmt.executeUpdate();
                    updateStmt.close();
                    conn.commit();

                    System.out.println("[" + hostname + "]: catalog updated [" + nodes[targetNode].getHostname() + "]"); 
                    
                }
                else 
                {
                    conn = DriverManager.getConnection(hostname + "?useSSL=false", username, password);
                    conn.setAutoCommit(false);
                    deleteStmt = conn.prepareStatement("delete from dtables where tname = ? and nodeurl = ?");

                    deleteStmt.setString(1, firstThreeWords[2].toUpperCase());
                    deleteStmt.setString(2, nodes[targetNode].getHostname());

                    deleteStmt.executeUpdate();
                    deleteStmt.close();
                    conn.commit();

                    System.out.println("[" + hostname + "]: catalog removed [" + nodes[targetNode].getHostname() + "]");
                }

                if(conn != null)
                {
                    conn.close();
                }
			} 
			catch(SQLException sqle)
			{
				System.out.println("[" + hostname + "]");
                printSQLException(sqle);
			}
			catch(NullPointerException nu)
			{
				System.out.println("NullPointerException");
			} 
            catch(InterruptedException e)
            {
              //hmmm... 
            }
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
    * Parses the first three words of the argument which will be either in the form
    * "create table 'tname'" or "drop table 'tname'"". 
    *
    * @param ddl String containing the DDL obtained from the input ddl file 
    *
    * @return ddlHolder a String[] containing the first three words of the ddl
    */
    public static String[] simpleParse(String ddl)
    {
        String temp = "";
        String[] ddlHolder;
        int firstThreeWords;
    
        /**
        * If ddl is "create statement", then grab characters until "(" else
        * we can just split the "drop table" statement.
        */
        if(ddl.toLowerCase().startsWith("create"))
        {
            firstThreeWords = ddl.indexOf("(");

            for(int i = 0; i < firstThreeWords; i++)
            {
                temp += ddl.charAt(i);
            }

            ddlHolder = temp.split(" ");

        } 
        else
        {
            ddlHolder = ddl.split(" ");
        }

        return ddlHolder;
    }
}
