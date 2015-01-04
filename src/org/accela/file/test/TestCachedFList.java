package org.accela.file.test;

import java.io.File;

import org.accela.file.FFactory;
import org.accela.file.FList;

public class TestCachedFList extends TestFList
{
	@Override
	protected FList<String> createFList(File file)
	{
		return FFactory.getInstance(delegate).create(file, 128, 1024);
	}

}
