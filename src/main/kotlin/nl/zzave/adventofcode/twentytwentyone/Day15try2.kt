package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day15try2.Node
import nl.zzave.adventofcode.twentytwentyone.Day15try2.Edge
import java.util.*


typealias RiskGraph = Pair<Set<Node>, Set<Edge>>

object Day15try2 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Long {
//        return 40L
        val graph = parseToGraph(input)

        prettyPrint(graph.second)
        val topLeft: Node =
            graph.first.first { n ->
                n.x == graph.first.minOf { it.x } &&
                        n.y == graph.first.minOf { it.y }
            }

        val bottomRight =
            graph.first.first { n ->
                n.x == graph.first.maxOf { it.x } &&
                        n.y == graph.first.maxOf { it.y }
            }

        return dijkstra(graph, topLeft, bottomRight)
    }

    /**
     * function dijkstra(G, S)
    for each vertex V in G
    distance[V] <- infinite
    previous[V] <- NULL
    If V != S, add V to Priority Queue Q
    distance[S] <- 0

    while Q IS NOT EMPTY
    U <- Extract MIN from Q
    for each unvisited neighbour V of U
    tempDistance <- distance[U] + edge_weight(U, V)
    if tempDistance < distance[V]
    distance[V] <- tempDistance
    previous[V] <- U
    return distance[], previous[]
     */
    private fun dijkstra(graph: RiskGraph, source: Node, target: Node): Long {
        val (nodes, edges) = graph
        val distance = mutableMapOf<Node, Long>()
        val previous = mutableMapOf<Node, Node>()

        val priorityQueue = PriorityQueue<Pair<Node, Long>>(compareBy { it.second })
        nodes.forEach {
            distance[it] = Long.MAX_VALUE
            priorityQueue.add(it to Long.MAX_VALUE)
        }


        distance[source] = 0
        priorityQueue.remove(source to Long.MAX_VALUE)
        priorityQueue.offer(source to 0)


        val edgesByFrom: Map<Node, List<Edge>> = edges.groupBy { it.from }
        while (priorityQueue.isNotEmpty()) {
            val (u: Node, dist: Long) = priorityQueue.remove()

            // Early return if target is reached!
            if (u == target) {
                var x = previous[u]
                val list = mutableListOf<Node>()
                list.add(target)
                while (x != null) {
                    list.add(x)
                    x = previous[x]

                }
                logln("Path: ${list.reversed()}. size: ${list.size}")
                val usedEdges = list
                    .reversed()
                    .zipWithNext { a, b -> edges.first { it.from == a && it.to == b } }
                logln("Edges: $usedEdges: size: ${usedEdges.size}, sum: ${usedEdges.sumOf { it.weight }}")

                return dist
            }

            (edgesByFrom[u] ?: error("You've reached Nirvana, but are not allowed in :( "))
                .filter { priorityQueue.contains(it.to to distance[it.to]) }
                .forEach {
                    val alt = dist + it.weight
                    val currentDist = distance[it.to]
                    if (currentDist == null || alt < currentDist) {
                        priorityQueue.remove(it.to to currentDist)
                        priorityQueue.offer(it.to to alt)
                        distance[it.to] = alt
                        previous[it.to] = it.from
                    }
                }

            if (priorityQueue.size % 100 == 0) {
                logln("${priorityQueue.size}\t" + "=".repeat(priorityQueue.size / 100))
            }
        }

        error("There is no path from $source to $target")
    }

    private fun parseToGraph(input: List<String>, magicRepetitions: Int = 1): Pair<Set<Node>, Set<Edge>> {
        check(input.size == input.first().length)
        check(input.size == input.last().length)

        val nodes: Set<Node> = input.flatMapIndexed { yIndex, rowValue ->
            rowValue.flatMapIndexed { xIndex, value ->
                val foundNodes = mutableListOf<Node>()
                repeat(magicRepetitions) { rowRepeat ->
                    repeat(magicRepetitions) { colRepeat ->
                        foundNodes.add(Node(xIndex + input.size * colRepeat, yIndex + input.size * rowRepeat))
                    }
                }

                foundNodes
            }
        }.toSet()

        val edges = mutableSetOf<Edge>()
        for (x in 0 until input.size * magicRepetitions) {
            for (y in 0 until input.size * magicRepetitions) {
                val increase = x / input.size + y / input.size
                //right
                if (x + 1 < input.size * magicRepetitions) {
                    val increase1 = (x + 1) / input.size + y / input.size
                    val weight = input[y % input.size][(x + 1) % input.size].digitToInt() + increase1
                    val rightEdge = Edge(
                        Node(x, y),
                        Node(x + 1, y),
                        if (weight > 9) weight % 9 else weight
                    )

                    val weight2 = input[y % input.size][x % input.size].digitToInt() + increase
                    val reversedRightEdge =
                        Edge(
                            Node(x + 1, y),
                            Node(x, y),
                            if (weight2 > 9) weight2 % 9 else weight2
                        )
                    edges.add(rightEdge)
                    edges.add(reversedRightEdge)
                }
                //down
                if (y + 1 < input.size * magicRepetitions) {
                    val increase3 = (y + 1) / input.size + x / input.size
                    val weight3 = input[(y + 1) % input.size][x % input.size].digitToInt() + increase3
                    val downEdge =
                        Edge(
                            Node(x, y),
                            Node(x, y + 1),
                            if (weight3 > 9) weight3 % 9 else weight3
                        )
                    val weight4 = input[y % input.size][x % input.size].digitToInt() + increase
                    val reversedDownEdge =
                        Edge(
                            Node(x, y + 1),
                            Node(x, y),
                            if (weight4 > 9) weight4 % 9 else weight4
                        )
                    edges.add(downEdge)
                    edges.add(reversedDownEdge)
                }
            }
        }

        return nodes to edges

    }


    override fun solvePart2(input: List<String>): Long {
        val graph = parseToGraph(input, 5)

        if (debugMode) prettyPrint(graph.second, padStart = 1)
        val minOf = graph.first.minOf { it.x }
        val minOf1 = graph.first.minOf { it.y }
        val topLeft: Node =
            graph.first.first { n ->
                n.x == minOf && n.y == minOf1
            }

        val maxOf = graph.first.maxOf { it.x }
        val maxOf1 = graph.first.maxOf { it.y }
        val bottomRight =
            graph.first.first { n ->
                n.x == maxOf && n.y == maxOf1
            }

        logln("Running dijkstra....")
        return dijkstra(graph, topLeft, bottomRight)
    }

    data class Node(
        val x: Int,
        val y: Int
    ) {
        override fun toString(): String = "($x,$y)"
    }

    data class Edge(
        val from: Node,
        val to: Node,
        val weight: Int
    ) {
        fun reversed(): Edge = Edge(to, from, weight)
    }


    fun prettyPrint(
        coords: Set<Edge>,
        padding: Char = ' ',
        padStart: Int = 1
    ) {
        val minX = coords.minOf { it.to.x }
        val maxX = coords.maxOf { it.to.x }
        val minY = coords.minOf { it.to.y }
        val maxY = coords.maxOf { it.to.y }
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print(
                    coords.firstOrNull { it.to.x == x && it.to.y == y }?.weight.let { it ?: "X" }.toString()
                        .padStart(padStart, padding)
                )
            }
            println("")
        }

        println("")
    }

}


fun main() {
    Day15try2.testSolution("day15-test.data", 40, 315)
    println("--------- NOW FOR REALS --------")
    Day15try2.runSolution("day15.data")
}
