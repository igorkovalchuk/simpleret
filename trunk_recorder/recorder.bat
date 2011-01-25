@rem Use a similar script to run the Trace Recorder.

@set CLASSES=bin\
@set TOOLS_JAR="C:\Program Files\Java\jdk1.6.0_22\lib\tools.jar"
@set LOG4J_JAR=lib\log4j-1.2.15.jar
@set HOST=localhost
@set PORT=8000
@echo .
@echo . Connection to the port ... %PORT%
@echo .

java -cp %CLASSES%;%LOG4J_JAR%;%TOOLS_JAR% com.googlecode.simpleret.recorder.JPDATraceRecorder -host %HOST% -port %PORT% %1 %2 %3 %4 %5 %6 %7 %8 %9
