package nl.toefel.kotlin.html.dsl


fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

abstract class Tag(val name: String) : Element {
    val children = mutableListOf<Element>()
    val attributes = mutableMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    infix fun String.to(value: String) {
        attributes[this] = value
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${renderAttributes()}>\n")
        children.forEach { it.render(builder, "$indent    ") }
        builder.append("$indent</$name>\n")
    }

    private fun renderAttributes(): String = attributes.map { """ ${it.key}="${it.value}"""" }.joinToString("")

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

class HTML : Tag("html") {
    fun body(init: BODY.() -> Unit): BODY = initTag(BODY(), init)
}

class BODY : Tag("body") {
    fun div(init: DIV.() -> Unit): DIV = initTag(DIV(), init)
    fun a(init: Anchor.() -> Unit): Anchor = initTag(Anchor(), init)
    fun span(init: SPAN.() -> Unit): SPAN = initTag(SPAN(), init)
}

class DIV : Tag("div") {
    fun div(init: DIV.() -> Unit): DIV = initTag(DIV(), init)
    fun a(init: Anchor.() -> Unit): Anchor = initTag(Anchor(), init)
    fun span(init: SPAN.() -> Unit): SPAN = initTag(SPAN(), init)
}

class SPAN : Tag("span") {
    fun div(init: DIV.() -> Unit): DIV = initTag(DIV(), init)
    fun a(init: Anchor.() -> Unit): Anchor = initTag(Anchor(), init)
    fun span(init: SPAN.() -> Unit): SPAN = initTag(SPAN(), init)
}

class Anchor : Tag("anchor") {
    var href: String
        get() = attributes["href"]!!
        set(value) {
            attributes["href"] = value
        }
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}
