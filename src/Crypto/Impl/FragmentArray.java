package Crypto.Impl;

import java.math.BigInteger;
import java.util.*;

public class FragmentArray {
    private BitSet fragmentBits;
    private int length;
    private int fragmentSize;

    public FragmentArray(BigInteger fragments, int fragmentSize) {
        this(BitSet.valueOf(fragments.toByteArray()),fragmentSize);
    }

    public FragmentArray(BitSet fragmentBits, int fragmentSize) {
        this.fragmentSize = fragmentSize;
        this.fragmentBits = fragmentBits;

        if (fragmentSize < 1) {
            throw new IllegalArgumentException();
        }
        this.length = (int) Math.ceil((double) fragmentBits.length() / fragmentSize);
    }

    public int getFragmentValue(int index) {
        int start = index * fragmentSize;
        int end   = start + fragmentSize;
        if (start > length*fragmentSize) {
            return -1;
        }
        if (end > length*fragmentSize) {
            end = fragmentBits.length();
        }
        BitSet fragment = fragmentBits.get(start, end);
        return fragmentToInt(fragment);
    }

    public int fragmentToInt (BitSet fragment) {
        int output = 0;
        for (int i = fragment.length(); (i = fragment.previousSetBit(i-1)) >= 0; ) {
            output |= (1 << i);
        }
        return output;
    }

    /**
     *
     * @return the number of fragments
     */
    public int getLength() {
        return this.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj==this) return true;
        if (obj.getClass() != this.getClass()) return false;
        FragmentArray fragArr = (FragmentArray) obj;
        return fragArr.fragmentBits.equals(this.fragmentBits);
    }

    @Override
    public String toString() {
        String output = "";
        int[] ints = getFragmentsAsArray();
        for (int i = 0; i < ints.length; i++) {
            output += ints[i] + " ";
        }
        return output;
    }

    public int[] getFragmentsAsArray() {
        int[] output = new int[length];
        for (int i = 0; i < length; i++) {
            output[i] = getFragmentValue(i);
        }
        return output;
    }

    public boolean incrementFragment(int index) {
        int start = index * fragmentSize;
        int end   = start + fragmentSize;
        int nextSet = fragmentBits.nextClearBit(start);
        if (nextSet<end) {
            fragmentBits.set(nextSet);
            if (nextSet > start) {
                fragmentBits.clear(start,nextSet);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean decrementFragment(int index) {
        int start = index * fragmentSize;
        int end   = start + fragmentSize;
        int nextSet = fragmentBits.nextSetBit(start);
        if (nextSet<end) {
            fragmentBits.clear(nextSet);
            if (nextSet > start) {
                fragmentBits.set(start,nextSet);
            }
            return true;
        } else {
            return false;
        }
    }
}
