package com.escodro.alkaa.kaspresso.base

import android.Manifest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.kaspersky.kaspresso.interceptors.behaviorkautomator.impl.autoscroll.AutoScrollObjectBehaviorInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.sections.InitSection
import com.kaspersky.kaspresso.testcases.core.testcontext.BaseTestContext
import com.escodro.alkaa.presentation.MainActivity
import org.junit.Rule

open class BaseTestCase(builder: Kaspresso.Builder = defaultBuilder) : TestCase(builder) {

//    /**
//     * Указываем необходимые разрещения для приложения перед тестом
//     */
//    @get:Rule
//    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    )

    /**
     * Действия, выполняемые до запуска теста
     *
     * @param [before] Действия, которые следует выполнить до теста
     * @param [after] Действия, которые следует выполнить после теста
     */
    protected fun launch(
            before: BaseTestContext.() -> Unit = {},
            after: BaseTestContext.() -> Unit = {}
    ): InitSection<Unit, Unit> {

        ActivityTestRule(MainActivity::class.java, false, false)
                .launchActivity(null)

        return before(actions = before).after(after)
    }

    companion object {
        val defaultBuilder
            get() = Kaspresso.Builder.simple().apply {
                flakySafetyParams.timeoutMs = 10_000L
                objectBehaviorInterceptors = objectBehaviorInterceptors.filter {
                    it.javaClass != AutoScrollObjectBehaviorInterceptor::class.java
                }.toMutableList()
            }
    }
}