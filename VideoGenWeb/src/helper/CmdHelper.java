package helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

// Tools to execute commands (runtime).
public class CmdHelper {
	/**
	 * Execute a command and get the output from runtime.
	 * 
	 * @param cmd : Command
	 * @return Runtime output
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("resource")
	public static String execCmdAndGetOutput(String cmd) throws java.io.IOException {
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream())
				.useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * Execute a command and wait process end.
	 * 
	 * @param command
	 * @return true if success, false if fail
	 */
	public static boolean execCmd(String command) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
			//TimeUnit.SECONDS.sleep(30);
			InputStream stream = p.getInputStream();
			java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
			while (s.hasNext()) {
				System.out.println(s.next());
			}
			int exitValue = p.waitFor();
			System.out.println("Process exit value: " + 0);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	/**
	 * Execute a command and wait process end, read stream.
	 * 
	 * @param command
	 * @return true if success, false if fail
	 */
	public static boolean execCmdVerbose(String command) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
			InputStream is = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			int exitValue = p.waitFor();
			System.out.println("Process exit value: " + exitValue);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
