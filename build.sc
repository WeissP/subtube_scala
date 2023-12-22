import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import mill._, scalalib._

class Deps(org: String, version: String) {
  def apply(pkgs: String*) = {
    pkgs.map(pkg => ivy"${org}::${pkg}::${version}")
  }
}

object http4s extends Deps("org.http4s", "0.23.24")
object tapir extends Deps("com.softwaremill.sttp.tapir", "1.9.5")
object iron extends Deps("io.github.iltotore", "2.3.0")

object backend extends ScalaModule with ScalafixModule {
  def scalaVersion = "3.3.1"
  def ivyDeps = Agg.from(
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
      )
      ++ iron("iron", "iron-circe"),
  )

}
