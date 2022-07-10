package ie.atu.sw;
import java.util.Scanner;

public class Menu {
	public ConsoleColour introColor;
	public ConsoleColour menuTextColor;
	public ConsoleColour menuValueColor;
	public Menu(ConsoleColour introColor, ConsoleColour menuTextColor) {
		this.introColor = introColor;
		this.menuTextColor = menuTextColor;
	}
	public Menu() {
		introColor = ConsoleColour.WHITE;
		menuTextColor = ConsoleColour.GREEN;
		menuValueColor = ConsoleColour.PURPLE;
	}
	public void printIntro() {
		System.out.println(ConsoleColour.WHITE);
		System.out.println("************************************************************");
		System.out.println("*      ATU - Dept. Computer Science & Applied Physics     *");
		System.out.println("*                                                          *");
		System.out.println("*                  N-Gram Frequency Builder                *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
	}
	
	public ConsoleMenuOption printMainMenu(ConsoleMenuItem[] menuItems) {
		ConsoleMenuOption result = ConsoleMenuOption.INVALID_ENTRY;
		System.out.println(menuTextColor);
		for(int i=0;i<menuItems.length;i++) {
			ConsoleMenuItem menuItem = menuItems[i];
			
			if(menuItem.Value != null) {
				System.out.print("(" + menuItem.Order + ") " + menuItem.Text);
				System.out.print(menuValueColor);
				System.out.print(" [" + menuItem.Value + "]");
				System.out.println(menuTextColor);
			} else {
				System.out.println("(" + menuItem.Order + ") " + menuItem.Text);
			}
		}
		//Output a menu of options and solicit text from the user
		System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		System.out.println("Select Option [1-" + (menuItems.length) + "]>");
		System.out.println();
		
		Scanner s = new Scanner(System.in);
		String str = s.nextLine();
		result = ConsoleMenuOption.fromInteger(str);
		return result;
	}

}
