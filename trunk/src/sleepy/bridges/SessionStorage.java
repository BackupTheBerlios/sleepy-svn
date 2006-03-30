
package sleepy.bridges;

public interface SessionStorage
{
	public Object putSession( Object key, Object data );
	public Object getSession( Object key );
	public Object removeSession( Object key );
	public boolean hasSession( Object key );
	
}
