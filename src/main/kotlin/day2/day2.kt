import java.io.File

var validSledPasswords = 0
var validTobogganPasswords = 0

data class PasswordPolicy(
    val first: Int,
    val second: Int,
    val letter: Char?,
    val password: String
)

fun main(args: Array<String>) {
    println("Hello Day 2!")

    try {
        File("./src/main/resources/day2.data").forEachLine { input ->
            Regex("([0-9]+)-([0-9]+) ([A-z]): (.*)").find(input)
                ?.let { matchResult ->

                    val passwordPolicy = PasswordPolicy(
                        first = matchResult.groups[1]?.value?.toInt() ?: -1,
                        second = matchResult.groups[2]?.value?.toInt() ?: -1,
                        letter = matchResult.groups[3]?.value?.get(0),
                        password = matchResult.groups[4]?.value ?: ""
                    )
                    determineValidSledRentalPassword(passwordPolicy, input)
                    determineValidTobogganPassword(passwordPolicy, input)
                }
        }
    } catch (e: Exception) {
        println(e)
    }

    println("Matching sledPasswords: $validSledPasswords")
    println("Matching tobogganPasswords: $validTobogganPasswords")

}


fun determineValidTobogganPassword(passwordPolicy: PasswordPolicy, input: String) {
//    println("-- determineValidTobogganPassword --")
    val (first, second, letter, password) = passwordPolicy

    val isValidPassword = ((password[first - 1] == letter) xor (password[second - 1] == letter))
    if (isValidPassword) validTobogganPasswords++

    println("$input --> $isValidPassword")
}

private fun determineValidSledRentalPassword(passwordPolicy: PasswordPolicy, input: String) {
//    println("-- determineValidSledRentalPassword --")
    val (min, max, letter, password) = passwordPolicy
    val count: Int = password.count { char -> char == letter }

    val isValidPassword = count in min..max
    if (isValidPassword) validSledPasswords++

    println("$input --> $isValidPassword")
}
