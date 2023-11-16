# pc2lib2: The PC² Assistant Tool

A simple tool with convienience functions for PC².

# Getting Started

Due to the way PC^2 works, you must use legacy encryption methods. To do this, add this to your vm options:

```
-Djdk.crypto.KeyAgreement.legacyKDF=true
```

Without it, the library will always fail to load a contest.

Since the library simulates the running of a PC^2 server, the following line of code is recommended to forcibly close the server, but will also terminate the program. It is recommended you put this line at the end of your main method.

```java
System.exit(0);
```

View the [wiki](https://github.com/lingfeishengtian/pc2lib2/wiki) for more documentation.
