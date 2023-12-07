import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

void main() {
	long result = multiplyPossibilities(input);
	assert result == 288;
	System.out.println(result);
}

long multiplyPossibilities(String input) {
	return parseRaces(input).stream()
			.mapToLong(Race::possibilities)
			.reduce(1, Math::multiplyExact);
}

List<Race> parseRaces(String input) {
	var inputLines = input.lines().toList();
	var times = inputLines.get(0).substring("Time:".length()).strip().split("\\s+");
	var distances = inputLines.get(1).substring("Distance:".length()).strip().split("\\s+");

	return IntStream
			.range(0, times.length)
			.mapToObj(i -> new Race(Long.parseLong(times[i]), Long.parseLong(distances[i])))
			.toList();
}

record Race(long time, long distance) {

	long possibilities() {
		return LongStream.range(1, time + 1)
				.map(chargeTime -> chargeTime * (time - chargeTime))
				.filter(travelledDistance -> travelledDistance > distance)
				.count();
	}

}

final String input = """
		Time:      7  15   30
		Distance:  9  40  200
		""";
