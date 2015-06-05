package com.highpowerbear.hpbtrader.linear.strategy.model;

import com.highpowerbear.hpbtrader.linear.definitions.LinEnums;
import com.highpowerbear.hpbtrader.linear.entity.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robertk
 */
public class BacktestResult {
    private Strategy strategy;
    private List<StrategyLog> strategyLogs = new ArrayList<>();
    private List<Order> orders = new ArrayList<>(); // will include also OrderEvent list
    private List<Trade> trades = new ArrayList<>(); // will include also TradeOrder list
    private List<TradeLog> tradeLogs = new ArrayList<>();
    private Long nextStrategyLogId = 1L;
    private Long nextOrderId = 1L;
    private Long nextTradeId = 1L;
    private Long nextTradeLogId = 1L;

    public BacktestResult(Strategy strategy) {
        this.strategy = strategy;
        this.strategy.setStrategyMode(LinEnums.StrategyMode.BTEST);
    }
    
    public Trade getActiveTrade() {
        Trade dbActiveTrade = null;
        Trade activeTrade = null; // need to create a copy to simulate jpa detachment
        for (Trade t : trades) {
            if (LinEnums.TradeStatus.INIT_OPEN.equals(t.getTradeStatus() ) || LinEnums.TradeStatus.OPEN.equals(t.getTradeStatus())) {
                dbActiveTrade = t;
                break;
            }
        }
        if (dbActiveTrade != null) {
            activeTrade = dbActiveTrade.deepCopy(new Trade());
        }
        return activeTrade;
    }
    public void updateStrategy(Strategy strategy, Quote quote) {
        if (!this.strategy.valuesEqual(strategy)) {
            StrategyLog strategyLog = new StrategyLog();
            strategyLog.setId(nextStrategyLogId++);
            strategyLog.setStrategy(strategy);
            strategyLog.setStrategyMode(LinEnums.StrategyMode.BTEST);
            strategyLog.setLogDate(quote.getqDateBarClose());
            strategy.copyValues(strategyLog);
            strategyLogs.add(strategyLog);
        }
        this.strategy = strategy;
        this.strategy.setStrategyMode(LinEnums.StrategyMode.BTEST);
    }
    
    public void updateTrade(Trade trade, Quote quote) {
        Trade dbTrade = findTrade(trade.getId());
        if (dbTrade == null || !dbTrade.valuesEqual(trade)) {
            TradeLog tradeLog = new TradeLog();
            tradeLog.setId(nextTradeLogId++);
            tradeLog.setTrade(trade);
            tradeLog.setLogDate(quote.getqDateBarClose());
            trade.copyValues(tradeLog);
            tradeLog.setPrice(quote.getqClose());
            tradeLogs.add(tradeLog);
        }
        if (dbTrade == null) {
            trade.setId(nextTradeId++);
            trades.add(trade);
        } else {
            trade.deepCopy(dbTrade);
        }
    }
    
    public void addOrder(Order order) {
        order.setId(nextOrderId++);
        order.setStrategyMode(LinEnums.StrategyMode.BTEST);
        orders.add(order);
    }
    
    private Trade findTrade(Long tradeId) {
        if (tradeId == null) {
            return null;
        }
        Trade foundTrade = null;
        for (Trade t : trades) {
            if (t.getId().equals(tradeId)) {
                foundTrade = t;
                break;
            }
        }
        return foundTrade;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }

    public List<StrategyLog> getStrategyLogs() {
        return strategyLogs;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<TradeLog> getTradeLogs() {
        return tradeLogs;
    }
}