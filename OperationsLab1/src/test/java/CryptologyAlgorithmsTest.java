import com.university.Algorithms.CryptologyAlgorithms;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;

public class CryptologyAlgorithmsTest {

    public static final int NUMBER_OF_TESTS = 10;
    public static final int NUMBER_OF_BITS = 512;

    @Test
    public void constantsTest() throws NoSuchFieldException, IllegalAccessException {
        CryptologyAlgorithms algorithms = new CryptologyAlgorithms();
        Field field = CryptologyAlgorithms.class.
                getDeclaredField("LENGTH_OF_COMPUTER_WORD");
        field.setAccessible(true);
        int LENGTH_OF_COMPUTER_WORD = (Integer) field.get(algorithms);
        Assert.assertEquals(64, LENGTH_OF_COMPUTER_WORD);

        field = CryptologyAlgorithms.class.
                getDeclaredField("KARATSUBA_MIN_LENGTH");
        field.setAccessible(true);
        int KARATSUBA_MIN_LENGTH = (Integer) field.get(algorithms);
        Assert.assertEquals(20, KARATSUBA_MIN_LENGTH);
    }

    @Test
    public void fermatTest() {
        Random random = new Random();
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            BigInteger testNumber = BigInteger.probablePrime(NUMBER_OF_BITS, random);
            Assert.assertTrue(CryptologyAlgorithms.fermatTest(testNumber));
        }
    }

    @Test
    public void millerRabinTest() {
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger testNumber = BigInteger.probablePrime(NUMBER_OF_BITS,random);
            Assert.assertTrue(CryptologyAlgorithms.millerRabinTest(testNumber));
        }
    }

    @Test
    public void modPowTest() {
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger e = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger n = new BigInteger(NUMBER_OF_BITS,random);
            Assert.assertEquals(a.modPow(e,n), CryptologyAlgorithms.modPow(a,e,n));
        }
    }

    @Test
    public void karatsubaMultiplyTest() {
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger b = new BigInteger(NUMBER_OF_BITS,random);
            Assert.assertEquals(a.multiply(b), CryptologyAlgorithms.karatsubaMultiply(a,b));
        }
    }

    @Test
    public void gcdTest(){
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger b = new BigInteger(NUMBER_OF_BITS,random);
            Assert.assertEquals(a.gcd(b), CryptologyAlgorithms.extendedGCD(a,b).getDivisor());
        }
    }
}
