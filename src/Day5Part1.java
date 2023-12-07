import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

void main() {
	long result = findSmallestLocation(input);
	assert result == 35;
	System.out.println(result);
}

long findSmallestLocation(String input) {
	var parsedInput = parseInput(input);

	return parsedInput
			.seeds.stream()
			.mapToLong(parsedInput.maps()::map)
			.min()
			// input is never empty
			.getAsLong();
}

Input parseInput(String input) {
	var lines = input.lines().toList();
	return new Input(parseSeeds(lines), parseMaps(lines));
}

private static List<Long> parseSeeds(List<String> inputLines) {
	var seedStrings = inputLines.getFirst().substring("seeds: ".length()).split((" "));
	return Stream
			.of(seedStrings)
			.map(Long::parseLong)
			.toList();
}

private static CategoryMaps parseMaps(List<String> inputLines) {
	var maps = new ArrayList<CategoryMap>();

	var ranges = new ArrayList<MapRange>();

	for (int i = 2; i < inputLines.size(); i++) {
		var line = inputLines.get(i);
		if (line.isBlank()) {
			maps.add(new CategoryMap(List.copyOf(ranges)));
			ranges.clear();
		} else if (!line.contains(":")) {
			ranges.add(MapRange.fromLine(line));
		}
	}
	// finish the last map after the last line
	maps.add(new CategoryMap(List.copyOf(ranges)));

	return new CategoryMaps(maps);
}

record Input(List<Long> seeds, CategoryMaps maps) { }

record CategoryMaps(List<CategoryMap> maps) {

	long map(long seed) {
		return maps.stream().reduce(
				seed,
				(source, map) -> map.map(source),
				(_, _) -> { throw new IllegalStateException(); }
		);
	}

}

record CategoryMap(List<MapRange> ranges) {

	long map(long source) {
		return ranges.stream()
				.flatMap(range -> range.apply(source).stream())
				.findFirst()
				.orElse(source);
	}

}

record MapRange(long sourceStart, long destStart, long length) {

	static MapRange fromLine(String line) {
		var interval = line.split(" ");

		long sourceStart = Long.parseLong(interval[1]);
		long destStart = Long.parseLong(interval[0]);
		long length = Long.parseLong(interval[2]);

		return new MapRange(sourceStart, destStart, length);
	}

	Optional<Long> apply(long source) {
		long distance = source - sourceStart;
		return 0 <= distance && distance < length
				? Optional.of(destStart + distance)
				: Optional.empty();
	}

}

final String input = """
		seeds: 79 14 55 13

		seed-to-soil map:
		50 98 2
		52 50 48

		soil-to-fertilizer map:
		0 15 37
		37 52 2
		39 0 15

		fertilizer-to-water map:
		49 53 8
		0 11 42
		42 0 7
		57 7 4

		water-to-light map:
		88 18 7
		18 25 70

		light-to-temperature map:
		45 77 23
		81 45 19
		68 64 13

		temperature-to-humidity map:
		0 69 1
		1 0 69

		humidity-to-location map:
		60 56 37
		56 93 4
		""";
