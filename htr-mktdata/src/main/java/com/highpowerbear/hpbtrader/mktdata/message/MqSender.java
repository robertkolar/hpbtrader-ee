package com.highpowerbear.hpbtrader.mktdata.message;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.common.HtrEnums;
import com.highpowerbear.hpbtrader.shared.common.HtrUtil;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 25.11.2015.
 */
// needs to be Stateless instead of ApplicationScoped since it is called from external thread, the following is reported:
// Error - java.lang.RuntimeException: javax.naming.NameNotFoundException: java:comp/TransactionSynchronizationRegistry
@Stateless
public class MqSender {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    @Inject private JMSContext jmsContext;
    @Resource(lookup = HtrDefinitions.MKTDATA_TO_STRATEGY_QUEUE)
    private Queue mktDataToStrategyQ;

    public void notifyDataBarsCreated(DataSeries dataSeries) {
        try {
            String corId = String.valueOf(dataSeries.getId());
            String msg = HtrUtil.constructMessage(HtrEnums.MessageType.DATABARS_CREATED, dataSeries.getAlias());
            l.info("BEGIN send message to MQ=" + HtrDefinitions.MKTDATA_TO_STRATEGY_QUEUE + ", corId=" + corId + ", msg=" + msg);
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage(msg);
            message.setJMSCorrelationID(corId);
            producer.send(mktDataToStrategyQ, message);
            l.info("END send message to MQ=" + HtrDefinitions.MKTDATA_TO_STRATEGY_QUEUE + ", corId=" + corId + ", msg=" + msg);
        } catch (JMSException e) {
            l.log(Level.SEVERE, "Error", e);
        }
    }
}
