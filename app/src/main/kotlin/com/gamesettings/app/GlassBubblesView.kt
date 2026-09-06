package com.gamesettings.app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin
import kotlin.random.Random

/**
 * پس‌زمینه‌ای تزئینی از چند حباب شیشه‌ای که به‌آرامی به سمت بالا شناور می‌شوند.
 *
 * طراحی شده برای سبک بودن:
 *  - فقط یک ValueAnimator با یک شنونده برای همه‌ی حباب‌ها (نه انیمیشن جداگانه به‌ازای هرکدوم)
 *  - رسم ساده با drawCircle + RadialGradient (بدون Bitmap، بدون allocation داخل onDraw)
 *  - انیمیشن هنگام جدا شدن View از صفحه (مثلاً وقتی کاربر از صفحه خارج می‌شه) متوقف می‌شود
 *  - در صورت فعال بودن «کاهش حرکت» در تنظیمات دسترس‌پذیری گوشی، حباب‌ها ثابت نمایش داده می‌شوند
 */
class GlassBubblesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Bubble(
        val xFraction: Float,      // موقعیت افقی پایه (۰ تا ۱ از عرض ویو)
        val radiusDp: Float,
        val speed: Float,          // چرخه در هر حرکت کامل (سرعت متفاوت = حس طبیعی‌تر)
        val phase: Float,          // افست زمانی شروع، تا همه هم‌زمان نباشند
        val driftAmplitudeDp: Float,
        val driftPhase: Float,
        val alpha: Int
    )

    private val bubbles: List<Bubble>
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress = 0f // ۰ تا ۱، بی‌نهایت تکرار می‌شود
    private var animator: ValueAnimator? = null
    private val density = context.resources.displayMetrics.density

    private val reduceMotion: Boolean
        get() = android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f

    init {
        val rnd = Random(42) // seed ثابت تا چیدمان حباب‌ها بین اجراها یکسان و هماهنگ بماند
        bubbles = List(6) {
            Bubble(
                xFraction = rnd.nextFloat(),
                radiusDp = 14f + rnd.nextFloat() * 22f,
                speed = 0.6f + rnd.nextFloat() * 0.5f,
                phase = rnd.nextFloat(),
                driftAmplitudeDp = 8f + rnd.nextFloat() * 10f,
                driftPhase = rnd.nextFloat() * 6.28f,
                alpha = 18 + rnd.nextInt(22)
            )
        }
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    private fun startAnimation() {
        if (reduceMotion || animator != null) return
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16000 // یک چرخه‌ی کامل و کند؛ سرعت واقعی هر حباب از speed خودش میاد
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        for (bubble in bubbles) {
            val radiusPx = bubble.radiusDp * density

            // مسیر عمودی: حباب باید کاملاً از لبه‌ی بالا خارج بشه، بعد کاملاً از پایین دوباره وارد بشه؛
            // این‌طوری چرخه‌ی تکرار هیچ‌وقت با یه «پرش» دیده نمی‌شه (چون در هر دو سرِ چرخه کاملاً نامرئیه)
            val cycle = (progress * bubble.speed + bubble.phase) % 1f
            val travel = height + 2f * radiusPx
            val cy = travel * (1f - cycle) - radiusPx

            // انحراف افقی ملایم و موجی، برای این‌که مسیر کاملاً مستقیم و مصنوعی به‌نظر نرسه
            val driftPx = sin(cycle * 6.28f + bubble.driftPhase) * bubble.driftAmplitudeDp * density
            val cx = width * bubble.xFraction + driftPx

            if (cy < -radiusPx || cy > height + radiusPx) continue

            paint.shader = RadialGradient(
                cx, cy, radiusPx,
                intArrayOf(
                    withAlpha(0xFFFFFF, (bubble.alpha * 1.6f).toInt().coerceAtMost(255)),
                    withAlpha(0xFFFFFF, bubble.alpha / 2),
                    withAlpha(0xFFFFFF, 0)
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy, radiusPx, paint)
        }
    }
}

/** کمکی برای ساخت رنگ ARGB از یک رنگ پایه (RGB) + مقدار آلفای دلخواه */
private fun withAlpha(rgb: Int, alpha: Int): Int {
    val a = alpha.coerceIn(0, 255)
    return (a shl 24) or (rgb and 0x00FFFFFF)
}
