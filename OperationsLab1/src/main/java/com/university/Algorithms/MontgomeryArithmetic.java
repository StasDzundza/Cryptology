package com.university.Algorithms;

import java.math.BigInteger;
import java.util.Objects;

public final class MontgomeryArithmetic {
    private BigInteger modulus;  // Must be an odd number at least 3

    // Computed numbers
    private BigInteger reducer;       // Is a power of 2
    private int reducerBits;          // Equal to log2(reducer)
    private BigInteger reciprocal;    // Equal to reducer^-1 mod modulus
    private BigInteger mask;          // Because x mod reducer = x & (reducer - 1)
    private BigInteger factor;        // Equal to (reducer * reducer^-1 - 1) / n
    private BigInteger convertedOne;  // Equal to convertIn(BigInteger.ONE)



    // The modulus must be an odd number at least 3
    public MontgomeryArithmetic(BigInteger modulus) {
        // Modulus
        Objects.requireNonNull(modulus);
        if (!modulus.testBit(0) || modulus.compareTo(BigInteger.ONE) <= 0)
            throw new IllegalArgumentException("Modulus must be an odd number at least 3");
        this.modulus = modulus;

        reducerBits = (modulus.bitLength() / 8 + 1) * 8;  // This is a multiple of 8
        reducer = BigInteger.ONE.shiftLeft(reducerBits);  // This is a power of 256
        mask = reducer.subtract(BigInteger.ONE);
        assert reducer.compareTo(modulus) > 0 && reducer.gcd(modulus).equals(BigInteger.ONE);

        reciprocal = reducer.modInverse(modulus);
        factor = reducer.multiply(reciprocal).subtract(BigInteger.ONE).divide(modulus);
        convertedOne = reducer.mod(modulus);
    }

    public BigInteger convertIn(BigInteger x) {
        return x.shiftLeft(reducerBits).mod(modulus);
    }

    public BigInteger convertOut(BigInteger x) {
        return x.multiply(reciprocal).mod(modulus);
    }

    public BigInteger multiply(BigInteger x, BigInteger y) {
        assert x.signum() >= 0 && x.compareTo(modulus) < 0;
        assert y.signum() >= 0 && y.compareTo(modulus) < 0;
        BigInteger product = x.multiply(y);
        BigInteger temp = product.and(mask).multiply(factor).and(mask);
        BigInteger reduced = product.add(temp.multiply(modulus)).shiftRight(reducerBits);
        BigInteger result = reduced.compareTo(modulus) < 0 ? reduced : reduced.subtract(modulus);
        assert result.signum() >= 0 && result.compareTo(modulus) < 0;
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