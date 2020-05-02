import com.university.Algorithms.MontgomeryArithmetic;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

public class MontgomeryArithmeticTest {
    private static final int NUMBER_OF_TESTS = 100;
    public static final int NUMBER_OF_BITS = 512;


    @Test(expected = NullPointerException.class)
    public void negativeModulePassingTest(){
        MontgomeryArithmetic montgomeryArithmetic = new MontgomeryArithmetic(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalModulePassingTest(){
        MontgomeryArithmetic montgomeryArithmetic1 = new MontgomeryArithmetic(BigInteger.TEN);
        MontgomeryArithmetic montgomeryArithmetic2 = new MontgomeryArithmetic(BigInteger.ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeExponentTest(){
        Random random = new Random();
        MontgomeryArithmetic montgomeryArithmetic = new MontgomeryArithmetic(getRandomOddBigInteger(NUMBER_OF_BITS,random));
        BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
        BigInteger e = BigInteger.valueOf(Long.valueOf(-1));
        montgomeryArithmetic.pow(montgomeryArithmetic.convertIn(a),e);
    }

    @Test
    public void modPowTest(){
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger e = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger n = getRandomOddBigInteger(NUMBER_OF_BITS,random);
            MontgomeryArithmetic montgomeryArithmetic = new MontgomeryArithmetic(n);
            Assert.assertEquals(a.modPow(e,n), montgomeryArithmetic.convertOut(
                    montgomeryArithmetic.pow(montgomeryArithmetic.convertIn(a),e)));
        }
    }

    @Test
    public void montgomeryMultiplyTest(){
        Random random = new Random();
        for(int i = 0; i < NUMBER_OF_TESTS; i++){
            BigInteger a = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger e = new BigInteger(NUMBER_OF_BITS,random);
            BigInteger n = getRandomOddBigInteger(NUMBER_OF_BITS,random);
            MontgomeryArithmetic montgomeryArithmetic = new MontgomeryArithmetic(n);
            Assert.assertEquals(a.multiply(e).mod(n), montgomeryArithmetic.convertOut(
                    montgomeryArithmetic.multiply(montgomeryArithmetic.convertIn(a),montgomeryArithmetic.convertIn(e))));
        }
    }



    public static BigInteger getRandomOddBigInteger(int bitLength, Random random) {
        while(true) {
            BigInteger a = new BigInteger(bitLength, random);
            if(a.testBit(0))
                return a;
        }
    }
}
