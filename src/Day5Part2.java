import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

void main() {
	long result = findSmallestLocation(input);
	assert result == 46;
	System.out.println(result);
}

long findSmallestLocation(String input) {
	var parsedInput = parseInput(input);

	return parsedInput
			.seeds.stream()
			.flatMap(seed -> parsedInput.maps().map(seed).stream())
			.min(Comparator.naturalOrder())
			// input is never empty and so output is neither
			.get()
			.start();
}

Input parseInput(String input) {
	var lines = input.lines().toList();
	return new Input(parseSeeds(lines), parseMaps(lines));
}

private static List<Range> parseSeeds(List<String> inputLines) {
	var seedStrings = inputLines.getFirst().substring("seeds: ".length()).split((" "));
	return Stream
			.of(seedStrings)
			.map(Long::parseLong)
			// let's use gatherers for the heck of it - this is probably much simpler with a regex
			.<Range>gather(Gatherer.of(
					() -> new AtomicLong(-1),
					(state, number, downstream) -> {
						var seed = state.get();
						if (seed == -1)
							state.set(number);
						else {
							downstream.push(new Range(seed, number));
							state.set(-1);
						}
						return true;
					},
					(_, _) -> {
						throw new IllegalStateException();
					},
					(_, _) -> {
					}))
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

record Input(List<Range> seeds, CategoryMaps maps) {

}

record CategoryMaps(List<CategoryMap> maps) {

	List<Range> map(Range seed) {
		return maps.stream()
				.reduce(
						List.of(seed),
						(source, map) -> map.map(source),
						(_, _) -> {
							throw new IllegalStateException();
						}
				);
	}

}

record CategoryMap(List<MapRange> mapRanges) {

	List<Range> map(List<Range> source) {
		return mapRanges.stream()
				.reduce(
						Ranges.allUnmapped(source.toArray(Range[]::new)),
						(ranges, mapRange) -> mapRange.map(ranges),
						(_, _) -> {
							throw new IllegalStateException();
						})
				.applyIdentityToUnmapped()
				.mapped();
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

	Ranges map(Ranges source) {
		var mapped = new ArrayList<Range>(source.mapped());
		var unmapped = new ArrayList<Range>();

		source
				.unmapped().stream()
				.map(this::map)
				.forEach(ranges -> {
					mapped.addAll(ranges.mapped());
					unmapped.addAll(ranges.unmapped());
				});

		return new Ranges(List.copyOf(unmapped), List.copyOf(mapped));
	}

	private Ranges map(Range source) {
		long distance = sourceStart - source.start();
		if (distance > 0) {
			// this:         |start ---
			// source:  |start ---
			long possibleOverlap = source.start() + source.length() - sourceStart;
			if (possibleOverlap <= 0)
				// this:                               |start ---
				// source:  |start --- start+length|
				return Ranges.allUnmapped(source);
			else {
				// this:                      |start ---
				// source:  |start --- start+length|
				var unmappedBeforeThis = new Range(source.start(), distance);
				var mapped = new Range(destStart, Math.min(length, possibleOverlap));
				if (length < possibleOverlap) {
					// this:         |start --- start+length|
					// source:   |start       ---      start+length|
					var unmappedAfterThis = new Range(sourceStart + length, possibleOverlap - length);
					return new Ranges(List.of(unmappedBeforeThis, unmappedAfterThis), List.of(mapped));
				} else {
					// this:         |start --- start+length|
					// source:   |start --- start+length|
					return new Ranges(List.of(unmappedBeforeThis), List.of(mapped));
				}
			}
		} else {
			// this:    |start ---
			// source:    |start ---
			long possibleOverlap = sourceStart + length - source.start();
			if (possibleOverlap <= 0)
				// this:   |start --- start+length|
				// source:                            |start ---
				return Ranges.allUnmapped(source);
			else {
				// this:   |start --- start+length|
				// source:         |start ---
				var mapped = new Range(destStart - distance, Math.min(source.length(), possibleOverlap));
				if (source.length() <= possibleOverlap) {
					// this:   |start    ---     start+length|
					// source:    |start --- start+length|
					return Ranges.allMapped(mapped);
				} else {
					// this:   |start --- start+length|
					// source:     |start --- start+length|
					var unmappedAfterThis = new Range(source.start() + possibleOverlap, source.length() - possibleOverlap);
					return new Ranges(List.of(unmappedAfterThis), List.of(mapped));
				}
			}
		}
	}

}

record Ranges(List<Range> unmapped, List<Range> mapped) {

	static Ranges allUnmapped(Range... unmapped) {
		return new Ranges(List.of(unmapped), List.of());
	}

	static Ranges allMapped(Range... mapped) {
		return new Ranges(List.of(), List.of(mapped));
	}

	Ranges applyIdentityToUnmapped() {
		var allMapped = Stream
				// applying identity to unmapped simply makes them mapped
				.concat(unmapped.stream(), mapped.stream())
				.toList();
		return new Ranges(List.of(), allMapped);
	}

}

record Range(long start, long length) implements Comparable<Range> {

	@Override
	public int compareTo(Range other) {
		return Long.compare(start, other.start());
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
