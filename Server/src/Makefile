JC = javac
JAVA = java
MAIN_CLASS = Server

run:
	$(JC) -d ..\bin Server.java ClientHandler.java
	$(JAVA) -cp ..\bin $(MAIN_CLASS)

clean:
	del /Q ..\bin\*.class
