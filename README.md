# pc2lib2: The PC² Assistant Tool

A simple tool with convienience functions for PC².

# Getting Started

Due to the way PC^2 works, you must use legacy encryption methods. To do this, add this to your vm options:

```
-Djdk.crypto.KeyAgreement.legacyKDF=true
```

Without it, the library will always fail to load a contest.

**Important:** All judges and the host must use the same PC² installation path. If judges are on a different operating system than the host, the host can adjust the Judge path to match the judge's OS path formatting in the Problems section. This ensures that all judges can access the problem data correctly regardless of their platform.

Since the library simulates the running of a PC^2 server, the following line of code is recommended to forcibly close the server, but will also terminate the program. It is recommended you put this line at the end of your main method.

```java
System.exit(0);
```

View the [wiki](https://github.com/lingfeishengtian/pc2lib2/wiki) for more documentation.

For detailed documentation on the pc2pqascript automation script and its commands, see [pc2pqascript.README.md](pc2pqascript.README.md).

You can run a pc2pqascript with a command like this:

```bash
echo "run script.pc2pqascript" | java -cp pc2lib2-1.2.0.jar -Djdk.crypto.KeyAgreement.legacyKDF=true com.lingfeishengtian.cli.CLIStarter
```
