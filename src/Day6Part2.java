import java.util.stream.LongStream;

void main() {
	long result = multiplyPossibilities(input);
	assert result == 71503;
	System.out.println(result);
}

long multiplyPossibilities(String input) {
	return parseRace(input).possibilities();
}

Race parseRace(String input) {
	var inputLines = input.lines().toList();
	var time = Long.parseLong(inputLines
			.get(0)
			.substring("Time:".length())
			.replaceAll("\\s+", ""));
	var distance = Long.parseLong(inputLines
			.get(1)
			.substring("Distance:".length())
			.replaceAll("\\s+", ""));

	return new Race(time, distance);
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
