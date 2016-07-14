#!/usr/bin/env amm

println(shellSession)

// requires .ammonite/predef.scala

// repl.prompt() = ">"
// repl.colors() = ammonite.repl.Colors.BlackWhite

import ammonite.ops._
// import ammonite.ops.ImplicitWd._

case class Git_project(url:String,clone_args:String="",make:String="cd .nix && nix-build")

val gh0 = "https://github.com"

val sibylfs_src = Git_project(url= s"$gh0/sibylfs/sibylfs_src.git --branch 2016-07-14_fix_nix",make="cd sibylfs_src && nix-build")
val gh = sibylfs_src

// val all_out = tmp()


def git_clone(s:String) = {
  // val td = tmp.dir()
  // make sure .git is not picked up by nix
  val x = %%("git","clone","--depth=1",s.split(' ').toList)
  %%("bash","-c","rm -rf */.git")
  x
}

def make(gh:Git_project) = {

  println(gh)

  val dir = tmp.dir()

  cd! dir
  println("In directory: "+dir)

  val gc = git_clone(s"${gh.url}")

  val gd = %%("bash","-c",gh.make)(dir)

  println(gc.out.string+gc.err.string+gd.out.string+gd.err.string)

}

def make_e3_p3() = {

  val dir = tmp.dir()

  cd! dir
  println("In directory: "+dir)

  {
    val e3 = s"$gh0/tomjridge/e3.git"

    val gc1 = git_clone(e3)

    val p3 = s"$gh0/tomjridge/p3.git"

    val gc = git_clone(p3)

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

    val gc2 = git_clone(p1)

    val e3 = s"$gh0/tomjridge/e3.git"

    val gc1 = git_clone(e3)

    val p4 = s"$gh0/tomjridge/p4.git"

    val gc = git_clone(p4)

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
//   //   Future { make(Git_project(url=s"$gh0/tomjridge/p1.git",make="cd p1 && make"))},
//   //   Future { make(Git_project(url=s"$gh0/tomjridge/p3.git",make="cd p3 && make"))},
//   //   Future { make(Git_project(url=s"$gh0/tomjridge/p4.git",make="cd p4 && make"))}
//   )
//   println("ab")
//   val y = Future { make(sibylfs_src)}
//   Await.result(y,Duration.Inf)
}

try {
  make(sibylfs_src)

  make(Git_project(url=s"$gh0/tomjridge/p1.git",make="cd p1 && nix-build")) // FIXME keeps on being rebuilt; this seems to happen on every new checkout;

  make(Git_project(url=s"$gh0/tomjridge/e3.git",make="cd e3 && nix-build"))

  make(Git_project(
    url=s"$gh0/tomjridge/simple_earley.git",
    make="cd simple_earley && nix-build"))

  make(Git_project(url=s"/tmp/l/scala_btree",make="cd scala_btree && nix-build"))
  // make_e3_p3 not working 2016-06-10
  // make_e3_p4 not working 2016-06-10

// todo: example_grammars; mycsv ocaml_btree? p5_scala simple_earley tst
} catch {
  case e => println(e)
}
//println(read(all_out))
println("End")
System.exit(1)
