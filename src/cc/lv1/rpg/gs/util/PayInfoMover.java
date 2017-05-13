package cc.lv1.rpg.gs.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import cc.lv1.rpg.gs.GameServer;
import cc.lv1.rpg.gs.dao.DatabaseAccessor;
import cc.lv1.rpg.gs.load.impl.MergeLoader;

public class PayInfoMover
{
	
	private int count = 0;
	
	private int payCount = 0;
	
	private void process()
	{
		GameServer.getInstance().initialWithoutNetServer();
		DatabaseAccessor sourceDA = GameServer.getInstance().getDatabaseAccessor();

		for (int i = 0; i < MergeLoader.mergeDAs.length; i++)
		{
			System.err.println("Open Source DB "+MergeLoader.mergeDAs[i].getDbname());
		}
		System.err.println("Open Target DB "+sourceDA.getDbname());
		
		
		for (int i = 0; i < MergeLoader.mergeDAs.length; i++)
		{
			int ii = 0;
			while(toTargetPay(MergeLoader.mergeDAs[i],sourceDA,ii, ii+5000))
				ii += 5000;
		}

		System.out.println("Pay count = "+payCount);
		System.out.println("Total Count = "+count);
	}
	
	public boolean toTargetPay(DatabaseAccessor da,DatabaseAccessor sourceDA,int begin,int end)
	{
		PreparedStatement statement = null;
		Connection conn = null;
	
		int roleId = 0;
		String accountName = "";
		int payType = 0;
		int payState = 0;
		int payMoney = 0;
		String payExp = "";
		String payTime = "";
		boolean isHas = false;
		
		try 
		{
			conn = da.getConnectionFactory().getConnection();
			synchronized(conn)
			{
				statement = conn.prepareStatement("SELECT TOP "+(end-begin)+" roleId,accountName,payType,payState,payMoney,payExp,payTime FROM T_SERVER_PAY WHERE (ID NOT IN (SELECT TOP "+begin+" id FROM T_SERVER_PAY ORDER BY id))ORDER BY ID ");
				ResultSet result = statement.executeQuery();
				int currCount = 0;
				while(result.next())
				{
					roleId = result.getInt("roleId");
					accountName = result.getString("accountName");
					payType = result.getInt("payType");
					payState = result.getInt("payState");
					payMoney = result.getInt("payMoney");
					payExp = result.getString("payExp");
					payTime = result.getString("payTime");
					addPaymentInfo(sourceDA, roleId, accountName, payType, payState, payMoney, payExp, payTime);
					currCount++;
					System.out.println(begin+":"+end+":"+currCount+" Insert to Source "+sourceDA.getDbname()+" from "+da.getDbname()+"("+roleId+":"+accountName+":"+payType+":"+payState+":"+payMoney+":"+payExp+":"+payTime+")");
					isHas = true;
					count ++;
					
					if(payType == 1)
						payCount += payMoney;
				}
				statement.close();
			}
			da.getConnectionFactory().recycleConnection(conn);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			//System.exit(-1);
		} 
		return isHas;
	}
	
	
	public void addPaymentInfo(DatabaseAccessor da,int roleId,String accountName,int payType,int payState,int payMoney,String payExp,String payTime)
	{
		PreparedStatement statement = null;
		Connection conn = null;

		try 
		{
			conn = da.getConnectionFactory().getConnection();
			synchronized(conn)
			{	
				statement = conn.prepareStatement("insert into T_Server_Pay(roleId,accountName,payType,payState,payMoney,payExp,payTime) values(?,?,?,?,?,?,?)");
				statement.setInt(1, roleId);
				statement.setString(2, accountName);
				statement.setInt(3, payType);
				statement.setInt(4, payState);
				statement.setInt(5, payMoney);
				statement.setString(6, payExp);
				statement.setString(7, payTime);
				statement.execute();
			}	
			da.getConnectionFactory().recycleConnection(conn);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		
			//System.exit(-1);
		}
	}
	//39056   42437   35493
	
	public static void main(String[] args)
	{
		PayInfoMover pim = new PayInfoMover();
		pim.process();
	}
	
	
}
