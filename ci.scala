#!/usr/bin/env amm

println(shellSession)

// requires .ammonite/predef.scala

// repl.prompt() = ">"
// repl.colors() = ammonite.repl.Colors.BlackWhite

import ammonite.ops._
// import ammonite.ops.ImplicitWd._

case class Gh_project(url:String,make:String="cd .nix && nix-build")

val gh0 = "https://github.com"

val sibylfs_src = Gh_project(url= s"$gh0/sibylfs/sibylfs_src.git",make="cd sibylfs_src && nix-build")
val gh = sibylfs_src

// val all_out = tmp()


def make(gh:Gh_project) = {

  println(gh)

  val dir = tmp.dir()

  cd! dir
  println("In directory: "+dir)

  val gc = %%("git","clone",s"${gh.url}")

  val gd = %%("bash","-c",gh.make)(dir)

  println(gc.out.string+gc.err.string+gd.out.string+gd.err.string)

}


def make_e3_p3() = {

  val dir = tmp.dir()

  cd! dir
  println("In directory: "+dir)

  {
    val e3 = s"$gh0/tomjridge/e3.git"

    val gc1 = %%("git","clone",e3)

    val p3 = s"$gh0/tomjridge/p3.git"

    val gc = %%("git","clone",p3)

    val gd = %%("bash","-c","cd p3 && nix-build")(dir)

    println(
      gc1.out.string+gc1.err.string+
        gc.out.string+gc.err.string+
        gd.out.string+gd.err.string)

  }


}



def make_e3_p4() = {

  val dir = tmp.dir()

  cd! dir
  println("In directory: "+dir)

  {
    val p1 = s"$gh0/tomjridge/p1.git"

    val gc2 = %%("git","clone",p1)

    val e3 = s"$gh0/tomjridge/e3.git"

    val gc1 = %%("git","clone",e3)

    val p4 = s"$gh0/tomjridge/p4.git"

    val gc = %%("git","clone",p4)

    val gd = %%("bash","-c","cd p4 && nix-build")(dir)

    println(
      gc2.out.string+gc2.err.string+
      gc1.out.string+gc1.err.string+
        gc.out.string+gc.err.string+
        gd.out.string+gd.err.string)

  }


}


println("Start")
{ // FIXME couldn't get concurrency working
  import scala.concurrent._
  import scala.concurrent.duration._
  import ExecutionContext.Implicits.global
  import scala.concurrent.forkjoin._
// 
//   val x = List(
//     Future { make(sibylfs_src)}
//   //   Future { make(Gh_project(url=s"$gh0/tomjridge/p1.git",make="cd p1 && make"))},
//   //   Future { make(Gh_project(url=s"$gh0/tomjridge/p3.git",make="cd p3 && make"))},
//   //   Future { make(Gh_project(url=s"$gh0/tomjridge/p4.git",make="cd p4 && make"))}
//   )
//   println("ab")
//   val y = Future { make(sibylfs_src)}
//   Await.result(y,Duration.Inf)
}

try {
  make(sibylfs_src)

  make(Gh_project(url=s"$gh0/tomjridge/p1.git",make="cd p1 && nix-build")) // FIXME keeps on being rebuilt; this seems to happen on every new checkout;

  make(Gh_project(url=s"$gh0/tomjridge/e3.git",make="cd e3 && nix-build"))
  // make_e3_p3 not working 2016-06-10
  // make_e3_p4 not working 2016-06-10

// todo: example_grammars; mycsv ocaml_btree? p5_scala simple_earley tst
} catch {
  case e => println(e)
}
//println(read(all_out))
println("End")
System.exit(1)
