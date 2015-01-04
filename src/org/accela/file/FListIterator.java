package org.accela.file;

public interface FListIterator<T> extends java.util.Iterator<T>
{
	public boolean hasNext() throws FIOException;

	public T next() throws FIOException, FDataFormatException;

	public boolean hasPrev() throws FIOException;

	public T prev() throws FIOException, FDataFormatException;

	public void add(T element) throws FIOException;

	public void remove() throws FIOException;

	public void set(T element) throws FIOException;

	public long nextIndex();

	public long prevIndex();
}
