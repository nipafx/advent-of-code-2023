import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

void main() {
	int result = sumCardPoints(input);
	assert result == 13;
	System.out.println(result);
}

int sumCardPoints(String input) {
	return input
			.lines()
			.map(this::parseCard)
			.mapToInt(Card::points)
			.sum();
}

Card parseCard(String line) {
	var matchedLine = CARD_LINE.matcher(line);
	if (!matchedLine.matches())
		throw new IllegalStateException();

	int cardNumber = Integer.parseInt(matchedLine.group("cardNumber"));
	var winningNumbers = parseNumbers(matchedLine.group("winningNumbers"));
	var numbers = parseNumbers(matchedLine.group("numbers"));

	return new Card(cardNumber, winningNumbers, numbers);
}

List<Integer> parseNumbers(String numbers) {
	return Stream
			.of(numbers.strip().split("\\s+"))
			.map(Integer::parseInt)
			.toList();
}

record Card(int number, List<Integer> winningNumbers, List<Integer> numbers) {

	int points() {
		long winners = numbers.stream()
				.filter(winningNumbers::contains)
				.count();
		return winners == 0 ? 0 : 1 << winners - 1;
	}

}

final Pattern CARD_LINE = Pattern
		.compile("Card\\s+(?<cardNumber>\\d+): (?<winningNumbers>[\\s\\d]+) \\| (?<numbers>[\\s\\d]+)");

final String input = """
		Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
		Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
		Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
		Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
		Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
		Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
		""";