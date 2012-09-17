package com.jake.molnar.tutorial.stockwatcher.client;

import com.google.gwt.user.client.Random;

public class Stock {
	
	private static final double MAX_PRICE = 100.0; // $100.00
	private static final double MIN_PRICE = 0.01; // $0.01
	private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%
	
	private String symbol;
	private double price;
	private double change;
	
	private int tableRow;
	
	public Stock() {
	}
	
	public Stock(String symbol, double price, double change) {
		this.symbol = symbol;
		this.price = price;
		this.change = change;
	}
	
	public static Stock createRandomlyPricedStock(String symbol) {
		double price = Random.nextDouble() * MAX_PRICE;
		double change = 0.0;
		return new Stock(symbol, price, change);
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public double getPrice() {
		return price;
	}
	
	public double getChange() {
		return change;
	}
	
	public int getTableRow() {
		return tableRow;
	}
	
	public double getChangePercent() {
		return 100.0 * this.change/this.price;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public void setChange(double change) {
		this.change = change;
	}
	
	public void setTableRow(int tableRow) {
		this.tableRow = tableRow;
	}
	
	public void randomPriceChange() {
		// Percent change between -0.02 and 0.02
		double randomChangePercent = Random.nextDouble()*2*MAX_PRICE_CHANGE - MAX_PRICE_CHANGE;
		double newChange = price*randomChangePercent;
		double newPrice = price + newChange;
		
		// Checks to see if new price is in valid range
		if (newPrice < MIN_PRICE || newPrice > MAX_PRICE) {
			if (newPrice < MIN_PRICE) {
				newPrice = MIN_PRICE;
			}
			else if (newPrice > MAX_PRICE) {
				newPrice = MAX_PRICE;
			}
			newChange = (newPrice - price);
			randomChangePercent = newChange/price;
		}
		
		// Sets new price and change values
		price = newPrice;
		change = newChange;
	}

}
