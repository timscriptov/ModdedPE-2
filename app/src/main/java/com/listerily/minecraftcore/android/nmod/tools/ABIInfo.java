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

public class ABIInfo
{
	public static String getTargetABIType()
	{
		String property = System.getProperty("os.arch");
        return (property.equals("i686") || property.startsWith("x86")) ? "x86" : "armeabi-v7a";
	}
}
