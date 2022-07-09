package com.escodro.alkaa.kaspresso

import io.github.kakaocup.kakao.common.builders.ViewBuilder

/**
 * Позволяет указывать на сколько parent'ов нужно подняться от указанного View
 *
 * @param [countParents] Количество parent'ов
 * @param [function] Matcher самого верхнего parent'а
 */
fun ViewBuilder.withParent(countParents: Int, function: ViewBuilder.() -> Unit) {
    if (countParents == 0) function()
    else withParent(countParents - 1) { withParent(function) }
}