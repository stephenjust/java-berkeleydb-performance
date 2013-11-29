JFLAGS = -g -cp ./:$(CLASSPATH)
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        BaseDb.java \
        BtreeDB.java \
        HashTableDb.java \
        Indexfile.java \
	DatabaseApp.java

default: classes

classes: $(CLASSES:.java=.class)
	chmod +x mydbtest

clean:
	$(RM) *.class
