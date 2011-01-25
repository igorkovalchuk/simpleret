package com.googlecode.simpleret.recorder;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;

public class JPDATraceRecorder {
	
	private boolean shared = false;
	private String address = "";
	private String host = "";
	private String port = "";
	
	private static final String CONNECTOR_SOCKET = "com.sun.jdi.SocketAttach";
	private static final String CONNECTOR_SHARED = "com.sun.jdi.SharedMemoryAttach";
	
	public static void usage() {
		System.out.println("\nSimple Reverse Engineering Tools. Program Trace Recorder.");
		System.out.println("");
		System.out.println("Please use:");
		System.out.println("	java -cp \".;log4j_path/log4j.jar;jdk_lib_path/tools.jar\" com.googlecode.simpleret.recorder.JPDATraceRecorder");				
		System.out.println("For a shared memory transport on the Microsoft Windows platform:");
		System.out.println("	-local some-shared-memory-address-name");
		System.out.println("Otherwise:");
		System.out.println("	-host somehost -port someport");
		System.out.println("");
		System.exit(1);
	}
	
	public void arguments(String[] args) {
		Set<String> noargStatic = new HashSet<String>();
		Set<String> noarg = new HashSet<String>();

		Set<String> withargStatic = new HashSet<String>();
		Map<String,String> witharg = new HashMap<String,String>();
		withargStatic.add("-local");
		withargStatic.add("-host");
		withargStatic.add("-port");

		int length = args.length;
		String key;
		for(int i=0; i < length; i++) {
			key = args[i];
			if (noargStatic.contains(key)) {
				noarg.add(key);
			} else if (withargStatic.contains(key)) {
				if ( (i + 1) < length ) {
					String value = args[i+1].trim();
					if (value.equals(""))
						usage();
					witharg.put(key, value);
				} else {
					usage();
				}
			}
		}
		
		if (witharg.containsKey("-local")) {
			this.shared = true;
			this.address = witharg.get("-local");
		} else {
			if (witharg.containsKey("-host")) {
				this.host = witharg.get("-host");
			} else {
				usage();
			}
			if (witharg.containsKey("-port")) {
				this.port = witharg.get("-port");
			} else {
				usage();
			}
		}
		
	}

	public static void main(String[] args) throws Exception{
		System.out.println("");
		JPDATraceRecorder recorder = new JPDATraceRecorder();
		recorder.arguments(args);
		try {
			recorder.run();
		} catch (ConnectException e) {
			System.out.println("Looks like the other program doesn't wait for a debugging/tracing connection ...\n");
			throw e;
		}
	}

	public void run() throws Exception{

        Configuration c = new Configuration();
        c.setInputFile("trace.cfg.test.txt");

		AttachingConnector connector = null;
		AttachingConnector connectorShared = null;
		
        List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
        Iterator<Connector> i = connectors.iterator();
        /*
			com.sun.jdi.CommandLineLaunch
			com.sun.jdi.RawCommandLineLaunch
			com.sun.jdi.SocketAttach
			com.sun.jdi.SocketListen
			com.sun.jdi.SharedMemoryAttach
			com.sun.jdi.SharedMemoryListen
         */
        while (i.hasNext()) {
            Connector aConnector = i.next();
        	/*
    			com.sun.jdi.connect.AttachingConnector
    			com.sun.jdi.connect.Connector
    			com.sun.tools.jdi.ConnectorImpl
    			com.sun.tools.jdi.GenericAttachingConnector
    			com.sun.tools.jdi.SocketAttachingConnector
        	 */
            if (this.shared) {
            	if (aConnector.name().equals(CONNECTOR_SHARED)) {
            		connectorShared = (AttachingConnector) aConnector;
            	}
            } else {
            	if (aConnector.name().equals(CONNECTOR_SOCKET)) {
            		connector = (AttachingConnector) aConnector;
            		break;
            	}
            }
        }

        if (this.shared) {
        	if (connectorShared == null)
        		throw new RuntimeException("No such connector: " + CONNECTOR_SHARED);
        } else {
        	if (connector == null)
        		throw new RuntimeException("No such connector: " + CONNECTOR_SOCKET);
        }
        
        Map<String, Argument> arguments;
        VirtualMachine vm;
        
        if (this.shared) {
        	arguments = connectorShared.defaultArguments();
        	Connector.Argument address = (Connector.Argument)arguments.get("name");
        	address.setValue(this.address);
        	vm = connectorShared.attach(arguments);
        } else {
        	arguments = connector.defaultArguments();
        	Connector.Argument host = (Connector.Argument)arguments.get("hostname");
            Connector.Argument port = (Connector.Argument)arguments.get("port");
            host.setValue(this.host);
            port.setValue(this.port);
            vm = connector.attach(arguments);
        }
                
        vm.setDebugTraceMode(0);
        
//        Configuration c = new Configuration();

        /*
        String configurationData = "" +
        	"[enabled]\n" +
        	"[display]\n" +
        	"[include]:com.googlecode.*\n" +
        	"[file]:trace.output.test3.txt\n";

        StringReader sr = new StringReader(configurationData);
        c.setInputReader(new BufferedReader(sr));
        */

        c.initialize();
        String[] include = c.getIncludeSignatures();

		Recorder recorder = new Recorder(c);
		
        EventThread eventThread = new EventThread(vm, include, null,recorder);
        eventThread.setEventRequests(false);
        eventThread.start();
        vm.resume();

        System.out.println("\nPlease see results in [" + c.getOutputFileName() + "]\n");

	}

}
