package xyz.binfish.misa.util;

import java.awt.Color;

public enum MessageType {

	ERROR("#FF0033"),
	WARNING("#FF8F20"),
	SUCCESS("#00AE86"),
	INFO("#00AE86");

	private final String color;

	MessageType(String color) {
		this.color = color;
	}

	public Color getColor() {
		return Color.decode(this.color);
	}
}
