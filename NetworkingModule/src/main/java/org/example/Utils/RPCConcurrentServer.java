package org.example.Utils;

import org.example.ClientRPCWorker;
import org.example.IServices;


import java.net.Socket;

public class RPCConcurrentServer extends AbstractConcurrentServer {

    private IServices tripServices;
    public RPCConcurrentServer(int port, IServices tripServices) {
        super(port);
        this.tripServices = tripServices;
        System.out.println("RPCConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRPCWorker worker = new ClientRPCWorker(tripServices, client);
        Thread tw = new Thread(worker);
        return tw;

    }
}
