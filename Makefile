VER=1.0.4

BINDIR = ./byzClass
COMPILEDIR = ./src/byzantine
LIBDIR =./jars

.PHONY: all clean doc release oralmsg byz

byz:	cleanByz compAll doJar
	@echo  Compilazione eseguita con successo
	
compAll:
	cd $(COMPILEDIR);\
	javac -d "./../.$(BINDIR)" -cp "../../$(LIBDIR)/peersim-1.0.5.jar:../../$(LIBDIR)/jep-2.3.0.jar:/$(LIBDIR)djep-1.0.0.jar:.*.class:../../$(LIBDIR)/jackson-core-2.12.1.jar:../../$(LIBDIR)/jackson-databind-2.12.1.jar:../../$(LIBDIR)/jackson-annotations-2.12.1.jar" *.java

doJar:
	cd $(BINDIR);\
	jar cvf byz.jar ByzantineNode.class Initializer.class OralMessage.class Majority.class FinalControl.class MyNode.class Message.class InfoForGraphics.class FinalInformation.class CreateJSON.class JsonControl.class MessagesHandler.class

oralmsg:
	sed -i '1s|.*|'"NUMNODES=$$NUM_NODES|" ./config-Byzantine.txt;\
	sed -i '2s|.*|'"BADNODES=$$TRAITORS|" ./config-Byzantine.txt;\
	sed -i '3s|.*|'"C_TRAITOR=$$C_TRAITOR|" ./config-Byzantine.txt;\
	sed -i '4s|.*|'"VALUE=$$VALUE|" ./config-Byzantine.txt;
	java -cp "$(LIBDIR)/peersim-1.0.5.jar:$(LIBDIR)/jep-2.3.0.jar:$(LIBDIR)/djep-1.0.0.jar:$(BINDIR)/byz.jar:$(LIBDIR)/jackson-core-2.12.1.jar:$(LIBDIR)/jackson-databind-2.12.1.jar:$(LIBDIR)/jackson-annotations-2.12.1.jar" peersim.Simulator config-Byzantine.txt

cleanByz:	
	rm -f byz.jar;\
	cd $(BINDIR);\
	rm -f $(BINDIR)
all:
	javac -classpath src:jep-2.3.0.jar:djep-1.0.0.jar `find src -name "*.java"`
clean:
	rm -f `find -name "*.class"`
doc:
	rm -rf doc/*
	javadoc -overview overview.html -classpath src:jep-2.3.0.jar:djep-1.0.0.jar -d doc \
                -group "Peersim" "peersim*" \
                -group "Examples" "example.*" \
		peersim \
		peersim.cdsim \
		peersim.config \
		peersim.core \
		peersim.dynamics \
		peersim.edsim \
		peersim.graph \
		peersim.rangesim \
		peersim.reports \
		peersim.transport \
		peersim.util \
		peersim.vector \
		example.aggregation \
		example.loadbalance \
		example.edaggregation \
		example.hot \
		example.newscast 

docnew:
	rm -rf doc/*
	javadoc -overview overview.html -docletpath peersim-doclet.jar -doclet peersim.tools.doclets.standard.Standard -classpath src:jep-2.3.0.jar:djep-1.0.0.jar -d doc \
                -group "Peersim" "peersim*" \
                -group "Examples" "example.*" \
		peersim \
		peersim.cdsim \
		peersim.config \
		peersim.core \
		peersim.dynamics \
		peersim.edsim \
		peersim.graph \
		peersim.rangesim \
		peersim.reports \
		peersim.transport \
		peersim.util \
		peersim.vector \
		example.aggregation \
		example.loadbalance \
		example.hot \
		example.edaggregation \
		example.newscast 


release: clean all docnew
	rm -fr peersim-$(VER)
	mkdir peersim-$(VER)
	cp -r doc peersim-$(VER)
	cp Makefile overview.html README CHANGELOG RELEASE-NOTES build.xml peersim-doclet.jar peersim-$(VER)
	mkdir peersim-$(VER)/example
	cp example/*.txt peersim-$(VER)/example
	mkdir peersim-$(VER)/src
	cp --parents `find src/peersim src/example -name "*.java"` peersim-$(VER)
	cd src ; jar cf ../peersim-$(VER).jar `find peersim example -name "*.class"`
	mv peersim-$(VER).jar peersim-$(VER)
	cp jep-2.3.0.jar peersim-$(VER)
	cp djep-1.0.0.jar peersim-$(VER)
