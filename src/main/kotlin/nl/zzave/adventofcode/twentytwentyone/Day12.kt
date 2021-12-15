package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day12.Node

typealias Edge = Pair<Node, Node>

object Day12 : TwentyTwentyOneProblem<Int> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Int {
        val graph = parseInput(input)
        logln("Graph:")
        logln(graph)

        val allRoutes = traverseGraph(graph)

        return allRoutes.count()
    }

    private fun traverseGraph(graph: Graph): List<List<Edge>> {
        // get start node
        val startNode = graph.nodes.find { it.isStart() } ?: error("No start node 😱")
        return traverseGraph(graph, startNode, emptyList())
    }

    private fun traverseGraph(graph: Graph, node: Node, path: List<Edge>): List<List<Edge>> {
        // find all options
        if (node.isEnd()) return listOf(path)

        val possibilities = graph.edges.filter { it.first == node }

        var resultingPaths = mutableListOf<List<Edge>>()
        possibilities.forEach {
            if (it.second.isLarge() || path.none { e -> e.first == it.second }) {
                val updatedPaths: List<List<Edge>?> = traverseGraph(graph, it.second, path + it)
                resultingPaths.addAll(updatedPaths.mapNotNull { p -> p })
            }
        }

        return resultingPaths


    }

    private fun traverseGraphWithDuplication(graph: Graph): List<List<Edge>> {
        // get start node
        val startNode = graph.nodes.find { it.isStart() } ?: error("No start node 😱")
        return traverseGraphWithDuplication(graph, startNode, emptyList())
    }


    private fun traverseGraphWithDuplication(graph: Graph, node: Node, path: List<Edge>): List<List<Edge>> {
        // find all options
        if (node.isEnd()) return listOf(path)

        val possibilities = graph.edges.filter { it.first == node }
        val logIndent = "\t".repeat(path.size)
        val hasASmallCaveBeenVisitedMultipleTimes = path
            .map { it.second }
            .filter { it.isSmall() }
            .also { debugln("${logIndent}Small caves visited: $it") }
            .let { it.size != it.toSet().size }

        val resultingPaths = mutableListOf<List<Edge>>()
        possibilities
            .also { debugln("${logIndent}Exploring possibilities for $path: $it") }
            .filter { !it.second.isStart() }
            .filter { it.second.isLarge() || path.none { e -> e.first == it.second } || !hasASmallCaveBeenVisitedMultipleTimes }
            .forEach {
                debugln("${logIndent}Found valid edge $it for $path")
                val updatedPaths: List<List<Edge>?> = traverseGraphWithDuplication(graph, it.second, path + it)
                val elements = updatedPaths.mapNotNull { p -> p }
                resultingPaths.addAll(elements)
            }

        debugln("${logIndent}From: $path, found complete paths (first 100): ${resultingPaths.take(100)}")
        return resultingPaths


    }


    private fun parseInput(input: List<String>): Graph {
        val nodes = mutableSetOf<Node>()
        val edges = mutableSetOf<Edge>()
        input.forEach {
            val (from, to) = it.trim().split("-")
            val fromNode = Node(from)
            nodes.add(fromNode)
            val toNode = Node(to)
            nodes.add(toNode)

            edges.add(fromNode to toNode)
            edges.add(toNode to fromNode)
        }

        return Graph(nodes, edges)
    }

    override fun solvePart2(input: List<String>): Int {
        val graph = parseInput(input)
        logln("Graph:")
        logln(graph)

        val allRoutes = traverseGraphWithDuplication(graph)

        return allRoutes.toSet().count()
    }


    data class Node(
        val value: String,
    ) {
        fun isSmall(): Boolean = value.lowercase() == value
        fun isLarge(): Boolean = value.uppercase() == value
        fun isEnd(): Boolean = value == "end"
        fun isStart(): Boolean = value == "start"

        override fun toString(): String {
            return value
        }
    }

    data class Graph(
        val nodes: Set<Node>,
        val edges: Set<Edge>
    )
}


fun main() {
    Day12.testSolution("day12-test.data", 19, 103)
    println("--------- NOW FOR REALS --------")
    Day12.runSolution("day12.data")

}
