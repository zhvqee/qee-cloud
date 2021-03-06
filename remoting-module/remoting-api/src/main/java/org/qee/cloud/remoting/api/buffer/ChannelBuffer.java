package org.qee.cloud.remoting.api.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 抽象层定义channelBuffer ，为了不与实际实现的channelbuffer挂钩，
 * 比如NettyBackedChannelBuffer 实现该接口，其具体的buffer 功能委派给netty本身的ByteBuf
 */
public interface ChannelBuffer {

    /**
     * Returns the number of bytes (octets) this buffer can contain.
     */
    int capacity();

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer to
     * {@code 0}. This method is identical to {@link #setIndex(int, int)
     * setIndex(0, 0)}.
     * <p/>
     * Please note that the behavior of this method is different from that of
     * NIO buffer, which sets the {@code limit} to the {@code capacity} of the
     * buffer.
     */
    void clear();

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * specified absolute {@code index} until the destination's position reaches
     * its limit. This method does not modify {@code readerIndex} or {@code
     * writerIndex} of this buffer while the destination's {@code position} will
     * be increased.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   dst.remaining()} is greater than {@code
     *                                   this.capacity}
     */
    void getBytes(int index, ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * specified absolute {@code index} until the destination becomes
     * non-writable.  This method is basically same with {@link #getBytes(int,
     * ChannelBuffer, int, int)}, except that this method increases the {@code
     * writerIndex} of the destination by the number of the transferred bytes
     * while {@link #getBytes(int, ChannelBuffer, int, int)} does not. This
     * method does not modify {@code readerIndex} or {@code writerIndex} of the
     * source buffer (i.e. {@code this}).
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   dst.writableBytes} is greater than
     *                                   {@code this.capacity}
     */
    void getBytes(int index, ChannelBuffer dst);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * specified absolute {@code index}.  This method is basically same with
     * {@link #getBytes(int, ChannelBuffer, int, int)}, except that this method
     * increases the {@code writerIndex} of the destination by the number of the
     * transferred bytes while {@link #getBytes(int, ChannelBuffer, int, int)}
     * does not. This method does not modify {@code readerIndex} or {@code
     * writerIndex} of the source buffer (i.e. {@code this}).
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0}, if {@code index +
     *                                   length} is greater than {@code
     *                                   this.capacity}, or if {@code length} is
     *                                   greater than {@code dst.writableBytes}
     */
    void getBytes(int index, ChannelBuffer dst, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * specified absolute {@code index}. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of both the source (i.e. {@code
     * this}) and the destination.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0}, if the specified {@code
     *                                   dstIndex} is less than {@code 0}, if
     *                                   {@code index + length} is greater than
     *                                   {@code this.capacity}, or if {@code
     *                                   dstIndex + length} is greater than
     *                                   {@code dst.capacity}
     */
    void getBytes(int index, ChannelBuffer dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * specified absolute {@code index}. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of this buffer.
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   length} is greater than {@code
     *                                   this.capacity}
     * @throws IOException               if the specified stream threw an
     *                                   exception during I/O
     */
    void getBytes(int index, OutputStream dst, int length) throws IOException;

    /**
     * Returns {@code true} if and only if this buffer is backed by an NIO
     * direct buffer.
     */
    boolean isDirect();

    /**
     * Marks the current {@code readerIndex} in this buffer.  You can reposition
     * the current {@code readerIndex} to the marked {@code readerIndex} by
     * calling {@link #resetReaderIndex()}. The initial value of the marked
     * {@code readerIndex} is {@code 0}.
     */
    void markReaderIndex();

    /**
     * Marks the current {@code writerIndex} in this buffer.  You can reposition
     * the current {@code writerIndex} to the marked {@code writerIndex} by
     * calling {@link #resetWriterIndex()}. The initial value of the marked
     * {@code writerIndex} is {@code 0}.
     */
    void markWriterIndex();

    /**
     * Returns {@code true} if and only if {@code (this.writerIndex -
     * this.readerIndex)} is greater than {@code 0}.
     */
    boolean readable();

    /**
     * Returns the number of readable bytes which is equal to {@code
     * (this.writerIndex - this.readerIndex)}.
     */
    int readableBytes();

    /**
     * Gets a byte at the current {@code readerIndex} and increases the {@code
     * readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less
     *                                   than {@code 1}
     */
    byte readByte();

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} and increases the {@code readerIndex} by the
     * number of the transferred bytes (= {@code dst.length}).
     *
     * @throws IndexOutOfBoundsException if {@code dst.length} is greater than
     *                                   {@code this.readableBytes}
     */
    void readBytes(byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} and increases the {@code readerIndex} by the
     * number of the transferred bytes (= {@code length}).
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code dstIndex} is
     *                                   less than {@code 0}, if {@code length}
     *                                   is greater than {@code this.readableBytes},
     *                                   or if {@code dstIndex + length} is
     *                                   greater than {@code dst.length}
     */
    void readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} until the destination's position reaches its
     * limit, and increases the {@code readerIndex} by the number of the
     * transferred bytes.
     *
     * @throws IndexOutOfBoundsException if {@code dst.remaining()} is greater
     *                                   than {@code this.readableBytes}
     */
    void readBytes(ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} until the destination becomes non-writable,
     * and increases the {@code readerIndex} by the number of the transferred
     * bytes.  This method is basically same with {@link
     * #readBytes(ChannelBuffer, int, int)}, except that this method increases
     * the {@code writerIndex} of the destination by the number of the
     * transferred bytes while {@link #readBytes(ChannelBuffer, int, int)} does
     * not.
     *
     * @throws IndexOutOfBoundsException if {@code dst.writableBytes} is greater
     *                                   than {@code this.readableBytes}
     */
    void readBytes(ChannelBuffer dst);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} and increases the {@code readerIndex} by the
     * number of the transferred bytes (= {@code length}).  This method is
     * basically same with {@link #readBytes(ChannelBuffer, int, int)}, except
     * that this method increases the {@code writerIndex} of the destination by
     * the number of the transferred bytes (= {@code length}) while {@link
     * #readBytes(ChannelBuffer, int, int)} does not.
     *
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.readableBytes} or if {@code
     *                                   length} is greater than {@code
     *                                   dst.writableBytes}
     */
    void readBytes(ChannelBuffer dst, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at the
     * current {@code readerIndex} and increases the {@code readerIndex} by the
     * number of the transferred bytes (= {@code length}).
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code dstIndex} is
     *                                   less than {@code 0}, if {@code length}
     *                                   is greater than {@code this.readableBytes},
     *                                   or if {@code dstIndex + length} is
     *                                   greater than {@code dst.capacity}
     */
    void readBytes(ChannelBuffer dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to a newly created buffer starting at the
     * current {@code readerIndex} and increases the {@code readerIndex} by the
     * number of the transferred bytes (= {@code length}). The returned buffer's
     * {@code readerIndex} and {@code writerIndex} are {@code 0} and {@code
     * length} respectively.
     *
     * @param length the number of bytes to transfer
     * @return the newly created buffer which contains the transferred bytes
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.readableBytes}
     */
    ChannelBuffer readBytes(int length);

    /**
     * Repositions the current {@code readerIndex} to the marked {@code
     * readerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException if the current {@code writerIndex} is
     *                                   less than the marked {@code
     *                                   readerIndex}
     */
    void resetReaderIndex();

    /**
     * Marks the current {@code writerIndex} in this buffer.  You can reposition
     * the current {@code writerIndex} to the marked {@code writerIndex} by
     * calling {@link #resetWriterIndex()}. The initial value of the marked
     * {@code writerIndex} is {@code 0}.
     */
    void resetWriterIndex();

    /**
     * Returns the {@code readerIndex} of this buffer.
     */
    int readerIndex();

    /**
     * Sets the {@code readerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code readerIndex} is
     *                                   less than {@code 0} or greater than
     *                                   {@code this.writerIndex}
     */
    void readerIndex(int readerIndex);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * current {@code readerIndex}.
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.readableBytes}
     * @throws IOException               if the specified stream threw an
     *                                   exception during I/O
     */
    void readBytes(OutputStream dst, int length) throws IOException;

    /**
     * Sets the specified byte at the specified absolute {@code index} in this
     * buffer.  The 24 high-order bits of the specified value are ignored. This
     * method does not modify {@code readerIndex} or {@code writerIndex} of this
     * buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or {@code index + 1} is
     *                                   greater than {@code this.capacity}
     */
    void setByte(int index, int value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   src.length} is greater than {@code
     *                                   this.capacity}
     */
    void setBytes(int index, byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0}, if the specified {@code
     *                                   srcIndex} is less than {@code 0}, if
     *                                   {@code index + length} is greater than
     *                                   {@code this.capacity}, or if {@code
     *                                   srcIndex + length} is greater than
     *                                   {@code src.length}
     */
    void setBytes(int index, byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer's position
     * reaches its limit. This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   src.remaining()} is greater than {@code
     *                                   this.capacity}
     */
    void setBytes(int index, ByteBuffer src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer becomes
     * unreadable.  This method is basically same with {@link #setBytes(int,
     * ChannelBuffer, int, int)}, except that this method increases the {@code
     * readerIndex} of the source buffer by the number of the transferred bytes
     * while {@link #setBytes(int, ChannelBuffer, int, int)} does not. This
     * method does not modify {@code readerIndex} or {@code writerIndex} of the
     * source buffer (i.e. {@code this}).
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   src.readableBytes} is greater than
     *                                   {@code this.capacity}
     */
    void setBytes(int index, ChannelBuffer src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index}.  This method is basically same with
     * {@link #setBytes(int, ChannelBuffer, int, int)}, except that this method
     * increases the {@code readerIndex} of the source buffer by the number of
     * the transferred bytes while {@link #setBytes(int, ChannelBuffer, int,
     * int)} does not. This method does not modify {@code readerIndex} or {@code
     * writerIndex} of the source buffer (i.e. {@code this}).
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0}, if {@code index +
     *                                   length} is greater than {@code
     *                                   this.capacity}, or if {@code length} is
     *                                   greater than {@code src.readableBytes}
     */
    void setBytes(int index, ChannelBuffer src, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index}. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of both the source (i.e. {@code
     * this}) and the destination.
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0}, if the specified {@code
     *                                   srcIndex} is less than {@code 0}, if
     *                                   {@code index + length} is greater than
     *                                   {@code this.capacity}, or if {@code
     *                                   srcIndex + length} is greater than
     *                                   {@code src.capacity}
     */
    void setBytes(int index, ChannelBuffer src, int srcIndex, int length);

    /**
     * Transfers the content of the specified source stream to this buffer
     * starting at the specified absolute {@code index}. This method does not
     * modify {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @param length the number of bytes to transfer
     * @return the actual number of bytes read in from the specified channel.
     * {@code -1} if the specified channel is closed.
     * @throws IndexOutOfBoundsException if the specified {@code index} is less
     *                                   than {@code 0} or if {@code index +
     *                                   length} is greater than {@code
     *                                   this.capacity}
     * @throws IOException               if the specified stream threw an
     *                                   exception during I/O
     */
    int setBytes(int index, InputStream src, int length) throws IOException;

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer in
     * one shot.  This method is useful when you have to worry about the
     * invocation order of {@link #readerIndex(int)} and {@link
     * #writerIndex(int)} methods.  For example, the following code will fail:
     * <p/>
     * <pre>
     * // Create a buffer whose readerIndex, writerIndex and capacity are
     * // 0, 0 and 8 respectively.
     * {@link ChannelBuffer} buf = {@link ChannelBuffers}.buffer(8);
     *
     * // IndexOutOfBoundsException is thrown because the specified
     * // readerIndex (2) cannot be greater than the current writerIndex (0).
     * buf.readerIndex(2);
     * buf.writerIndex(4);
     * </pre>
     * <p/>
     * The following code will also fail:
     * <p/>
     * <pre>
     * // Create a buffer whose readerIndex, writerIndex and capacity are
     * // 0, 8 and 8 respectively.
     * {@link ChannelBuffer} buf = {@link ChannelBuffers}.wrappedBuffer(new
     * byte[8]);
     *
     * // readerIndex becomes 8.
     * buf.readLong();
     *
     * // IndexOutOfBoundsException is thrown because the specified
     * // writerIndex (4) cannot be less than the current readerIndex (8).
     * buf.writerIndex(4);
     * buf.readerIndex(2);
     * </pre>
     * <p/>
     * By contrast, {@link #setIndex(int, int)} guarantees that it never throws
     * an {@link IndexOutOfBoundsException} as long as the specified indexes
     * meet basic constraints, regardless what the current index values of the
     * buffer are:
     * <p/>
     * <pre>
     * // No matter what the current state of the buffer is, the following
     * // call always succeeds as long as the capacity of the buffer is not
     * // less than 4.
     * buf.setIndex(2, 4);
     * </pre>
     *
     * @throws IndexOutOfBoundsException if the specified {@code readerIndex} is
     *                                   less than 0, if the specified {@code
     *                                   writerIndex} is less than the specified
     *                                   {@code readerIndex} or if the specified
     *                                   {@code writerIndex} is greater than
     *                                   {@code this.capacity}
     */
    void setIndex(int readerIndex, int writerIndex);

    /**
     * Increases the current {@code readerIndex} by the specified {@code length}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.readableBytes}
     */
    void skipBytes(int length);

    /**
     * Converts this buffer's readable bytes into a NIO buffer.  The returned
     * buffer might or might not share the content with this buffer, while they
     * have separate indexes and marks.  This method is identical to {@code
     * buf.toByteBuffer(buf.readerIndex(), buf.readableBytes())}. This method
     * does not modify {@code readerIndex} or {@code writerIndex} of this
     * buffer.
     */
    ByteBuffer toByteBuffer();

    /**
     * Converts this buffer's sub-region into a NIO buffer.  The returned buffer
     * might or might not share the content with this buffer, while they have
     * separate indexes and marks. This method does not modify {@code
     * readerIndex} or {@code writerIndex} of this buffer.
     */
    ByteBuffer toByteBuffer(int index, int length);

    /**
     * Returns {@code true} if and only if {@code (this.capacity -
     * this.writerIndex)} is greater than {@code 0}.
     */
    boolean writable();

    /**
     * Returns the number of writable bytes which is equal to {@code
     * (this.capacity - this.writerIndex)}.
     */
    int writableBytes();

    /**
     * Sets the specified byte at the current {@code writerIndex} and increases
     * the {@code writerIndex} by {@code 1} in this buffer. The 24 high-order
     * bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less
     *                                   than {@code 1}
     */
    void writeByte(int value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex} by
     * the number of the transferred bytes (= {@code src.length}).
     *
     * @throws IndexOutOfBoundsException if {@code src.length} is greater than
     *                                   {@code this.writableBytes}
     */
    void writeBytes(byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex} by
     * the number of the transferred bytes (= {@code length}).
     *
     * @param index  the first index of the source
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code srcIndex} is
     *                                   less than {@code 0}, if {@code srcIndex
     *                                   + length} is greater than {@code
     *                                   src.length}, or if {@code length} is
     *                                   greater than {@code this.writableBytes}
     */
    void writeBytes(byte[] src, int index, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} until the source buffer's position
     * reaches its limit, and increases the {@code writerIndex} by the number of
     * the transferred bytes.
     *
     * @throws IndexOutOfBoundsException if {@code src.remaining()} is greater
     *                                   than {@code this.writableBytes}
     */
    void writeBytes(ByteBuffer src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} until the source buffer becomes
     * unreadable, and increases the {@code writerIndex} by the number of the
     * transferred bytes.  This method is basically same with {@link
     * #writeBytes(ChannelBuffer, int, int)}, except that this method increases
     * the {@code readerIndex} of the source buffer by the number of the
     * transferred bytes while {@link #writeBytes(ChannelBuffer, int, int)} does
     * not.
     *
     * @throws IndexOutOfBoundsException if {@code src.readableBytes} is greater
     *                                   than {@code this.writableBytes}
     */
    void writeBytes(ChannelBuffer src);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex} by
     * the number of the transferred bytes (= {@code length}).  This method is
     * basically same with {@link #writeBytes(ChannelBuffer, int, int)}, except
     * that this method increases the {@code readerIndex} of the source buffer
     * by the number of the transferred bytes (= {@code length}) while {@link
     * #writeBytes(ChannelBuffer, int, int)} does not.
     *
     * @param length the number of bytes to transfer
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.writableBytes} or if {@code
     *                                   length} is greater then {@code
     *                                   src.readableBytes}
     */
    void writeBytes(ChannelBuffer src, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex} by
     * the number of the transferred bytes (= {@code length}).
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code srcIndex} is
     *                                   less than {@code 0}, if {@code srcIndex
     *                                   + length} is greater than {@code
     *                                   src.capacity}, or if {@code length} is
     *                                   greater than {@code this.writableBytes}
     */
    void writeBytes(ChannelBuffer src, int srcIndex, int length);

    /**
     * Transfers the content of the specified stream to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex} by
     * the number of the transferred bytes.
     *
     * @param length the number of bytes to transfer
     * @return the actual number of bytes read in from the specified stream
     * @throws IndexOutOfBoundsException if {@code length} is greater than
     *                                   {@code this.writableBytes}
     * @throws IOException               if the specified stream threw an
     *                                   exception during I/O
     */
    int writeBytes(InputStream src, int length) throws IOException;

    /**
     * Returns the {@code writerIndex} of this buffer.
     */
    int writerIndex();

    /**
     * Sets the {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code writerIndex} is
     *                                   less than {@code this.readerIndex} or
     *                                   greater than {@code this.capacity}
     */
    void writerIndex(int writerIndex);

    /**
     * Returns the backing byte array of this buffer.
     *
     * @throws UnsupportedOperationException if there no accessible backing byte
     *                                       array
     */
    byte[] array();

    /**
     * Returns {@code true} if and only if this buffer has a backing byte array.
     * If this method returns true, you can safely call {@link #array()} and
     * {@link #arrayOffset()}.
     */
    boolean hasArray();

    /**
     * Returns the offset of the first byte within the backing byte array of
     * this buffer.
     *
     * @throws UnsupportedOperationException if there no accessible backing byte
     *                                       array
     */
    int arrayOffset();

}
