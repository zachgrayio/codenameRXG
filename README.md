# codenameRXG

### DSL usage:

Actor extension:

```kotlin
var Actor.health: Int by ActorAttribute({ 3 })
val Actor.flying : Boolean get() = position.y in 0f..ground
```

Keybindings:

```
ESC on RELEASED does togglePaused
A on PRESSED does ifNot(paused) { player    moveLeft step       play playerMoveAnimation() }
S on PRESSED does ifNot(paused) { player    play "crouch" }
D on PRESSED does ifNot(paused) { player    moveRight step      play playerMoveAnimation() }
A on RELEASED does playerStand
S on RELEASED does playerStand
D on RELEASED does playerStand
```

### Build/Run
To build/run the demo game: `./gradlew run`

### Build distributable
`./gradlew distZip`, and run by CD'ing into the bin directory and executing the run script from there. NOTE: if building for Mac, uncomment the jvm args in build.gradle.
