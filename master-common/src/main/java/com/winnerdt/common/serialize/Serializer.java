package com.winnerdt.common.serialize;

import java.util.List;

/**
 * @author:zsk
 * @CreateTime:2019-07-09 15:22
 */

public interface Serializer {

    /**
     * Serializes the specified object to byte array.
     *
     * @param obj the specified object
     * @param <T> the generics class
     * @return the byte array
     */
    <T> byte[] serializeObject(T obj);

    /**
     * Deserializes the specified byte array to object.
     *
     * @param bytes the specified byte array
     * @param clazz the specified class
     * @param <T> the generics class
     * @return the object
     */
    <T> T deserializeObject(byte[] bytes, Class<T> clazz);

    /**
     * Serializes the specified list to byte array.
     *
     * @param objList the specified list
     * @param <T> the generics class
     * @return the byte array
     */
    <T> byte[] serializeList(List<T> objList);

    /**
     * Deserializes the specified byte array to list.
     *
     * @param paramArrayOfByte the specified byte array
     * @param targetClass the specified list
     * @param <T> the generics class
     * @return the list
     */
    <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass);
}