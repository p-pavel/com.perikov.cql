lazy val `cql-grammar` = project
  .in(file("modules/grammar"))

lazy val `cql-grammar-validated` = project
  .in(file("modules/grammar-validated"))
  .dependsOn(`cql-grammar`)
  .settings(
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.12.0"
  )

lazy val tests = project
  .in(file("modules/tests"))
  .dependsOn(`cql-grammar`, `cql-grammar-validated`, `cql-datastax`, `cql-compiletime`)

lazy val `cql-datastax` = project
  .in(file("modules/datastax"))
  .dependsOn(`cql-grammar`)

lazy val `cql-compiletime` = project
  .in(file("modules/compiletime"))
  .dependsOn(`cql-grammar`, `cql-grammar-validated`)


lazy val cql = project
  .in(file("."))
  .aggregate(`cql-grammar`, `cql-grammar-validated`, `cql-datastax`, `cql-compiletime`, tests)