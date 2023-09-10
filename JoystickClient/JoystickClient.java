import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.Robot;
import java.time.LocalTime;

class JoystickClient {

	private String address = "http://localhost:8080";
	private String[] arr;
	private Robot robot;
	private double angle = 0.0, dx = 2.0;
	private boolean stop = false, run = false;
	private boolean ps = false, pr = false;
	private Map<String, Integer> keys = new HashMap<>();
	private int[] numbers = new int[] {65, 87, 83, 68};
	private String[] names = new String[] {"a", "w", "s", "d"};

	public static void main(String[] args) {
		JoystickClient joystick = new JoystickClient();
		joystick.fillKeys();
		joystick.start();
		joystick.startClient();
	}

	public void start() {
		try {
			System.out.println("Enter the last number of server address:");
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			int num = Integer.valueOf(r.readLine());
			address = "http://192.168.0.10" + num + ":8080";
			System.out.println("Server address: " + address);
			robot = new Robot();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	public void startClient() {
		try {
			System.out.println("Begin...");
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			InputStream stream = conn.getInputStream();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(reader);
			String line;
			System.out.println("line: " + br.readLine());
			control();
			weels();
			while ((line = br.readLine()) != null) {
				System.out.println(LocalTime.now());
				arr = line.split(" ");
				angle = Double.valueOf(arr[0]);
				stop = arr[1].equals("1") ? true : false;
				run = arr[2].equals("1") ? true : false;
			}
		} catch (Exception e) {
			System.out.println("Server error: " + e);
		}
	}

	private void control() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if (run && !pr) {
						robot.keyPress(keys.get("w"));
						pr = true;
					} else if (!run && pr) {
						robot.keyRelease(keys.get("w"));
						pr = false;
					}
					if (stop && !ps) {
						robot.keyPress(keys.get("s"));
						ps = true;
					} else if (!stop && ps) {
						robot.keyRelease(keys.get("s"));
						ps = false;
					}
				} catch (Exception e) {}
			}
		}, 1, 10);
	}

	private void weels() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					long a = (long) Math.ceil(40*Math.abs(angle));
					if (angle < -dx) {
						robot.keyPress(keys.get("a"));
						Thread.sleep(a);
						robot.keyRelease(keys.get("a"));
					}
					if (angle > dx) {
						robot.keyPress(keys.get("d"));
						Thread.sleep(a);
						robot.keyRelease(keys.get("d"));
					}
				} catch (Exception e) {}
			}
		}, 1, 420);
	}

	public void fillKeys() {
		for (int i = 0; i < names.length; i++) keys.put(names[i], numbers[i]);
	}
}
