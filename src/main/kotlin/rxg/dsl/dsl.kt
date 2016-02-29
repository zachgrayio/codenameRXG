package rxg.dsl

/**
 * Most of the gameplay DSL is defined in GameplayEngine interface out of necessity, but since values cant be defined
 * in an interface, some helpers are defined here as well.
 */
object GameplayDSL {
    val ifNot:(notValue:Boolean, closure:()->Unit) -> ()->Unit = { notValue, closure -> if(!notValue) closure else { {} } }
    val ifIs:(isValue:Boolean, closure:()->Unit) -> ()->Unit = { isValue, closure -> if(isValue) closure else { {} } }
}
