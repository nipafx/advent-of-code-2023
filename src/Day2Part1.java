import java.util.List;
import java.util.stream.Stream;

void main() {
	int result = sumPossibleGames(input, new CubeSet(12, 13, 14));
	assert result == 8;
	System.out.println(result);
}

int sumPossibleGames(String input, CubeSet bagContent) {
	return input
			.lines()
			.map(this::parseGame)
			.filter(game -> game.isPossible(bagContent))
			.mapToInt(Game::id)
			.sum();
}

private Game parseGame(String line) {
	var gameLine = line.split(":");
	var idString = gameLine[0].substring(5);
	var iterationsString = gameLine[1].strip();

	var id = Integer.parseInt(idString);
	var iterations = Stream
			.of(iterationsString.split(";"))
			.map(String::strip)
			.map(this::parseIteration)
			.toList();
	return new Game(id, iterations);
}

private CubeSet parseIteration(String iteration) {
	int red = 0;
	int green = 0;
	int blue = 0;

	for (String cubes : iteration.split(",")) {
		var coloredCubes = cubes.strip().split(" ");
		int amount = Integer.parseInt(coloredCubes[0]);
		var color = coloredCubes[1];
		switch (color) {
			case "red" -> red = amount;
			case "green" -> green = amount;
			case "blue" -> blue = amount;
		}
	}

	return new CubeSet(red, green, blue);
}

record Game(int id, List<CubeSet> iterations) {

	public boolean isPossible(CubeSet bagContent) {
		return iterations.stream().allMatch(iteration -> iteration.isPossible(bagContent));
	}

}

record CubeSet(int red, int green, int blue) {

	public boolean isPossible(CubeSet bagContent) {
		return red <= bagContent.red()
				&& green <= bagContent.green()
				&& blue <= bagContent.blue();
	}

}

final String input = """
		Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
		Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
		Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
		Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
		Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
		""";
