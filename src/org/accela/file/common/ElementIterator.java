package org.accela.file.common;

import java.io.IOException;


public interface ElementIterator<T>
{
	public boolean hasNext() throws IOException;
	
	public T next() throws IOException, DataFormatException;
}
