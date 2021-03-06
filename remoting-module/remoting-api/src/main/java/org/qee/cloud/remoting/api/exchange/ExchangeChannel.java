package org.qee.cloud.remoting.api.exchange;

import org.qee.cloud.common.exceptions.RemotingException;
import org.qee.cloud.remoting.api.channel.Channel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface ExchangeChannel extends Channel {

    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws RemotingException
     */
    CompletableFuture<Object> request(Object request, ExecutorService executor) throws RemotingException;

    /**
     * send request.
     *
     * @param request
     * @param timeout
     * @return response future
     * @throws RemotingException
     */
    CompletableFuture<Object> request(Object request, int timeout, ExecutorService executor) throws RemotingException;

    /**
     * get message handler.
     *
     * @return message handler
     */
    ExchangeHandler getExchangeHandler();
}
