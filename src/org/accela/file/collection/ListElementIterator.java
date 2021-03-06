package org.accela.file.collection;

import java.io.IOException;

import org.accela.file.common.DataFormatException;
import org.accela.file.common.ElementIterator;

public interface ListElementIterator<T> extends ElementIterator<T>
{
	public boolean hasPrev() throws IOException;

	public T prev() throws IOException, DataFormatException;

	public void add(T element) throws IOException;

	public void remove() throws IOException;

	public void set(T element) throws IOException;

	public long nextIndex();

	public long prevIndex();

	public ListIteratorMove getLastMove();
	
	public T getLast() throws IOException, DataFormatException;
}
