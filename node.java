/**
* node class that holds information about each node in the cluster.
*/

public class node
{
	private String hostname, username, password, driver;
	private int nodeNum, status;

	node() 
	{
		this.status = 1;
	}

	node(String hostname, String username, String password, int nodeNum, String driver)
	{
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.nodeNum = nodeNum;
		this.driver = driver;
		this.status = 1;
	}

	public String getHostname()
	{
		return this.hostname;
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getPassword()
	{
		return this.password;
	}

	public int getNodeNum()
	{
		return this.nodeNum;
	}

	public String getDriver()
	{
		return this.driver;
	}

	public int getStatus()
	{
		return this.status;
	}

	public void setStatus(int s)
	{
		this.status = s;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setNodeNum(int nodeNum)
	{
		this.nodeNum = nodeNum;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public void printNode() 
	{
		System.out.println("********Node#" + nodeNum + " info********");
		System.out.println("hostname: " + this.hostname);
		System.out.println("***************************"); 
	}
}