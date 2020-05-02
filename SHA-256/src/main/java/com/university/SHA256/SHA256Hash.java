package com.university.SHA256;

import java.nio.ByteBuffer;

public class SHA256Hash {
    //Initialize array of round constants:
    //(first 32 bits of the fractional parts of the cube roots of the first 64 primes 2..311)
    private static final int[] ROUND_CONSTANTS = { 0x428a2f98, 0x71374491, 0xb5c0fbcf,
            0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74,
            0x80deb1fe, 0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786,
            0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc,
            0x76f988da, 0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
            0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967, 0x27b70a85,
            0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb,
            0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b, 0xc24b8b70,
            0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3,
            0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f,
            0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7,
            0xc67178f2 };

    //Initialize hash values:
    //(first 32 bits of the fractional parts of the square roots of the first 8 primes 2..19)
    private static final int[] INITIAL_HASH = { 0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 };

    private static final int BITS_IN_BLOCK = 512;
    private static final int NUM_OF_ADDITIONAL_WORDS = 64;
    private static final int BUFFER_SIZE = 8;
    private static final int NUM_OF_WORDS_IN_BLOCK = 16;

    public static byte[]hash(byte[]input){

        byte[]extendedMessage = extendInitialMessage(input);
        int[] words = convertToIntArray(extendedMessage);

        //Algorithm used arrays
        int[] ADDITIONAL_WORDS = new int[NUM_OF_ADDITIONAL_WORDS];
        int[] HASH = new int[BUFFER_SIZE];
        int[] TEMP_BUFFER = new int[BUFFER_SIZE];

        System.arraycopy(INITIAL_HASH, 0, HASH, 0, INITIAL_HASH.length);

        // for each block which contains 16 words(1 word = 32 bits = 4 bytes)
        for (int i = 0, n = words.length / NUM_OF_WORDS_IN_BLOCK; i < n; ++i) {

            // initialize other ADDITIONAL_WORDS
            System.arraycopy(words, i * NUM_OF_WORDS_IN_BLOCK, ADDITIONAL_WORDS, 0, NUM_OF_WORDS_IN_BLOCK);
            for (int t = 16; t < ADDITIONAL_WORDS.length; ++t) {
                ADDITIONAL_WORDS[t] = smallSig1(ADDITIONAL_WORDS[t - 2]) + ADDITIONAL_WORDS[t - 7] + smallSig0(ADDITIONAL_WORDS[t - 15])
                        + ADDITIONAL_WORDS[t - NUM_OF_WORDS_IN_BLOCK];
            }
            System.arraycopy(HASH, 0, TEMP_BUFFER, 0, HASH.length);
            for (int t = 0; t < ADDITIONAL_WORDS.length; ++t) {
                int t1 = TEMP_BUFFER[7] + bigSig1(TEMP_BUFFER[4])
                        + ch(TEMP_BUFFER[4], TEMP_BUFFER[5], TEMP_BUFFER[6]) + ROUND_CONSTANTS[t] + ADDITIONAL_WORDS[t];
                int t2 = bigSig0(TEMP_BUFFER[0]) + maj(TEMP_BUFFER[0], TEMP_BUFFER[1], TEMP_BUFFER[2]);
                System.arraycopy(TEMP_BUFFER, 0, TEMP_BUFFER, 1, TEMP_BUFFER.length - 1);
                TEMP_BUFFER[4] += t1;
                TEMP_BUFFER[0] = t1 + t2;
            }
            // add values in TEMP_BUFFER to values in H
            for (int t = 0; t < HASH.length; ++t) {
                HASH[t] += TEMP_BUFFER[t];
            }
        }
        return convertToByteArray(HASH);
    }


    private static byte[] extendInitialMessage(byte[]message){
        int numOfBytesInBlock = BITS_IN_BLOCK / 8;

        // new message length: original length + 1-bit + 8-byte length + padding
        int newMessageLength = message.length + 1 + 8;
        int padBytes = (numOfBytesInBlock - newMessageLength % numOfBytesInBlock) % numOfBytesInBlock;
        newMessageLength += padBytes;

        final byte[] extendedMessage = new byte[newMessageLength];
        System.arraycopy(message, 0, extendedMessage, 0, message.length);

        // write 1-bit
        extendedMessage[message.length] = (byte) 0b10000000;

        // write 8-byte integer describing the original message length
        int lenPos = message.length + 1 + padBytes;
        ByteBuffer.wrap(extendedMessage, lenPos, 8).putLong(message.length * 8);

        return extendedMessage;
    }

    private static int[] convertToIntArray(byte[]data){
        if (data.length % Integer.BYTES != 0) {
            throw new IllegalArgumentException("Illegal byte array length. Length should divide to 4");
        }
        ByteBuffer buf = ByteBuffer.wrap(data);
        int[] convertedData = new int[data.length / Integer.BYTES];
        for (int i = 0; i < convertedData.length; ++i) {
            convertedData[i] = buf.getInt();
        }
        return convertedData;
    }

    private static byte[]convertToByteArray(int[]data){
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Integer.BYTES);
        for (int i = 0; i < data.length; ++i) {
            byteBuffer.putInt(data[i]);
        }
        return byteBuffer.array();
    }

    private static int ch(int x, int y, int z) {
        return (x & y) | ((~x) & z);
    }

    private static int maj(int x, int y, int z) {
        return (x & y) | (x & z) | (y & z);
    }

    private static int bigSig0(int x) {
        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13)
                ^ Integer.rotateRight(x, 22);
    }

    private static int bigSig1(int x) {
        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11)
                ^ Integer.rotateRight(x, 25);
    }

    private static int smallSig0(int x) {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18)
                ^ (x >>> 3);
    }

    private static int smallSig1(int x) {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19)
                ^ (x >>> 10);
    }
}
