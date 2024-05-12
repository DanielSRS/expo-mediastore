package expo.modules.mediastore

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoMediastoreModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoMediastore")

    Function("hello") {
      "Hello dan! 👋"
    }
  }
}
