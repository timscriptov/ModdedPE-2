/*
 * Copyright (C) 2018 - 2019 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
