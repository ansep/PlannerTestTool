package it.uniroma1.plannertests.writer;

public class WriteTimer {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
