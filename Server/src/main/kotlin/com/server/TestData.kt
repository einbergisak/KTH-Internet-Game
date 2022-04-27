package com.server

import kotlinx.serialization.Serializable

@Serializable
data class TestData(val id: Int, var k: Pair<Int, String>)
