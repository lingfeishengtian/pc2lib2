# pc2lib2
PC2LIB but remade.

pc2lib2 allows you to automatically create contests with one button. No need for even opening the administrator panel to setup!

When the a release is made, you can do things such as create contests from pc2 zip file, setup contests with a click of a button, add accounts easily, etc.

# Documentation

**EVERYONE WHO WANTS TO USE THIS SHOULD AND MUST READ GETTING STARTED**

## Getting Started

Due to the way PC^2 works, you must use legacy encryption methods. To do this, add this to your vm options:

```
-Djdk.crypto.KeyAgreement.legacyKDF=true
```

Without it, the library will always fail to load a contest.

Another useful thing you could add is

```java
System.exit(0);
```

to the last line of your main code (unless you're using GUI then thats optinal) because the library wont stop running.

## Contest Instances

Contest instances are required to load up a contest. They search and initialize profiles.

```java
ContestInstance contest = new ContestInstance(<path to your bin folder in your pc^2 folder>, <set this to true, only set to false if you really want to create a new profile and override the old profiles.properties>, <contest password>); // Initialize the contest instance
contest.startDataViewing(); // Open the contest and start viewing data
System.exit(0); // Useful for CLI applications
```


