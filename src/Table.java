import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Table {
	private List<String> cols = null;
	private List<List<String>> data = null;
	private List<Integer> maxColLen = null;
	
	public Table(List<String> columns) {
		if (columns == null || columns.size() == 0)
			throw new IllegalArgumentException("Invalid column list");
		cols = columns;
		data = new ArrayList<List<String>>();
		maxColLen = new ArrayList<Integer>();
		
		for (String label: cols) {
			maxColLen.add(label.length());
		}
		if (maxColLen.size() != cols.size()) throw new IllegalStateException("WTF");
	}
	
	public void insert(String data, String ... ndata) {
		if (ndata.length + 1 != cols.size())
			throw new IllegalArgumentException("Number of columns does not match table definition");
		
		List<String> row = new ArrayList<String>();
		
		row.add(data);
		if (data.length() > maxColLen.get(0)) maxColLen.set(0, data.length());
		for (int i = 1; i <= ndata.length; i++) {
			row.add(ndata[i]);
			if (ndata[i].length() > maxColLen.get(i)) maxColLen.set(i, ndata[i].length());
		}
		this.data.add(row);
	}
	
	public void insert(List<String> data) {
		if (data.size() != cols.size())
			throw new IllegalArgumentException("Number of columns does not match table definition");
		
		List<String> row = data;
		
		for (int i = 0; i < data.size(); i++) {
			int len = maxColLen.get(i);
			if (data.get(i) != null && data.get(i).length() > len) maxColLen.set(i, data.get(i).length());
		}
		this.data.add(row);
	}
	
	public void print() {
		System.out.print("\u250C\u252C");
		for (int w: maxColLen) {
			for (int i = 0; i < w+2; i++) {
				System.out.print("\u2500");
			}
			System.out.print("\u252C");
		}
		System.out.println("\u2510");
		// Print headings
		printRow(cols, 1);
		// Print data
		for (int i = 0; i < data.size(); i++) {
			if (i != data.size() - 1)
				printRow(data.get(i), 0);
			else
				printRow(data.get(i), 2);
		}
	}
	private void printRow(List<String> data, int style) {
		System.out.print("\u2502\u2502");
		for (int i = 0; i < data.size(); i++) {
			System.out.print(String.format(" %"+maxColLen.get(i)+"s ", data.get(i)));
			System.out.print("\u2502");
		}
		System.out.println("\u2502");
		if (style == 0) {
			System.out.print("\u251C\u253C");
			for (int w: maxColLen) {
				for (int i = 0; i < w+2; i++) {
					System.out.print("\u2500");
				}
				System.out.print("\u253C");
			}
			System.out.println("\u2524");
		} else if (style == 1) {
			System.out.print("\u255E\u256A");
			for (int w: maxColLen) {
				for (int i = 0; i < w+2; i++) {
					System.out.print("\u2550");
				}
				System.out.print("\u256A");
			}
			System.out.println("\u2561");
		} else {
			System.out.print("\u2514\u2534");
			for (int w: maxColLen) {
				for (int i = 0; i < w+2; i++) {
					System.out.print("\u2500");
				}
				System.out.print("\u2534");
			}
			System.out.println("\u2518");
		}
	}
	
	public static void printResultSet(ResultSet rs) {
		try {
			ResultSetMetaData md = rs.getMetaData();
			
			List<String> cols = new ArrayList<String>();
			for (int i = 0; i < md.getColumnCount(); i++) {
				cols.add(md.getColumnLabel(i+1));
			}
			Table t = new Table(cols);
			while(rs.next()) {
				List<String> row = new ArrayList<String>();
				for (int i = 0; i < md.getColumnCount(); i++) {
					if (rs.getObject(i+1) == null)
						row.add(null);
					else
						row.add(rs.getObject(i+1).toString());
				}
				t.insert(row);
			}
			
			t.print();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
