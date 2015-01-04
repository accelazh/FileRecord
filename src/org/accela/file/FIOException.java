package org.accela.file;

public class FIOException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public FIOException()
	{
		super();
	}

	public FIOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FIOException(String message)
	{
		super(message);
	}

	public FIOException(Throwable cause)
	{
		super(cause);
	}

}
