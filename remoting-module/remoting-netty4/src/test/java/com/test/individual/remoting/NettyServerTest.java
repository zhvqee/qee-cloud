package com.test.individual.remoting;

import org.qee.cloud.common.exceptions.RemotingException;
import org.qee.cloud.common.model.URL;
import org.qee.cloud.remoting.api.channel.Channel;
import org.qee.cloud.remoting.api.channelHanlder.ChannelHandler;
import org.qee.cloud.remoting.netty4.server.Netty4Server;
import org.junit.Test;

public class NettyServerTest {

    @Test
    public void test() throws RemotingException {

        URL url1 = URL.valueOf("test1://127.0.0.1:5587/DemoService?weight=1&active=0");

        ChannelHandler channelHandler = new ChannelHandler() {

            @Override
            public void connected(Channel channel) throws RemotingException {

            }

            @Override
            public void disconnected(Channel channel) throws RemotingException {

            }

            @Override
            public void sent(Channel channel, Object message) throws RemotingException {

            }

            @Override
            public void received(Channel channel, Object message) throws RemotingException {

            }

            @Override
            public void caught(Channel channel, Throwable exception) throws RemotingException {

            }
        };

        Netty4Server netty4Server = new Netty4Server(url1, channelHandler);

    }
}
