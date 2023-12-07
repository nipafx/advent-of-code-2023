void main() {
	int result = sumCalibrationValues(input);
	assert result == 142;
	System.out.println(result);
}

int sumCalibrationValues(String input) {
	return input
			.lines()
			.mapToInt(this::extractCalibrationValue)
			.sum();
}

int extractCalibrationValue(String line) {
	var digits = line
			.chars()
			// integers [0-9] are in interval [48-57]
			.mapToObj(c -> c - 48)
			.filter(digit -> 0 <= digit && digit <= 9)
			.toList();
	return digits.getFirst() * 10 + digits.getLast();
}

final String input = """
		1abc2
		pqr3stu8vwx
		a1b2c3d4e5f
		treb7uchet
		""";
