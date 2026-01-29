package com.bcasekuritas.mybest.ext.listener

interface OnClickStr { fun onClickStr(value: String?) }
interface OnClickInt { fun onClickInt(value: Int?) }
interface OnClickStrInt { fun onClickStrInt(valueStr: String?, valueInt: Int?) }
interface OnClickInts { fun onClickInts(valueInt: Int?, valueInts: Int?) }
interface OnClickStrs { fun onClickStrs(valueStr1: String?, valueStr2: String?) }
interface OnClickAny { fun onClickAny(valueAny: Any?) }
interface OnClickAnyInt { fun onClickAnyInt(valueAny: Any?, valueInt: Int) }
interface OnClickAnyStr { fun onClickAnyStr(valueAny: Any?, valueString: String) }
interface OnClickBoolean { fun onClickBoolean(value: Boolean?) }
interface OnClickStrIntBoolean { fun onClickStrIntBoolean(valueStr: String?, valueInt: Int?, valueBoolean: Boolean?) }
