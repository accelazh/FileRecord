package org.accela.file.collection.impl;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import org.accela.common.Assertion;
import org.accela.file.collection.KeyList;
import org.accela.file.collection.ListIteratorMove;
import org.accela.file.collection.ListKeyIterator;
import org.accela.file.collection.util.Node;
import org.accela.file.collection.util.NodePersistanceDelegate;
import org.accela.file.common.DataFormatException;
import org.accela.file.common.Sizable;
import org.accela.file.record.RecordPool;
import org.accela.file.record.impl.ObjectRecordPool;

//链表的所有的一切都是建立在迭代器的基础上
public class LinkedKeyList implements KeyList
{
	private CoreKeyList list = null;

	public LinkedKeyList(RecordPool accesser, long key) throws IOException
	{
		this.list = new LinkedCoreKeyList(accesser, key);
	}

	public RecordPool getAccesser()
	{
		return this.list.getAccesser();
	}

	public synchronized long getKey()
	{
		return this.list.getKey();
	}

	@Override
	public synchronized void clear() throws IOException
	{
		ListKeyIterator itr = iterator(0);
		while (itr.hasNext())
		{
			itr.next();
			itr.remove();
		}

		assert (list.size() == 0) : Assertion.declare();
	}

	@Override
	public synchronized Long get(long idx) throws IOException
	{
		if (idx < 0 || idx >= list.size())
		{
			throw new IllegalArgumentException("idx out of bound: " + idx);
		}

		ListKeyIterator itr = iterator(idx);
		return itr.next();
	}

	@Override
	public synchronized long indexOf(long idx, Long element) throws IOException
	{
		if (null == element)
		{
			throw new IllegalArgumentException("element should not be null");
		}

		long ret = -1;
		ListKeyIterator itr = iterator(idx);
		while (itr.hasNext())
		{
			long value = itr.next();
			if (value == element)
			{
				ret = itr.prevIndex();
				break;
			}
		}

		assert (-1 == ret || (ret >= idx && get(ret).equals(element))) : Assertion
				.declare();
		return ret;
	}

	@Override
	public synchronized void insert(long idx, Long element) throws IOException
	{
		if (null == element)
		{
			throw new IllegalArgumentException("element should not be null");
		}
		if (element < 0)
		{
			throw new IllegalArgumentException("element should not be negative");
		}

		ListKeyIterator itr = iterator(idx);
		itr.add(element);

		assert (get(idx).equals(element)) : Assertion.declare();
	}

	private ListKeyIterator iterator(boolean head)
			throws IOException
	{
		return new FullCoreListKeyIterator(list.iterator(head), head ? 0 : list
				.size());
	}

	@Override
	public synchronized ListKeyIterator iterator(long idx) throws IOException
	{
		if (idx < 0 || idx > list.size())
		{
			throw new IllegalArgumentException("idx out of bound: " + idx);
		}

		ListKeyIterator itr = null;
		if (idx <= list.size() / 2)
		{
			itr = this.iterator(true);
		}
		else
		{
			itr = this.iterator(false);
		}

		while (itr.nextIndex() > idx && itr.hasPrev())
		{
			itr.prev();
		}
		while (itr.nextIndex() < idx && itr.hasNext())
		{
			itr.next();
		}

		if (itr.nextIndex() != idx)
		{
			throw new IOException("can't move itr to specified idx: "
					+ "itr.nextIndex()="
					+ itr.nextIndex()
					+ ", idx="
					+ idx
					+ ", size="
					+ size());
		}

		return itr;
	}

	@Override
	public synchronized void remove(long idx) throws IOException
	{
		if (idx < 0 || idx >= list.size())
		{
			throw new IllegalArgumentException("idx out of bound: " + idx);
		}

		ListKeyIterator itr = iterator(idx);
		itr.next();
		itr.remove();
	}

	@Override
	public synchronized void set(long idx, Long element) throws IOException
	{
		if (idx < 0 || idx >= list.size())
		{
			throw new IllegalArgumentException("idx out of bound: " + idx);
		}
		if (null == element)
		{
			throw new IllegalArgumentException("element should not be null");
		}
		if (element < 0)
		{
			throw new IllegalArgumentException("element should not be negative");
		}

		ListKeyIterator itr = iterator(idx);
		itr.next();
		itr.set(element);

		assert (get(idx).equals(element));
	}

	@Override
	public synchronized void close() throws IOException
	{
		this.list.close();
	}

	@Override
	public synchronized void flush() throws IOException
	{
		this.list.flush();
	}

	@Override
	public synchronized long size()
	{
		return list.size();
	}

	// =========================================================================
	// 内部类FullCoreListKeyIterator，将CoreListKeyIterator转换成ListKeyIterator
	// =========================================================================
	private static class FullCoreListKeyIterator implements ListKeyIterator
	{
		private CoreListKeyIterator itr = null;

		private long nextIdx = 0;

		public FullCoreListKeyIterator(CoreListKeyIterator itr,
				long initNextIndex)
		{
			if (null == itr)
			{
				throw new IllegalArgumentException("itr should not be null");
			}
			if (initNextIndex < 0)
			{
				throw new IllegalArgumentException(
						"initNextIndex should not be negative");
			}

			this.itr = itr;
			this.nextIdx = initNextIndex;
		}

		@Override
		public long nextIndex()
		{
			return nextIdx;
		}

		@Override
		public long prevIndex()
		{
			return nextIdx - 1;
		}

		@Override
		public void add(Long element) throws IOException
		{
			if (null == element)
			{
				throw new IllegalArgumentException("element should not be null");
			}

			itr.add(element);
			nextIdx++;
		}

		@Override
		public boolean hasNext() throws IOException
		{
			return itr.hasNext();
		}

		@Override
		public boolean hasPrev() throws IOException
		{
			return itr.hasPrev();
		}

		@Override
		public Long next() throws IOException
		{
			Long ret = itr.next();
			nextIdx++;
			return ret;
		}

		@Override
		public Long prev() throws IOException
		{
			Long ret = itr.prev();
			nextIdx--;
			return ret;
		}

		@Override
		public void remove() throws IOException
		{
			ListIteratorMove lastMove = itr.getLastMove();
			itr.remove();
			if (lastMove.equals(ListIteratorMove.next))
			{
				nextIdx--;
			}
		}

		@Override
		public void set(Long element) throws IOException
		{
			if (null == element)
			{
				throw new IllegalArgumentException("element should not be null");
			}

			itr.set(element);
		}

		@Override
		public ListIteratorMove getLastMove()
		{
			return itr.getLastMove();
		}

		@Override
		public Long getLast() throws IOException
		{
			return itr.getLast();
		}

	}

	// =========================================================================
	// 为了简化LinkedKeyList的实现，使用内部类LinkedCoreKeyList作为内部类，分离链表
	// 迭代器的操作
	// =========================================================================

	private static interface CoreKeyList extends Closeable, Flushable, Sizable
	{
		public long getKey();

		public RecordPool getAccesser();

		public CoreListKeyIterator iterator(boolean head) throws IOException;
	}

	private static interface CoreListKeyIterator
	{
		public boolean hasNext() throws IOException;

		public boolean hasPrev() throws IOException;

		public long next() throws IOException;

		public long prev() throws IOException;

		public void add(long element) throws IOException;

		public void remove() throws IOException;

		public void set(long element) throws IOException;

		public ListIteratorMove getLastMove();

		public long getLast() throws IOException;
	}

	private static class LinkedCoreKeyList implements CoreKeyList
	{
		private ObjectRecordPool accesser = null;

		private NodePersistanceDelegate delegate = null;

		private long head = 0;

		private long tail = 0;

		private long size = 0;

		private long modCount = 0;

		public LinkedCoreKeyList(RecordPool accesser, long key)
				throws IOException
		{
			if (null == accesser)
			{
				throw new IllegalArgumentException(
						"accesser should not be null");
			}

			this.delegate = new NodePersistanceDelegate();
			this.accesser = new ObjectRecordPool(accesser);

			init(key);

			this.modCount = 0;
		}

		public RecordPool getAccesser()
		{
			return this.accesser.getAccesser();
		}

		private void init(long key) throws IOException
		{
			repairByHead(key);
		}

		// construct list, and init head, tail and size
		private void repairByHead(long key) throws IOException
		{
			// get head
			Node headNode = tryGet(key);
			if (null == headNode)
			{
				headNode = new Node(0, 0, 0);
				key = put(headNode);
			}
			head = key;

			// find tail
			tail = findLastAndRepairConnectivity(head);
			if (tail == head)
			{
				tail = 0;
			}

			Node tailNode = tryGet(tail);
			if (null == tailNode)
			{
				tailNode = new Node(head, 0, 0);
				tail = put(tailNode);

				headNode.setNext(tail);
				setIfContains(head, headNode);
			}

			// set all not fake
			setAllUnfake(head);

			// repair head
			headNode.setPrev(0);
			headNode.setFake(true);
			setIfContains(head, headNode);

			// repair tail
			tailNode.setNext(0);
			tailNode.setFake(true);
			setIfContains(tail, tailNode);

			// count size
			long headSize = countHeadSize();
			long tailSize = countTailSize();

			if (headSize != tailSize)
			{
				throw new IOException("list data corrupted");
			}

			this.size = headSize;
		}

		private long findLastAndRepairConnectivity(long key) throws IOException
		{
			long curKey = key;
			Node curNode = tryGet(key);
			if (null == curNode)
			{
				return 0;
			}

			Node nextNode = null;
			long count = 0;
			while ((nextNode = tryGet(curNode.getNext())) != null
					&& count < accesser.poolSize())
			{
				nextNode.setPrev(curKey);
				setIfContains(curNode.getNext(), nextNode);

				curKey = curNode.getNext();
				curNode = nextNode;
				count++;
			}

			return curKey;
		}

		private void setAllUnfake(long key) throws IOException
		{
			long curKey = key;
			Node node = null;
			while ((node = tryGet(curKey)) != null)
			{
				node.setFake(false);
				setIfContains(curKey, node);

				curKey = node.getNext();
			}
		}

		private long countHeadSize() throws IOException
		{
			Node headNode = tryGet(head);
			if (null == headNode)
			{
				return 0;
			}

			long count = 0;
			NodeIterator itr = new NodeIterator(head, headNode.getNext());
			while (itr.hasNext())
			{
				count++;
				itr.next();
			}

			return count;
		}

		private long countTailSize() throws IOException
		{
			Node tailNode = tryGet(tail);
			if (null == tailNode)
			{
				return 0;
			}

			long count = 0;
			NodeIterator itr = new NodeIterator(tailNode.getPrev(), tail);
			while (itr.hasPrev())
			{
				count++;
				itr.prev();
			}

			return count;
		}

		private Node unsafeGetIfContains(long key) throws IOException,
				DataFormatException
		{
			return this.accesser.getIfContains(key, delegate);
		}

		private Node tryGet(long key) throws IOException
		{
			try
			{
				return unsafeGetIfContains(key);
			}
			catch (DataFormatException ex)
			{
				return null;
			}
		}

		private boolean setIfContains(long key, Node node) throws IOException
		{
			assert (node != null);
			return accesser.setIfContains(key, node, delegate);
		}

		private long put(Node node) throws IOException
		{
			assert (node != null);
			return accesser.put(node, delegate);
		}

		@Override
		public synchronized long getKey()
		{
			return this.head;
		}

		@Override
		public synchronized CoreListKeyIterator iterator(boolean head)
				throws IOException
		{
			return new SyncListNodeIterator(head);
		}

		@Override
		public synchronized long size()
		{
			assert (size >= 0);
			return this.size;
		}

		@Override
		public synchronized void close() throws IOException
		{
			accesser.close();
		}

		@Override
		public synchronized void flush() throws IOException
		{
			accesser.flush();
		}

		// don't rely on head, tail or size
		// don't do any repair
		// don't iterator over fake nodes
		private class NodeIterator implements CoreListKeyIterator
		{
			private long nextKey = 0;

			private Node nextPreload = null;

			private long prevKey = 0;

			private Node prevPreload = null;

			private ListIteratorMove lastMove = ListIteratorMove.none;

			private Node lastPreload = null;

			private long count = 0;

			public NodeIterator(long prevKey, long nextKey)
			{
				this.nextKey = nextKey;
				this.prevKey = prevKey;

				this.lastMove = ListIteratorMove.none;
				this.count = 0;
				this.nextPreload = null;
				this.prevPreload = null;
				this.lastPreload = null;
			}

			private long lastKey()
			{
				if (lastMove.equals(ListIteratorMove.next))
				{
					return prevKey;
				}
				else if (lastMove.equals(ListIteratorMove.prev))
				{
					return nextKey;
				}
				else if (lastMove.equals(ListIteratorMove.none))
				{
					return 0;
				}
				else
				{
					assert (false) : Assertion.declare();
					return 0;
				}
			}

			private void preloadNext() throws IOException
			{
				if (Math.abs(count) >= accesser.poolSize())
				{
					this.nextPreload = null;
				}

				Node node = tryGet(nextKey);
				if (null == node || node.isFake())
				{
					node = null;
				}

				this.nextPreload = node;
			}

			private void preloadPrev() throws IOException
			{
				if (Math.abs(count) >= accesser.poolSize())
				{
					this.prevPreload = null;
				}

				Node node = tryGet(prevKey);
				if (null == node || node.isFake())
				{
					node = null;
				}

				this.prevPreload = node;
			}

			@Override
			public boolean hasNext() throws IOException
			{
				if (nextPreload != null)
				{
					return true;
				}

				preloadNext();
				return nextPreload != null;
			}

			@Override
			public boolean hasPrev() throws IOException
			{
				if (prevPreload != null)
				{
					return true;
				}

				preloadPrev();
				return prevPreload != null;
			}

			@Override
			public long next() throws IOException
			{
				if (null == nextPreload)
				{
					preloadNext();
				}

				Node node = nextPreload;
				if (null == node)
				{
					throw new NoSuchElementException();
				}

				prevKey = nextKey;
				lastMove = ListIteratorMove.next;
				lastPreload = node;
				nextKey = node.getNext();
				nextPreload = null;
				prevPreload = null;
				count++;

				return node.getElement();
			}

			@Override
			public long prev() throws IOException
			{
				if (null == prevPreload)
				{
					preloadPrev();
				}

				Node node = prevPreload;
				if (null == node)
				{
					throw new NoSuchElementException();
				}

				nextKey = prevKey;
				lastMove = ListIteratorMove.prev;
				lastPreload = node;
				prevKey = node.getPrev();
				prevPreload = null;
				nextPreload = null;
				count--;

				return node.getElement();
			}

			@Override
			public void add(long element) throws IOException
			{
				if (element < 0)
				{
					throw new IllegalArgumentException(
							"element should not be negative");
				}

				Node node = new Node(prevKey, nextKey, element);
				long key = put(node);

				Node prev = tryGet(prevKey);
				if (prev != null)
				{
					prev.setNext(key);
					LinkedCoreKeyList.this.setIfContains(prevKey, prev);
				}

				Node next = tryGet(nextKey);
				if (next != null)
				{
					next.setPrev(key);
					LinkedCoreKeyList.this.setIfContains(nextKey, next);
				}

				prevKey = key;
				lastMove = ListIteratorMove.none;
				lastPreload = null;
				prevPreload = null;
				nextPreload = null;
			}

			@Override
			public void remove() throws IOException
			{
				if (lastMove.equals(ListIteratorMove.none))
				{
					throw new NoSuchElementException();
				}

				final long lastKey = lastKey();
				Node last = lastPreload;
				assert (last != null);

				Node lastPrev = tryGet(last.getPrev());
				if (lastPrev != null)
				{
					lastPrev.setNext(last.getNext());
					LinkedCoreKeyList.this.setIfContains(last.getPrev(),
							lastPrev);
				}

				Node lastNext = tryGet(last.getNext());
				if (lastNext != null)
				{
					lastNext.setPrev(last.getPrev());
					LinkedCoreKeyList.this.setIfContains(last.getNext(),
							lastNext);
				}

				accesser.removeIfContains(lastKey);

				if (lastKey == prevKey)
				{
					prevKey = last.getPrev();
				}
				if (lastKey == nextKey)
				{
					nextKey = last.getNext();
				}
				this.lastMove = ListIteratorMove.none;
				lastPreload = null;
				this.prevPreload = null;
				this.nextPreload = null;
				decrAbsCount();

			}

			private void decrAbsCount()
			{
				if (count > 0)
				{
					count--;
				}
				else if (count < 0)
				{
					count++;
				}
				else
				{
					count = 0;
				}
			}

			@Override
			public void set(long element) throws IOException
			{
				if (lastMove.equals(ListIteratorMove.none))
				{
					throw new NoSuchElementException();
				}

				assert (lastPreload != null);
				lastPreload.setElement(element);
				LinkedCoreKeyList.this.setIfContains(lastKey(), lastPreload);
			}

			@Override
			public ListIteratorMove getLastMove()
			{
				return this.lastMove;
			}

			@Override
			public long getLast()
			{
				if (lastMove.equals(ListIteratorMove.none))
				{
					throw new NoSuchElementException();
				}

				assert (lastPreload != null);
				return lastPreload.getElement();
			}
		}

		// rely on head, tail, and size
		private class ListNodeIterator implements CoreListKeyIterator
		{
			private NodeIterator itr = null;

			public ListNodeIterator(boolean head) throws IOException
			{
				if (head)
				{
					Node headNode = tryGet(LinkedCoreKeyList.this.head);
					this.itr = new NodeIterator(LinkedCoreKeyList.this.head,
							(headNode != null) ? headNode.getNext() : 0);
				}
				else
				{
					Node tailNode = tryGet(LinkedCoreKeyList.this.tail);
					this.itr = new NodeIterator((tailNode != null) ? tailNode
							.getPrev() : 0, LinkedCoreKeyList.this.tail);
				}
			}

			@Override
			public boolean hasNext() throws IOException
			{
				return itr.hasNext();
			}

			@Override
			public boolean hasPrev() throws IOException
			{
				return itr.hasPrev();
			}

			@Override
			public long next() throws IOException
			{
				return itr.next();
			}

			@Override
			public long prev() throws IOException
			{
				return itr.prev();
			}

			@Override
			public void add(long element) throws IOException
			{
				itr.add(element);
				size++;
			}

			@Override
			public void remove() throws IOException
			{
				itr.remove();
				size -= Math.min(1, size);
			}

			@Override
			public void set(long element) throws IOException
			{
				itr.set(element);
			}

			@Override
			public ListIteratorMove getLastMove()
			{
				return itr.getLastMove();
			}

			@Override
			public long getLast()
			{
				return itr.getLast();
			}

		}

		private class SyncListNodeIterator implements CoreListKeyIterator
		{
			private ListNodeIterator itr = null;

			private long localModCount = 0;

			public SyncListNodeIterator(boolean head) throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					this.localModCount = LinkedCoreKeyList.this.modCount;
					this.itr = new ListNodeIterator(head);
				}
			}

			private void testModCount()
			{
				if (localModCount != modCount)
				{
					throw new ConcurrentModificationException();
				}
			}

			@Override
			public boolean hasNext() throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.hasNext();
				}
			}

			@Override
			public boolean hasPrev() throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.hasPrev();
				}
			}

			@Override
			public long next() throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.next();
				}
			}

			@Override
			public long prev() throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.prev();
				}
			}

			@Override
			public void add(long element) throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					localModCount++;
					modCount++;
					itr.add(element);
				}
			}

			@Override
			public void remove() throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					localModCount++;
					modCount++;
					itr.remove();
				}
			}

			@Override
			public void set(long element) throws IOException
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					itr.set(element);
				}
			}

			@Override
			public ListIteratorMove getLastMove()
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.getLastMove();
				}
			}

			@Override
			public long getLast()
			{
				synchronized (LinkedCoreKeyList.this)
				{
					testModCount();
					return itr.getLast();
				}
			}

		}

	}
}
