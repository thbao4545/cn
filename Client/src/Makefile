JC = javac
JAVA = java
MAIN_CLASS = Client

run:
	$(JC) -d ..\bin CLient.java P2P.java P2PHandler.java 
	$(JAVA) -cp ..\bin $(MAIN_CLASS)

clean:
	del /Q ..\bin\*.class