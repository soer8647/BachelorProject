package Configuration;

public class GlobalCounter {
    private static int conflictCount = 0;

    public synchronized static void reportConflict() {
        conflictCount++;
    }

    public static int getConflictCount() {
        return conflictCount;
    }
}
