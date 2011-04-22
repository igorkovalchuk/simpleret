@rem Use a similar script to run the Trace Recorder.

@set CLASSES=bin\
@set TOOLS_JAR="C:\Program Files\Java\jdk1.6.0_23\lib\tools.jar"
@set HOST=localhost
@set PORT=1414
@echo .
@echo . Connection to the port ... %PORT%
@echo .

java -cp %CLASSES%;%TOOLS_JAR% com.googlecode.simpleret.recorder.JPDATraceRecorder -host %HOST% -port %PORT% %1 %2 %3 %4 %5 %6 %7 %8 %9
