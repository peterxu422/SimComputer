/**
 * @author Peter Xu peterxu422@gmail.com
 */
public class STValues {
	
	private String type, kind;
	private int index;
	
	public STValues(String t, String k, int i) {
		type = t;
		kind = k;
		index = i;
	}
	
	public String getType() 		{return type;}
	public String getKind()			{return kind;}
	public int getIndex()			{return index;}
}
