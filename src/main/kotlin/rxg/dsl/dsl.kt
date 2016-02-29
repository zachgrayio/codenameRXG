package rxg.dsl

object GameplayDSL {
   // companion object {
        val ifNot:(notValue:Boolean, closure:()->Unit) -> ()->Unit = { notValue, closure -> if(!notValue) closure else { {} } }
        val ifIs:(isValue:Boolean, closure:()->Unit) -> ()->Unit = { isValue, closure -> if(isValue) closure else { {} } }
    //}
}
