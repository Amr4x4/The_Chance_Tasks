fun isValidIPv4(ip: String): Boolean {
    // Split the IP address by dots
    val parts = ip.split(".")

    // Check if we have exactly 4 parts
    if (parts.size != 4) return false

    // Check each part
    for (part in parts) {
        // Check if part is empty
        if (part.isEmpty()) return false

        // Check if part contains only digits
        if (!part.all { it.isDigit() }) return false

        // Check for leading zeros
        if (part.length > 1 && part[0] == '0') return false

        // Convert to number and check range
        val num = part.toIntOrNull() ?: return false
        if (num < 0 || num > 255) return false
    }

    return true
}

fun main() {
    val testCases = listOf(
        // Valid IPv4 cases
        "0.0.0.0" to true,
        "255.255.255.255" to true,
        "192.168.1.1" to true,
        "127.0.0.1" to true,
        "1.2.3.4" to true,
        "10.20.30.40" to true,
        "172.16.254.1" to true,

        // Leading zeros
        "01.2.3.4" to false,
        "192.168.001.1" to false,
        "0.01.0.0" to false,

        // Numbers out of range
        "256.0.0.1" to false,
        "192.300.1.1" to false,
        "999.999.999.999" to false,

        // Incorrect dot placement
        "192.168.1" to false,
        "192.168.1.1.1" to false,
        "192168.1.1" to false,

        // Non-numeric characters
        "192.168.1.a" to false,
        "hello.world.test" to false,
        "123.45.67.89abc" to false,

        // Empty or spaces
        "" to false,
        " " to false,
        " . . . " to false
    )

    // Run tests
    for ((ip, expected) in testCases) {
        val result = isValidIPv4(ip)
        println("IP: $ip")
        println("Expected: $expected")
        println("Actual: $result")
        println("Test ${if (result == expected) "passed" else "failed"}")
        println()
    }
}