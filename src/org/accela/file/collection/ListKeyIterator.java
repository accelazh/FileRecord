package org.accela.file.collection;

import java.io.IOException;

//֮���Լ̳�ListElementIterator<T>������Ϊ�����������������ʵ����������װ�е�Ԫ�ء�
//ֻ���������е�Ԫ�ض��ǳ����͵ļ�
public interface ListKeyIterator extends ListElementIterator<Long>
{
	public Long next() throws IOException;

	public Long prev() throws IOException;

	public Long getLast() throws IOException;
}
