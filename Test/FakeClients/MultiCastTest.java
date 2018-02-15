package FakeClients;

import java.util.ArrayList;
import java.util.List;

public class MultiCastTest {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new MulticastClient().start();
        }
        new MulticastServerThread().start();
    }
}
