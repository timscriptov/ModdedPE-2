package com.listerily.minecraftcore.android.nmod.exception;

public class NModWarning
{
	private int mType;
	private Throwable mCause;
	
	public NModWarning(int type,Throwable cause)
	{
		mType = type;
		mCause = cause;
	}
	
	public int getType()
	{
		return mType;
	}
	
	public Throwable getCause()
	{
		return mCause;
	}
}
