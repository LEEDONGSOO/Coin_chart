package exchange;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Exchange implements Runnable{
	/*
	 * name : name of exchange
	 * numOfMarket : number of Market
	 * market : �ŷ��ҿ� �����ϴ� market ������ K : coinpair, V : Market ���� ����
	 * 			MarketŬ������ obj�� �ŷ��ҿ��� ����ϴ� ������ ��üȭ �� ������ BTC/KRW, ETH/KRW, BTC/USD ��
	 * 			���� �ٸ� coinpair�� ���� ������ �ǹ�
	 * APIurl : �ŷ��� �ü��� �������� ���� �ŷ��� API URL
	 * 			ex) Bithumb : https://api.bithumb.com/public/
	 * 				Coinone : https://api.coinone.co.kr/
	 */
	String name;
	int numOfMarket = 0, IOExceptionCount = 0;
	URL APIurl;
	Map<String, Market> markets = new HashMap<String, Market>();
	private Connection connection;
	private Statement st;
	
	//Constructor
	public Exchange(String name, String APIurl) throws MalformedURLException {
		this.name = name;
		this.APIurl = new URL(APIurl);
	}
	
	void _addMarket(String coin, String base, String recentTradesSubUrl) {
		//�̹� add �� market���� üũ
		if(markets.containsKey(coin + base)) {
			System.err.println(coin + base + "market already exist in " + this.name);
			return;
		}
		numOfMarket++;
		Market market = new Market(coin, base, this.name, recentTradesSubUrl);
		markets.put(market.coinpair, market);			
	}
	
	//�ŷ����� �ֱ� �ŷ����� DB�� ������
	void renewDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql:localhost", "coin_chart_manager", "coin_chart");
			st = connection.createStatement();
		
			StringBuffer values = new StringBuffer();
			
			Collection<Market> marketCollection = markets.values();
			Iterator<Market> e = marketCollection.iterator();
			Market market;
			while(e.hasNext()) {
				market = e.next();
				for(int i = 0; i < market.dataRows.length; i++) {
					values.append("('" + market.dataRows[i].date + "','" + market.dataRows[i].price +
							"','" + market.dataRows[i].qty + "')");
				}
				st.executeUpdate("insert into " + this.name + " values " + values.toString());
				values.delete(0, values.length() - 1);
			}
			st.close();
			connection.close();
		}catch(SQLException se1) {
			se1.printStackTrace();
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			try {
				if(st!=null)
					st.close();
			}catch(SQLException se2) {
				se2.printStackTrace();
			}
			try {
				if(connection!=null)
					connection.close();
			}catch(SQLException se3) {
				se3.printStackTrace();
			}
		}
	}
	
	//�ŷ��Ҹ��� API�� return value�� ���� �����ϹǷ�
	//�����͸� �о���̴� ����� �ŷ��Ҹ��� �ٸ��� �����ǹǷ�
	//abstract method�� ����
	//APIȣ���� �̿��Ͽ� market�� �ֱ� �ŷ��� �о� �ߺ� ������ �о��� ����� �ߺ��Ǵ� �κ� ���� ��
	//market�� dataRows�� ���� ������ renewDB()���� �̷����
	abstract void getRecentTrades() throws Exception;
	
	//convert market.jsonRecentTrades to market.dataRows
	//getRecentTrades()�� ������ ������ abstract�� ����
	abstract void makeDataRows(Market market);
	
	public abstract void addMarket(String coin, String base);
	
	//getRecentTrades ����ȣ��
	public void run(){
		while(true) {
			try {
				getRecentTrades();
			} catch(MalformedURLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			} catch(IOException e) {					
				System.err.println(e.getMessage());
				System.err.println("IOException count : " + this.IOExceptionCount);
				if(this.IOExceptionCount > 20)
					System.exit(-1);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
}
