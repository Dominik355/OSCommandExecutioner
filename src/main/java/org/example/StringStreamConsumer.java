package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.StringJoiner;

public class StringStreamConsumer extends Thread {

    private InputStream inputStream;

    private StringJoiner stringJoiner = new StringJoiner("\n");

    private StringStreamConsumer(InputStream inputStream) {
        super("StringStreamConsumer-" + inputStream.hashCode());
        this.inputStream = inputStream;
        start();
        System.out.println("separate Thread for output created and started");
    }

    public static StringStreamConsumer create(InputStream inputStream) {
        return new StringStreamConsumer(Objects.requireNonNull(inputStream, "InputStream for this Thread can not be null !"));
    }

    @Override
    public void run() {
        System.out.println("Starting StringStreamConsumer in new thread: " + Thread.currentThread().getName());
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            bufferedReader.lines().forEach(line -> stringJoiner.add(line));
        }
        catch (IOException e) {
            stringJoiner.add("IOException: ");
            stringJoiner.add(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return stringJoiner.toString();
    }

    public void joinWithTermination() throws IOException, InterruptedException {
        inputStream.close();
        this.join();
    }

}
