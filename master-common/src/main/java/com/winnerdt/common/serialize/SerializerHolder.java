package com.winnerdt.common.serialize;

import com.winnerdt.common.serialize.protostuff.ProtostuffSerializer;

/**
 * @author:zsk
 * @CreateTime:2019-07-09 15:21
 */

public class SerializerHolder {
    private static final Serializer SERIALIZER = new ProtostuffSerializer();

    /**
     * Get the implementation of serializer.
     *
     * @return the {@link Serializer} implementation
     */
    public static Serializer serializerImpl() {
        return SERIALIZER;
    }
}