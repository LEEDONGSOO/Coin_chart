package exchange;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
	int numOfMarket = 0;
	URL APIurl;
	Map<String, Market> markets = new HashMap<String, Market>();
	
	//Constructor
	public Exchange(String name, String APIurl) throws MalformedURLException {
		this.name = name;
		this.APIurl = new URL(APIurl);
	}
	
	public void addMarket(String coin, String base, String recentTradesSubUrl) {
		numOfMarket++;
		Market market = new Market(coin, base, this.name, recentTradesSubUrl);
		markets.put(market.coinpair, market);		
	}
	
	void renewDB() {
		
	}
	
	//�ŷ��Ҹ��� API�� return value�� ���� �����ϹǷ�
	//�����͸� �о���̴� ����� �ŷ��Ҹ��� �ٸ��� �����ǹǷ�
	//abstract method�� ����
	abstract void getRecentTrades() throws Exception;
	
	public void run() {
		
	}
	
}
