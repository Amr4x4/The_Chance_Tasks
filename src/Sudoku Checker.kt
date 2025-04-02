// Test cases
fun testFun() {
    val testCases = listOf(
        arrayOf(
            arrayOf("5", "3", "-", "-"),
            arrayOf("6", "-", "-", "3"),
            arrayOf("-", "9", "8", "-"),
            arrayOf("-", "-", "-", "-"),
        ) to true,
        arrayOf(
            arrayOf("5", "3", "5", "-"),
            arrayOf("6", "-", "-", "3"),
            arrayOf("-", "9", "8", "-"),
            arrayOf("-", "-", "-", "-"),
        ) to false,
    )
    for ((index, testCase) in testCases.withIndex()) {
        val (board, expected) = testCase
        val result = sudokuChecker(board.size, board)
        println("Test #$index: ${if (result == expected) "Passed" else "Failed"}")
    }
}

fun main(){
    testFun()
    println("Enter the num of test cases:")
    val numOfTestCases = readlnOrNull()?.toInt()
    if(numOfTestCases != null){
       val results = mutableListOf<Boolean>()
        for ( i in 0..<numOfTestCases){
            println("Enter your sudoku size")
            val size = readlnOrNull()!!.toInt()
            println("Write the Sudoku Row By Row")
            val elements = inputElements(size)
            println("Your Sudoku is: ")
            printElements(elements)
            results.add(sudokuChecker(size, elements))
        }
       println(results)
    } else{
      println("Your input must be Integer(1,2,3....).")
    }
}

fun inputElements(size: Int): Array<Array<String>> {
    val twoDimensionalArray = Array(size) { Array(size) { "" } }
    for (i in 0..<size) {
        for (j in 0..<size) {
            twoDimensionalArray[i][j] = readlnOrNull().toString()
        }
    }
    return twoDimensionalArray
}
fun printElements(elements:Array<Array<String>>) {
    for (row in elements) {
        println(row.joinToString(" "))
    }
}

fun sudokuChecker(size: Int, elements: Array<Array<String>>): Boolean {
    fun isValidGroup(group: List<String>): Boolean {
        val nums = group.filter { it != "-" }
        return nums.size == nums.toSet().size
    }
    for (row in elements) {
        if (!isValidGroup(row.toList())) return false
    }
    for (col in 0..<size) {
        val column = List(size) { row -> elements[row][col] }
        if (!isValidGroup(column)) return false
    }
    val boxSize = Math.sqrt(size.toDouble()).toInt()
    for (rowStart in 0..<size step boxSize) {
        for (colStart in 0..<size step boxSize) {
            val box = mutableListOf<String>()
            for (r in 0..<boxSize) {
                for (c in 0..<boxSize) {
                    box.add(elements[rowStart + r][colStart + c])
                }
            }
            if (!isValidGroup(box)) return false
        }
    }

    return true
}


