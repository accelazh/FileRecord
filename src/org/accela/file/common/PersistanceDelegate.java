package org.accela.file.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//����㲻ϲ��ΪT����������дһ��PersistanceDelegate����ϣ�����Լ�
//�����Լ��ĳ־û�����ô��ֻ��Ҫ��Tʵ��PersistanceDelegate�ӿڼ���
public interface PersistanceDelegate<T>
{
	public T read(DataInput in) throws IOException,
			DataFormatException;

	public void write(DataOutput out, T object) throws IOException;

}
