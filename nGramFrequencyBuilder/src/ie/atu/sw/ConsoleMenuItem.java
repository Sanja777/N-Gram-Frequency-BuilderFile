package ie.atu.sw;

public class ConsoleMenuItem {
	public ConsoleMenuItem(){
		
	}
	public ConsoleMenuItem(int order, String text){
		Order = order;
		Text = text;
	}
	public ConsoleMenuItem(int order, String text, String value){
		Order = order;
		Text = text;
		Value = value;
	}
	public int Order;
	public String Text;
	public String Value;
}
