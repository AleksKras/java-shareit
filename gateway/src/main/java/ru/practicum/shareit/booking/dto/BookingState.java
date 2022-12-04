package ru.practicum.shareit.booking.dto;

public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING,

	//неподдерживаемый
	UNSUPPORTED;


	public static BookingState from(String state) {
		BookingState bookingState;
		try {
			bookingState = BookingState.valueOf(state);
		} catch (IllegalArgumentException e) {
			bookingState = BookingState.UNSUPPORTED;
		}
		return bookingState;
	}

}
