package tw.com.jarmanager.api.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.lang.model.SourceVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import tw.com.jarmanager.api.socket.Test;

public class ProcessUtial {
	private static final Logger logger = LogManager.getLogger(Test.class);

	private Long windowsProcessId(Process process) {
		if (process.getClass().getName().equals("java.lang.Win32Process")
				|| process.getClass().getName().equals("java.lang.ProcessImpl")) {
			/* determine the pid on windows plattforms */
			try {
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				long handl = f.getLong(process);

				Kernel32 kernel = Kernel32.INSTANCE;
				WinNT.HANDLE handle = new WinNT.HANDLE();
				handle.setPointer(Pointer.createConstant(handl));
				int ret = kernel.GetProcessId(handle);
				logger.debug("Detected pid: {}", ret);
				return Long.valueOf(ret);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Long unixLikeProcessId(Process process) {
		Class<?> clazz = process.getClass();
		try {
			if (clazz.getName().equals("java.lang.UNIXProcess")) {
				Field pidField = clazz.getDeclaredField("pid");
				pidField.setAccessible(true);
				Object value = pidField.get(process);
				if (value instanceof Integer) {
					logger.debug("Detected pid: {}", value);
					return ((Integer) value).longValue();
				}
			}
		} catch (SecurityException sx) {
			sx.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void destoryProcess(Long pid) throws IOException {

		if (pid != null) {
			Platform platform = Platform.detect();

			if (platform.isUnixLike()) {
				Runtime.getRuntime().exec("kill -9 " + pid.toString());
			} else {
				Runtime.getRuntime().exec("taskkill /F /PID " + pid.toString());
			}
		}

	}

	public Long getProcessID(Process process) {
		Long pid = null;
		Platform platform = Platform.detect();

		// JDK 9
		if (SourceVersion.latest().toString().equals("RELEASE_9")) {
			try {
				Method getPid = Process.class.getMethod("getPid");
				pid = (Long) getPid.invoke(process);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		} else {
			if (platform.isUnixLike()) {
				pid = unixLikeProcessId(process);
			} else {
				pid = windowsProcessId(process);
			}
		}
		return pid;

	}

	public static void main(String[] args) throws IOException {
		// Processes processes=new Processes ();
		// TODO Auto-generated method stub
		// Runtime rt = Runtime.getRuntime();
		// Process process = rt.exec("java -jar D:\\JarManagerAPI.jar
		// -startManager D:\\jarTest\\JarManagerAPI.xml");
		//
		// Long Long = windowsProcessId(process);
		//
		// System.out.println("Long:" + Long);
	}

}
