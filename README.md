# OSCommandExecutioner

Execute OS Commands using JVM Runtime and get String output.
3 threads take a place to execute command, 1 is actually executing and 2 are used for reading standard output and error output. 
Maybe change Threads to Executors.newSingleThreadExecutor() - that should be safer
