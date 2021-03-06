package com.highpowerbear.hpbtrader.mktdata.websocket;

import com.highpowerbear.hpbtrader.shared.common.HtrDefinitions;
import com.highpowerbear.hpbtrader.shared.entity.DataSeries;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.11.2015.
 */
@ApplicationScoped
public class WebsocketController {
    private static final Logger l = Logger.getLogger(HtrDefinitions.LOGGER);

    private Set<Session> sessions = new HashSet<>();

    Set<Session> getSessions() {
        return sessions;
    }

    void sendMessage(Session s, String message) {
        try {
            RemoteEndpoint.Async remote = s.getAsyncRemote();
            remote.setSendTimeout(HtrDefinitions.WEBSOCKET_ASYNC_SEND_TIMEOUT);
            remote.sendText(message);
        } catch (Throwable t) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, t);
        }
    }

    public void broadcastMessage(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + seriesSessions.size());
        sessions.stream().filter(Session::isOpen).forEach(s -> sendMessage(s, message));
    }

    public void notifyDataBarsCreated(DataSeries dataSeries) {
        broadcastMessage("dataBar," + "*" + ",dataSeriesId," + dataSeries.getId() + ",data bars created");
    }
}
