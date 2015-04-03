package ca.ualberta.cs.C291BerkeleyDB.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoicesMenu {

	private String title;
	private Map<Integer, String> menuItems;
	
	public ChoicesMenu() {
		title = null;
		menuItems = new HashMap<Integer, String>();
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void addChoice(Integer number, String label) {
		menuItems.put(number, label);
	}
	
	public void removeChoice(Integer number) {
		menuItems.remove(number);
	}
	
	private void printTitle() {
		if (title == null) return;
		System.out.println(title);
		System.out.println(new String(new char[title.length()]).replace("\0", "="));
	}
	
	private void printItems() {
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(menuItems.keySet());
		Collections.sort(keys);
		
		for (Integer key: keys) {
			System.out.printf("%d) %s\n", key, menuItems.get(key));
		}
	}
	
	private int getInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer inputnumber;
		while (true) {
			try {
				String input = br.readLine();
				inputnumber = Integer.parseInt(input);
				if (menuItems.keySet().contains(inputnumber)) {
					return inputnumber;
				} else {
					System.err.println("Invalid choice. Please try again.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.err.println("Invalid entry. Please try again.");
			}
		}
	}
	
	public int prompt() {
		printTitle();
		printItems();
		return getInput();
	}
}
