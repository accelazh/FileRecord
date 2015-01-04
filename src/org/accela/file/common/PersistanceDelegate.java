package org.accela.file.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//如果你不喜欢为T这个类另外编写一个PersistanceDelegate，而希望它自己
//负责自己的持久化，那么你只需要让T实现PersistanceDelegate接口即可
public interface PersistanceDelegate<T>
{
	public T read(DataInput in) throws IOException,
			DataFormatException;

	public void write(DataOutput out, T object) throws IOException;

}
