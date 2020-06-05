package com.university.algorithms;

import java.math.BigInteger;
import java.util.Objects;

public final class MontgomeryArithmetic {
    private BigInteger modulus;

    private BigInteger reducer;
    private int reducerBits;
    private BigInteger reciprocal;
    private BigInteger mask;
    private BigInteger factor;
    private BigInteger convertedOne;

    public MontgomeryArithmetic(BigInteger modulus) {
        // modulus must be an odd number at least 3
        Objects.requireNonNull(modulus);
        if (!modulus.testBit(0) || modulus.compareTo(BigInteger.ONE) <= 0)
            throw new IllegalArgumentException("Modulus must be an odd number at least 3");
        this.modulus = modulus;

        reducerBits = (modulus.bitLength() / 8 + 1) * 8;
        reducer = BigInteger.ONE.shiftLeft(reducerBits);
        mask = reducer.subtract(BigInteger.ONE);
        assert reducer.compareTo(modulus) > 0 && reducer.gcd(modulus).equals(BigInteger.ONE);

        reciprocal = reducer.modInverse(modulus);
        factor = reducer.multiply(reciprocal).subtract(BigInteger.ONE).divide(modulus);
        convertedOne = reducer.mod(modulus);
    }

    public BigInteger convertToMontgomeryNum(BigInteger x) {
        return x.shiftLeft(reducerBits).mod(modulus);
    }

    public BigInteger convertFromMontgomeryNum(BigInteger x) {
        return x.multiply(reciprocal).mod(modulus);
    }

    public BigInteger multiply(BigInteger x, BigInteger y) {
        BigInteger product = x.multiply(y);
        BigInteger temp = product.and(mask).multiply(factor).and(mask);
        BigInteger reduced = product.add(temp.multiply(modulus)).shiftRight(reducerBits);
        BigInteger result = reduced.compareTo(modulus) < 0 ? reduced : reduced.subtract(modulus);
        return result;
    }

    public BigInteger pow(BigInteger x, BigInteger y) {
        assert x.signum() >= 0 && x.compareTo(modulus) < 0;
        if (y.signum() == -1)
            throw new IllegalArgumentException("Negative exponent");

        BigInteger z = convertedOne;
        for (int i = 0, len = y.bitLength(); i < len; i++) {
            if (y.testBit(i))
                z = multiply(z, x);
            x = multiply(x, x);
        }
        return z;
    }

}