Java Database Performance Demonstrator
======================================

This project was created as an assignment for my databases course, [CMPUT291](https://www.cs.ualberta.ca/undergraduate-students/course-directory/introduction-file-and-database-management).

To use this file execute the following commands:

1. `make`
2. `mydbtest btree` OR
`mydbtest hash` OR
`mydbtest indexfile`

Alternatively, you could use gradle directly to build the project.

Running this project requires you to have Berkeley DB installed, with 
the native Java API available. You can get Berkeley DB from
[here](http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/downloads/index.html)
(you do **not** want the Java Edition). Once downloaded, you should 
compile it with Java support and install it. Make sure that `db.jar` is 
available in `/usr/share/java/` and the native libraries are in your 
Java library path.
