package Crypto;

import Crypto.Impl.FragmentArray;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.BitSet;

import static junit.framework.TestCase.assertEquals;

public class TestFragmentArray {
    private FragmentArray myFragment;
    private BitSet set247;

    @Before
    public void setUp() {
        myFragment = new FragmentArray(new BigInteger("247"), 4);
        set247 = new BitSet();
        set247.set(0, true);
        set247.set(1, true);
        set247.set(2, true);
        set247.set(3, false);
        set247.set(4, true);
        set247.set(5, true);
        set247.set(6, true);
        set247.set(7, true);
        set247.set(8, false);
    }

    @Test
    public void FragmentToInt() {
        assertEquals(247, myFragment.fragmentToInt(set247));
    }

    @Test
    public void GetFragmentValue() {
        assertEquals(15, myFragment.getFragmentValue(myFragment.getLength()-1));
        assertEquals(7, myFragment.getFragmentValue(myFragment.getLength()-2));
    }

    @Test
    public void decrement() {
        int before = myFragment.getFragmentValue(3);
        myFragment.decrementFragment(3);
        assertEquals(before-1, myFragment.getFragmentValue(3));
    }

    @Test
    public void notDecrementZero() {
        boolean result = myFragment.decrementFragment(0);
        assertEquals(false, result);
    }


    @Test
    public void increment() {
        int before = myFragment.getFragmentValue(0);
        myFragment.incrementFragment(0);
        assertEquals(before+1, myFragment.getFragmentValue(0));
    }




}
