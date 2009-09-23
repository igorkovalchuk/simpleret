package com.googlecode.simpleret.recorder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;

public class JPDATraceRecorder {

	public static void main(String[] args) throws Exception{
		
		AttachingConnector connector = null;
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
            if (aConnector.name().equals("com.sun.jdi.SocketAttach")) {
            	connector = (AttachingConnector) aConnector;
            	/*
            		com.sun.jdi.connect.AttachingConnector
            		com.sun.jdi.connect.Connector
            		com.sun.tools.jdi.ConnectorImpl
            		com.sun.tools.jdi.GenericAttachingConnector
            		com.sun.tools.jdi.SocketAttachingConnector
            	 */
            	break;
            }
        }

        if (connector == null) {
        	throw new RuntimeException("No such connector: " + AttachingConnector.class);
        }

        Map<String, Argument> arguments = connector.defaultArguments();
        Connector.Argument host = (Connector.Argument)arguments.get("hostname");
        Connector.Argument port = (Connector.Argument)arguments.get("port");
        host.setValue("127.0.0.1");
        port.setValue("8000");

        VirtualMachine vm = connector.attach(arguments);
        vm.setDebugTraceMode(0);
        EventThread eventThread = new EventThread(vm, null, null);
        eventThread.setEventRequests(false);
        eventThread.start();
        vm.resume();
	}

}
