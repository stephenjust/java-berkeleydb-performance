
public interface ISearch {
	public abstract void getByKey(String skey);
	public abstract void getByValue(String value);
	public abstract void retrieveRange(String startKey, String endKey);
}
