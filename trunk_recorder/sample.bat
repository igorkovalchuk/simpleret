@rem Use the similar script to explore a software.
@rem In this example we explore com.googlecode.simpleret.example.Main

@set CLASSES=bin\
@set PORT=8000
@echo .
@echo . Listening port ... %PORT%
@echo .

java -cp %CLASSES% -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=%PORT% com.googlecode.simpleret.example.Main
