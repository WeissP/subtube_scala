import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import mill._, scalalib._

class Deps(org: String, version: String) {
  def apply(pkgs: String*) = {
    pkgs.map(pkg => ivy"${org}::${pkg}::${version}")
  }
}

object http4s extends Deps("org.http4s", "0.23.24")
object typelevel extends Deps("org.typelevel", "2.6.0")
object tapir extends Deps("com.softwaremill.sttp.tapir", "1.9.5")
object iron extends Deps("io.github.iltotore", "2.3.0")
object ciris extends Deps("is.cir", "3.5.0")

object backend extends ScalaModule with ScalafixModule {
  def mainClass = Some("com.jb.Main")

  def exp(args: Task[Args] = T.task(Args())) = T.command {
    super.runMain("com.jb.Experiment")()
  }

  def migrate() = T.command {
    println("Start migrating...")
    super.runMain("com.jb.migrate.Main")()
  }

  def scalaVersion = "3.3.1"
  def scalacOptions = Seq("-source:future")
  def folkArgs = "-Dotel.java.global-autoconfigure.enabled=true"
  def ivyDeps = Agg.from(
    List(
      ivy"ch.qos.logback:logback-classic::1.4.14",
      ivy"org.tpolecat::skunk-core::1.1.0-M2",
      ivy"org.typelevel::otel4s-java:0.4.0",
      ivy"io.opentelemetry:opentelemetry-exporter-otlp:1.33.0",
      ivy"io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:1.33.0",
      ivy"com.github.geirolz::fly4s-core::0.0.19",
      // ivy"org.flywaydb:flyway-core:9.22.3",
      ivy"com.beachape::enumeratum::1.7.3",
      ivy"org.postgresql:postgresql:42.6.0",
    ) ++
      ciris("ciris", "ciris-http4s", "ciris-enumeratum") ++
      typelevel("log4cats-slf4j") ++
      http4s(
        "http4s-ember-server",
        "http4s-ember-client",
        "http4s-dsl",
        "http4s-circe",
      )
      ++ tapir(
        "tapir-http4s-server",
        "tapir-swagger-ui-bundle",
        "tapir-json-circe",
        "tapir-sttp-stub-server",
        "tapir-iron",
        "tapir-json-pickler",
        "tapir-files",
      )
      ++ iron("iron", "iron-circe", "iron-cats", "iron-ciris"),
  )

}
