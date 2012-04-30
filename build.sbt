
name := "RecentUsers"

version := "0.1"

resolvers ++= Seq(
	"Bukkit Repo" at "http://repo.bukkit.org/content/repositories/releases"
	//"Spout Repo" at "http://repo.spout.org"
)

//libraryDependencies += "org.spout" % "spoutapi" % "dev-SNAPSHOT"

libraryDependencies += "org.bukkit" % "bukkit" % "1.2.5-R1.2"
