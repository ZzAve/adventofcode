package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day19Test {

    @Test
    fun x() {
        val rules = listOf(
            "0: 4 1 5",
            "1: 2 3 | 3 2",
            "2: 4 4 | 5 5",
            "3: 4 5 | 5 4",
            "4: \"a\"",
            "5: \"b\""
        )
        val messages = listOf(
            "ababbb",
            "bababa",
            "abbbab",
            "aaabbb",
            "aaaabbb"
        )
        val (part1, part2) = Day19.solve2(messages, rules)
        assertEquals(2, part1)
    }

    @Test
    fun y() {
        val rules = listOf(
            "42: 9 14 | 10 1",
            "9: 14 27 | 1 26",
            "10: 23 14 | 28 1",
            "1: \"a\"",
            "11: 42 31",
            "5: 1 14 | 15 1",
            "19: 14 1 | 14 14",
            "12: 24 14 | 19 1",
            "16: 15 1 | 14 14",
            "31: 14 17 | 1 13",
            "6: 14 14 | 1 14",
            "2: 1 24 | 14 4",
            "0: 8 11",
            "13: 14 3 | 1 12",
            "15: 1 | 14",
            "17: 14 2 | 1 7",
            "23: 25 1 | 22 14",
            "28: 16 1",
            "4: 1 1",
            "20: 14 14 | 1 15",
            "3: 5 14 | 16 1",
            "27: 1 6 | 14 18",
            "14: \"b\"",
            "21: 14 1 | 1 14",
            "25: 1 1 | 1 14",
            "22: 14 14",
            "8: 42",
            "26: 14 22 | 1 20",
            "18: 15 15",
            "7: 14 5 | 1 21",
            "24: 14 1",
        )
        val messages = listOf(
            "abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa",
            "bbabbbbaabaabba",
            "babbbbaabbbbbabbbbbbaabaaabaaa",
            "aaabbbbbbaaaabaababaabababbabaaabbababababaaa",
            "bbbbbbbaaaabbbbaaabbabaaa",
            "bbbababbbbaaaaaaaabbababaaababaabab",
            "ababaaaaaabaaab",
            "ababaaaaabbbaba",
            "baabbaaaabbaaaababbaababb",
            "abbbbabbbbaaaababbbbbbaaaababb",
            "aaaaabbaabaaaaababaa",
            "aaaabbaaaabbaaa",
            "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa",
            "babaaabbbaaabaababbaabababaaab",
            "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba",
        )
        val (part1, part2) = Day19.solve2(messages, rules)
        assertEquals(3, part1)
        assertEquals(12, part2)
    }

    @Test
    fun x2(){
        val rules = listOf(
//            "0: \"aa\"",
//            "0: 1",
            "0: 1 2 3 | 1 1 1",
//            "0: 1| 1 0",
            "1: \"a\"",
            "2: \"b\"",
            "3: \"c\"",
        )
        val messages = listOf(
//            "aaabb",
            "aabb",
            "ab",
            "aaa",
            "aab",
            "abc",
            "abcc",
            "acc"
        )
        val (part1, part2) = Day19.solve2(messages, rules)
//        assertEquals(2, part1)
    }
}