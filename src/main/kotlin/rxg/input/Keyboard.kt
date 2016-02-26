package rxg.input

data class KeyEvent(val key: Keys, val keyAction: KeyActions)

enum class KeyActions(val glfwValue:Int) {
    PRESSED(0x1),
    RELEASED(0x0),
    HELD(0x2);

    companion object {
        fun from(value: Int): KeyActions = KeyActions.values().first { it.glfwValue == value }
    }
}

enum class Keys(val glfwValue:Int) {
    ESC(0x100),
    ENTER(0x101),
    SPACE(0x20),
    _0(0x30),
    _1(0x31),
    _2(0x32),
    _3(0x33),
    _4(0x34),
    _5(0x35),
    _6(0x36),
    _7(0x37),
    _8(0x38),
    _9(0x39),
    A(0x41),
    B(0x42),
    C(0x43),
    D(0x44),
    E(0x45),
    F(0x46),
    G(0x47),
    H(0x48),
    I(0x49),
    J(0x4A),
    K(0x4B),
    L(0x4C),
    M(0x4D),
    N(0x4E),
    O(0x4F),
    P(0x50),
    Q(0x51),
    R(0x52),
    S(0x53),
    T(0x54),
    U(0x55),
    V(0x56),
    W(0x57),
    X(0x58),
    Y(0x59),
    Z(0x5A);

    companion object {
        fun from(value: Int): Keys = Keys.values().first { it.glfwValue == value }
    }
}
