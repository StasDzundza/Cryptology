import com.university.SHA256.SHA256Hash;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class SHA256HashTest {
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

    private static final int[] INITIAL_HASH = { 0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 };

    @Test
    public void roundConstantsTest() throws NoSuchFieldException, IllegalAccessException {
        SHA256Hash sha256 = new SHA256Hash();
        Field field = SHA256Hash.class.
                getDeclaredField("ROUND_CONSTANTS");
        field.setAccessible(true);
        int[]sha256RoundConstants = (int[]) field.get(sha256);
        Assert.assertArrayEquals(ROUND_CONSTANTS, sha256RoundConstants);
    }

    @Test
    public void initialHashTest() throws NoSuchFieldException, IllegalAccessException {
        SHA256Hash sha256 = new SHA256Hash();
        Field field = SHA256Hash.class.
                getDeclaredField("INITIAL_HASH");
        field.setAccessible(true);
        int[]sha256InitialHash = (int[]) field.get(sha256);
        Assert.assertArrayEquals(INITIAL_HASH, sha256InitialHash);
    }

    @Test
    public void sha256ConstantsTest() throws NoSuchFieldException, IllegalAccessException {
        SHA256Hash sha256 = new SHA256Hash();
        Field field = SHA256Hash.class.
                getDeclaredField("BITS_IN_BLOCK");
        field.setAccessible(true);
        int BITS_IN_BLOCK = (int) field.get(sha256);
        Assert.assertEquals(512, BITS_IN_BLOCK);

        field = SHA256Hash.class.
                getDeclaredField("NUM_OF_ADDITIONAL_WORDS");
        field.setAccessible(true);
        int NUM_OF_ADDITIONAL_WORDS = (int) field.get(sha256);
        Assert.assertEquals(64, NUM_OF_ADDITIONAL_WORDS);

        field = SHA256Hash.class.
                getDeclaredField("BUFFER_SIZE");
        field.setAccessible(true);
        int BUFFER_SIZE = (int) field.get(sha256);
        Assert.assertEquals(8, BUFFER_SIZE);

        field = SHA256Hash.class.
                getDeclaredField("NUM_OF_WORDS_IN_BLOCK");
        field.setAccessible(true);
        int NUM_OF_WORDS_IN_BLOCK = (int) field.get(sha256);
        Assert.assertEquals(16, NUM_OF_WORDS_IN_BLOCK);
    }

    @Test
    public void testHash() throws NoSuchAlgorithmException {
        int NUMBER_OF_TEST = 1000;
        int MAX_INPUT_LEN = 1000;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Random rand = new Random();
        for(int i = 0; i < NUMBER_OF_TEST; ++i) {
            byte[] input = new byte[rand.nextInt(MAX_INPUT_LEN)];
            rand.nextBytes(input);
            byte[]hash = SHA256Hash.hash(input);
            Assert.assertArrayEquals(hash, digest.digest(input));
            Assert.assertEquals(256,hash.length*8);
        }
    }
}
