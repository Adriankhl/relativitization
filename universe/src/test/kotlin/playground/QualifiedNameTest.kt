package playground

import kotlin.test.Ignore

private open class A {
    fun getName() {
        println(this::class.qualifiedName)
    }
}

private class B : A()

internal class QualifiedNameTest {
    @Ignore
    fun testInheritance() {
        val a = A()
        val b = B()
        println(a.getName())
        println(b.getName())
    }

}