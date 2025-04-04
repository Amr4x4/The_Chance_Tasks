object SudokuValidator {
    private val EMPTY_CELL_MARKERS = setOf(".", "0", "")
    private val VALID_SIZES = listOf(1, 3, 4, 9, 16)

    fun validate(board: Array<Array<String>>): Pair<Boolean, String> {
        if (board.isEmpty()) {
            return Pair(false, "Board is empty")
        }

        if (board.all { row -> row.all { it in EMPTY_CELL_MARKERS } }) {
            return Pair(false, "All cells are empty")
        }

        if (board.any { it.size != board.size }) {
            return Pair(false, "Board is not square - all rows must have the same length")
        }

        val size = board.size
        if (size !in VALID_SIZES) {
            return Pair(false, "Invalid Sudoku size: $size. Allowed sizes: ${VALID_SIZES.joinToString()}")
        }

        val validChars = getValidCharacters(size)

        // Check rows
        for ((i, row) in board.withIndex()) {
            validateGroup(row.toList(), "Row", i + 1, size, validChars)?.let {
                return Pair(false, it)
            }
        }

        // Check columns
        for (col in 0 until size) {
            val column = List(size) { board[it][col] }
            validateGroup(column, "Column", col + 1, size, validChars)?.let {
                return Pair(false, it)
            }
        }

        // Check boxes
        if (size in listOf(3, 9, 16)) {
            val boxSize = getBoxSize(size)
            var boxIndex = 1
            for (rowStart in 0 until size step boxSize) {
                for (colStart in 0 until size step boxSize) {
                    val box = getBox(board, rowStart, colStart, boxSize)
                    validateGroup(box, "Box", boxIndex, size, validChars)?.let {
                        return Pair(false, it)
                    }
                    boxIndex++
                }
            }
        }

        return Pair(true, "Valid Sudoku")
    }


    private fun getValidCharacters(size: Int): List<String> = when (size) {
        1 -> listOf("1")
        3 -> (1..9).map { it.toString() }
        4 -> (1..4).map { it.toString() }
        9 -> (1..9).map { it.toString() }
        16 -> (1..9).map { it.toString() } + listOf("A", "B", "C", "D", "E", "F", "G")
        else -> emptyList()
    }

    private fun getBoxSize(size: Int): Int = when (size) {
        3 -> 1
        9 -> 3
        16 -> 4
        else -> 0
    }

    private fun getBox(
        board: Array<Array<String>>,
        rowStart: Int,
        colStart: Int,
        boxSize: Int
    ): List<String> {
        val box = mutableListOf<String>()
        for (r in 0 until boxSize) {
            for (c in 0 until boxSize) {
                box.add(board[rowStart + r][colStart + c])
            }
        }
        return box
    }

    private fun validateGroup(
        group: List<String>,
        groupType: String,
        index: Int,
        size: Int,
        validChars: List<String>
    ): String? {
        val numbers = group.filter { it !in EMPTY_CELL_MARKERS }

        // Special handling for 1x1 Sudoku
        if (size == 1) {
            if (numbers.isNotEmpty() && numbers[0] != "1") {
                return "Invalid number for 1x1 Sudoku"
            }
            return null
        }

        // Check for invalid characters
        val invalidChars = numbers.filter { it !in validChars }
        if (invalidChars.isNotEmpty()) {
            return "$groupType $index contains invalid characters: ${invalidChars.joinToString()}"
        }

        // Check for numbers out of range
        when (size) {
            3, 9 -> {
                val outOfRange = numbers.filter { it.toInt() > size }
                if (outOfRange.isNotEmpty()) {
                    return "$groupType $index contains numbers larger than $size: ${outOfRange.joinToString()}"
                }
            }
            4 -> {
                val outOfRange = numbers.filter { it.toInt() > 4 }
                if (outOfRange.isNotEmpty()) {
                    return "$groupType $index contains numbers larger than 4: ${outOfRange.joinToString()}"
                }
            }
            16 -> {
                val outOfRange = numbers.filter {
                    when {
                        it.toIntOrNull() != null -> it.toInt() > 9
                        it in "A".."G" -> false
                        else -> true
                    }
                }
                if (outOfRange.isNotEmpty()) {
                    return "$groupType $index contains invalid numbers. For 16x16 Sudoku, numbers must be 1-9 or A-G: ${outOfRange.joinToString()}"
                }
            }
        }

        // Check for duplicates
        val duplicates = numbers.groupBy { it }.filter { it.value.size > 1 }.keys
        if (duplicates.isNotEmpty()) {
            return "$groupType $index contains duplicate numbers: ${duplicates.joinToString()}"
        }

        return null
    }
}
fun main() {
    // Test cases
    val testCases = listOf(
        // 4x4 Sudoku test cases
        Triple(
            "Valid 4x4 Sudoku",
            arrayOf(
                arrayOf("1", "2", "3", "4"),
                arrayOf("3", "4", "1", "2"),
                arrayOf("2", "1", "4", "3"),
                arrayOf("4", "3", "2", "1")
            ),
            true
        ),
        Triple(
            "Invalid 4x4 Sudoku (duplicate in row)",
            arrayOf(
                arrayOf("1", "2", "3", "4"),
                arrayOf("3", "4", "1", "2"),
                arrayOf("2", "1", "4", "4"),
                arrayOf("4", "3", "2", "1")
            ),
            false
        ),
        Triple(
            "Invalid 4x4 Sudoku (duplicate in column)",
            arrayOf(
                arrayOf("1", "2", "3", "4"),
                arrayOf("3", "4", "1", "2"),
                arrayOf("2", "1", "4", "3"),
                arrayOf("1", "3", "2", "4")
            ),
            false
        ),
        Triple(
            "Invalid 4x4 Sudoku (invalid number)",
            arrayOf(
                arrayOf("1", "2", "3", "5"),
                arrayOf("3", ".", "1", "2"),
                arrayOf("2", "1", "4", "."),
                arrayOf("4", "3", "2", "1")
            ),
            false
        ),
        Triple(
            "Valid 4x4 Sudoku with empty cells",
            arrayOf(
                arrayOf("1", "2", ".", "4"),
                arrayOf("3", "4", "1", "2"),
                arrayOf("2", "1", "4", "3"),
                arrayOf("4", "3", "2", "1")
            ),
            true
        ),
        //
        Triple(
            "Invalid 4x4 Sudoku (all empty)",
            arrayOf(
                arrayOf(".", ".", ".", "."),
                arrayOf(".", ".", ".", "."),
                arrayOf(".", ".", ".", "."),
                arrayOf(".", ".", ".", ".")
            ),
            false
        ),
        // 5x5 Sudoku test cases
        Triple(
            "Invalid Sudoku size (5x5)",
            arrayOf(
                arrayOf("1", "2", "3", "4", "5"),
                arrayOf("5", "4", "3", "2", "1"),
                arrayOf("2", "3", "1", "5", "4"),
                arrayOf("3", "1", "5", "4", "2"),
                arrayOf("4", "5", "2", "1", "3")
            ),
            false
        ),
        // 4x9 Sudoku test cases
        Triple(
            "Invalid Sudoku size (4x9)",
            arrayOf(
                arrayOf("1", "2", "3", "4"),
                arrayOf("5", "4", "3", "2"),
                arrayOf("2", "3", "1", "5"),
                arrayOf("3", "1", "5", "4"),
                arrayOf("4", "5", "2", "1"),
                arrayOf("1", "2", "3", "4"),
                arrayOf("5", "4", "3", "2"),
                arrayOf("2", "3", "1", "5"),
                arrayOf("3", "1", "5", "4"),
                arrayOf("4", "5", "2", "1")
            ),
            false
        ),
        // 9x9 Sudoku test cases
        Triple(
            "Valid 9x9 Sudoku",
            arrayOf(
                arrayOf("5", "3", "4", "6", "7", "8", "9", "1", "2"),
                arrayOf("6", "7", "2", "1", "9", "5", "3", "4", "8"),
                arrayOf("1", "9", "8", "3", "4", "2", "5", "6", "7"),
                arrayOf("8", "5", "9", "7", "6", "1", "4", "2", "3"),
                arrayOf("4", "2", "6", "8", "5", "3", "7", "9", "1"),
                arrayOf("7", "1", "3", "9", "2", "4", "8", "5", "6"),
                arrayOf("9", "6", "1", "5", "3", "7", "2", "8", "4"),
                arrayOf("2", "8", "7", "4", "1", "9", "6", "3", "5"),
                arrayOf("3", "4", "5", "2", "8", "6", "1", "7", "9")
            ),
            true
        ),
        Triple(
            "Invalid 9x9 Sudoku (duplicate in box)",
            arrayOf(
                arrayOf("5", "3", "4", "6", "7", "8", "9", "1", "2"),
                arrayOf("6", "7", "2", "1", "9", "5", "3", "4", "8"),
                arrayOf("1", "9", "8", "3", "4", "2", "5", "6", "7"),
                arrayOf("8", "5", "9", "7", "6", "1", "4", "2", "3"),
                arrayOf("4", "2", "6", "8", "5", "3", "7", "9", "1"),
                arrayOf("7", "1", "3", "9", "2", "4", "8", "5", "6"),
                arrayOf("9", "6", "1", "5", "3", "7", "2", "8", "4"),
                arrayOf("2", "8", "7", "4", "1", "9", "6", "3", "5"),
                arrayOf("3", "4", "5", "2", "8", "6", "1", "7", "5")
            ),
            false
        ),
        Triple(
            "Invalid 9x9 Sudoku (invalid number)",
            arrayOf(
                arrayOf("5", "3", "4", "6", "7", "8", "9", "1", "2"),
                arrayOf("6", "7", "2", "1", "9", "5", "3", "4", "8"),
                arrayOf("1", "9", "8", "3", "4", "2", "5", "6", "7"),
                arrayOf("8", "5", "9", "7", "6", "1", "4", "2", "3"),
                arrayOf("4", "2", "6", "8", "5", "3", "7", "9", "1"),
                arrayOf("7", "1", "3", "9", "2", "4", "8", "5", "6"),
                arrayOf("9", "6", "1", "5", "3", "7", "2", "8", "4"),
                arrayOf("2", "8", "7", "4", "1", "9", "6", "3", "5"),
                arrayOf("3", "4", "5", "2", "8", "6", "1", "7", "0")
            ),
            false
        ),
        Triple(
            "Valid 9x9 Sudoku with empty cells",
            arrayOf(
                arrayOf("5", "3", ".", ".", "7", ".", ".", ".", "."),
                arrayOf("6", ".", ".", "1", "9", "5", ".", ".", "."),
                arrayOf(".", "9", "8", ".", ".", ".", ".", "6", "."),
                arrayOf("8", ".", ".", ".", "6", ".", ".", ".", "3"),
                arrayOf("4", ".", ".", "8", ".", "3", ".", ".", "1"),
                arrayOf("7", ".", ".", ".", "2", ".", ".", ".", "6"),
                arrayOf(".", "6", ".", ".", ".", ".", "2", "8", "."),
                arrayOf(".", ".", ".", "4", "1", "9", ".", ".", "5"),
                arrayOf(".", ".", ".", ".", "8", ".", ".", "7", "9")
            ),
            true
        ),
        // 16x16 Sudoku test cases
        Triple(
            "Valid 16x16 Sudoku",
            arrayOf(
                arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G"),
                arrayOf("4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3"),
                arrayOf("7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6"),
                arrayOf("A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1"),
                arrayOf("5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4"),
                arrayOf("8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7"),
                arrayOf("B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"),
                arrayOf("3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2"),
                arrayOf("6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5"),
                arrayOf("9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8"),
                arrayOf("C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"),
                arrayOf("D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C"),
                arrayOf("E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D"),
                arrayOf("F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E"),
                arrayOf("G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
            ),
            true
        ),
        Triple(
            "Invalid 16x16 Sudoku (duplicate in row)",
            arrayOf(
                arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G"),
                arrayOf("4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3"),
                arrayOf("7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6"),
                arrayOf("A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1"),
                arrayOf("5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4"),
                arrayOf("8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7"),
                arrayOf("B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"),
                arrayOf("3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2"),
                arrayOf("6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5"),
                arrayOf("9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8"),
                arrayOf("C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"),
                arrayOf("D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C"),
                arrayOf("E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D"),
                arrayOf("F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E"),
                arrayOf("G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "G")
            ),
            false
        ),
        Triple(
            "Invalid 16x16 Sudoku (invalid number)",
            arrayOf(
                arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G"),
                arrayOf("4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3"),
                arrayOf("7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6"),
                arrayOf("A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1"),
                arrayOf("5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4"),
                arrayOf("8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7"),
                arrayOf("B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"),
                arrayOf("3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2"),
                arrayOf("6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5"),
                arrayOf("9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8"),
                arrayOf("C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"),
                arrayOf("D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C"),
                arrayOf("E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D"),
                arrayOf("F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E"),
                arrayOf("G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "H")
            ),
            false
        ),
        Triple(
            "Valid 16x16 Sudoku with empty cells",
            arrayOf(
                arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G"),
                arrayOf("4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3"),
                arrayOf("7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6"),
                arrayOf("A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1"),
                arrayOf("5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4"),
                arrayOf("8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7"),
                arrayOf("B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"),
                arrayOf("3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2"),
                arrayOf("6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5"),
                arrayOf("9", "A", "B", "C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8"),
                arrayOf("C", "D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B"),
                arrayOf("D", "E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C"),
                arrayOf("E", "F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D"),
                arrayOf("F", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E"),
                arrayOf("G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", ".")
            ),
            false
        )
    )

    // Run test cases
    for ((description, board, expected) in testCases) {
        val (result, reason) = SudokuValidator.validate(board)
        println("Test: $description")
        println("Expected: $expected")
        println("Actual: $result")
        if (!result) {
            println("Reason: $reason")
        }
        println("****************************")
    }
}