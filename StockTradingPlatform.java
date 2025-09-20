package codeAlpha_Task2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Stock {
    String symbol;
    double price;

    Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
}

class User {
    String name;
    Map<String, Integer> portfolio = new HashMap<>(); // stock -> quantity
    double balance;

    User(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    void buyStock(Stock stock, int qty) {
        double cost = stock.price * qty;
        if (balance >= cost) {
            balance -= cost;
            portfolio.put(stock.symbol, portfolio.getOrDefault(stock.symbol, 0) + qty);
        } else {
            throw new IllegalArgumentException("Not enough balance!");
        }
    }

    void sellStock(Stock stock, int qty) {
        if (portfolio.getOrDefault(stock.symbol, 0) >= qty) {
            portfolio.put(stock.symbol, portfolio.get(stock.symbol) - qty);
            balance += stock.price * qty;
        } else {
            throw new IllegalArgumentException("Not enough shares to sell!");
        }
    }

    double portfolioValue(Map<String, Stock> stockMarket) {
        double value = balance;
        for (String sym : portfolio.keySet()) {
            value += portfolio.get(sym) * stockMarket.get(sym).price;
        }
        return value;
    }
}

public class StockTradingPlatform extends JFrame {
    private JTextArea marketArea, portfolioArea;
    private JTextField symbolField, qtyField;
    private JButton buyButton, sellButton, refreshButton;

    private Map<String, Stock> stockMarket = new HashMap<>();
    private User user;

    public StockTradingPlatform() {
        setTitle("Stock Trading Platform");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Market Data
        stockMarket.put("AAPL", new Stock("AAPL", 150.0));
        stockMarket.put("GOOG", new Stock("GOOG", 2800.0));
        stockMarket.put("TSLA", new Stock("TSLA", 700.0));

        user = new User("Trader1", 10000.0);

        // Top Panel (Market Data)
        marketArea = new JTextArea(8, 40);
        marketArea.setEditable(false);
        add(new JScrollPane(marketArea), BorderLayout.NORTH);

        // Center Panel (Portfolio)
        portfolioArea = new JTextArea(8, 40);
        portfolioArea.setEditable(false);
        add(new JScrollPane(portfolioArea), BorderLayout.CENTER);

        // Bottom Panel (Controls)
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Stock Symbol:"));
        symbolField = new JTextField(5);
        controlPanel.add(symbolField);

        controlPanel.add(new JLabel("Quantity:"));
        qtyField = new JTextField(5);
        controlPanel.add(qtyField);

        buyButton = new JButton("Buy");
        sellButton = new JButton("Sell");
        refreshButton = new JButton("Refresh");

        controlPanel.add(buyButton);
        controlPanel.add(sellButton);
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Button Actions
        buyButton.addActionListener(e -> tradeStock(true));
        sellButton.addActionListener(e -> tradeStock(false));
        refreshButton.addActionListener(e -> refreshDisplay());

        // Initial Display
        refreshDisplay();
    }

    private void tradeStock(boolean isBuy) {
        String symbol = symbolField.getText().trim().toUpperCase();
        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity!");
            return;
        }

        Stock stock = stockMarket.get(symbol);
        if (stock == null) {
            JOptionPane.showMessageDialog(this, "Stock not found!");
            return;
        }

        try {
            if (isBuy) {
                user.buyStock(stock, qty);
                JOptionPane.showMessageDialog(this, "Bought " + qty + " shares of " + symbol);
            } else {
                user.sellStock(stock, qty);
                JOptionPane.showMessageDialog(this, "Sold " + qty + " shares of " + symbol);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

        refreshDisplay();
    }

    private void refreshDisplay() {
        // Market Data
        StringBuilder marketText = new StringBuilder("Market Data:\n");
        for (Stock stock : stockMarket.values()) {
            marketText.append(stock.symbol).append(" - $").append(stock.price).append("\n");
        }
        marketArea.setText(marketText.toString());

        // Portfolio Data
        StringBuilder portfolioText = new StringBuilder("Portfolio:\n");
        portfolioText.append("Balance: $").append(user.balance).append("\n");
        for (String sym : user.portfolio.keySet()) {
            int qty = user.portfolio.get(sym);
            portfolioText.append(sym).append(": ").append(qty).append(" shares\n");
        }
        portfolioText.append("\nTotal Portfolio Value: $")
                .append(user.portfolioValue(stockMarket));
        portfolioArea.setText(portfolioText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatform().setVisible(true));
    }
}

