package com.cttic.cell.phone.signal.condition;

public interface ICondition {

	public boolean getResult();

	public void installCondExpression(String conditionStr);

	// ���õ�һ��ֵ
	public void setFirstValue(String value);

	// ���õڶ���ֵ
	public void setSecondValue(String value);

	public String getLeftKey();

	// ���������
	public void setOp(String op);

	public String getRightKey();

	public boolean isReady();
}
