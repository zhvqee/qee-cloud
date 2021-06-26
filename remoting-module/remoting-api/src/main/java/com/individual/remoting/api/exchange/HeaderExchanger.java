package com.individual.remoting.api.exchange;

import com.individual.common.exceptions.RemotingException;
import com.individual.common.model.URL;
import com.individual.remoting.api.transport.Transporters;

/**
 * 默认的exchanger 提供心跳
 * decodeHandler->headerExchangerHandler-> 上层的channelHandler.
 * decodeHandler 接受的消息为request,或者response,或者event ,
 * 则decodeHandler 需要再次解码为response 和 request里面的data 进行解码。
 * 其data的值 DecodeableRpcInvocation，或者为 DecodeableRpcResult ，解码这个之后可以进行发放层面的调用
 */
public class HeaderExchanger implements Exchanger {

    @Override
    public ExchangeServer bind(URL url, ExchangeHandler channelHandler) throws RemotingException {
        return new HeaderExchangeServer(Transporters.bind(url, new DecodeHanlder(new HeaderExchangeHandler(channelHandler))));
    }

    @Override
    public ExchangeClient connect(URL url, ExchangeHandler channelHandler) throws RemotingException {
        return new HeaderExchangeClient(Transporters.connect(url, new DecodeHanlder(new HeaderExchangeHandler(channelHandler))));

    }
}
