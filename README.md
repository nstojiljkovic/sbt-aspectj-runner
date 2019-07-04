sbt-aspectj-runner
=========

This project contains two [sbt] plugins that automatically configure your build to perform [Load-time weaving] \(LTW\)
with Aspectj when running your application from within SBT, both for regular applications and Play Framework projects
[in development mode] and ensure that your aspects will always be woven as expected.

SBT version 1.1 is required.

## Why this plugin?

First and foremost, simplicity. Although adding the AspectJ Weaver agent is just about adding the `-javaagent` option
to the JVM, doing so can be challenging when running from SBT. These plugins take care of the corner cases and ensure
that hitting `run` will just work, regardless your project type or whether you are forking the JVM or not.

## Regular Projects (non-Play)

### Configuring

Add the `sbt-aspectj-runner` plugin to your `project/plugins.sbt` file using the code bellow:

```scala
addSbtPlugin("com.github.nstojiljkovic" % "sbt-aspectj-runner" % "1.2.3")
```

### Running

Just `run`, like you do all the time!

Here is what the plugin will do depending on your `fork` settings:
* **fork in run := true**: The forked process will run with the `-javaagent:<jarpath>` and that's all.
* **fork in run := false**: A custom classloader called [WeavingURLClassLoader] will be used. This classloader will
  perform the same load-time weaving duties done by the AspectJ Weaver agent.


## Play Projects

### Configuring

For Play Framework 2.7 projects add the `sbt-aspectj-runner-play-2.7` to your `project/plugins.sbt` file:

```scala
addSbtPlugin("com.github.nstojiljkovic" % "sbt-aspectj-runner-play-2.7" % "1.2.3")

```

For Play Framework 2.6 projects add the `sbt-aspectj-runner-play-2.6` to your `project/plugins.sbt` file:

```scala
addSbtPlugin("com.github.nstojiljkovic" % "sbt-aspectj-runner-play-2.6" % "1.2.3")

```

For Play 2.4 and 2.5 you can use the older `sbt-aspectj-play-runner` plugin:

```scala
addSbtPlugin("com.github.nstojiljkovic" % "sbt-aspectj-play-runner" % "1.2.3")

```

This plugin has been tested with **Play 2.4.8**, **Play 2.5.10**, **Play 2.6.23** and **Play 2.7.3**.

### Running

Just `run`, like you do all the time! A notice will be shown saying that you are running your application with the
AspectJ Weaver.

The Play Framework SBT plugin will not allow the JVM to be forked so this plugin will override the way class loaders are
created to use [WeavingURLClassLoader] instead, making sure that aspects will be woven when running on Development mode.
