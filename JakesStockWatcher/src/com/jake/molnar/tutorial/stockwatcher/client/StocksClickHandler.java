package com.jake.molnar.tutorial.stockwatcher.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;

public class StocksClickHandler implements ClickHandler {
	
	private ArrayList<Stock> stocks;
	private FlexTable stocksFlexTable;
	
	public StocksClickHandler(ArrayList<Stock> stocks, FlexTable stocksFlexTable) {
		this.stocks = stocks;
		this.stocksFlexTable = stocksFlexTable;
	}

	@Override
	public void onClick(ClickEvent event) {
		Object o = event.getSource();
		if (o instanceof StockRemovalButton) {
			StockRemovalButton button = (StockRemovalButton)o;
			Stock stockToBeRemoved = button.getStock();
			int row = stocks.indexOf(stockToBeRemoved) + 1;
			stocks.remove(stockToBeRemoved);
			stocksFlexTable.removeRow(row);
		}
	}

}
