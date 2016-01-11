# Online #

## Start ##

For this **demo version** Windows and Linux users can use **Java Web Start**

  * If you have not Java on your PC then download it from http://www.java.com

  * Open simpleret-20101027-1255.jnlp file in your browser.

  * Press the "**Cancel**" button in the message box _"The application has requested permission to establish connections to <your computer>. Do you want to allow this action?"_

This Java Web Start application runs **restricted**, which means that it does not have access to some system resources such as local files.

http://en.wikipedia.org/wiki/Java_Web_Start (English)

http://ru.wikipedia.org/wiki/Java_Web_Start (Russian)

_Linux was tested with !OpenJDK Java 6 Web Start._

## Usage ##

On the screen you can see a window with some strange content - this is a short list of recorded methods (the trace). A real application can have hundreds of thousands of such records.

  1. click _(Edit)-(List of Signatures N1)_ or press a key (1);
  1. enter com.googlecode.simpleret.example.Gamma.start and press OK; this field can accept a Java regular expression too;
  1. click _(Edit)-(Instrumantal Box)_ or press a key (0) - now you can see the _Instruments_;
  1. click on _Signatures_ and press OK;
  1. choose the yellow color - you will see that all signatures of com.googlecode.simpleret.example.Gamma.start are in yellow;
  1. anytime you can show/hide the marked records by pressing (Z)-key or  _(View)-(Show Colorized)_ - this is the main feature of this software - the filtering of a program trace;
  1. click _(Edit)-(Instrumantal Box)_;
  1. set the checkbox _Include current element_ to OFF, and _Include parents_ to ON, click on _Signatures_, and press OK;
  1. choose the grey color - you will see that all **parent** signatures of com.googlecode.simpleret.example.Gamma.start are grey now;
  1. press (Z)-key on and off just to see the differences;
  1. click _(Edit)-(Instrumantal Box)_;
  1. set the checkbox _Include current element_ to OFF, and _Include sub-elements_ to ON, click on _Signatures_, and press OK;
  1. choose the green color - you will see that all **child** signatures of com.googlecode.simpleret.example.Gamma.start are green;
  1. press (Z)-key on and off;

Also you can select a color by pressing on the signature's number (on the left side of the list) to unmark/delete/undelete all(!) records of the selected color - _(Edit)-(Instrumantal Box)-(By selected color)_.



# Locally #

Today it is not packaged, so please use **Eclipse Europa IDE** and _Import_ this project from SVN.


## Recording a program trace ##


You can find the Trace Recorder in **svn/trunk\_recorder**


  * Create a new directory to hold your configuration file and output in APPDATA (in Linux - yourhomepath/simpleret);

  * cd %APPDATA%
  * mkdir simpleret
  * cd simpleret

  * Create a configuration file here "trace.cfg.test.txt" with the nextcontent:

```

# Enable tracing.
[enabled]  

# Print results to the file. 
[file]:trace.output.test.txt  

# Also print results to the Console. 
[display]  

# Include these classes. This is the fastest option.
[include]:com.googlecode.* 

# This is a very very slow option. Please use [include] option to make it faster.
# If specified, then ignore all calls,
# except classes that begin with this word.
[runtime]:com.googlecode.special.

```

Run an example:

(see **sample.bat**)

```
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000 com.googlecode.simpleret.example.Main
```

or (for a shared memory transport on the Microsoft Windows platform)

```
java -Xdebug -Xrunjdwp:transport=dt_shmem,server=y,suspend=n,address=mysharedmemory com.googlecode.simpleret.example.Main
```

Note: for a large real life applications please also set -Xrunjdwp:suspend=n and turn on the Trace Recorder only temporarily, i.e. for some concrete actions.

Listening for transport dt\_socket at address: 8000

Run a trace recorder:

(see **recorder.bat**)

```
java -cp ".;log4j_path/log4j.jar;jdk_lib_path/tools.jar" com.googlecode.simpleret.recorder.JPDATraceRecorder -host 127.0.0.1 -port 8000
```

For a shared memory transport:

```
-local some-shared-memory-address-name
```

Otherwise:

```
-host somehost -port someport
```

See results in **trace.out.test.txt**

## Importing a program trace ##

...

That is simple.

Note: today this software deletes the previous trace recorder's data from the database if you are importing a new data. You need to create one more database (and point hibernate.properties - hibernate.connection.url to this new database) when you want to keep the previous data.

com.googlecode.simpleret.RunImport

...


## Program Trace Viewer - keyboard reference ##

Keys:
  * Q,W,E,R - scrolling backward;
  * A,S,D,F - scrolling forward;
  * Z - show only colourised records;
  * 1,2,3 - prepare a list of signatures to filter; every signature looks like `com.package.class.method` or `com.package.class` or like a Java regular expression `^.*method.*$` ;
  * 0 - Instrumental window;
  * 9 - input a range of identifiers to show;
  * X - display that range;
  * L - change the current deepness (level) of view;
  * H - HTML export;
  * U - AmaterasUML export;
  * Note: click on a link (number/id) to use the current colour of that line for"Clean colour".