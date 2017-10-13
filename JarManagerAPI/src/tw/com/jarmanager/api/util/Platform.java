package tw.com.jarmanager.api.util;

public enum Platform {
	Linux, Windows, OS_X, Solaris, FreeBSD;

	public static Platform detect() {
		String osName = System.getProperty("os.name");
		if (osName.equals("Linux"))
			return Linux;
		if (osName.startsWith("Windows", 0))
			return Windows;
		if (osName.equals("Mac OS X"))
			return OS_X;
		if (osName.contains("SunOS"))
			return Solaris;
		if (osName.equals("FreeBSD"))
			return FreeBSD;
		throw new IllegalArgumentException("Could not detect Platform: os.name=" + osName);
	}

	public boolean isUnixLike() {
		return this != Windows;
	}
}
