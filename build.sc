import mill._, scalalib._, publish._
import mill.scalajslib._

trait SbtLayoutScalaJSModule extends ScalaJSModule {
  override def sources = T.sources { millSourcePath / 'src / 'main / 'scala }
  override def resources = T.sources { millSourcePath / 'src / 'main / 'resources }
}

trait CommonScalaJsModule extends SbtLayoutScalaJSModule {
  override def scalaVersion = "2.11.12"
  override def scalaJSVersion = "0.6.26"
  override def scalacOptions = Seq("-deprecation", "-feature")
  override def scalacPluginIvyDeps = T {
    super.scalacPluginIvyDeps() ++
    Seq(
      ivy"org.scalamacros:::paradise:2.1.1",
      ivy"org.spire-math::kind-projector:0.9.9"
    )
  }
}

trait AdbPublishModule extends ScalaModule with PublishModule {
  def publishVersion = "0.3.0"

  def pomSettings = PomSettings(
    description = "Amazing components & design language in Scala.js",
    organization = "org.ant-design-binding",
    url = "https://ant-design-binding.org/",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("ant-design-binding", "ant-design-binding"),
    developers = Seq(
      Developer("lxohi", "lxohi", "https://github.com/lxohi")
    )
  )
}

object `adb-component-document-util` extends CommonScalaJsModule with AdbPublishModule {
  override def ivyDeps = Agg(
    ivy"com.thoughtworks.binding::dom::11.6.0",
    ivy"com.thoughtworks.binding::futurebinding::11.6.0",
    ivy"com.thoughtworks.binding::bindable::1.0.1",
    ivy"com.beachape::enumeratum::1.5.13"
  )
}

object `adb-component` extends CommonScalaJsModule with AdbPublishModule {
  override def moduleDeps = Seq(`adb-component-document-util`)
}

object `adb-web-document` extends CommonScalaJsModule {
  override def moduleDeps = Seq(`adb-component`)
}

def copyWebDocumentAssets = T {
  val ctx = implicitly[mill.util.Ctx]
  os.remove.all(ctx.dest)
  os.makeDir.all(ctx.dest)
  val dest = ctx.dest

  val fullOptJsPath = `adb-web-document`.fastOpt().path
  os.copy(fullOptJsPath, dest / "adb-web-document-opt.js")
  os.copy(os.Path(fullOptJsPath.toString+".map"), dest / "out.js.map")

  val resourcePaths = `adb-web-document`.resources().map(_.path)
  assert(resourcePaths.forall(os.exists(_)))
  for {
    p <- resourcePaths
    (file, mapping) <-
      if (os.isFile(p)) Iterator(p -> os.rel / p.last)
      else os.walk(p).filterNot(os.isDir).map(sub =>
        if (os.isFile(sub)) sub -> sub.relativeTo(p) else throw new RuntimeException(s"Found broken file: $sub (may be broken links?)")).sorted
  } {
    os.copy(file, dest / mapping)
  }
}

