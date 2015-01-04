package org.accela.file.test;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import junit.framework.TestCase;

import org.accela.file.FFactory;
import org.accela.file.FList;
import org.accela.file.FListIterator;
import org.accela.file.common.BytePersistanceDelegate;
import org.accela.file.common.DataFormatException;
import org.accela.file.common.PersistanceDelegate;

public abstract class TestFList extends TestCase
{
	private Random rand = new Random();

	private File testFile = new File("testFList.txt");

	private FList<String> list = null;

	protected static final StringPersistanceDelegate delegate = new StringPersistanceDelegate();

	private static class StringPersistanceDelegate implements
			PersistanceDelegate<String>
	{
		@Override
		public String read(DataInput in) throws IOException
		{
			return in.readUTF();
		}

		@Override
		public void write(DataOutput out, String object) throws IOException
		{
			out.writeUTF(object);
		}
	}

	@Override
	public void setUp() throws IOException
	{
		close();

		testFile.delete();
		if (testFile.exists())
		{
			throw new IOException("can't remove testFile");
		}

		reopen(false);
	}

	@Override
	public void tearDown()
	{
		close();
	}

	private void open(boolean restore)
	{
		if (restore && list != null)
		{
			BytePersistanceDelegate<FList<String>> bd = new BytePersistanceDelegate<FList<String>>(
					FFactory.getInstance(delegate));
			byte[] bytes = bd.writeBytes(list);
			bd = new BytePersistanceDelegate<FList<String>>(FFactory
					.getInstance(delegate));
			try
			{
				this.list = bd.readBytes(bytes);
			}
			catch (DataFormatException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}
		else
		{
			list = createFList(testFile);
		}
	}

	protected abstract FList<String> createFList(File file);

	private void close()
	{
		if (list != null)
		{
			list.close();
		}
	}

	private void reopen(boolean restore)
	{
		close();
		open(restore);
	}

	public void testSimple()
	{
		List<String> elements = new LinkedList<String>();

		// test list methods
		list.add("hello, my name is flist");
		elements.add("hello, my name is flist");
		consistency(elements);
		reopen(true);
		consistency(elements);

		for (int i = 0; i < 100; i++)
		{
			long idx = rand.nextInt((int) list.size());
			list.insert(idx, "hello world: " + i);
			list.add(null);

			elements.add((int) idx, "hello world: " + i);
			elements.add(null);
		}
		
		consistency(elements);
		reopen(true);
		consistency(elements);

		for (int i = 0; i < 20; i++)
		{
			long idx = rand.nextInt((int) list.size());
			list.remove(idx);

			elements.remove((int) idx);
		}
		consistency(elements);
		reopen(true);
		consistency(elements);

		for (int i = 0; i < 20; i++)
		{
			long idx = rand.nextInt((int) list.size());
			boolean ret = list.remove(list.get(idx));
			assert (ret);

			ret = elements.remove(elements.get((int) idx));
			assert (ret);
		}
		consistency(elements);
		reopen(true);
		consistency(elements);

		// test iterator
		FListIterator<String> itr = list.iterator(list.size() / 2);
		ListIterator<String> eItr = elements.listIterator(elements.size() / 2);
		while (itr.hasNext())
		{
			String str = itr.next();
			String eStr = eItr.next();
			assert (checkEqual(str, eStr));

			double rnd = rand.nextDouble();
			if (rnd < 0.33)
			{
				itr.add("nice world: " + itr.prevIndex());
				eItr.add("nice world: " + eItr.previousIndex());
			}
			else if (rnd < 0.66)
			{
				itr.set("big world: " + itr.nextIndex());
				eItr.set("big world: " + eItr.nextIndex());
			}
			else
			{
				itr.remove();
				eItr.remove();
			}
		}
		consistency(elements);
		reopen(true);
		consistency(elements);
		assert (elements.size() > 0);

		// test reopen(false)
		reopen(false);
		assert (list.size() == 0);
		elements = new LinkedList<String>();
		consistency(elements);

		// test clear
		list.add("hello, my name is flist");
		elements.add("hello, my name is flist");
		consistency(elements);
		reopen(true);
		consistency(elements);
		
		for (int i = 0; i < 100; i++)
		{
			long idx = rand.nextInt((int) list.size());
			list.insert(idx, "hello world: " + i);
			list.add(null);

			elements.add((int) idx, "hello world: " + i);
			elements.add(null);
		}
		consistency(elements);
		reopen(true);
		consistency(elements);

		list.clear();
		assert (list.size() == 0);
		elements = new LinkedList<String>();
		consistency(elements);
	}

	private void consistency(List<String> elements)
	{
		assert (list.size() == elements.size());

		for (int i = 0; i < list.size(); i++)
		{
			assert (checkEqual(list.get(i), elements.get(i)));
		}
		int idx = 0;
		
		for (String s : list)
		{
			assert (checkEqual(s, elements.get(idx)));
			idx++;
		}
		assert (idx == elements.size());

		for (int i = 0; i < elements.size(); i++)
		{
			assert (list.contains(elements.get(i)));
		}

		FListIterator<String> itr = list.iterator(list.size());
		idx = (int) list.size() - 1;
		while (itr.hasPrev())
		{
			long prevIdx = itr.prevIndex();
			assert (checkEqual(itr.prev(), elements.get((int) prevIdx)));
			assert (prevIdx == idx);
			idx--;
		}

		assert (itr.prevIndex() == -1);
		assert (-1 == idx);

		if (elements.size() > 0)
		{
			assert (list.indexOf(elements.get(0)) == 0);
			assert (list.lastIndexOf(elements.get(elements.size() - 1)) == elements
					.size() - 1);
		}
	}

	private boolean checkEqual(String s1, String s2)
	{
		if (s1 != null)
		{
			return s1.equals(s2);
		}
		else
		{
			return null == s2;
		}
	}

}
