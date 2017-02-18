package pl.kpob.extensions

/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */

fun String.addDiacriticalMarks(): String = this.replace("a".toRegex(), "(a|ą)")
        .replace("c".toRegex(), "(c|ć)")
        .replace("e".toRegex(), "(e|ę)")
        .replace("l".toRegex(), "(l|ł)")
        .replace("n".toRegex(), "(n|ń)")
        .replace("o".toRegex(), "(o|ó)")
        .replace("s".toRegex(), "(s|ś)")
        .replace("z".toRegex(), "(z|ź|ż)")
        .replace(" ".toRegex(), "( |-)")