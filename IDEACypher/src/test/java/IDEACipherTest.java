import com.university.cipher.IdeaCipher;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Random;

public class IDEACipherTest {

    private static final int KEY_SIZE = 16;//16 bytes = 128 bits
    private static int NUM_OF_TESTS = 10;

    @Test
    public void IDEACypherTest() {
        int dataSize = 8;
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            boolean encryptMode = true;

            byte[] byteKey = getRandomByteArray(KEY_SIZE);
            byte[] byteData = getRandomByteArray(dataSize);

            IdeaCipher idea = new IdeaCipher(byteKey, encryptMode);
            byte[] encryptedData = idea.crypt(byteData);

            idea.setMode(!encryptMode);
            byte[]decryptedData = idea.crypt(encryptedData);

            for(int j = 0; j < dataSize; j++){
                Assert.assertEquals(byteData[i],decryptedData[i]);
            }
            dataSize += 10;
        }
    }

    @Test
    public void constantsTest() throws NoSuchFieldException, IllegalAccessException {
        Field field = IdeaCipher.class.
                getDeclaredField("ROUNDS");
        field.setAccessible(true);
        int ROUNDS = (Integer) field.get(IdeaCipher.class);
        Assert.assertEquals(8, ROUNDS);

        field = IdeaCipher.class.
                getDeclaredField("KEY_SIZE");
        field.setAccessible(true);
        int KEY_SIZE = (Integer) field.get(IdeaCipher.class);
        Assert.assertEquals(16, KEY_SIZE);


        field = IdeaCipher.class.
                getDeclaredField("DATA_BLOCK_SIZE");
        field.setAccessible(true);
        int DATA_BLOCK_SIZE = (Integer) field.get(IdeaCipher.class);
        Assert.assertEquals(8, DATA_BLOCK_SIZE);

        field = IdeaCipher.class.
                getDeclaredField("BIT_MASK_8");
        field.setAccessible(true);
        int BIT_MASK_8 = (Integer) field.get(IdeaCipher.class);
        Assert.assertEquals(0xFF, BIT_MASK_8);

        field = IdeaCipher.class.
                getDeclaredField("BIT_MASK_16");
        field.setAccessible(true);
        int BIT_MASK_16 = (Integer) field.get(IdeaCipher.class);
        Assert.assertEquals(0xFFFF, BIT_MASK_16);
    }


    private byte[] getRandomByteArray(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return array;
    }
}
