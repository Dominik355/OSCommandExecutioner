package org.example;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Command command = new Command.CommandBuilder()
                .timeout(10)
                .timeUnit(TimeUnit.SECONDS)
                .cmdarray(new String[]{"ping", "google.com"})
                .build();
        System.out.println("Command has been create,now executing");
        command.execute();
        System.out.println("RESULT:\n" + command);
        System.out.println(command.getOutput().orElse("output empty") + "\n" + command.getErrorOutput().orElse("error empty"));
    }
}