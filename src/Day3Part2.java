import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

void main() {
	int result = sumGearRatios(input);
	assert result == 467835;
	System.out.println(result);
}

int sumGearRatios(String input) {
	var schematic = parseSchematic(input);
	return schematic
			.gearSymbols().stream()
			.flatMap(gearSymbol -> createGear(gearSymbol, schematic.partNumbers()).stream())
			.mapToInt(Gear::ratio)
			.sum();
}

Schematic parseSchematic(String input) {
	var inputLines = input.lines().toList();
	var partNumbers = new ArrayList<PartNumber>();
	var gearSymbols = new ArrayList<Coordinates>();

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
				if (c == '*')
					gearSymbols.add(new Coordinates(x, y));
			}
		}
		if (!partNumber.isEmpty())
			partNumbers.add(createPartNumber(partNumber.toString(), line.length(), y));
	}

	return new Schematic(partNumbers, gearSymbols);
}

PartNumber createPartNumber(String partNumber, int xAfter, int y) {
	var xBefore = xAfter - partNumber.length() - 1;
	return new PartNumber(
			Integer.parseInt(partNumber),
			new Coordinates(xBefore, y - 1),
			new Coordinates(xAfter, y + 1));
}

Optional<Gear> createGear(Coordinates gearSymbol, Collection<PartNumber> partNumbers) {
	var parts = partNumbers.stream()
			.filter(part -> part.touches(gearSymbol))
			.toList();
	return parts.size() == 2
			? Optional.of(new Gear(parts.get(0), parts.get(1)))
			: Optional.empty();
}

record Gear(PartNumber part1, PartNumber part2) {

	int ratio() {
		return part1.number() * part2.number();
	}

}

record Schematic(List<PartNumber> partNumbers, List<Coordinates> gearSymbols) { }

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