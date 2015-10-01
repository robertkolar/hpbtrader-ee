package com.highpowerbear.hpbtrader.options.rest;

import com.highpowerbear.hpbtrader.options.data.OptData;
import com.highpowerbear.hpbtrader.options.common.OptEnums;
import com.highpowerbear.hpbtrader.options.common.OptDefinitions;
import com.highpowerbear.hpbtrader.options.common.SingletonRepo;
import com.highpowerbear.hpbtrader.options.persistence.OptDao;
import com.highpowerbear.hpbtrader.options.execution.SignalProcessor;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rkolar
 */
@Path("options")
public class OptService {
    private static final Logger l = Logger.getLogger(OptDefinitions.LOGGER);

    private OptDao optDao = SingletonRepo.getInstance().getOptDao();
    private SignalProcessor processor = SingletonRepo.getInstance().getSignalProcessor();
    private OptData optData = SingletonRepo.getInstance().getOptData();
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String echo(@QueryParam("id") int id) {
        return "You sent " + id;
    }
}