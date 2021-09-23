package xyz.binfish.misa.exceptions;

import java.sql.SQLException;

public class UnimplementedParameterException extends SQLException {

	private String str;

	public UnimplementedParameterException(Object parameter) {
		str = String.format("Parameter not implemented at for %s", parameter);
	}

	public UnimplementedParameterException(Object parameter, int pos) {
		str = String.format("Parameter not implemented at for %s on position :%s:", parameter, pos);
	}

	@Override
	public String toString() {
		return str;
	}
}
