package ui;

import javax.swing.UIManager;

/*The class contains the application main entry point.*/
public class Main {

	/**
	 * Main entry point method for the checkers application.
	 * args command line arguments
	 */
	public static void main(String[] args) {
		
		// Set native operating system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Instantiate and display main checkers window
		CheckersWindow window = new CheckersWindow();
		window.setDefaultCloseOperation(CheckersWindow.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
