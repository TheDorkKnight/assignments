package com.jake.molnar.tutorial.stockwatcher.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class StockWatcher implements EntryPoint {
	
	private static final int REFRESH_INTERVAL = 5000; // ms
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<Stock> stocks = new ArrayList<Stock>();
	private StocksClickHandler clickHandler = new StocksClickHandler(stocks, stocksFlexTable);
	
	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		// Create table for stock data
		stocksFlexTable.setText(0, 0, "Symbol");
		stocksFlexTable.setText(0, 1, "Price");
		stocksFlexTable.setText(0, 2, "Change");
		stocksFlexTable.setText(0, 3, "Remove");
		
		// Add styles to elements in the stock list table
		stocksFlexTable.setCellPadding(6);
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		stocksFlexTable.getColumnFormatter().addStyleName(3, "watchListRemoveColumn");
		
		// Assemble Add Stock panel
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");
		
		// Assemble Main panel
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);
		
		// Add style to lastUpdatedLabel
		lastUpdatedLabel.addStyleName("updateLabel");
		
		// Associate the Main panel with the HTML host page
		RootPanel.get("stockList").add(mainPanel);
		
		// Move cursor focus to the input box
		newSymbolTextBox.setFocus(true);
		
		// Setup timer to refresh list automatically
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		
		// Listen for mouse events on the Add button
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				addStock();
			}
		});
		
		// Listen for keyboard events in the input box
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent kpe) {
				// These are both when ENTER is pressed
				int test1 = kpe.getUnicodeCharCode();
				int test2 = (int)kpe.getCharCode();
				// This is equal to 13
				int control = KeyCodes.KEY_ENTER;
				if (kpe.getUnicodeCharCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
				// Here is a band-aid.  I wish I had a proper fix
				else if (kpe.getCharCode() == 0) {
					addStock();
				}
			}
		});
		
	}
	
	/**
	 * Add stock to FlexTable. Executed when the user clicks the 
	 * addStockButton or when user presses enter in the newSymbolTextBox
	 */
	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		newSymbolTextBox.setFocus(true);
		
		// Stock code must be between 1 and 10 chars that are numbers,
		// letters, and dots
		if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
			Window.alert("'" + symbol + "' is not a valid symbol.");
			newSymbolTextBox.selectAll();
			return;
		}
		
		newSymbolTextBox.setText("");
		
		// Don't add the stock if it's already in the table
		for (Stock stock : stocks) {
			if (stock.getSymbol() == symbol)
				return;
		}
		
		// Add the stock to the table
		int row = stocksFlexTable.getRowCount();
		Stock newStock = Stock.createRandomlyPricedStock(symbol);
		stocks.add(newStock);
		stocksFlexTable.setText(row, 0, symbol);
		stocksFlexTable.setWidget(row, 2, new Label());
		stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
		
		// Add a button to remove this stock
		Button removeStockButton = new StockRemovalButton(newStock, "x");
		removeStockButton.addClickHandler(clickHandler);
		stocksFlexTable.setWidget(row, 3, removeStockButton);
		
		
		// Get the stock price
		refreshWatchList();
		
	}

	/**
	 * Generate random stock prices
	 */
	private void refreshWatchList() {
		
		for (Stock stock : stocks) {
			stock.randomPriceChange();
			updateTable(stock);
		}
		
		// Display timestamp showing last refresh
		lastUpdatedLabel.setText("Last update : "
				+ DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
	}

	/**
	 * Update a single row in the stock table
	 * 
	 * @param stock Stock data for a single row
	 */
	private void updateTable(Stock stock) {
		// Make sure the stock is still in the stock table
		if (!stocks.contains(stock)) {
			return;
		}
		
		int row = stocks.indexOf(stock) + 1;
		
		// Format the data in the Price and Change fields
		String priceText = NumberFormat.getFormat("#,##0.00").format(stock.getPrice());
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(stock.getChange());
		String changePercentText = changeFormat.format(stock.getChangePercent());
		
		// Populate the Price and Change fields with new data
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + " (" + changePercentText + "%)");
		
		// Change the colour of text in the Change field based on its value
		String changeStyleName = "noChange";
		if (stock.getChangePercent() < -0.1f) {
			changeStyleName = "negativeChange";
		}
		else if (stock.getChangePercent() > 0.1f) {
			changeStyleName = "positiveChange";
		}
		
		changeWidget.setStyleName(changeStyleName);
	}
	
}

