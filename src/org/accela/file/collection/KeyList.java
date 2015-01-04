package org.accela.file.collection;

import java.io.IOException;

public interface KeyList extends ElementList<Long>
{
	public Long get(long idx) throws IOException;

	public ListKeyIterator iterator(long idx) throws IOException;

}
