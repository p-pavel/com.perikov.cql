

val `cql-grammar` = project
  .in(file("modules/grammar"))

val `cql-grammar-verified` = project
  .in(file("modules/grammar-verified"))
  .dependsOn(`cql-grammar`)

val `cql-datastax` = project
  .in(file("modules/datastax"))
  .dependsOn(`cql-grammar`)

val `cql-compiletime` = project
  .in(file("modules/compiletime"))
  .dependsOn(`cql-grammar`, `cql-grammar-verified`)

val `cql-tests` = project
  .in(file("modules/tests"))
  .dependsOn(`cql-grammar`, `cql-grammar-verified`, `cql-datastax`, `cql-compiletime`)

val cql = project
  .in(file("."))
  .aggregate(`cql-grammar`, `cql-grammar-verified`, `cql-datastax`, `cql-compiletime`)
