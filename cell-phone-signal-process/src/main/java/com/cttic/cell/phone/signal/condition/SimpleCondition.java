package com.cttic.cell.phone.signal.condition;

import org.apache.commons.lang3.math.NumberUtils;

public class SimpleCondition implements ICondition {
	private String firstValue;
	private String secondValue;

	private String leftKey;
	private String op;
	private String rightKey;
	private boolean isReady = false;

	@Override
	public boolean getResult() {
		if (op.equalsIgnoreCase("=")) {
			return firstValue.equalsIgnoreCase(secondValue);
		}
		return false;
	}

	@Override
	public void installCondExpression(String conditionStr) {
		if (conditionStr.trim().equalsIgnoreCase("true")) {
			setFirstValue(conditionStr);
			setSecondValue(conditionStr);
			setOp("=");
			isReady = true;
			return;
		}
		int pos = conditionStr.indexOf("}");
		String fieldName = conditionStr.substring(0, pos + 1);
		String other = conditionStr.substring(pos + 1).trim();
		//		System.out.println(fieldName);
		//		System.out.println(other);

		setLeftKey(fieldName);
		if (other.startsWith("=") || other.startsWith("==")) {
			setOp("=");
			setRightKey(other.startsWith("==") ? other.substring(2) : other.substring(1));
			setSecondValue(this.rightKey);
		} else if (other.startsWith(">") && !other.startsWith(">=")) {
			setOp(">");
			setRightKey(other.substring(1));
			setSecondValue(this.rightKey);
		}
		//		System.out.println(leftKey);
		//		System.out.println(rightKey);
	}

	@Override
	public void setFirstValue(String value) {
		this.firstValue = value.trim();
	}

	@Override
	public void setSecondValue(String value) {
		this.secondValue = value.trim();
	}

	@Override
	public void setOp(String op) {
		this.isReady = false;
		this.op = op.trim();
	}

	@Override
	public String getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(String leftKey) {
		this.isReady = false;
		this.leftKey = leftKey;
	}

	@Override
	public String getRightKey() {
		return rightKey;
	}

	public void setRightKey(String rightKey) {
		this.isReady = false;
		this.rightKey = rightKey;
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	public static void main(String[] args) {
		String conditonStr = "{callType} ==4923";
		SimpleCondition condition = new SimpleCondition();
		condition.installCondExpression(conditonStr);
		//		String[] split = conditonStr.split("}");

		String fileName1 = "TrafRTTE_BJMCC_TD_VOICE_RTT_GY_YCGS6_20170719180100.dat";
		String fileName2 = "TrafRTTE_BJMCC_TD_VOICE_RTT_GY_YCGS6_20170719180101.dat";
		String datetime1 = fileName1.substring(fileName1.lastIndexOf("_") + 1, fileName1.lastIndexOf("."));
		String datetime2 = fileName2.substring(fileName2.lastIndexOf("_") + 1, fileName2.lastIndexOf("."));
		System.out.println(datetime1.compareTo(datetime2) > 0);
		System.out.println(NumberUtils.isDigits(datetime1));

	}
}
