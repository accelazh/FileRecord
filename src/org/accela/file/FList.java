package org.accela.file;

public interface FList<T> extends java.lang.Iterable<T>
{
	public void close() throws FIOException;

	public void flush() throws FIOException;

	public long size();

	public void clear() throws FIOException;

	public void insert(long idx, T element) throws FIOException;

	public void remove(long idx) throws FIOException;

	public void set(long idx, T element) throws FIOException;

	public T get(long idx) throws FIOException, FDataFormatException;

	public long indexOf(long idx, T element) throws FIOException;

	public FListIterator<T> iterator(long idx) throws FIOException;

	@Override
	public FListIterator<T> iterator() throws FIOException;

	public void add(T element) throws FIOException;

	public boolean contains(T element) throws FIOException;

	public boolean remove(T element) throws FIOException;

	public boolean isEmpty();

	public long indexOf(T element) throws FIOException;

	public long lastIndexOf(T element) throws FIOException;
}
