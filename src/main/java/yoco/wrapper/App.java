package yoco.wrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start the UI wrapper.
 *
 * @author Brad Chen <brad@bradchen.com>
 */
public class App implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private Display display;
	private Shell primaryShell;
	private Shell secondaryShell;
	private Browser primaryBrowser;
	private Browser secondaryBrowser;

	public void run() {
		try {
			startUI();
		} catch (Throwable th) {
			logger.error("Error occurred.", th);
		}
	}

	private void startUI() {
		display = Display.getDefault();

		// set absolute size
		if (isFullscreen()) {
			Monitor primaryMonitor = display.getPrimaryMonitor();
			Monitor secondaryMonitor = getSecondaryMonitor();

			primaryShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
			Rectangle bounds = primaryMonitor.getBounds();
			primaryShell.setLocation(bounds.x, bounds.y);
			primaryShell.setSize(bounds.width, bounds.height);

			secondaryShell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
			bounds = secondaryMonitor.getBounds();
			secondaryShell.setLocation(bounds.x, bounds.y);
			secondaryShell.setSize(bounds.width, bounds.height);
		} else {
			primaryShell = new Shell();
			primaryShell.setSize(1024, 768);

			secondaryShell = new Shell();
			secondaryShell.setSize(800, 600);
		}

		primaryShell.setLayout(new FillLayout());
		secondaryShell.setLayout(new FillLayout());

		primaryBrowser = new Browser(primaryShell, SWT.NONE);
		secondaryBrowser = new Browser(secondaryShell, SWT.NONE);
		primaryShell.open();
		secondaryShell.open();
		primaryBrowser.setUrl("http://www.google.ca");
		secondaryBrowser.setUrl("http://www.google.ca");

		while (!primaryShell.isDisposed() || !secondaryShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private boolean isFullscreen() {
		return isPrimaryMonitor1024x768() && isSecondaryMonitor800x600();
	}

	private boolean isPrimaryMonitor1024x768() {
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle bounds = monitor.getBounds();
		return (bounds.width == 1024) && (bounds.height == 768);
	}

	private boolean isSecondaryMonitor800x600() {
		Monitor monitor = getSecondaryMonitor();
		if (monitor == null) {
			return false;
		}

		Rectangle bounds = monitor.getBounds();
		return (bounds.width == 800) && (bounds.height == 600);
	}

	private Monitor getSecondaryMonitor() {
		Monitor[] monitors = display.getMonitors();
		if (monitors.length < 2) {
			return null;
		}
		return monitors[1];
	}

	public static void main(String[] args) {
		new Thread(new App()).run();
	}

}
