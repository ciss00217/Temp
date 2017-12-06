package tw.com.jarmanager.api.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import com.google.gson.Gson;

import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.service.JarManagerAPIServiceMethod;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.RequestVO;

public class SocketServer extends Thread {
	private static final Logger logger = LogManager.getLogger(SocketServer.class);

	private int port;
	private List<JarProjectVO> jarProjectVOList;
	private HashSet<String> changeIdSet;
	private boolean isApiXMLChange;
	private JarManagerAPIXMLVO jarManagerAPIXMLVO;
	
	public JarManagerAPIXMLVO getJarManagerAPIXMLVO() {
		return jarManagerAPIXMLVO;
	}

	public void setJarManagerAPIXMLVO(JarManagerAPIXMLVO jarManagerAPIXMLVO) {
		this.jarManagerAPIXMLVO = jarManagerAPIXMLVO;
	}

	public HashSet<String> getChangeIdSet() {
		return changeIdSet;
	}

	public void setChangeIdSet(HashSet<String> changeIdSet) {
		this.changeIdSet = changeIdSet;
	}

	public boolean isApiXMLChange() {
		return isApiXMLChange;
	}

	public void setApiXMLChange(boolean isApiXMLChange) {
		this.isApiXMLChange = isApiXMLChange;
	}

	public SocketServer(int port) {
		this.port = port;
		this.changeIdSet=new HashSet<String>();
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
			logger.debug("Server listening requests...");
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
			logger.debug("Server listening requests...");
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
		// ManagerVO managerVO = new ManagerVO();
		// managerVO.setFolderPath("test");
		// server.setManagerVO(managerVO);
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
					String socketRequestStr = input.readUTF();

					logger.debug("socketRequestStr:" + socketRequestStr);

					if (socketRequestStr != null && socketRequestStr.length() > 0) {

						RequestVO responseVO = gson.fromJson(socketRequestStr, RequestVO.class);
						String action = responseVO.getAction();
						if ("getJarProjectVOList".equals(action)) {

							// 正在執行的
							List<JarProjectVO> jarProjectVOList = getJarProjectVOList();
							// 關閉的
							List<JarProjectVO> noOpen = JarManagerAPIService
									.getXMLJarProjectVOList(JarManagerAPIService.xmlFile);

							for (JarProjectVO item : jarProjectVOList) {
								noOpen.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID()
										.equals(item.getBeatID()));
							}

							jarProjectVOList.addAll(noOpen);

							if (jarProjectVOList != null) {
								String json = gson.toJson(jarProjectVOList);
								//System.out.println("jarProjectVOList.json :" + json);
								output.writeUTF(json);
							} else {
								logger.debug("jarProjectVOList is null");
							}

						} else if ("sendChangeBeatID".equals(action)) {
							List<String> ids = responseVO.getIds();
							jarManagerAPIXMLVO = responseVO.getJarManagerAPIXMLVO();

							for (String str : ids) {
								changeIdSet.add(str);
							}
							setApiXMLChange(true);

							output.writeUTF("Success");

						}

						output.flush();
						break;
					}
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