# pc2pqascript Documentation

This document explains the structure and commands of the `pc2pqascript` automation script for PC² contest setup. Each command is described in detail, including its purpose and usage.

Always use absolute paths. This is a limitation with how PC² handles file paths, and using relative paths may lead to errors if the script is executed from a different directory. Absolute paths ensure that the script can locate the necessary files and directories regardless of the execution context.

## Overview
A `pc2pqascript` is a plain text file where each line is a command to automate the setup and configuration of a PC² contest environment. The script is processed sequentially, and each command modifies the contest state or adds data as specified.

## Command Reference

### 1. `new <contest_dir> <admin_password>`
- **Purpose:** Creates a new contest at the specified directory with the given admin password.
- **Example:**
  ```
  new /Users/hunterhan/tomcat/webapps/ROOT theamazingsecuremima
  ```
- **Effect:** Initializes a new contest profile and sets the admin password.

### 2. `add problem <problem_dir> <problem_list_file>`
- **Purpose:** Adds multiple problems to the contest from a directory, using a list file that specifies problem names and test cases.
- **Example:**
  ```
  add problem /Users/hunterhan/Desktop/TravisCompetitionDataFiles /Users/hunterhan/Desktop/TravisCompetitionDataFiles/problemlistTravis.txt
  ```
- **Effect:** Reads the problem list file and adds each problem and its test cases from the specified directory.

### 3. `add problem <problem_dir> <input_file> <output_file>`
- **Purpose:** Adds a single problem to the contest, specifying the directory and the input/output files directly.
- **Example:**
  ```
  add problem DryRun /Users/hunterhan/Desktop/TravisCompetitionDataFiles/dryrun.dat /Users/hunterhan/Desktop/TravisCompetitionDataFiles/dryrun.out
  ```
- **Effect:** Adds a single problem with the given input and output files.

### 4. `setDefaultContest`
- **Purpose:** Sets default properties for the contest according to UIL rules.
- **Example:**
  ```
  setDefaultContest
  ```
- **Effect:** Marks the current contest as the default, so it is loaded automatically by PC² tools.

### 5. `setNewPasscodes <passcodes_file>`
- **Purpose:** Sets new team passcodes from a specified file.
- **Example:**
  ```
  setNewPasscodes /Users/hunterhan/Desktop/passwords.txt
  ```
- **Effect:** Reads the passcodes from the file and assigns them to teams in the contest.

### 6. `save`
- **Purpose:** Saves the current contest configuration to disk.
- **Example:**
  ```
  save
  ```
- **Effect:** Writes all current contest settings, problems, and accounts to the contest profile files.

## Example Script
```
new /Users/hunterhan/tomcat/webapps/ROOT theamazingsecuremima
add problem /Users/hunterhan/Desktop/TravisCompetitionDataFiles /Users/hunterhan/Desktop/TravisCompetitionDataFiles/problemlistTravis.txt
setDefaultContest
setNewPasscodes /Users/hunterhan/Desktop/passwords.txt
save
```

## Notes
- All file and directory paths must be absolute or relative to the script's execution directory.
- Commands are case-sensitive and must be written exactly as shown.
- The order of commands matters: for example, you must create a contest before adding problems.
- If you encounter errors, check that all paths exist and are accessible, and that the files are formatted correctly.

---
For more information, see the main project README or the PC² documentation.
