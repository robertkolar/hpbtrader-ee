package com.highpowerbear.hpbtrader.mktdata.websocket;

import com.highpowerbear.hpbtrader.mktdata.common.MktDefinitions;
import com.highpowerbear.hpbtrader.shared.defintions.HtrSettings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by robertk on 20.11.2015.
 */
@Named
@ApplicationScoped
public class WebsocketController {
    private static final Logger l = Logger.getLogger(MktDefinitions.LOGGER);

    private Set<Session> sessions = new HashSet<>();

    public Set<Session> getSessions() {
        return sessions;
    }

    public void sendMessage(Session s, String message) {
        try {
            RemoteEndpoint.Async remote = s.getAsyncRemote();
            remote.setSendTimeout(HtrSettings.WEBSOCKET_ASYNC_SEND_TIMEOUT);
            remote.sendText(message);
        } catch (Throwable t) {
            l.log(Level.SEVERE, "Error sending websocket message " + message, t);
        }
    }

    public void broadcastMessage(String message) {
        //l.l().debug("Sending websocket message=" + message + ", clients=" + seriesSessions.size());
        sessions.stream().filter(Session::isOpen).forEach(s -> sendMessage(s, message));
    }
}