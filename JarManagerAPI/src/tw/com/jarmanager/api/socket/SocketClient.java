package tw.com.jarmanager.api.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tw.com.jarmanager.api.vo.JarProjectVO;

public class SocketClient {
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

					System.out.println(json);

					Type listType = new TypeToken<List<JarProjectVO>>() {
					}.getType();

					List<JarProjectVO> jarProjectVOList = gson.fromJson(json, listType);
				}

			} catch (IOException e) {
				System.out.println(e.getMessage());

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

	public List<JarProjectVO> getJarProjectVOList() throws IOException {
		// String host = "127.0.0.1";
		// int port = 9527;
		Socket socket = null;
		List<JarProjectVO> jarProjectVOList = null;

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

					System.out.println("getJarProjectVOList:"+json);

					Type listType = new TypeToken<List<JarProjectVO>>() {
					}.getType();

					jarProjectVOList = gson.fromJson(json, listType);

				}

			} catch (IOException e) {
				System.out.println(e.getMessage());

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
		return jarProjectVOList;
	}
}