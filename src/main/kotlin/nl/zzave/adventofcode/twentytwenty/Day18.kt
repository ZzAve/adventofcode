package nl.zzave.adventofcode.twentytwenty

object Day18 {
    private val homework: List<String> = getTwentyTwentyFile("day18.data")

    fun solve(homework: List<String> = Day18.homework): Pair<Long, Long> {
        val tokenized = homework.map {
            it.replace("(", " ( ")
                .replace(")", " ) ")
                .split(' ')
                .filter { it.trim().isNotEmpty() }
        }
        val simpleMathOutcomes = tokenized.map {
            solveExpression(it)
        }
        val simpleMathSum = simpleMathOutcomes.sum()
        println("Sum of homework solutions: $simpleMathSum")

        val advancedMathOutcomes = tokenized.map {
            solveExpression(it, true)
        }
        val advancedMathSum = advancedMathOutcomes.sum()
        println("Sum of homework solutions: $advancedMathSum")
        return simpleMathSum to advancedMathSum
    }


    // expression : number, a+b, c*d, a+(b*c)
    // plus :  expression + expression
    // multiply : expression * expression
    // a + b + c
    // sum + c
    // sum

    enum class Operation {
        PLUS,
        MULTIPLY
    }

    data class Expression(
        val first: Long = 0L,
        val second: Long = 0L,
        val op1: Operation = Operation.PLUS,
        var op2: Operation = Operation.PLUS,
        val withPrecedence: Boolean = false
    )

    private fun solveExpression(expression: List<String>, withPrecedence: Boolean = false): Long {
        var sum = mutableListOf<Expression>()

        var activeExpression = Expression(withPrecedence = withPrecedence)
        expression.forEach { i ->
            when (i) {
                "+" -> activeExpression.op2 = Operation.PLUS
                "*" -> activeExpression.op2 = Operation.MULTIPLY
                "(" -> {  //park expression and jump up
                    sum.add(activeExpression)
                    activeExpression = Expression(withPrecedence=withPrecedence)
                }
                ")" -> { //take last expression}

                    val upperExpression = sum.removeAt(sum.size - 1)
                    val finalSub = activeExpression.final()
                    activeExpression = upperExpression.apply(finalSub)
                }
                else -> { // is a number start a new expression
                    val value = i.toLong()
                    activeExpression = activeExpression.apply(value)
                }
            }
        }

        return activeExpression.final()
    }

    private fun Expression.final(): Long {
        return applyOperation(op1, first, second)
    }

    private fun Expression.apply(third: Long): Expression {
        return if (!withPrecedence){
            Expression(
                first = applyOperation(op1, first, second), op1 = op2, second = third,
                withPrecedence = withPrecedence
            )
        } else {
            // a + b * c ---> 0 + x
            when (op2) {
                Operation.PLUS -> {
                    Expression(
                        first = first,
                        op1 = op1,
                        second = applyOperation(op2, second, third),
                        withPrecedence = withPrecedence
                    )
                }
                Operation.MULTIPLY ->
                    Expression(
                        first = applyOperation(op1, first, second), op1 = op2, second = third,
                        withPrecedence = withPrecedence
                    )

            }
        }
    }

    private fun applyOperation(operation: Operation, left: Long, right: Long): Long {
        return when (operation) {
            Operation.PLUS -> left + right
            Operation.MULTIPLY -> left * right
        }
    }


}

fun main() {
    Day18.solve()
}