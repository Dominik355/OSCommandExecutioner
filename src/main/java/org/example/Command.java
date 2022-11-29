package org.example;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Command {

    private final String[] cmdarray;

    private Integer exitValue;
    private String output, error;
    private ProcessHandler processHandler;

    private Command(int timeout, TimeUnit timeUnit, String... cmdarray) {
        this.cmdarray = cmdarray;
        this.processHandler = new ProcessHandler(timeout, timeUnit, null);
    }

    public void execute() {
        System.out.println("Executing Command [" + Arrays.asList(cmdarray) + "]");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(cmdarray);

            StringStreamConsumer outputStream = StringStreamConsumer.create(process.getInputStream());
            StringStreamConsumer errorOutputStream = StringStreamConsumer.create(process.getErrorStream());

            try {
                processHandler.waitFor(process);
            } finally {
                outputStream.joinWithTermination();
                errorOutputStream.joinWithTermination();
            }

            exitValue = process.exitValue();
            output = outputStream.toString();
            error = errorOutputStream.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getOutput() {
        return Optional.ofNullable(output);
    }

    public Optional<String> getErrorOutput() {
        return Optional.ofNullable(error);
    }

    public boolean isOverTime() {
        return processHandler.isOverTime();
    }

    public boolean failedToDestroyProcess() {
        return processHandler.hasFailedOnDestroy();
    }

    public Integer getExitValue() {
        return exitValue;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n, ", Command.class.getSimpleName() + "[", "]")
                .add("cmdarray=" + Arrays.toString(cmdarray))
                .add("exitValue=" + exitValue)
                .add("output='" + output + "'")
                .add("error='" + error + "'")
                .add("processHandler=" + processHandler)
                .toString();
    }

    public static class CommandBuilder {
        private static final int DEFAULT_TIMEOUT = 30000;
        private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
        private String[] cmdarray;
        private Integer timeout;
        private TimeUnit timeUnit;

        public CommandBuilder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public CommandBuilder timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public CommandBuilder cmdarray(String... cmdarray) {
            this.cmdarray = cmdarray;
            return this;
        }

        public Command build() {
            if (Objects.requireNonNull(cmdarray, "cmdarray can not be null !").length == 0) {
                throw new IllegalArgumentException("cmdarray can not be empty, atleast 1 element is required");
            }
            return new Command(
                    timeout != null ? timeout : DEFAULT_TIMEOUT,
                    timeUnit != null ? timeUnit : DEFAULT_TIMEUNIT,
                    cmdarray);
        }
    }

}
