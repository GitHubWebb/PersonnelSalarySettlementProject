package com.personal.salary.kotlin.ktx

import java.math.BigDecimal

/**
 * Whether the Double value is 0.
 */
val Double.isZero: Boolean
    get() = BigDecimal(this).compareTo(BigDecimal.ZERO) == 0