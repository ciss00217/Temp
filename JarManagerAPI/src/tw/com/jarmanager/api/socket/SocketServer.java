package tw.com.jarmanager.api.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.ManagerVO;
import tw.com.jarmanager.api.vo.ResponseVO;

public class SocketServer extends Thread {
	private int port;
	private List<JarProjectVO> jarProjectVOList;

	public SocketServer(int port) {
		this.port = port;
	}

	
	public List<JarProjectVO> getJarProjectVOList() {
		return jarProjectVOList;
	}


	public void setJarProjectVOList(List<JarProjectVO> jarProjectVOList) {
		this.jarProjectVOList = jarProjectVOList;
	}


	@Override
	public void run() {

		ServerSocket serverSocket = null;
		ExecutorService threadExecutor = Executors.newCachedThreadPool();
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server listening requests...");
			while (true) {
				Socket socket = serverSocket.accept();
				threadExecutor.execute(new RequestThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (threadExecutor != null)
				threadExecutor.shutdown();
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	
		
	}

	public void listenRequest() {
		ServerSocket serverSocket = null;
		ExecutorService threadExecutor = Executors.newCachedThreadPool();
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server listening requests...");
			while (true) {
				Socket socket = serverSocket.accept();
				threadExecutor.execute(new RequestThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (threadExecutor != null)
				threadExecutor.shutdown();
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SocketServer server = new SocketServer(9527);
		server.start();
//		ManagerVO managerVO = new ManagerVO();
//		managerVO.setFolderPath("test");
//		server.setManagerVO(managerVO);
	}

	/**
	 * 處理Client端的Request執行續。
	 *
	 * @version
	 */
	class RequestThread implements Runnable {
		private Socket clientSocket;

		public RequestThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			System.out.printf("有%s連線進來!\n", clientSocket.getRemoteSocketAddress());
			DataInputStream input = null;
			DataOutputStream output = null;

			try {
				input = new DataInputStream(this.clientSocket.getInputStream());
				output = new DataOutputStream(this.clientSocket.getOutputStream());
				Gson gson = new Gson();
				while (true) {
					String action = input.readUTF();
					System.out.println("action:" + action);
					if ("getJarProjectVOList".equals(action)) {
						List<JarProjectVO> jarProjectVOList  = getJarProjectVOList();
						if(jarProjectVOList!=null){
							String json = gson.toJson(jarProjectVOList);
							System.out.println("jarProjectVOList.json :" + json);
							output.writeUTF(json);
						}else{
							System.out.println("jarProjectVOList is null");
						}
			
					}

					output.flush();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (input != null)
						input.close();
					if (output != null)
						output.close();
					if (this.clientSocket != null && !this.clientSocket.isClosed())
						this.clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}