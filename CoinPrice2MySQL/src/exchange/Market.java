package exchange;

import java.util.Calendar;

public class Market {
	String coin, base, coinpair, jsonRecentTrades, exchange;
	String recentTradesSubUrl;
	DataRow[] dataRows;
	
	//Constructor
	public Market(String coin, String base, String exchange, String recentTradesSubUrl) {
		this.coin = coin;
		this.base = base;
		this.coinpair = coin.concat(base);
		this.exchange = exchange;
		this.recentTradesSubUrl = recentTradesSubUrl;
	}
	
	class DataRow{
		/*
		 * date : �ŷ� ü�� �ð� GMT +9:00 �������� ����
		 * price : ü�� ����
		 * qty : ü�� ����
		 */
		Calendar date;
		double price;
		double qty;
	}
}
