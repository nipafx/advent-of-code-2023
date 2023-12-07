import java.util.Map;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

void main() {
	int result = sumCalibrationValues(input);
	assert result == 281;
	System.out.println(result);
}

int sumCalibrationValues(String input) {
	return input
			.lines()
			.mapToInt(this::extractCalibrationValue)
			.sum();
}

int extractCalibrationValue(String line) {
	var lineDigits = digits
			.entrySet().stream()
			.flatMap(digit -> firstAndLastOccurrence(line, digit.getKey(), digit.getValue()))
			.sorted(comparing(Occurrence::index))
			.toList();
	return lineDigits.getFirst().digit() * 10 + lineDigits.getLast().digit();
}

Stream<Occurrence> firstAndLastOccurrence(String line, int digit, String digitWord) {
	return Stream.of(
					new Occurrence(line.indexOf(digitWord), digit),
					new Occurrence(line.lastIndexOf(digitWord), digit),
					new Occurrence(line.indexOf("" + digit), digit),
					new Occurrence(line.lastIndexOf("" + digit), digit))
			.filter(occurrence -> occurrence.index >= 0);
}

record Occurrence(int index, int digit) { }

final Map<Integer, String> digits = Map.of(
		0, "zero",
		1, "one",
		2, "two",
		3, "three",
		4, "four",
		5, "five",
		6, "six",
		7, "seven",
		8, "eight",
		9, "nine"
);

final String input = """
		two1nine
		eightwothree
		abcone2threexyz
		xtwone3four
		4nineeightseven2
		zoneight234
		7pqrstsixteen
		""";
