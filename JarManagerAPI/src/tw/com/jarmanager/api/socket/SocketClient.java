package tw.com.jarmanager.api.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tw.com.jarmanager.api.factory.RabbitFactory;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.RequestVO;


/*************************
 * 
 * 用來與SocketServer溝通的class
 * 
 * ***********************/
public class SocketClient {
	private static final Logger logger = LogManager.getLogger(SocketClient.class);

	static String host = "";
	static int port = 0;

	public SocketClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws IOException {

		Socket socket = null;

		try {
			socket = new Socket(host, port);
			DataInputStream input = null;
			DataOutputStream output = null;

			try {
				input = new DataInputStream(socket.getInputStream());

				output = new DataOutputStream(socket.getOutputStream());

				output.writeUTF("getJarProjectVOList");

				String json = input.readUTF();
				if (json != null) {

					Gson gson = new Gson();

					logger.debug(json);

					Type listType = new TypeToken<List<JarProjectVO>>() {
					}.getType();

					List<JarProjectVO> jarProjectVOList = gson.fromJson(json, listType);
				}

			} catch (IOException e) {
				logger.debug(e.getMessage());

			} finally {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}

		}
	}

	
	/***********************************
	 * 用來獲得存放在SocketServer上的JarProjectVOList
	 * 
	 * 也就是執行的jar的狀態
	 * *********************************/
	public List<JarProjectVO> getJarProjectVOList() throws IOException {
		Socket socket = null;
		List<JarProjectVO> jarProjectVOList = null;
		Gson gson = new Gson();

		try {
			socket = new Socket(host, port);
			DataInputStream input = null;
			DataOutputStream output = null;

			try {
				input = new DataInputStream(socket.getInputStream());

				output = new DataOutputStream(socket.getOutputStream());

				RequestVO requestVO = new RequestVO();

				requestVO.setAction("getJarProjectVOList");

				String requestStr = gson.toJson(requestVO, RequestVO.class);

				output.writeUTF(requestStr);

				String json = input.readUTF();
				if (json != null) {

					System.out.println("getJarProjectVOList:" + json);

					Type listType = new TypeToken<List<JarProjectVO>>() {
					}.getType();

					jarProjectVOList = gson.fromJson(json, listType);
				}

			} catch (IOException e) {
				e.printStackTrace();
				logger.debug(e.getMessage());

			} finally {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			}
		} catch (ConnectException e) {
			logger.debug("not Connect");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}

		}
		return jarProjectVOList;
	}
	
	/**************************************
	 * 用來發送更改的id和再更改時的JarManagerAPIXMLVO
	 * 
	 * 用於新增 刪除 修改 JarManagerAPIXMLVO所呼叫
	 * ************************************/
	public boolean sendChangeBeatID(List<String> ids,JarManagerAPIXMLVO jarManagerAPIXMLVO) throws IOException {
		Socket socket = null;
		boolean isSucess = false;
		Gson gson = new Gson();

		try {
			socket = new Socket(host, port);
			DataInputStream input = null;
			DataOutputStream output = null;
			

			try {
				input = new DataInputStream(socket.getInputStream());

				output = new DataOutputStream(socket.getOutputStream());

				RequestVO requestVO = new RequestVO();
				requestVO.setIds(ids);
				requestVO.setJarManagerAPIXMLVO(jarManagerAPIXMLVO);
				requestVO.setAction("sendChangeBeatID");

				String requestStr = gson.toJson(requestVO, RequestVO.class);

				output.writeUTF(requestStr);

				String res = input.readUTF();
				if (res != null && "Sucess".equals(res)) {
					isSucess = true;
				}

			} catch (ConnectException e) {
				logger.debug("not Connect");
			}  catch (IOException e) {
				e.printStackTrace();
				logger.debug(e.getMessage());

			} finally {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}

		}
		return isSucess;
	}
	
	/**************************************
	 * 用來發送更改的id和再更改時的JarManagerAPIXMLVO
	 * 
	 * 利用多載來實現不同接口但相同功能
	 * 
	 * 用於新增 刪除 修改 JarManagerAPIXMLVO所呼叫
	 * ************************************/
	public boolean sendChangeBeatID(JarProjectVO jarProjectVO,JarManagerAPIXMLVO jarManagerAPIXMLVO) throws IOException {
		Socket socket = null;
		boolean isSucess = false;
		Gson gson = new Gson();

		try {
			socket = new Socket(host, port);
			DataInputStream input = null;
			DataOutputStream output = null;
			List<String> ids = new ArrayList<String>();
			ids.add(jarProjectVO.getBeatID());

			try {
				input = new DataInputStream(socket.getInputStream());

				output = new DataOutputStream(socket.getOutputStream());

				RequestVO requestVO = new RequestVO();
				requestVO.setIds(ids);
				requestVO.setJarManagerAPIXMLVO(jarManagerAPIXMLVO);
				
				requestVO.setAction("sendChangeBeatID");

				String requestStr = gson.toJson(requestVO, RequestVO.class);

				output.writeUTF(requestStr);

				String res = input.readUTF();
				if (res != null && "Sucess".equals(res)) {
					isSucess = true;
				}

			} catch (IOException e) {
				e.printStackTrace();
				logger.debug(e.getMessage());

			} finally {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			}
			
			
		} catch (ConnectException e) {
			logger.debug("not Connect");
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}

		}
		return isSucess;
	}

	
}