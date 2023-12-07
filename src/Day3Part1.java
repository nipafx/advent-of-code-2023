import java.util.ArrayList;
import java.util.List;

void main() {
	int result = sumPartNumbers(input);
	assert result == 4361;
	System.out.println(result);
}

int sumPartNumbers(String input) {
	var schematic = parseSchematic(input);
	return schematic
			.partNumbers().stream()
			.filter(partNumber -> schematic
					.symbols().stream()
					.anyMatch(partNumber::touches))
			.mapToInt(PartNumber::number)
			.sum();
}

Schematic parseSchematic(String input) {
	var inputLines = input.lines().toList();
	var partNumbers = new ArrayList<PartNumber>();
	var symbols = new ArrayList<Coordinates>();

	// parse line bine line...
	for (int y = 0; y < inputLines.size(); y++) {
		var line = inputLines.get(y);
		var partNumber = new StringBuilder();
		// ... character by character
		for (int x = 0; x < line.length(); x++) {
			var c = line.charAt(x);
			if (Character.isDigit(c))
				partNumber.append(c);
			else {
				if (!partNumber.isEmpty()) {
					partNumbers.add(createPartNumber(partNumber.toString(), x, y));
					partNumber = new StringBuilder();
				}
				if (c != '.')
					symbols.add(new Coordinates(x, y));
			}
		}
		if (!partNumber.isEmpty())
			partNumbers.add(createPartNumber(partNumber.toString(), line.length(), y));
	}

	return new Schematic(partNumbers, symbols);
}

PartNumber createPartNumber(String partNumber, int xAfter, int y) {
	var xBefore = xAfter - partNumber.length() - 1;
	return new PartNumber(
			Integer.parseInt(partNumber),
			new Coordinates(xBefore, y - 1),
			new Coordinates(xAfter, y + 1));
}

record Schematic(List<PartNumber> partNumbers, List<Coordinates> symbols) { }

record PartNumber(int number, Coordinates topLeft, Coordinates bottomRight) {

	boolean touches(Coordinates coordinates) {
		return topLeft.x() <= coordinates.x()
				&& coordinates.x() <= bottomRight.x()
				&& topLeft.y() <= coordinates.y()
				&& coordinates.y() <= bottomRight.y();
	}

}

// x/y start at 0 in top/left corner and increase to the right and downwards
record Coordinates(int x, int y) { }

String input = """
		467..114..
		...*......
		..35..633.
		......#...
		617*......
		.....+.58.
		..592.....
		......755.
		...$.*....
		.664.598..
		""";