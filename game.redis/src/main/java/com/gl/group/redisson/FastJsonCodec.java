package com.gl.group.redisson;

import com.alibaba.fastjson.JSON;
import com.gl.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;
import org.redisson.client.codec.Codec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FastJsonCodec implements Codec {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonCodec.class);
    private Class<?> keyClass;
    private Class<?> valueClass;

    public FastJsonCodec() {
        super();
    }
    public FastJsonCodec(Class<?> valueClass) {
        super();
        this.valueClass = valueClass;
    }

    public FastJsonCodec(Class<?> keyClass, Class<?> valueClass) {
        super();
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object o) throws IOException {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            out.writeCharSequence(JsonUtil.toJSONStringWriteClassNameWithFiled(o), CharsetUtil.UTF_8);
            return null;
        }
    };

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf byteBuf, State state) throws IOException {
            String str = byteBuf.toString(CharsetUtil.UTF_8);
            if(valueClass != null && str.startsWith("{")) {
                return JsonUtil.parseObject(str, valueClass);
            } else if(keyClass != null && !str.startsWith("{")) {
                if(keyClass.isAssignableFrom(Long.class)) {
                    return Long.parseLong(str);
                } else if(keyClass.isAssignableFrom(Integer.class)) {
                    return Integer.parseInt(str);
                } else {
                    return str;
                }
            }
            return "";
        }
    };

    @Override
    public Decoder<Object> getMapValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getMapValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getMapKeyDecoder() {
        return decoder;
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
