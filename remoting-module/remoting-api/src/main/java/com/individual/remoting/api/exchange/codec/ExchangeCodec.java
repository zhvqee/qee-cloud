package com.individual.remoting.api.exchange.codec;

import com.individual.common.extentions.ExtensionLoader;
import com.individual.common.utils.ByteUtils;
import com.individual.remoting.api.buffer.ChannelBuffer;
import com.individual.remoting.api.buffer.stream.ChannelBufferInputStream;
import com.individual.remoting.api.buffer.stream.ChannelBufferOutputStream;
import com.individual.remoting.api.codec.Codec;
import com.individual.remoting.api.exchange.request.Request;
import com.individual.remoting.api.exchange.response.Response;
import com.individual.serialization.api.ObjectInput;
import com.individual.serialization.api.ObjectOutput;
import com.individual.serialization.api.Serialization;

import java.io.IOException;

/**
 * 交换层 编解码
 * <p> 2+1+1+8+4 =16字节 1
 * +----------------------------------------------------------------+|
 * | 2 magic | 1 flag |1 status | 8 invokerId | 4 body length | body |
 * |  0,1    | 2      | 3       | 4,5,6,7,8,9,10,11 |  12          |   n  |
 * +-----------------------------------------------------------------+
 */


public class ExchangeCodec implements Codec {

    /**
     * 魔数
     */
    protected static final short MAGIC = (short) 0xaabb;

    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;


    // header length.
    protected static final int HEADER_LENGTH = 16;


    /**
     * message encode to buffer
     *
     * @param buffer
     * @param message
     * @throws IOException
     */
    @Override
    public void encode(ChannelBuffer buffer, Object message) throws IOException {
        if (message instanceof Request) {
            Request request = (Request) message;
            encodeRequest(buffer, request);

        } else if (message instanceof Response) {
            Response response = (Response) message;
            encodeResponse(buffer, response);
        }
    }

    /**
     * buffer decode return object
     * <p>
     * 上面编码的时候根据request 和response 分别进行编码，
     * 但是在解码的时候则不需要，处理16字节的头部外，剩余是可以组成一个返回对象
     * 接着根据 flag 来判断是请求还是响应，然后转（要么request或者response）
     * 通过反序列化方法objectInput.readObject() 就可以得到一个对象。不需要关注类型
     *
     * @param buffer
     * @return
     * @throws IOException
     */

    @Override
    public Object decode(ChannelBuffer buffer) throws IOException {

        int readableBytes = buffer.readableBytes();
        if (readableBytes < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];
        buffer.readBytes(header);
        //验证 header.

        int len = ByteUtils.byte2Int(header, 12);
        if (buffer.readableBytes() < len) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        if ((header[2] & FLAG_REQUEST) != 0) {
            long id = ByteUtils.byte2Long(header, 4);
            Request request = new Request();
            request.setId(id);
            if ((header[2] & FLAG_TWOWAY) != 0) {
                request.setTwoWay(true);
            }
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
            ChannelBufferInputStream inputStream = new ChannelBufferInputStream(buffer);
            ObjectInput objectInput = serialization.deserilize(inputStream);
            request.setData(objectInput.readObject());
        } else {

            Response response = new Response();

            //todo 待做

        }


        return null;
    }


    private void encodeRequest(ChannelBuffer buffer, Request request) throws IOException {
        byte[] header = new byte[HEADER_LENGTH];

        ByteUtils.writeShort(header, 0, MAGIC);

        header[2] = FLAG_REQUEST;
        if (request.isTwoWay()) {
            header[2] |= FLAG_TWOWAY;
        }
        ByteUtils.writeLong(header, 4, request.getId());

        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();

        // encode request data.
        int savedWriteIndex = buffer.writerIndex();
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
        ChannelBufferOutputStream outputStream = new ChannelBufferOutputStream(buffer);
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        objectOutput.writeObject(request.getData());
        objectOutput.flushBuffer();
        int writtenBytes = outputStream.getWrittenBytes();
        outputStream.flush();
        outputStream.close();
        checkPayload(writtenBytes);

        ByteUtils.writeInt(header, 12, writtenBytes);

        buffer.writerIndex(savedWriteIndex);
        outputStream.write(header);
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + writtenBytes);
    }

    private void checkPayload(int writtenBytes) {
    }

    //2 magic | 1 flag |1 status | 8 invokerId | 4 body length | body |
    private void encodeResponse(ChannelBuffer buffer, Response response) throws IOException {
        byte[] header = new byte[HEADER_LENGTH];

        ByteUtils.writeShort(header, 0, MAGIC);
        header[3] = (byte) response.getStatus();
        ByteUtils.writeLong(header, 4, response.getId());

        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();

        int readerIndex = buffer.readerIndex();
        buffer.readerIndex(readerIndex + HEADER_LENGTH);
        ChannelBufferOutputStream outputStream = new ChannelBufferOutputStream(buffer);
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        objectOutput.writeObject(response.getData());
        int writtenBytes = outputStream.getWrittenBytes();
        outputStream.flush();
        outputStream.close();
        ByteUtils.writeInt(header, 12, writtenBytes);

        buffer.writerIndex(readerIndex);
        outputStream.write(header);
        buffer.writerIndex(readerIndex + HEADER_LENGTH + writtenBytes);
    }


}
