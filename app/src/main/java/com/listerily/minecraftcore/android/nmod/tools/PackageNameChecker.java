package com.listerily.minecraftcore.android.nmod.tools;

public class PackageNameChecker
{
	static private boolean isValidJavaIdentifier(String className)
	{
		if (className.length() == 0 || !Character.isJavaIdentifierStart(className.charAt(0)))
			return false;
		String name = className.substring(1);
		for (int i = 0; i < name.length(); i++)
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
				return false;
		return true;
	} 

	static public boolean isValidPackageName(String fullName)
	{
		if (fullName == null)
			return false;

		if (!fullName.contains("."))
			return false;

		boolean flag = true;
		try
		{
			if (!fullName.endsWith("."))
			{
				int index = fullName.indexOf("."); 
				if (index != -1)
				{
					String[] str = fullName.split("\\.");
					for (String name : str)
					{
						if (name.equals(""))
						{
							flag = false;
							break;
						}
						else if (!isValidJavaIdentifier(name))
						{
							flag = false;
							break;
						}
					}
				}
				else if (!isValidJavaIdentifier(fullName))
				{
					flag = false;
				}
			}
			else
			{
				flag = false;
			}
		}
		catch (Exception ex)
		{
			flag = false;
		}
		return flag;
	}
}
