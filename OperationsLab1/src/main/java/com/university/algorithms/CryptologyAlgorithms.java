package com.university.algorithms;

import java.math.BigInteger;
import java.util.Random;

public class CryptologyAlgorithms {

    public static final int ITERATIONS_NUMBER = 100;
    public static final int LENGTH_OF_COMPUTER_WORD = 64;
    public static final int KARATSUBA_MIN_LENGTH = 20;

    private static BigInteger randNumber(BigInteger max) {
        BigInteger rNum;
        do {
            rNum = new BigInteger(max.bitLength(), new Random());
        } while (rNum.compareTo(BigInteger.ONE) <= 0 || rNum.compareTo(max) > 0);
        return rNum;
    }

    public static boolean fermatTest(BigInteger number) {
        if (number.intValue() == 2) {
            return false;
        } else {
            Random random = new Random();
            for (int i = 0; i < ITERATIONS_NUMBER; i++) {
                BigInteger a = BigInteger.probablePrime(number.bitLength() - 1, random);
                BigInteger gcd = number.gcd(a);
                if (!gcd.equals(BigInteger.ONE)) {
                    return false;
                }
                if (!a.modPow(number.subtract(BigInteger.ONE), number).equals(BigInteger.ONE)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean millerRabinTest(BigInteger number) {
        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        } else {
            BigInteger tmp = number.subtract(BigInteger.ONE);
            //Introduce number as : 2^s * t (t is odd)
            BigInteger two = new BigInteger(String.valueOf(2));
            int s = 0;
            while (tmp.mod(two).equals(BigInteger.ZERO)) {
                tmp = tmp.divide(two);
                s++;
            }
            BigInteger t = tmp;
            firstLoop:
            for (int i = 0; i < ITERATIONS_NUMBER; i++) {
                BigInteger a = randNumber(number.subtract(two));
                BigInteger x = a.modPow(t, number);
                if (x.equals(BigInteger.ONE) || x.equals(number.subtract(BigInteger.ONE))) {
                    continue firstLoop;
                } else {
                    for (int j = 0; j < s - 1; j++) {
                        x = x.modPow(two, number);
                        if (x.equals(BigInteger.ONE)) {
                            return false;
                        } else if (x.equals(number.subtract(BigInteger.ONE))) {
                            continue firstLoop;
                        }
                    }
                }
                return false;
            }
            return true;
        }
    }

    public static BigInteger modPow(BigInteger x, BigInteger y, BigInteger m) {
        if (y.equals(BigInteger.ZERO))
            return BigInteger.ONE;
        BigInteger z = modPow(x, y.divide(BigInteger.TWO), m);
        if (y.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return (z.multiply(z).mod(m));
        else
            return (x.multiply(z).multiply(z).mod(m));
    }

    public static BigInteger karatsubaMultiply(BigInteger x, BigInteger y) {
        int n = Math.max(x.bitLength(), x.bitLength());
        if (n < KARATSUBA_MIN_LENGTH) {
            return x.multiply(y);
        }
        n = n / 2 + (n % 2);

        BigInteger a1 = x.shiftRight(n);
        BigInteger a2 = x.subtract(a1.shiftLeft(n));
        BigInteger b1 = y.shiftRight(n);
        BigInteger b2 = y.subtract(b1.shiftLeft(n));

        BigInteger a1b1 = karatsubaMultiply(a1, b1);
        BigInteger a2b2 = karatsubaMultiply(a2, b2);
        BigInteger a1a2b1b2 = karatsubaMultiply(a1.add(a2), b1.add(b2));

        return a1b1.shiftLeft(n * 2).add(a2b2).add(a1a2b1b2.subtract(a1b1).subtract(a2b2).shiftLeft(n));
    }

    public static class GCDResult {
        private BigInteger divisor, x, y;

        public BigInteger getDivisor() {
            return divisor;
        }

        public BigInteger getX() {
            return x;
        }

        public BigInteger getY() {
            return y;
        }
    }

    public static GCDResult extendedGCD(BigInteger a, BigInteger b) {

        if (b.equals(BigInteger.ZERO)) {
            GCDResult result = new GCDResult();
            result.divisor = a;
            result.x = BigInteger.ONE;
            result.y = BigInteger.ZERO;
            return result;
        }

        GCDResult r = extendedGCD(b, a.mod(b));

        GCDResult result = new GCDResult();
        result.divisor = r.divisor;
        result.x = r.y;
        result.y = r.x.subtract((a.divide(b)).multiply(r.y));

        return result;
    }
}
