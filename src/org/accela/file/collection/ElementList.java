package org.accela.file.collection;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import org.accela.file.common.Clearable;
import org.accela.file.common.DataFormatException;
import org.accela.file.common.Sizable;

public interface ElementList<T> extends Closeable, Flushable, Sizable, Clearable
{
	public void insert(long idx, T element) throws IOException;

	public void remove(long idx) throws IOException;

	public void set(long idx, T element) throws IOException;

	public T get(long idx) throws IOException, DataFormatException;

	public long indexOf(long idx, T element) throws IOException;

	public ListElementIterator<T> iterator(long idx) throws IOException;
}
