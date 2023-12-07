import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * NOTE: This solution, specifically the state transition function, is fragile and likely to be incomplete,
 *       but it works for the puzzle input. It definitely doesn't fall into the "elegant" category that I'm
 *       striving for here, but after having gone through the trouble of implementing it, I wanted ro
 *       preserve it.
 */

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
	var state = State.NONE;
	var first = new AtomicInteger(0);
	for (int i = 0; i < line.length(); i++) {
		state = state.forward(line.charAt(i));
		var digit = state.digit();
		if (digit.isPresent()) {
			first.set(digit.getAsInt());
			break;
		}
	}

	state = State.NONE;
	var last = new AtomicInteger(0);
	for (int i = line.length() - 1; i >= 0; i--) {
		state = state.backward(line.charAt(i));
		var digit = state.digit();
		if (digit.isPresent()) {
			last.set(digit.getAsInt());
			break;
		}
	}

	return first.get() * 10 + last.get();
}

enum State {
	NONE,
	Z, ZE, ZER, ZERO,
	O, ON, ONE,
	T, TW, TWO,
	T3, TH, THR, THRE, THREE,
	F, FO, FOU, FOUR,
	F5, FI, FIV, FIVE,
	S, SI, SIX,
	S7, SE, SEV, SEVE, SEVEN,
	E, EI, EIG, EIGH, EIGHT,
	N, NI, NIN, NINE;

	/*
	 * Interpret a state like "SEV" to mean "we have seen the characters s, e, v".
	 */
	State forward(char c) {
		return switch (c) {
			case '0' -> ZERO;
			case '1' -> ONE;
			case '2' -> TWO;
			case '3' -> THREE;
			case '4' -> FOUR;
			case '5' -> FIVE;
			case '6' -> SIX;
			case '7' -> SEVEN;
			case '8' -> EIGHT;
			case '9' -> NINE;
			case 'e' -> switch (this) {
				case Z -> ZE;
				case ON -> ONE;
				case THR -> THRE;
				case THRE -> THREE;
				case FIV -> FIVE;
				case S -> SE;
				case SEV -> SEVE;
				case NIN -> NINE;
				default -> E;
			};
			case 'f' -> F;
			case 'g' -> switch (this) {
				case EI -> EIG;
				default -> NONE;
			};
			case 'h' -> switch (this) {
				case T -> TH;
				case EIG -> EIGH;
				default -> NONE;
			};
			case 'i' -> switch (this) {
				case F -> FI;
				case S -> SI;
				case E -> EI;
				case N -> NI;
				case NIN -> NI;
				default -> NONE;
			};
			case 'n' -> switch (this) {
				case O -> ON;
				case FO -> ON;
				case SEVE -> SEVEN;
				case NI -> NIN;
				default -> N;
			};
			case 'o' -> switch (this) {
				case ZER -> ZERO;
				case TW -> TWO;
				case F -> FO;
				default -> O;
			};
			case 'r' -> switch (this) {
				case ZE -> ZER;
				case TH -> THR;
				case FOU -> FOUR;
				default -> NONE;
			};
			case 's' -> S;
			case 't' -> switch (this) {
				case EIGH -> EIGHT;
				default -> T;
			};
			case 'u' -> switch (this) {
				case FO -> FOU;
				default -> NONE;
			};
			case 'v' -> switch (this) {
				case FI -> FIV;
				case SE -> SEV;
				default -> NONE;
			};
			case 'w' -> switch (this) {
				case T -> TW;
				default -> NONE;
			};
			case 'x' -> switch (this) {
				case SI -> SIX;
				default -> NONE;
			};
			default -> NONE;
		};
	}

	/*
	 * Interpret a state like "SEV" to mean "we are missing the characters s, e, v, i.e. we have seen e, n"
	 */
	State backward(char c) {
		return switch (c) {
			case '0' -> ZERO;
			case '1' -> ONE;
			case '2' -> TWO;
			case '3' -> THREE;
			case '4' -> FOUR;
			case '5' -> FIVE;
			case '6' -> SIX;
			case '7' -> SEVEN;
			case '8' -> EIGHT;
			case '9' -> NINE;
			case 'e' -> switch (this) {
				case ZE -> Z;
				case ON -> THR;
				case THR -> THR;
				case THRE -> THR;
				case SEVE -> SEV;
				case SE -> S7;
				case E -> EIGHT;
				default -> ON;
			};
			case 'f' -> switch (this) {
				case F -> FOUR;
				case F5 -> FIVE;
				default -> NONE;
			};
			case 'g' -> switch (this) {
				case EIG -> EI;
				default -> NONE;
			};
			case 'h' -> switch (this) {
				case EIGH -> EIG;
				case TH -> T3;
				default -> NONE;
			};
			case 'i' -> switch (this) {
				case O -> N;
				case FI -> F5;
				case SI -> S;
				case SE -> F5;
				case EI -> E;
				case NI -> N;
				default -> NONE;
			};
			case 'n' -> switch (this) {
				case ON -> O;
				case SEV -> O;
				case N -> NINE;
				case NIN -> NI;
				default -> SEVE;
			};
			case 'o' -> switch (this) {
				case ZER -> ZER;
				case O -> ONE;
				case TW -> TW;
				case FO -> F;
				default -> ZER;
			};
			case 'r' -> switch (this) {
				case ZER -> ZE;
				case THR -> TH;
				default -> FOU;
			};
			case 's' -> switch (this) {
				case S -> SIX;
				case S7 -> SEVEN;
				default -> NONE;
			};
			case 't' -> switch (this) {
				case T -> TWO;
				case T3 -> THREE;
				default -> EIGH;
			};
			case 'u' -> switch (this) {
				case FOU -> FO;
				default -> NONE;
			};
			case 'v' -> switch (this) {
				case ON -> FI;
				case SEV -> SE;
				default -> NONE;
			};
			case 'w' -> switch (this) {
				case ZER -> T;
				case TW -> T;
				default -> NONE;
			};
			case 'x' -> SI;
			default -> NONE;
		};
	}

	public OptionalInt digit() {
		return switch (this) {
			case ZERO -> OptionalInt.of(0);
			case ONE -> OptionalInt.of(1);
			case TWO -> OptionalInt.of(2);
			case THREE -> OptionalInt.of(3);
			case FOUR -> OptionalInt.of(4);
			case FIVE -> OptionalInt.of(5);
			case SIX -> OptionalInt.of(6);
			case SEVEN -> OptionalInt.of(7);
			case EIGHT -> OptionalInt.of(8);
			case NINE -> OptionalInt.of(9);
			default -> OptionalInt.empty();
		};
	}
}

final String input = """
		two1nine
		eightwothree
		abcone2threexyz
		xtwone3four
		4nineeightseven2
		zoneight234
		7pqrstsixteen
		""";
