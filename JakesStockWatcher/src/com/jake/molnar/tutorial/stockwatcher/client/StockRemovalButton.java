package com.jake.molnar.tutorial.stockwatcher.client;

import com.google.gwt.user.client.ui.Button;

public class StockRemovalButton extends Button {
	
	private Stock stock;
	
	public StockRemovalButton(Stock stock) {
		super();
		this.stock = stock;
		this.addStyleName("removeButton");
	}
	
	public StockRemovalButton(Stock stock, String label) {
		super(label);
		this.stock = stock;
		this.addStyleName("removeButton");
	}
	
	public Stock getStock() {
		return stock;
	}
	
	public void setStock(Stock stock) {
		this.stock = stock;
	}

}
