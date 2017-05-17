package rz.thesis.core.modules.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import rz.thesis.core.Core;


public class DiscoveryModuleThread extends Thread {

	public interface Callbacks {
		void onClientConnected(InetAddress address, int port);

		void onStart();

		void onStop();

		void onProgress(String message);
	}

	private final Core core;
	private final int discoveryPort;
	private final String discoveryMessage;

	private boolean stopThread = false;
	private DatagramSocket socket;
	private final Callbacks mCallbacks;

	public DiscoveryModuleThread(int discoveryPort, String discoveryMessage, Callbacks mCallbacks, Core core) {
		super("DiscoveryServerThread");
		this.discoveryMessage = discoveryMessage;
		this.mCallbacks = mCallbacks;
		this.core = core;
		this.discoveryPort = discoveryPort;
	}

	@Override
	public void run() {
		mCallbacks.onStart();
		mCallbacks.onProgress("Starting discovery server");
		try {
			socket = new DatagramSocket(discoveryPort, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			mCallbacks.onProgress("Discovery server started");
		} catch (SocketException e) {
			mCallbacks.onProgress("Error while starting server : " + e.getMessage());
		} catch (UnknownHostException e) {
			mCallbacks.onProgress("Error while starting server : " + e.getMessage());
		}
		while (!stopThread) {
			mCallbacks.onProgress("Waiting for a client broadcast message");
			byte[] recvBuf = new byte[1500];
			DatagramPacket dp = new DatagramPacket(recvBuf, recvBuf.length);
			try {
				socket.receive(dp);
			} catch (IOException e) {
				mCallbacks.onProgress("Receive thread stopped : " + e.getMessage());
				break;
			}
			mCallbacks.onProgress("Received a client broadcast message from " + dp.getAddress().getHostAddress() + ":"
					+ dp.getPort());
			String message = new String(dp.getData()).trim();
			if (discoveryMessage.equals(message)) {
				mCallbacks.onProgress("Message is a valid broadcast message, generating services list");
				DiscoveryServicesDefinitions sd = new DiscoveryServicesDefinitions(core.getServiceDefinitions());
				String servicesJson = new Gson().toJson(sd);
				byte[] servicesJsonBuffer = servicesJson.getBytes();
				DatagramPacket dpResponse = new DatagramPacket(servicesJsonBuffer, servicesJson.length(),
						dp.getAddress(), dp.getPort());
				mCallbacks.onProgress("Sending services definitions list");
				try {
					socket.send(dpResponse);
				} catch (IOException e) {
					mCallbacks.onProgress("Error while sending response " + e.getMessage());
				}
				mCallbacks.onProgress("Services definitions list sent");
				mCallbacks.onClientConnected(dp.getAddress(), dp.getPort());

			}

		}
		socket.close();
		socket = null;
		mCallbacks.onProgress("Discovery server stopped");
		mCallbacks.onStop();
	}

	public void startServer() {
		this.start();
	}

	public void stopServer() {
		mCallbacks.onProgress("Stopping discovery server");
		stopThread = true;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}

	}
}
