package com.university.Cipher;

public class IdeaCipher {

    private static final int ROUNDS = 8;

    private static final int KEY_SIZE = 16;//bytes - 128 bits

    private static final int DATA_BLOCK_SIZE = 8;//bytes - 64 bits

    private static final int BIT_MASK_8 = 0xFF;//255

    private static final int BIT_MASK_16 = 0xFFFF;//65535

    private static final int MODULUS = 0x10000;//2^16

    private static final int MODULUS_PLUS_ONE = 0x10001;//2^16 + 1

    private boolean encryptMode;

    private int[] subKey;

    public IdeaCipher(byte[] key, boolean encrypt) {
        int[] tempSubKey = createSubKeys(key);
        if (encrypt) {
            subKey = tempSubKey;
            encryptMode = true;
        } else {
            subKey = invertSubKey(tempSubKey);
            encryptMode = false;
        }
    }

    public void setMode(boolean encrypt) {
        if (encrypt && !encryptMode) {
            subKey = invertSubKey(subKey);
        } else if (!encrypt && encryptMode) {
            subKey = invertSubKey(subKey);
        }
    }

    public byte[] crypt(byte[] data) {
        byte[] toEncrypt = null;
        if (data.length % DATA_BLOCK_SIZE != 0) {
            int newSize = data.length + (DATA_BLOCK_SIZE - data.length % DATA_BLOCK_SIZE);
            toEncrypt = new byte[newSize];
            for (int i = 0; i < data.length; i++) {
                toEncrypt[i] = data[i];
            }
            for (int i = data.length; i < newSize; i++) {
                toEncrypt[i] = 0;
            }
        } else {
            toEncrypt = data.clone();
        }
        int steps = toEncrypt.length / DATA_BLOCK_SIZE;
        int dataPos = 0;
        while (steps != 0) {
            crypt(toEncrypt, dataPos);
            steps--;
            dataPos += DATA_BLOCK_SIZE;
        }
        return toEncrypt;
    }

    public void crypt(byte[] data, int dataPos) {

        int x0 = ((data[dataPos + 0] & BIT_MASK_8) << 8) | (data[dataPos + 1] & BIT_MASK_8);
        int x1 = ((data[dataPos + 2] & BIT_MASK_8) << 8) | (data[dataPos + 3] & BIT_MASK_8);
        int x2 = ((data[dataPos + 4] & BIT_MASK_8) << 8) | (data[dataPos + 5] & BIT_MASK_8);
        int x3 = ((data[dataPos + 6] & BIT_MASK_8) << 8) | (data[dataPos + 7] & BIT_MASK_8);

        int p = 0;
        for (int round = 0; round < ROUNDS; round++) {
            int y0 = mul(x0, subKey[p++]);
            int y1 = add(x1, subKey[p++]);
            int y2 = add(x2, subKey[p++]);
            int y3 = mul(x3, subKey[p++]);

            int t0 = mul(y0 ^ y2, subKey[p++]);
            int t1 = add(y1 ^ y3, t0);
            int t2 = mul(t1, subKey[p++]);
            int t3 = add(t0, t2);

            x0 = y0 ^ t2;
            x1 = y2 ^ t2;
            x2 = y1 ^ t3;
            x3 = y3 ^ t3;
        }

        int r0 = mul(x0, subKey[p++]);
        int r1 = add(x2, subKey[p++]);
        int r2 = add(x1, subKey[p++]);
        int r3 = mul(x3, subKey[p++]);

        data[dataPos + 0] = (byte) (r0 >> 8);
        data[dataPos + 1] = (byte) r0;
        data[dataPos + 2] = (byte) (r1 >> 8);
        data[dataPos + 3] = (byte) r1;
        data[dataPos + 4] = (byte) (r2 >> 8);
        data[dataPos + 5] = (byte) r2;
        data[dataPos + 6] = (byte) (r3 >> 8);
        data[dataPos + 7] = (byte) r3;
    }

    private static int add(int a, int b) {
        return ((a + b)% MODULUS) & BIT_MASK_16;
    }

    private static int addInv(int x) {
        return (MODULUS - x) & BIT_MASK_16;
    }

    private static int mul(int a, int b) {
        long r = (long) a * b;
        return (int) (r % MODULUS_PLUS_ONE) & BIT_MASK_16;
    }

    private static int mulInv(int x) {
        if (x <= 1) {
            return x;
        }
        int y = MODULUS_PLUS_ONE;
        int t0 = 1;
        int t1 = 0;
        while (true) {
            t1 += y / x * t0;
            y %= x;
            if (y == 1) {
                return MODULUS_PLUS_ONE - t1;
            }
            t0 += x / y * t1;
            x %= y;
            if (x == 1) {
                return t0;
            }
        }
    }

    private static int[] invertSubKey(int[] key) {
        int[] invKey = new int[key.length];
        int p = 0;
        int i = ROUNDS * 6;
        invKey[i + 0] = mulInv(key[p++]);
        invKey[i + 1] = addInv(key[p++]);
        invKey[i + 2] = addInv(key[p++]);
        invKey[i + 3] = mulInv(key[p++]);
        for (int r = ROUNDS - 1; r >= 0; r--) {
            i = r * 6;
            int m = r > 0 ? 2 : 1;
            int n = r > 0 ? 1 : 2;
            invKey[i + 4] = key[p++];
            invKey[i + 5] = key[p++];
            invKey[i + 0] = mulInv(key[p++]);
            invKey[i + m] = addInv(key[p++]);
            invKey[i + n] = addInv(key[p++]);
            invKey[i + 3] = mulInv(key[p++]);
        }
        return invKey;
    }

    private static int[] createSubKeys(byte[] userKey) {
        if (userKey.length != KEY_SIZE) {
            throw new IllegalArgumentException();
        }
        int numOfSubKeys = ROUNDS * 6 + 4;
        int[] key = new int[numOfSubKeys];
        //first 8 sub keys
        for (int i = 0; i < userKey.length / 2; i++) {
            key[i] = ((userKey[2 * i] & BIT_MASK_8) << 8) | (userKey[2 * i + 1] & BIT_MASK_8);
        }
        //other keys
        for (int i = userKey.length / 2; i < numOfSubKeys; i++) {
            key[i] = ((key[(i + 1) % 8 != 0 ? i - 7 : i - 15] << 9) | (key[(i + 2) % 8 < 2 ? i - 14 : i - 6] >> 7)) & BIT_MASK_16;
        }
        return key;
    }
}