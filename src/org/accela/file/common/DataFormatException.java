package org.accela.file.common;

//当从持久化的数据中读取参数，然后新建对象的时候，可能发生两种错误。
// 第一种是IOException，即普通的IO错误，使得读取数据失败。
// 第二种是，虽然成功地读取了参数，但是这些参数可能因为数据损坏而已经
// 是错误地，即你可能存储的时候存的是10，读取回来成了-10。当用这样的
// 参数新建对象的时候，可能发生IllegalArgumentException。IllegalArgumentException
// 是免检异常，反映的是编码的逻辑错误，但实际上，这个错误的成因却不是
// 逻辑错误，而是无法避免其发生的IO数据损坏错误，应该是非免检异常。
// 如果直接从read（DataInput）方法中直接抛出IllegalArgumentException，
// 就可能会导致程序终止，而不能够成功地区分第二种错误，并正确处理。
// 即使你用try-catch块捕捉IllegalArgumentException，这也不是一种好的
// 做法，因为你有可能把其它无关的IllegalArgumentException也捕捉了，并且
// 捕捉免检异常的做法并不能使另外的人明白这段代码是什么意思。
//针对第二种错误，你需要抛出这个异常，具体形式参见BufferPersistanceDelegate
public class DataFormatException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DataFormatException()
	{
		super();
	}

	public DataFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DataFormatException(String message)
	{
		super(message);
	}

	public DataFormatException(Throwable cause)
	{
		super(cause);
	}

}
