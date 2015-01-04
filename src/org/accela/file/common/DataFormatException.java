package org.accela.file.common;

//���ӳ־û��������ж�ȡ������Ȼ���½������ʱ�򣬿��ܷ������ִ���
// ��һ����IOException������ͨ��IO����ʹ�ö�ȡ����ʧ�ܡ�
// �ڶ����ǣ���Ȼ�ɹ��ض�ȡ�˲�����������Щ����������Ϊ�����𻵶��Ѿ�
// �Ǵ���أ�������ܴ洢��ʱ������10����ȡ��������-10������������
// �����½������ʱ�򣬿��ܷ���IllegalArgumentException��IllegalArgumentException
// ������쳣����ӳ���Ǳ�����߼����󣬵�ʵ���ϣ��������ĳ���ȴ����
// �߼����󣬶����޷������䷢����IO�����𻵴���Ӧ���Ƿ�����쳣��
// ���ֱ�Ӵ�read��DataInput��������ֱ���׳�IllegalArgumentException��
// �Ϳ��ܻᵼ�³�����ֹ�������ܹ��ɹ������ֵڶ��ִ��󣬲���ȷ����
// ��ʹ����try-catch�鲶׽IllegalArgumentException����Ҳ����һ�ֺõ�
// ��������Ϊ���п��ܰ������޹ص�IllegalArgumentExceptionҲ��׽�ˣ�����
// ��׽����쳣������������ʹ�������������δ�����ʲô��˼��
//��Եڶ��ִ�������Ҫ�׳�����쳣��������ʽ�μ�BufferPersistanceDelegate
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
