import java.io.*;
import java.util.*;
import java.sql.*;

import org.antlr.v4.runtime;
import org.antlr.v4.runtime.tree;

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
}

