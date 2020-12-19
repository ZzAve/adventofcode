package nl.zzave.adventofcode.twentytwenty

import java.io.File

const val BIRTH_YEAR = "byr"
const val ISSUE_YEAR = "iyr"
const val EXPIRATION_YEAR = "eyr"
const val HEIGHT = "hgt"
const val HAIR_COLOR = "hcl"
const val EYE_COLOR = "ecl"
const val PASSPORT_ID = "pid"

object Day4 {
    private val passportFields = setOf(
        BIRTH_YEAR,
        ISSUE_YEAR,
        EXPIRATION_YEAR,
        HEIGHT,
        HAIR_COLOR,
        EYE_COLOR,
        PASSPORT_ID,
//        "cid" // optional
    )

    private val passportBatch = File("./src/main/resources/day4.data").readLines()

    fun main(args: Array<String>) {
        println("Hello Day 4!")

        val passports = getPassports()
        println("Total Passports $passports")
        println("Total Passports ${passports.size}")

        val validPassports = passports.mapNotNull {
            getIfValidPassword(it)
        }

        val nrOfValidPasswords = validPassports.size

        println("Valid passports: $nrOfValidPasswords")

        val strictValidPassports = validPassports.map {
            isStrictValidPassword(it)
        }.count { it }

        println("Strict Valid passports: $strictValidPassports")
    }

    private fun getIfValidPassword(passport: Map<String, String>): Map<String, String>? {
        // should contain all 7 fields (don't care about the crazy id (cid) )
        var isValidPassword = passportFields.all { passport.contains(it) }

        return if (isValidPassword) passport else null;
    }

    private fun isStrictValidPassword(passport: Map<String, String>): Boolean =
        isValidBirthYear(passport[BIRTH_YEAR]) &&
                isValidIssueYear(passport[ISSUE_YEAR]) &&
                isValidExpirationYear(passport[EXPIRATION_YEAR]) &&
                isValidHeight(passport[HEIGHT]) &&
                isValidHairColor(passport[HAIR_COLOR]) &&
                isValidEyeColor(passport[EYE_COLOR]) &&
                isValidPassportId(passport[PASSPORT_ID])

    private fun String?.toSafeInt() = try {
        this?.toInt() ?: -1
    } catch (_: Exception) {
        -1
    }

    private fun isValidBirthYear(year: String?) = year.toSafeInt() in 1920..2002
    private fun isValidIssueYear(year: String?) = year.toSafeInt() in 2010..2020
    private fun isValidExpirationYear(year: String?) = year.toSafeInt() in 2020..2030

    private fun isValidHeight(height: String?): Boolean {
        val unit = height?.substring(height.length - 2)
        val value = height?.substring(0, height.length - 2).toSafeInt()
        return when (unit) {
            "cm" -> value in 150..193
            "in" -> value in 59..76
            else -> false
        }
    }

    private fun isValidHairColor(hairColor: String?) =
        hairColor != null &&
            hairColor
                .substring(1)
                .matches(Regex("^#[0-9a-f]{6}$"))


    private val validEyeColors = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    private fun isValidEyeColor(eyeColor: String?) = validEyeColors.contains(eyeColor)

    private fun isValidPassportId(passportId: String?) =
        passportId?.matches(Regex("^[0-9]{9}$")) ?: false

    private fun getPassports(): MutableList<Map<String, String>> {
        // split batch to individual passports
        // passport is Map<key,value> ?
        val passports = mutableListOf<Map<String, String>>()
        var currentPassport = mutableMapOf<String, String>()
        for (line in passportBatch) {
            when {
                line.isEmpty() -> {
                    //Next
                    passports.add(currentPassport)
                    currentPassport = mutableMapOf()
                }
                else -> {
                    // add key value pairs to passport
                    line.split(' ').forEach {
                        val (key, value) = it.split(":")
                        currentPassport[key] = value
                    }
                }
            }
        }

        if (currentPassport.isNotEmpty()) {
            passports.add(currentPassport)
        }

        return passports
    }

}

fun main(args: Array<String>) {
    Day4.main(args)
}