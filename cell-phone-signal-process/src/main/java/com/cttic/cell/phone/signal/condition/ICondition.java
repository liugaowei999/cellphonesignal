package com.cttic.cell.phone.signal.condition;

public interface ICondition {

	public boolean getResult();

	public void installCondExpression(String conditionStr);

	// 设置第一个值
	public void setFirstValue(String value);

	// 设置第二个值
	public void setSecondValue(String value);

	public String getLeftKey();

	// 设置运算符
	public void setOp(String op);

	public String getRightKey();

	public boolean isReady();
}
