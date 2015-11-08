package cats
package tests

import algebra.laws.OrderLaws

import cats.data.StreamingT
import cats.laws.discipline.{CoflatMapTests, MonadCombineTests, SerializableTests}
import cats.laws.discipline.arbitrary._

class StreamingTTests extends CatsSuite {

  checkAll("StreamingT[Eval, ?]", MonadCombineTests[StreamingT[Eval, ?]].monad[Int, Int, Int])
  checkAll("StreamingT[Eval, ?]", CoflatMapTests[StreamingT[Eval, ?]].coflatMap[Int, Int, Int])
  checkAll("StreamingT[Eval, Int]", OrderLaws[StreamingT[Eval, Int]].order)
  checkAll("Monad[StreamingT[Eval, ?]]", SerializableTests.serializable(Monad[StreamingT[Eval, ?]]))

  checkAll("StreamingT[Option, ?]", MonadCombineTests[StreamingT[Option, ?]].monad[Int, Int, Int])
  checkAll("StreamingT[Option, ?]", CoflatMapTests[StreamingT[Option, ?]].coflatMap[Int, Int, Int])
  checkAll("StreamingT[Option, Int]", OrderLaws[StreamingT[Option, Int]].order)
  checkAll("Monad[StreamingT[Option, ?]]", SerializableTests.serializable(Monad[StreamingT[Option, ?]]))

  checkAll("StreamingT[List, ?]", MonadCombineTests[StreamingT[List, ?]].monad[Int, Int, Int])
  checkAll("StreamingT[List, ?]", CoflatMapTests[StreamingT[List, ?]].coflatMap[Int, Int, Int])
  checkAll("StreamingT[List, Int]", OrderLaws[StreamingT[List, Int]].order)
  checkAll("Monad[StreamingT[List, ?]]", SerializableTests.serializable(Monad[StreamingT[List, ?]]))
}

class SpecificStreamingTTests extends CatsSuite {

  type S[A] = StreamingT[List, A]

  def cons[A](a: A, fs: List[S[A]]): S[A] = StreamingT.cons(a, fs)
  def wait[A](fs: List[S[A]]): S[A] = StreamingT.wait(fs)
  def empty[A]: S[A] = StreamingT.empty[List, A]

  test("counter-example #1"){
    val fa: S[Boolean] =
      cons(true, List(cons(true, List(empty)), empty))

    def f(b: Boolean): S[Boolean] =
      if (b) cons(false, List(cons(true, List(empty))))
      else empty

    def g(b: Boolean): S[Boolean] =
      if (b) empty
      else cons(true, List(cons(false, List(empty)), cons(true, List(empty))))

    val x = fa.flatMap(f).flatMap(g)
    val y = fa.flatMap(a => f(a).flatMap(g))
    x should === (y)
  }
}
