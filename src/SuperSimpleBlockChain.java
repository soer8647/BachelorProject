import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SuperSimpleBlockChain {
    /**
     * This finds a nonce so that Hash(previous_Hash + nonce) < target, 10 times
     */
    public static void main(String[] args) {
        //init variables
        int iteration = 0;
        int nonce = 0;
        String previous_block_Hash = ""; // maybe this should be something
        BigInteger hash;

        //make our target (which the hash must be lower than)
        BigInteger target = BigInteger.valueOf(2).pow(256).shiftRight(20);
        System.out.println("Target is < " + target);

        while(iteration<10) {

            // Attempt to find a "correct" nonce
            do {
                hash = hash_SHA256(previous_block_Hash + nonce);
                nonce++;
            } while (hash.compareTo(target) > 0);

            System.out.println("new block found!");
            previous_block_Hash = hash.toString();
            iteration++;
        }
    }

    public static BigInteger hash_SHA256(String text) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(text.getBytes());
        return new BigInteger(1,hash);
    }

}
