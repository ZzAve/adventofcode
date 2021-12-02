package nl.zzave.adventofcode.twentytwentyone


object Day2 {
    data class Instruction(
        val command: Command,
        val value: Int
    ) {
        enum class Command {
            FORWARD, DOWN, UP;
        }

        companion object {
            fun internalise(instructionsDto: List<String>): List<Instruction> =
                instructionsDto.map { internalise(it) }

            fun internalise(instructionDto: String): Instruction {
                return instructionDto.split(" ").let {
                    Instruction(
                        command = Command.valueOf(it[0].uppercase()),
                        value = it[1].toInt()
                    );
                }
            }
        }
    }

    data class Position(
        val horizontalPosition: Int,
        val depth: Int,
        val aim: Int
    )

    fun solvePart1(input: List<String>): Int {
        val instructions = Instruction.internalise(input)

        val endPosition =  instructions.fold(0 to 0){ acc, cur -> this.updatePosition(cur,acc) }
        println("Endposition is $endPosition")
        return endPosition.first * endPosition.second

    }

    private fun updatePosition(instruction: Instruction, acc: Pair<Int, Int>): Pair<Int, Int> =
        when (instruction.command){
               Instruction.Command.UP -> acc.first to ( acc.second - instruction.value)
               Instruction.Command.DOWN -> acc.first to ( acc.second + instruction.value)
               Instruction.Command.FORWARD -> (acc.first + instruction.value to acc.second)
           }

        private fun updatePosition(instruction: Instruction, position: Position): Position {
        return when (instruction.command){
            Instruction.Command.UP -> position.copy(aim = position.aim - instruction.value)
            Instruction.Command.DOWN -> position.copy(aim = position.aim + instruction.value)
            Instruction.Command.FORWARD -> position.copy(
                horizontalPosition = position.horizontalPosition + instruction.value,
                depth = position.depth + position.aim * instruction.value

            )
        }

    }

    fun solvePart2(input: List<String>): Long {
        val instructions = Instruction.internalise(input)

        val endPosition =  instructions.fold(Position(0,0,0)) { position, instruction ->
            updatePosition(instruction, position)
        }
        println("Endposition is $endPosition")
        return endPosition.horizontalPosition.toLong() * endPosition.depth

    }
}

fun main() {
    val input: List<String> = getTwentyTwentyOneFile("day2.data")
    val solvePart1 = Day2.solvePart1(input)
    println(solvePart1)

    val solvePart2 = Day2.solvePart2(input)
    println(solvePart2)
}
