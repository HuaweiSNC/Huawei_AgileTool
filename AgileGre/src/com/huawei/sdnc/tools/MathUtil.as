package com.huawei.sdnc.tools
{
//##################################################################
//#
//#		弧度与角度的相互转换
//#
//##################################################################


public class MathUtil
{
	public static function radToDegree(radians:Number):Number
	{
		return radians*(180/Math.PI);
	}
	public static function degreeToRadian(degrees:Number):Number
	{
		return degrees*(Math.PI/180);
	}
}
}