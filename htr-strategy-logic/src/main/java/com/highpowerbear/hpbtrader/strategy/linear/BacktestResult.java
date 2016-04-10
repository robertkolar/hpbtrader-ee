package com.highpowerbear.hpbtrader.strategy.linear;

import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robertk
 */
public class BacktestResult {
    private Strategy strategy;
    private List<StrategyLog> strategyLogs = new ArrayList<>();
    private List<IbOrder> ibOrders = new ArrayList<>(); // will include also OrderEvent list
    private List<Trade> trades = new ArrayList<>(); // will include also TradeIbOrder list
    private List<TradeLog> tradeLogs = new ArrayList<>();
    private Long nextStrategyLogId = 1L;
    private Long nextOrderId = 1L;
    private Long nextTradeId = 1L;
    private Long nextTradeLogId = 1L;

    public BacktestResult(Strategy strategy) {
        this.strategy = strategy;
        this.strategy.setStrategyMode(HtrEnums.StrategyMode.BTEST);
    }
    
    public Trade getActiveTrade() {
        Trade dbActiveTrade = null;
        Trade activeTrade = null; // need to create a copy to simulate jpa detachment
        for (Trade t : trades) {
            if (HtrEnums.TradeStatus.INIT_OPEN.equals(t.getTradeStatus() ) || HtrEnums.TradeStatus.OPEN.equals(t.getTradeStatus())) {
                dbActiveTrade = t;
                break;
            }
        }
        if (dbActiveTrade != null) {
            activeTrade = dbActiveTrade.deepCopy(new Trade());
        }
        return activeTrade;
    }
    public void updateStrategy(Strategy strategy, DataBar dataBar) {
        if (!this.strategy.valuesEqual(strategy)) {
            StrategyLog strategyLog = new StrategyLog();
            strategyLog.setId(nextStrategyLogId++);
            strategyLog.setStrategy(strategy);
            strategyLog.setStrategyMode(HtrEnums.StrategyMode.BTEST);
            strategyLog.setLogDate(dataBar.getBarCloseDate());
            strategy.copyValues(strategyLog);
            strategyLogs.add(strategyLog);
        }
        this.strategy = strategy;
        this.strategy.setStrategyMode(HtrEnums.StrategyMode.BTEST);
    }
    
    public void updateOrCreateTrade(Trade trade, DataBar dataBar) {
        Trade dbTrade = findTrade(trade.getId());
        if (dbTrade == null || !dbTrade.valuesEqual(trade)) {
            TradeLog tradeLog = new TradeLog();
            tradeLog.setId(nextTradeLogId++);
            tradeLog.setTrade(trade);
            tradeLog.setLogDate(dataBar.getBarCloseDate());
            trade.copyValues(tradeLog);
            tradeLog.setPrice(dataBar.getbBarClose());
            tradeLogs.add(tradeLog);
        }
        if (dbTrade == null) {
            trade.setId(nextTradeId++);
            trades.add(trade);
        } else {
            trade.deepCopy(dbTrade);
        }
    }
    
    public void addOrder(IbOrder ibOrder) {
        ibOrder.setId(nextOrderId++);
        ibOrder.setStrategyMode(HtrEnums.StrategyMode.BTEST);
        ibOrders.add(ibOrder);
    }
    
    private Trade findTrade(Long tradeId) {
        if (tradeId == null) {
            return null;
        }
        return trades.stream().filter(t -> (t.getId().equals(tradeId))).findAny().get();
    }
    
    public Strategy getStrategy() {
        return strategy;
    }

    public List<StrategyLog> getStrategyLogs() {
        return strategyLogs;
    }

    public List<IbOrder> getIbOrders() {
        return ibOrders;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<TradeLog> getTradeLogs() {
        return tradeLogs;
    }
}