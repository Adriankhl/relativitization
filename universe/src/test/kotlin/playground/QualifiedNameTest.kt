package playground

import kotlin.test.Test

private open class A {
    fun getName() {
        println(this::class.qualifiedName)
    }
}

private class B : A()

internal class QualifiedNameTest {
    @Test
    fun testInheritance() {
        val a = A()
        val b = B()
        println(a.getName())
        println(b.getName())
    }

}